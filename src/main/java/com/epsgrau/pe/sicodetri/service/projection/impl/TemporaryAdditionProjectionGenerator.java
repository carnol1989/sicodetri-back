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
import com.epsgrau.pe.sicodetri.service.projection.ProjectionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link ProjectionGenerator} implementation for generating temporary additions projections.
 * For each fixed asset, this generator selects the appropriate SUNAT tax table based on
 * the asset's accounting account prefix (332–337), checks eligibility criteria, and then
 * processes the asset over its full useful life to calculate monthly financial and tax
 * depreciation, determining additions or deductions accordingly.
 * After projection generation, existing depreciation, tax, addition, and deduction records
 * are removed and replaced with the newly computed entries.
 * <b>Class</b>: {@link TemporaryAdditionProjectionGenerator}<br/>
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
public class TemporaryAdditionProjectionGenerator extends AbstractProjectionGenerator
    implements ProjectionGenerator {

    @Autowired
    private TaxTableSunatService taxTableService;

    /**
     * Returns the projection type handled by this generator.
     *
     * @return {@link ProjectionType#TEMPORARY_ADDITIONS}
     */
    @Override
    public ProjectionType getType() {
        return ProjectionType.TEMPORARY_ADDITIONS;
    }

    /**
     * Generates temporary additions projections for each eligible fixed asset.
     * Iterates through the provided asset list; for each asset, determines the
     * SUNAT tax table based on its accounting account prefix and, if the asset
     * meets the eligibility criteria, processes the asset to compute and persist
     * its depreciation, additions, and deductions over the asset’s useful life.
     *
     * @param accountingPeriod the starting accounting period for the projection
     * @param fixedAssets      the list of fixed assets to project
     */
    @Override
    public void generate(LocalDate accountingPeriod, List<FinancialFixedAsset> fixedAssets) {
        for (FinancialFixedAsset f : fixedAssets) {
            String prefix = f.getFixedAssetAccountingAccount().substring(0, 3);
            TaxTableSunat table = taxTableService.findByAccountigAccountService(prefix);
            if (table == null) {
                continue;
            }
            if (isEligible(f)) {
                processAsset(f, accountingPeriod, table);
            }
        }
    }

    /**
     * Determines if a fixed asset is eligible for temporary additions projection.
     * Criteria:
     * <ul>
     *   <li>Acquisition date is not the default placeholder</li>
     *   <li>Accounting account prefix is one of 332–337</li>
     *   <li>Description does <em>not</em> contain "tasacion", "valor tasado", or "valortasado"</li>
     *   <li>Initial balance is greater than zero</li>
     * </ul>
     *
     * @param f the fixed asset to check
     * @return {@code true} if the asset qualifies, {@code false} otherwise
     */
    private boolean isEligible(FinancialFixedAsset f) {
        String desc = f.getWellAppraisedPreInvestmentStudyDry();
        String lower = desc == null ? "" : desc.toLowerCase(Locale.ROOT).trim();
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

    /**
     * Processes a single fixed asset by.:
     * <ol>
     *   <li>Initializing the first-period tax depreciation record</li>
     *   <li>Building a {@link Projection} model for the given period</li>
     *   <li>Iteratively generating monthly depreciation, additions, and deductions</li>
     *   <li>Deleting existing database records for this asset</li>
     *   <li>Persisting the newly generated records</li>
     * </ol>
     *
     * @param fixedAsset the fixed asset to project
     * @param period     the accounting period to start projection
     * @param table      the SUNAT tax table entry for this asset’s account prefix
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

        int years = 1;
        int remaining = table.getUsefulLifeYears();
        for (int i = 0; i < remaining; i++) {
            proj = processYearDepreciation(fixedAsset, proj, table, S_VAL_02, years);
            years++;
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
