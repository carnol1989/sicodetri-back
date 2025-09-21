package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_01;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_03;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.model.Projection;
import com.epsgrau.pe.sicodetri.service.AdditionService;
import com.epsgrau.pe.sicodetri.service.DeductionService;
import com.epsgrau.pe.sicodetri.service.FinancialDepreciationService;
import com.epsgrau.pe.sicodetri.service.TaxDepreciationService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract base for depreciation projection engines.
 *
 * <p><b>Purpose</b>:
 * Centralizes common month-by-month projection mechanics used by concrete
 * generators (e.g., temporary additions/deductions). It orchestrates the
 * calculation of financial and tax depreciation entries and decides whether
 * to register an {@link Addition} or a {@link Deduction} per period.</p>
 *
 * <p><b>How it works</b>:</p>
 * <ol>
 *   <li>Iterates over 12 monthly periods starting from the {@code Projection}'s
 *       current {@code accountigPeriodNext}.</li>
 *   <li>For each month, computes a {@link FinancialDepreciation} and a
 *       {@link TaxDepreciation} using the provided asset and SUNAT tax table.</li>
 *   <li>Compares monthly tax vs. financial depreciation and creates either an
 *       {@code Addition} or a {@code Deduction}, according to business rules
 *       and projection type (see notes below).</li>
 *   <li>Advances {@code accountigPeriodNext} month by month and updates the
 *       running {@code TaxDepreciation} state in the {@code Projection}.</li>
 * </ol>
 *
 * <p><b>Decision rules (summary)</b>:</p>
 * <ul>
 *   <li>If tax &gt; financial and current tax net value &gt; 0, or if prior tax
 *       net value &gt; 0 and the current net value falls to 0, a {@link Deduction}
 *       is recorded for the delta (tax − financial).</li>
 *   <li>Otherwise, an {@link Addition} is recorded. For types {@link com.epsgrau.pe.sicodetri.util.Constants#S_VAL_01}
 *       or {@link com.epsgrau.pe.sicodetri.util.Constants#S_VAL_03}, the addition uses the financial depreciation; for
 *       other types, the addition uses the tax depreciation up to the asset's
 *       useful life in years; beyond that, the amount is zero.</li>
 * </ul>
 *
 * <p><b>Scope &amp; lifecycle</b>:
 * This abstract class is not a Spring bean by itself; it relies on
 * autowired services provided by concrete subclasses. The method herein
 * mutates the passed {@link Projection} (lists and cursors). Callers are
 * expected to persist any produced entities via services at a higher layer.</p>
 *
 * <p><b>Thread-safety</b>:
 * Instances rely on Spring-managed, typically thread-safe services. The
 * projection method is not synchronized and modifies the provided
 * {@link Projection}; callers must ensure appropriate concurrency control.</p>
 *
 * <b>Class</b>: {@link AbstractProjectionGenerator}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <br/><u>Service Provider</u>: Consultor TI
 *     <br/><u>Developed by</u>:
 *     <ul>
 *     <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *     <li>Jun 10, 2025 (JAR) Class creation.</li>
 *     </ul>
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractProjectionGenerator {

    @Autowired
    protected FinancialDepreciationService financialDepreciationService;

    @Autowired
    protected TaxDepreciationService taxDepreciationService;

    @Autowired
    protected AdditionService additionService;

    @Autowired
    protected DeductionService deductionService;

    /**
     * Generates monthly depreciation entries for a full year starting at
     * {@link com.epsgrau.pe.sicodetri.model.Projection} (campo {@code accountigPeriodNext}), updating the supplied
     * {@link Projection} with.:
     * <ul>
     *   <li>12 {@link FinancialDepreciation} entries</li>
     *   <li>12 {@link TaxDepreciation} entries</li>
     *   <li>12 {@link Addition}/{@link Deduction} entries according to rules</li>
     * </ul>
     *
     * <p><b>Side effects</b>:</p>
     * <ul>
     *   <li>Appends entities to the {@code Projection}'s internal lists.</li>
     *   <li>Advances {@code accountigPeriodNext} by one month per iteration and
     *       adjusts it to the first month of the next year upon completion.</li>
     *   <li>Sets creator metadata ({@code addedBy}, {@code additionDate = now}) on
     *       generated entities.</li>
     *   <li>Updates {@code Projection.taxDepreciation} with the last monthly value.</li>
     * </ul>
     *
     * <p><b>Assumptions</b>:</p>
     * <ul>
     *   <li>{@code fixedAsset}, {@code projection}, and {@code taxTable} are non-null
     *       and consistent with the business calendar.</li>
     *   <li>{@code numberYears} is the elapsed full years since acquisition, used to
     *       cap tax-based additions beyond useful life.</li>
     * </ul>
     *
     * @param fixedAsset     the asset under projection
     * @param username       audit user to be set on generated entities
     * @param projection     projection accumulator (mutated by this method)
     * @param taxTable       SUNAT tax parameters for depreciation computation
     * @param typeProjection business code driving addition/deduction selection
     * @param numberYears    full years since acquisition for useful-life checks
     * @return the same {@link Projection} instance passed in, enriched with one
     *         year of monthly entries and the cursor moved to the next year
     */
    protected Projection processYearDepreciation(FinancialFixedAsset fixedAsset, String username,
        Projection projection, TaxTableSunat taxTable, String typeProjection,
        int numberYears) {
        for (int month = 0; month < 12; month++) {
            // 1) Depreciación financiera
            FinancialDepreciation fin = new FinancialDepreciation();
            fin.setFinancialFixedAsset(fixedAsset);
            fin.setAccountingPeriod(projection.getAccountigPeriodNext());
            fin.calculateDepreciation(fixedAsset, typeProjection, numberYears);
            fin.setAddedBy(username);
            fin.setAdditionDate(LocalDateTime.now());
            projection.getFinancialDepreciationList().add(fin);

            // 2) Depreciación tributaria
            TaxDepreciation tax = new TaxDepreciation();
            tax.setFinancialFixedAsset(fixedAsset);
            tax.setAccountingPeriod(projection.getAccountigPeriodNext());
            tax.calculateDepreciation(
                fixedAsset,
                taxTable,
                projection.getAccountigPeriod(),
                projection.getTaxDepreciation(),
                typeProjection
            );
            tax.setAddedBy(username);
            tax.setAdditionDate(LocalDateTime.now());
            tax.setFirst(false);
            projection.getTaxDepreciationList().add(tax);

            // 3) Lógica de Addition vs Deduction
            BigDecimal diff = tax.getValueDepreciation()
                .subtract(fin.getValueDepreciation());
            if ((tax.getValueDepreciation().compareTo(fin.getValueDepreciation()) > 0
                && tax.getNetTaxValue().compareTo(BigDecimal.ZERO) > 0)
                || (projection.getTaxDepreciation().getNetTaxValue().compareTo(BigDecimal.ZERO) > 0
                && tax.getNetTaxValue().compareTo(BigDecimal.ZERO) == 0)) {
                Deduction ded = new Deduction();
                ded.setAccountingPeriod(projection.getAccountigPeriodNext());
                ded.setTaxDepreciation(tax);
                ded.setValueDeduction(diff);
                ded.setAddedBy(username);
                ded.setAdditionDate(LocalDateTime.now());
                projection.getDeductionList().add(ded);
            } else {
                BigDecimal valueToAdd = (typeProjection.equals(S_VAL_01)
                    || typeProjection.equals(S_VAL_03))
                        ? fin.getValueDepreciation()
                            : (numberYears <= fixedAsset.getUsefulLifeYears().intValue()
                                ? tax.getValueDepreciation() : BigDecimal.ZERO);

                Addition add = new Addition();
                add.setAccountingPeriod(projection.getAccountigPeriodNext());
                add.setTaxDepreciation(tax);
                add.setValueAddition(valueToAdd);
                add.setAddedBy(username);
                add.setAdditionDate(LocalDateTime.now());
                projection.getAdditionList().add(add);
            }

            projection.setAccountigPeriodNext(
                projection.getAccountigPeriodNext().plusMonths(1)
            );
            projection.setTaxDepreciation(tax);
        }
        // Ajuste para el siguiente año
        projection.setAccountigPeriodNext(
            projection.getAccountigPeriodNext().plusYears(1).minusMonths(12)
        );
        return projection;
    }

}
