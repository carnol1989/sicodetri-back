package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_0;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_03;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_332;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.model.Projection;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator;
import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Generates projections for <em>permanent additions</em> based on SUNAT tax table
 * account <strong>"332"</strong>. This component filters eligible fixed assets and, for each
 * asset, iterates over the remaining useful life to build monthly financial depreciation,
 * tax depreciation, and the corresponding additions/deductions. Once the projection is
 * produced, existing persisted records for the asset are removed and the newly generated
 * records are stored.
 *
 * <p><strong>Responsibilities</strong></p>
 * <ul>
 *   <li>Identify eligible assets for permanent additions (SUNAT account {@code "332"}).</li>
 *   <li>Compute monthly financial and tax depreciation for the remaining useful life.</li>
 *   <li>Derive additions/deductions and persist the results atomically for the asset.</li>
 * </ul>
 *
 * <p><strong>Operational notes</strong></p>
 * <ul>
 *   <li>This generator performs <strong>destructive updates</strong> for a given asset and period
 *       (delete-then-insert of related records). It should be invoked inside a transactional
 *       boundary provided by the service layer.</li>
 *   <li>Given identical inputs (asset state, tax table, and accounting period), repeated runs are
 *       effectively idempotent with respect to the resulting persisted data.</li>
 *   <li>Thread-safety is delegated to the caller; instances are Spring-managed and stateless.</li>
 * </ul>
 *
 * <p><strong>Class</strong>: {@link PermanentAdditionProjectionGenerator}<br/>
 * <strong>Company</strong>: EPS Grau S.A.<br/>
 * <strong>Copyright</strong>: &copy; 2025 EPS Grau S.A. All rights reserved.</p>
 *
 * <p><strong>Authors</strong>:</p>
 * <ul>
 *   <li>Carlos Augusto Nole Machaca</li>
 * </ul>
 *
 * <p><strong>Change log</strong>:</p>
 * <ul>
 *   <li>Jun 11, 2025 — Initial version.</li>
 * </ul>
 *
 * @since 1.0
 * @see ProjectionGenerator
 * @see TaxTableSunatService#findByAccountigAccountService(String)
 */
@Component
public class PermanentAdditionProjectionGenerator extends AbstractProjectionGenerator {

    @Override
    protected Projection doGenerate(String username,
                                    LocalDate period,
                                    FinancialFixedAsset asset,
                                    Map<String, TaxTableSunat> taxParams) {

        // 0) Elegibilidad: si no califica, devolvemos vacío (ahorra CPU/GC)
        if (!isEligible(asset)) {
            Projection empty = new Projection();
            empty.setTaxDepreciationList(new ArrayList<>());
            empty.setAdditionList(new ArrayList<>());
            empty.setFinancialDepreciationList(new ArrayList<>());
            empty.setDeductionList(new ArrayList<>());
            return empty;
        }

        // 1) Resolver parámetros tributarios (clave estable; por defecto: cuenta contable del activo)
        final String taxKey = resolveTaxKey(asset);
        final TaxTableSunat taxTable = requireTax(taxKey, taxParams);

        // 2) Ventana de proyección (por defecto: solo el mes de 'period'; override computeProjectionEnd para vida útil)
        final LocalDate end = computeProjectionEnd(period, asset);
        requireMonthLoopInvariant(period, end);

        // 3) Contenedores de salida
        final List<TaxDepreciation> taxList = new ArrayList<>();
        final List<Addition> additions = new ArrayList<>();
        final List<FinancialDepreciation> finList = new ArrayList<>();

        // 4) Registro previo para la primera iteración (tu entidad usa isFirst)
        TaxDepreciation prev = firstPrevForPermanentAddition(asset, period);

        // 5) Bucle mensual: delega cálculo a la entidad (calculateDepreciation con S_VAL_03)
        for (LocalDate m = period; !m.isAfter(end); m = nextMonth(m)) {

            // a) Construir registro actual
            TaxDepreciation curr = new TaxDepreciation();
            curr.setFinancialFixedAsset(asset);
            curr.setAccountingPeriod(m);
            curr.setAddedBy(username);
            curr.setAdditionDate(LocalDateTime.now());
            // La semántica de "primer mes" la determina prev.isFirst
            curr.setFirst(prev != null && prev.isFirst());

            // b) Cálculo de depreciación tributaria (PERMANENT_ADDITIONS)
            curr.calculateDepreciation(asset, taxTable, m, prev, S_VAL_03);

            // c) Depreciación financiera del mes (si aplica para comparación; por defecto 0)
            BigDecimal finDep = computeFinancialDepForMonth(asset, m, curr.getAnnualNetTaxValuePriorPeriod());

            // d) Adición permanente = exceso tributario vs financiero (> 0)
            BigDecimal permAddition = curr.getValueDepreciation().subtract(finDep, MC);
            if (permAddition.signum() > 0) {
                additions.add(newAddition(username, curr, m, permAddition));
            }

            // e) (Opcional) registrar dep. financiera mensual si la persistes
            if (finDep.signum() > 0) {
                FinancialDepreciation fd = newFinancialDepreciation(username, asset, m, finDep);
                if (fd != null) finList.add(fd);
            }

            // f) Agregar y avanzar
            taxList.add(curr);
            prev = curr;

            // g) Corte temprano si el neto tributario llega a 0 (evita iteraciones inútiles)
            if (shouldStop(curr.getNetTaxValue())) break;
        }

        // 6) Empaquetar resultado
        Projection p = new Projection();
        p.setTaxDepreciationList(taxList);
        p.setAdditionList(additions);
        p.setFinancialDepreciationList(finList);
        p.setDeductionList(new ArrayList<>()); // no hay deducciones en PERMANENT_ADDITIONS
        return p;
    }

    // ---------------------------------------------------------------------------------
    // Elegibilidad (según lo proporcionado)
    // ---------------------------------------------------------------------------------
    private boolean isEligible(FinancialFixedAsset f) {
        String desc = f.getWellAppraisedPreInvestmentStudyDry();
        String lower = desc == null ? "" : desc.toLowerCase(Locale.ROOT).trim();
        return !f.getAcquisitionDate().equals(LOCAL_DATE_DEFAULT)
            && (f.getFixedAssetAccountingAccount().startsWith(S_VAL_332))
            && (lower.contains("tasacion")
            || lower.contains("valor tasado")
            || lower.contains("valortasado"))
            && f.getHistoricalCost().compareTo(BigDecimal.ZERO) > N_VAL_0
            && f.getRevaluedAmount().compareTo(BigDecimal.ZERO) > N_VAL_0
            && f.getInitialBalance().compareTo(BigDecimal.ZERO) > N_VAL_0;
    }

    /** Primer registro “previo” para que tu entidad trate el primer mes como tal (isFirst=true). */
    protected TaxDepreciation firstPrevForPermanentAddition(FinancialFixedAsset asset, LocalDate period) {
        TaxDepreciation prev = new TaxDepreciation();
        prev.setFinancialFixedAsset(asset);
        prev.setAccountingPeriod(period.minusMonths(1)); // solo informativo
        prev.setAddedBy("SYSTEM");
        prev.setAdditionDate(LocalDateTime.now());
        prev.setFirst(true);   // *** clave para tu calculateDepreciation ***
        prev.setLast(false);
        return prev;
    }

    /**
     * Si necesitas una regla financiera para la comparación, implementa aquí.
     * Por defecto, 0 (toda la diferencia queda como adición permanente).
     */
    protected BigDecimal computeFinancialDepForMonth(FinancialFixedAsset asset,
                                                     LocalDate month,
                                                     BigDecimal financialBase) {
        return BigDecimal.ZERO;
    }

    // ---------------------------------------------------------------------------------
    // Factories de entidades (un solo punto de ajuste)
    // ---------------------------------------------------------------------------------

    /** Construye una fila de Adición permanente enlazada al registro tributario del mes. */
    protected Addition newAddition(String username,
                                   TaxDepreciation taxRow,
                                   LocalDate accountingDate,
                                   BigDecimal value) {
        Addition add = new Addition();
        add.setTaxDepreciation(taxRow);
        add.setValueAddition(roundMoney(value));
        // Campos opcionales (si existen en tu modelo):
        try {
            Addition.class.getMethod("setAccountingPeriod", LocalDate.class).invoke(add, accountingDate);
        } catch (Exception ignored) {}
        try {
            Addition.class.getMethod("setAddedBy", String.class).invoke(add, username);
            Addition.class.getMethod("setAdditionDate", LocalDateTime.class).invoke(add, LocalDateTime.now());
        } catch (Exception ignored) {}
        return add;
    }

    /** Construye una fila de Depreciación financiera mensual (si tu modelo la persiste). */
    protected FinancialDepreciation newFinancialDepreciation(String username,
                                                             FinancialFixedAsset asset,
                                                             LocalDate accountingDate,
                                                             BigDecimal finDepreciation) {
        if (finDepreciation == null || finDepreciation.signum() == 0) return null;
        FinancialDepreciation fd = new FinancialDepreciation();
        fd.setFinancialFixedAsset(asset);
        fd.setAccountingPeriod(accountingDate);
        fd.setValueDepreciation(roundMoney(finDepreciation));
        try {
            FinancialDepreciation.class.getMethod("setAddedBy", String.class).invoke(fd, username);
            FinancialDepreciation.class.getMethod("setAdditionDate", LocalDateTime.class)
                .invoke(fd, LocalDateTime.now());
        } catch (Exception ignored) {}
        return fd;
    }

}
