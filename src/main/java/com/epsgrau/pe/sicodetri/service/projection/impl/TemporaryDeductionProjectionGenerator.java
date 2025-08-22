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
import com.epsgrau.pe.sicodetri.service.projection.ProjectionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link ProjectionGenerator} implementation for generating temporary deductions projections.
 * Uses the SUNAT tax table code "332" to calculate monthly differences between
 * tax and financial depreciation over the remaining useful life of each eligible fixed asset.
 * After the projection, existing records are cleared and the newly generated
 * depreciation, tax, deduction, and addition entries are persisted.
 * <b>Class</b>: {@link TemporaryDeductionProjectionGenerator}<br/>
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
@Component
public class TemporaryDeductionProjectionGenerator extends AbstractProjectionGenerator
    implements ProjectionGenerator {

    @Autowired
    private TaxTableSunatService taxTableService;

    /**
     * Returns the projection type handled by this generator.
     *
     * @return {@link ProjectionType#TEMPORARY_DEDUCTIONS}
     */
    @Override
    public ProjectionType getType() {
        return ProjectionType.TEMPORARY_DEDUCTIONS;
    }

    /**
     * Generates temporary deductions projections for each eligible fixed asset.
     * Retrieves the SUNAT tax table for code "332". For each fixed asset,
     * checks if it is eligible (acquisition date set, matching account prefix,
     * non-zero initial balance, zero revalued amount) and then processes it
     * over its remaining useful life to compute and persist depreciation,
     * tax, deductions, and any additions.
     *
     * @param accountingPeriod the starting accounting period for projection
     * @param fixedAssets      the list of fixed assets to project
     */
    @Override
    public void generate(String username, LocalDate accountingPeriod, List<FinancialFixedAsset> fixedAssets) {
        TaxTableSunat taxTable = taxTableService.findByAccountigAccountService(S_VAL_332);
        if (taxTable == null) {
            return;
        }
        for (FinancialFixedAsset fixedAsset : fixedAssets) {
            if (isEligible(fixedAsset)) {
                processAsset(fixedAsset, username, accountingPeriod, taxTable);
            }
        }
    }

    /**
     * Determines if a fixed asset is eligible for temporary deductions projection.
     * Criteria:
     * <ul>
     *   <li>Acquisition date is not the default placeholder</li>
     *   <li>Accounting account prefix is "332"</li>
     *   <li>Initial balance greater than zero</li>
     *   <li>Revalued amount equals zero</li>
     * </ul>
     *
     * @param f the fixed asset to check
     * @return {@code true} if the asset qualifies, {@code false} otherwise
     */
    private boolean isEligible(FinancialFixedAsset f) {
        return !f.getAcquisitionDate().equals(LOCAL_DATE_DEFAULT)
            && f.getFixedAssetAccountingAccount().startsWith(S_VAL_332)
            && f.getInitialBalance().compareTo(BigDecimal.ZERO) > N_VAL_0
            && f.getRevaluedAmount().compareTo(BigDecimal.ZERO) == N_VAL_0;
    }

    /**
     * Processes a single fixed asset by.:
     * <ol>
     *   <li>Initializing the first-period tax depreciation record</li>
     *   <li>Building a {@link Projection} model for the given period</li>
     *   <li>Iteratively generating monthly depreciation and determining deductions</li>
     *   <li>Deleting existing database records for this asset</li>
     *   <li>Persisting the newly generated records</li>
     * </ol>
     *
     * @param fixedAsset the fixed asset to project
     * @param period     the accounting period to start projection
     * @param table      the SUNAT tax table entry for code "332"
     */
    private void processAsset(FinancialFixedAsset fixedAsset,
                              String username,
                              LocalDate period,
                              TaxTableSunat table) {
        TaxDepreciation firstTax = new TaxDepreciation();
        firstTax.setNetTaxValue(BigDecimal.ZERO);
        firstTax.setFirst(true);
        firstTax.setLast(false);

        Projection proj = new Projection();
        proj.setAccountigPeriod(period);
        proj.setAccountigPeriodNext(period);
        proj.setFinancialDepreciationList(new ArrayList<>());
        proj.setTaxDepreciationList(new ArrayList<>());
        proj.setAdditionList(new ArrayList<>());
        proj.setDeductionList(new ArrayList<>());
        proj.setTaxDepreciation(firstTax);

        int yearsPassed = Period.between(fixedAsset.getAcquisitionDate(), period).getYears();
        int remaining = fixedAsset.getUsefulLifeYears().intValue() - yearsPassed;
        for (int i = 0; i < remaining; i++) {
            proj = processYearDepreciation(fixedAsset, username, proj, table, S_VAL_01, -1);
        }

        List<FinancialDepreciation> listFinancialDepreciation =
            financialDepreciationService.findByFinancialFixedAssetIdService(fixedAsset.getId());
        financialDepreciationService.deleteAllService(listFinancialDepreciation);
        financialDepreciationService.saveAllService(proj.getFinancialDepreciationList());

        List<TaxDepreciation> listTaxDepreciation =
            taxDepreciationService.findByFinancialFixedAssetIdService(fixedAsset.getId());
        taxDepreciationService.deleteAllService(listTaxDepreciation);
        taxDepreciationService.saveAllService(proj.getTaxDepreciationList());

        List<Deduction> listDeduction =
            deductionService.findByTaxDepreciationInService(listTaxDepreciation);
        deductionService.deleteAllService(listDeduction);
        deductionService.saveAllService(proj.getDeductionList());

        List<Addition> listAddition =
            additionService.findByTaxDepreciationInService(listTaxDepreciation);
        additionService.deleteAllService(listAddition);
        additionService.saveAllService(proj.getAdditionList());
    }

}
