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
import com.epsgrau.pe.sicodetri.service.projection.ProjectionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link ProjectionGenerator} implementation for generating permanent additions projections.
 * Selects assets eligible for permanent additions based on SUNAT tax table code "332",
 * then iterates through the remaining useful life of each asset to calculate monthly
 * financial and tax depreciation, and finally determines additions or deductions.
 * After projection generation, existing records are removed and new projections are persisted.
 * <b>Class</b>: {@link PermanentAdditionProjectionGenerator}<br/>
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
 *     <li>Jun 11, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Component
public class PermanentAdditionProjectionGenerator extends AbstractProjectionGenerator
    implements ProjectionGenerator {

    @Autowired
    private TaxTableSunatService taxTableService;

    /**
     * Returns the projection type handled by this generator.
     *
     * @return {@link ProjectionType#PERMANENT_ADDITIONS}
     */
    @Override
    public ProjectionType getType() {
        return ProjectionType.PERMANENT_ADDITIONS;
    }

    /**
     * Generates permanent additions projections for each eligible fixed asset.
     * Retrieves the SUNAT tax table entry for account "332". For each asset in
     * {@code fixedAssets}, if it meets the eligibility criteria, processes the
     * asset to create and persist depreciation, additions, and deductions.
     *
     * @param accountingPeriod the starting accounting period for projection
     * @param fixedAssets      the list of fixed assets to project
     */
    @Override
    public void generate(LocalDate accountingPeriod, List<FinancialFixedAsset> fixedAssets) {
        TaxTableSunat table = taxTableService.findByAccountigAccountService(S_VAL_332);
        if (table == null) {
            return;
        }
        for (FinancialFixedAsset f : fixedAssets) {
            if (isEligible(f)) {
                processAsset(f, accountingPeriod, table);
            }
        }
    }

    /**
     * Determines if a fixed asset is eligible for permanent additions projection.
     * Eligibility criteria:
     * <ul>
     *   <li>Acquisition date is not the default placeholder</li>
     *   <li>Accounting account prefix is "332"</li>
     *   <li>Description contains "tasacion", "valor tasado", or "valortasado"
     *   (case-insensitive)</li>
     *   <li>Historical cost, revalued amount, and initial balance are all greater than zero</li>
     * </ul>
     *
     * @param f the fixed asset to check
     * @return {@code true} if the asset qualifies, {@code false} otherwise
     */
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

    /**
     * Processes a single fixed asset by.:
     * <ol>
     *   <li>Initializing a first-period tax depreciation record</li>
     *   <li>Building a {@link Projection} model for the given period</li>
     *   <li>Iteratively generating monthly depreciation, additions, and deductions</li>
     *   <li>Deleting existing database records for this asset</li>
     *   <li>Persisting the newly generated records</li>
     * </ol>
     *
     * @param fixedAsset the fixed asset to project
     * @param period     the accounting period to start projection
     * @param table      the SUNAT tax table entry for code "332"
     */
    private void processAsset(FinancialFixedAsset fixedAsset,
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
            proj = processYearDepreciation(fixedAsset, proj, table, S_VAL_03, -1);
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
