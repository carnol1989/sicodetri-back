package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_0;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_02;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_332;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_333;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_334;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_335;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_336;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_337;

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
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Projection generator for <em>Temporary Additions</em>.
 *
 * <p>This component drives the monthly projection of financial and tax depreciation
 * for eligible fixed assets under the <strong>temporary additions</strong> regime.
 * For each asset, it:</p>
 * <ol>
 *   <li>Selects the applicable SUNAT tax table based on the first three digits of
 *       the accounting account (prefixes {@code 332} – {@code 337}).</li>
 *   <li>Validates business eligibility rules (see {@link #isEligible(FinancialFixedAsset)}).</li>
 *   <li>Iterates through the asset’s useful life, producing monthly financial and tax
 *       depreciation entries and deriving additions/deductions as needed.</li>
 *   <li>Replaces any existing persisted records (financial/tax depreciation, additions,
 *       deductions) with the newly computed set for the asset.</li>
 * </ol>
 *
 * <p><b>Important:</b> This generator performs a delete-and-insert refresh of the
 * calculated records for each processed asset.</p>
 *
 * <b>Class</b>: {@link TemporaryAdditionProjectionGenerator}<br/>
 * <b>Company</b>: EPS Grau<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau
 *
 * @see AbstractProjectionGenerator
 * @see ProjectionGenerator
 * @see ProjectionType#TEMPORARY_ADDITIONS
 */
@Component
public class TemporaryAdditionProjectionGenerator extends AbstractProjectionGenerator {

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

        // 2) Ventana de proyección (por defecto: el mes del 'period'; override computeProjectionEnd si vas por vida útil)
        final LocalDate end = computeProjectionEnd(period, asset);
        requireMonthLoopInvariant(period, end);

        // 3) Contenedores de salida
        final List<TaxDepreciation> taxList = new ArrayList<>();
        final List<Addition> additions = new ArrayList<>();
        final List<FinancialDepreciation> finList = new ArrayList<>();

        // 4) Registro previo para la primera iteración (tu entidad usa isFirst)
        TaxDepreciation prev = firstPrevForTemporaryAddition(username, asset, period);

        // 5) Bucle mensual: delega cálculo a la entidad (calculateDepreciation)
        for (LocalDate m = period; !m.isAfter(end); m = nextMonth(m)) {

            // a) Construir registro actual
            TaxDepreciation curr = new TaxDepreciation();
            curr.setFinancialFixedAsset(asset);
            curr.setAccountingPeriod(m);
            curr.setAddedBy(username);
            curr.setAdditionDate(LocalDateTime.now());
            // La semántica de "primer mes" la determina prev.isFirst
            curr.setFirst(prev != null && prev.isFirst());

            // b) Cálculo con método de dominio (TEMPORARY_ADDITIONS = S_VAL_02)
            curr.calculateDepreciation(asset, taxTable, m, prev, S_VAL_02);

            // c) Depreciación financiera del mes (si aplica para comparación; por defecto 0)
            BigDecimal finDep = computeFinancialDepForMonth(asset, m, curr.getAnnualNetTaxValuePriorPeriod());

            // d) Adición temporal = exceso trib. vs. financiero (> 0)
            BigDecimal tempAddition = curr.getValueDepreciation().subtract(finDep, MC);
            if (tempAddition.signum() > 0) {
                additions.add(newAddition(username, curr, m, tempAddition));
            }

            // e) (Opcional) registrar dep. financiera mensual si la persistes
            if (finDep.signum() > 0) {
                FinancialDepreciation fd = newFinancialDepreciation(username, asset, m, finDep);
                if (fd != null) finList.add(fd);
            }

            // f) Agregar y avanzar
            taxList.add(curr);
            prev = curr;

            // g) Corte temprano si el neto llega a 0 (evita iteraciones inútiles)
            if (shouldStop(curr.getNetTaxValue())) {
                break;
            }
        }

        // 6) Empaquetar resultado
        Projection p = new Projection();
        p.setTaxDepreciationList(taxList);
        p.setAdditionList(additions);
        p.setFinancialDepreciationList(finList);
        p.setDeductionList(new ArrayList<>());
        return p;
    }

    /**
     * Activo elegible para TEMPORARY_ADDITIONS.
     * Mantiene tu lógica: fecha válida, cuenta 332–337, sin “tasación” en descripción,
     * y saldo inicial > 0.
     */
    private boolean isEligible(FinancialFixedAsset f) {
        String desc = f.getWellAppraisedPreInvestmentStudyDry();
        String lower = (desc == null ? "" : desc.toLowerCase(Locale.ROOT).trim());
        return !f.getAcquisitionDate().equals(LOCAL_DATE_DEFAULT)
            && (f.getFixedAssetAccountingAccount().startsWith(S_VAL_332)
            || f.getFixedAssetAccountingAccount().startsWith(S_VAL_333)
            || f.getFixedAssetAccountingAccount().startsWith(S_VAL_334)
            || f.getFixedAssetAccountingAccount().startsWith(S_VAL_335)
            || f.getFixedAssetAccountingAccount().startsWith(S_VAL_336)
            || f.getFixedAssetAccountingAccount().startsWith(S_VAL_337))
            && !(lower.contains("tasacion")
            || lower.contains("valor tasado")
            || lower.contains("valortasado"))
            && f.getInitialBalance().compareTo(BigDecimal.ZERO) > N_VAL_0;
    }

    /** Primer registro “previo” para que tu entidad trate el primer mes como tal (isFirst=true). */
    protected TaxDepreciation firstPrevForTemporaryAddition(String username, FinancialFixedAsset asset,
                                                            LocalDate period) {
        TaxDepreciation prev = new TaxDepreciation();
        prev.setFinancialFixedAsset(asset);
        prev.setAccountingPeriod(period.minusMonths(1)); // solo informativo
        prev.setAddedBy(username);
        prev.setAdditionDate(LocalDateTime.now());
        prev.setFirst(true);   // *** clave para tu calculateDepreciation ***
        prev.setLast(false);
        // Campos numéricos pueden quedar en null/0; tu método los maneja en S_VAL_02
        return prev;
    }

    /**
     * Si necesitas una regla financiera para la comparación, implementa aquí.
     * Por defecto, 0 (toda la depreciación del mes se considera adición temporal).
     */
    protected BigDecimal computeFinancialDepForMonth(FinancialFixedAsset asset,
                                                     LocalDate month,
                                                     BigDecimal financialBase) {
        return BigDecimal.ZERO;
    }

    /** Construye una fila de Adición temporal enlazada al registro tributario del mes. */
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
