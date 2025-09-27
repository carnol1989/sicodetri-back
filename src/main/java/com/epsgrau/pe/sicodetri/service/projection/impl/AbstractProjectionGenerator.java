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
import com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for projection generators.
 *
 * <p>This batch-oriented version is PURE: it does not call repositories/services.
 * It replicates the previous (OK) business logic to ensure projections match
 * the expected Excel outputs.</p>
 *
 * <p>Key behavior (same as OK version):</p>
 * <ul>
 *   <li>Projects month-by-month using a year loop (12 months per year).</li>
 *   <li>Generates BOTH financial and tax depreciation rows.</li>
 *   <li>Decides Addition vs Deduction using the same rules as the OK generator.</li>
 *   <li>Calls TaxDepreciation.calculateDepreciation() passing the BASE period
 *       (projection.getAccountigPeriod()) rather than the iterated month.</li>
 * </ul>
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
@Slf4j
public abstract class AbstractProjectionGenerator implements ProjectionGenerator {

    @Override
    public final Projection generate(
        String username,
        LocalDate period,
        FinancialFixedAsset asset,
        Map<String, TaxTableSunat> taxParams
    ) {
        Objects.requireNonNull(asset, "asset must not be null");
        Objects.requireNonNull(period, "period must not be null");
        Objects.requireNonNull(taxParams, "taxParams must not be null");

        final String safeUser = (username == null || username.isBlank()) ? "ANONYMOUS" : username;
        final LocalDate normalized = YearMonth.from(period).atDay(1);

        return doGenerate(safeUser, normalized, asset, taxParams);
    }

    /**
     * Generator-specific implementation (pure).
     */
    protected abstract Projection doGenerate(
        String username,
        LocalDate period,
        FinancialFixedAsset asset,
        Map<String, TaxTableSunat> taxParams
    );

    /**
     * Same algorithm as the OK version: generate 12 monthly rows and update the projection cursor/state.
     *
     * @param fixedAsset     asset to project
     * @param username       audit user
     * @param projection     mutable projection state (cursor + lists)
     * @param taxTable       SUNAT tax parameters row
     * @param typeProjection projection type constant (S_VAL_01, S_VAL_02, S_VAL_03)
     * @param numberYears    year counter used only by some rules (kept to match OK)
     * @return updated projection
     */
    protected Projection processYearDepreciation(
        FinancialFixedAsset fixedAsset,
        String username,
        Projection projection,
        TaxTableSunat taxTable,
        String typeProjection,
        int numberYears
    ) {
        for (int month = 0; month < 12; month++) {

            // 1) Financial depreciation
            FinancialDepreciation fin = new FinancialDepreciation();
            fin.setFinancialFixedAsset(fixedAsset);
            fin.setAccountingPeriod(projection.getAccountigPeriodNext());
            fin.calculateDepreciation(fixedAsset, typeProjection, numberYears);
            fin.setAddedBy(username);
            fin.setAdditionDate(LocalDateTime.now());
            projection.getFinancialDepreciationList().add(fin);

            // 2) Tax depreciation
            TaxDepreciation tax = new TaxDepreciation();
            tax.setFinancialFixedAsset(fixedAsset);
            tax.setAccountingPeriod(projection.getAccountigPeriodNext());

            // IMPORTANT: pass BASE period (projection.getAccountigPeriod()) as in OK logic
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

            // 3) Addition vs Deduction logic (same as OK)
            BigDecimal finDep = Objects.requireNonNullElse(fin.getValueDepreciation(), BigDecimal.ZERO);
            BigDecimal taxDep = Objects.requireNonNullElse(tax.getValueDepreciation(), BigDecimal.ZERO);
            BigDecimal diff = taxDep.subtract(finDep);

            BigDecimal prevNet = Objects.requireNonNullElse(
                projection.getTaxDepreciation() != null ? projection.getTaxDepreciation().getNetTaxValue() : BigDecimal.ZERO,
                BigDecimal.ZERO
            );
            BigDecimal currNet = Objects.requireNonNullElse(tax.getNetTaxValue(), BigDecimal.ZERO);

            if ((taxDep.compareTo(finDep) > 0 && currNet.compareTo(BigDecimal.ZERO) > 0)
                || (prevNet.compareTo(BigDecimal.ZERO) > 0 && currNet.compareTo(BigDecimal.ZERO) == 0)) {

                Deduction ded = new Deduction();
                ded.setAccountingPeriod(projection.getAccountigPeriodNext());
                ded.setTaxDepreciation(tax);
                ded.setValueDeduction(diff);
                ded.setAddedBy(username);
                ded.setAdditionDate(LocalDateTime.now());
                projection.getDeductionList().add(ded);

            } else {

                BigDecimal valueToAdd =
                    (typeProjection.equals(S_VAL_01) || typeProjection.equals(S_VAL_03))
                        ? finDep
                        : (numberYears <= fixedAsset.getUsefulLifeYears().intValue()
                        ? taxDep
                        : BigDecimal.ZERO);

                Addition add = new Addition();
                add.setAccountingPeriod(projection.getAccountigPeriodNext());
                add.setTaxDepreciation(tax);
                add.setValueAddition(valueToAdd);
                add.setAddedBy(username);
                add.setAdditionDate(LocalDateTime.now());
                projection.getAdditionList().add(add);
            }

            projection.setAccountigPeriodNext(projection.getAccountigPeriodNext().plusMonths(1));
            projection.setTaxDepreciation(tax);
        }

        // Keep the same (redundant) adjustment as OK version for full equivalence
        projection.setAccountigPeriodNext(
            projection.getAccountigPeriodNext().plusYears(1).minusMonths(12)
        );

        return projection;
    }

    /**
     * Creates an empty Projection with initialized lists and a correct "first" previous TaxDepreciation.
     * This prevents NPEs inside TaxDepreciation.calculateDepreciation().
     */
    protected Projection newBaseProjection(LocalDate period, FinancialFixedAsset asset) {
        TaxDepreciation firstTax = new TaxDepreciation();
        firstTax.setFinancialFixedAsset(asset);
        firstTax.setNetTaxValue(BigDecimal.ZERO);
        firstTax.setValueDepreciation(BigDecimal.ZERO);
        firstTax.setAnnualAccumulatedDepreciationPriorPeriod(BigDecimal.ZERO);
        firstTax.setAnnualNetTaxValuePriorPeriod(BigDecimal.ZERO);
        firstTax.setFirst(true);
        firstTax.setLast(false);

        Projection p = new Projection();
        p.setAccountigPeriod(period);
        p.setAccountigPeriodNext(period);
        p.setFinancialDepreciationList(new ArrayList<>());
        p.setTaxDepreciationList(new ArrayList<>());
        p.setAdditionList(new ArrayList<>());
        p.setDeductionList(new ArrayList<>());
        p.setTaxDepreciation(firstTax);
        return p;
    }

    protected TaxTableSunat requireTaxByPrefix(String prefix, Map<String, TaxTableSunat> taxParams) {
        Objects.requireNonNull(prefix, "prefix");
        final String key = prefix.length() >= 3 ? prefix.substring(0, 3) : prefix;
        final TaxTableSunat t = taxParams.get(key);
        if (t == null) {
            throw new IllegalArgumentException("Missing TaxTableSunat for accounting_account prefix: " + key);
        }
        return t;
    }

}
