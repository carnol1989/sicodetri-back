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
 * Abstract base class for generating depreciation projections.
 * Implements the common logic for iterating through months within a year,
 * calculating financial and tax depreciations, and determining additions or
 * deductions for each period.
 * <b>Class</b>: {@link AbstractProjectionGenerator}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau <br/>
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>: <br/>
 *     <ul>
 *     <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:<br/>
 *     <ul>
 *     <li>Jun 10, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
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
     * Processes an entire year of depreciation for the given fixed asset,
     * advancing month by month, and populates the provided {@link Projection}
     * with financial depreciation, tax depreciation, and any required additions or deductions.
     *
     * @param fixedAsset     the fixed asset for which to generate the projection
     * @param projection     the projection model to populate (initial and next periods
     *                       set externally)
     * @param taxTable       the SUNAT tax table entry to use for tax depreciation calculations
     * @param typeProjection the projection type code (e.g., "TEMPORARY_DEDUCTION",
     *                       "PERMANENT_ADDITIONS")
     * @param numberYears    the number of years elapsed since acquisition, used to
     *                       limit projections
     * @return the updated {@link Projection} containing one year’s worth of entries
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
