package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_0;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_01;
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
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Generates projections for <em>temporary deductions</em> using SUNAT tax table
 * account <strong>"332"</strong>. For each eligible fixed asset, this component iterates
 * across the remaining useful life, computing monthly financial and tax depreciation and
 * deriving the corresponding deductions (and any required additions). After producing
 * the projection, previously persisted records for the asset are removed and the newly
 * generated records are stored.
 *
 * <p><strong>Responsibilities</strong></p>
 * <ul>
 *   <li>Identify assets applicable to temporary deductions (SUNAT account {@code "332"}).</li>
 *   <li>Compute monthly financial/tax depreciation over the remaining useful life.</li>
 *   <li>Derive deductions (and additions, if any) and persist results consistently.</li>
 * </ul>
 *
 * <p><strong>Operational notes</strong></p>
 * <ul>
 *   <li>This generator performs <strong>destructive updates</strong> per asset and period
 *       (delete-then-insert of projection artifacts). It should be invoked within a
 *       transactional boundary provided by the service layer.</li>
 *   <li>Given the same inputs (asset state, tax table, accounting period), repeated runs are
 *       effectively idempotent regarding the resulting persisted data.</li>
 *   <li>Instances are stateless and Spring-managed; thread-safety is delegated to the caller.</li>
 * </ul>
 *
 * <p><strong>Class</strong>: {@link TemporaryDeductionProjectionGenerator}<br/>
 * <strong>Company</strong>: EPS Grau S.A.<br/>
 * <strong>COPYRIGHT</strong>: &copy; 2025 EPS Grau S.A. All rights reserved.</p>
 *
 * <p><strong>Authors</strong>:</p>
 * <ul>
 *   <li>Carlos Augusto Nole Machaca</li>
 * </ul>
 *
 * <p><strong>Changes</strong>:</p>
 * <ul>
 *   <li>Jun 10, 2025 — Initial version.</li>
 * </ul>
 *
 * @since 1.0
 * @see ProjectionGenerator
 * @see TaxTableSunatService#findByAccountigAccountService(String)
 */
@Component
public class TemporaryDeductionProjectionGenerator extends AbstractProjectionGenerator {

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

        // 2) Ventana de proyección (por defecto: solo el mes de 'period'; override computeProjectionEnd si vas por vida útil)
        final LocalDate end = computeProjectionEnd(period, asset);
        requireMonthLoopInvariant(period, end);

        // 3) Contenedores de salida
        final List<TaxDepreciation> taxList = new ArrayList<>();
        final List<Deduction> deductions = new ArrayList<>();
        final List<FinancialDepreciation> finList = new ArrayList<>();

        // 4) Registro previo para la primera iteración (tu entidad usa isFirst)
        TaxDepreciation prev = firstPrevForTemporaryDeduction(username, asset, period);

        // 5) Bucle mensual: delega cálculo a la entidad (calculateDepreciation con S_VAL_01)
        for (LocalDate m = period; !m.isAfter(end); m = nextMonth(m)) {

            // a) Construir registro actual
            TaxDepreciation curr = new TaxDepreciation();
            curr.setFinancialFixedAsset(asset);
            curr.setAccountingPeriod(m);
            curr.setAddedBy(username);
            curr.setAdditionDate(LocalDateTime.now());
            // La semántica de "primer mes" la determina prev.isFirst
            curr.setFirst(prev != null && prev.isFirst());

            // b) Cálculo de depreciación tributaria
            curr.calculateDepreciation(asset, taxTable, m, prev, S_VAL_01);

            // c) Depreciación financiera del mes (regla propia; por defecto 0)
            BigDecimal finDep = computeFinancialDepForMonth(asset, m, curr.getAnnualNetTaxValuePriorPeriod());

            // d) Deducción temporal = exceso financiero vs tributario (> 0)
            BigDecimal tempDeduction = finDep.subtract(curr.getValueDepreciation(), MC);
            if (tempDeduction.signum() > 0) {
                deductions.add(newDeduction(username, curr, m, tempDeduction));
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
        p.setDeductionList(deductions);
        p.setFinancialDepreciationList(finList);
        p.setAdditionList(new ArrayList<>()); // no hay adiciones en TEMPORARY_DEDUCTIONS
        return p;
    }

    private boolean isEligible(FinancialFixedAsset f) {
        return !f.getAcquisitionDate().equals(LOCAL_DATE_DEFAULT)
            && f.getFixedAssetAccountingAccount().startsWith(S_VAL_332)
            && f.getInitialBalance().compareTo(BigDecimal.ZERO) > N_VAL_0
            && f.getRevaluedAmount().compareTo(BigDecimal.ZERO) == N_VAL_0;
    }

    /** Primer registro “previo” para que tu entidad trate el primer mes como tal (isFirst=true). */
    protected TaxDepreciation firstPrevForTemporaryDeduction(String username, FinancialFixedAsset asset,
                                                             LocalDate period) {
        TaxDepreciation prev = new TaxDepreciation();
        prev.setFinancialFixedAsset(asset);
        prev.setAccountingPeriod(period.minusMonths(1)); // solo informativo
        prev.setAddedBy(username);
        prev.setAdditionDate(LocalDateTime.now());
        prev.setFirst(true);   // *** clave para tu calculateDepreciation ***
        prev.setLast(false);
        return prev;
    }

    /**
     * Si necesitas una regla financiera para la comparación, implementa aquí.
     * Por defecto, 0 (para que la deducción sea 0 salvo que definas la regla).
     */
    protected BigDecimal computeFinancialDepForMonth(FinancialFixedAsset asset,
                                                     LocalDate month,
                                                     BigDecimal financialBase) {
        return BigDecimal.ZERO;
    }

    /** Construye una fila de Deducción temporal enlazada al registro tributario del mes. */
    protected Deduction newDeduction(String username,
                                     TaxDepreciation taxRow,
                                     LocalDate accountingDate,
                                     BigDecimal value) {
        Deduction d = new Deduction();
        d.setTaxDepreciation(taxRow);
        d.setValueDeduction(roundMoney(value));
        // Campos opcionales (si existen en tu modelo):
        try {
            Deduction.class.getMethod("setAccountingPeriod", LocalDate.class).invoke(d, accountingDate);
        } catch (Exception ignored) {}
        try {
            Deduction.class.getMethod("setAddedBy", String.class).invoke(d, username);
            Deduction.class.getMethod("setAdditionDate", LocalDateTime.class).invoke(d, LocalDateTime.now());
        } catch (Exception ignored) {}
        return d;
    }

    /** Construye una fila de Depreciación financiera mensual (si tu modelo la persiste). */
    protected FinancialDepreciation newFinancialDepreciation(String username,
                                                             FinancialFixedAsset asset,
                                                             LocalDate accountingDate,
                                                             BigDecimal finDepreciation) {
        if (finDepreciation == null || finDepreciation.signum() == 0) {
            return null;
        }
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
