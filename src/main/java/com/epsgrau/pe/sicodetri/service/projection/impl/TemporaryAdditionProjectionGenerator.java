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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
public class TemporaryAdditionProjectionGenerator extends AbstractProjectionGenerator
    implements ProjectionGenerator {

    @Autowired
    private TaxTableSunatService taxTableService;

    /**
     * Identifies the projection type handled by this generator.
     *
     * @return the {@link ProjectionType} value {@link ProjectionType#TEMPORARY_ADDITIONS}.
     */
    @Override
    public ProjectionType getType() {
        return ProjectionType.TEMPORARY_ADDITIONS;
    }

    /**
     * Generates temporary-additions projections for the supplied assets and starting period.
     *
     * <p>For each asset, this method selects the SUNAT tax table using the accounting account
     * prefix, verifies eligibility, and—if eligible—computes the monthly financial/tax
     * depreciation, additions, and deductions across the asset’s useful life. Existing
     * persisted calculation records for the asset are removed and replaced with the new ones.</p>
     *
     * @param username          user identifier used for auditing on generated records
     * @param accountingPeriod  starting accounting period for the projection
     * @param fixedAssets       list of fixed assets to evaluate and project
     *
     * @apiNote This method assumes each asset has a non-null accounting account with
     *           at least three characters (for prefix resolution).
     */
    @Override
    public void generate(String username, LocalDate accountingPeriod, List<FinancialFixedAsset> fixedAssets) {
        for (FinancialFixedAsset f : fixedAssets) {
            String prefix = f.getFixedAssetAccountingAccount().substring(0, 3);
            TaxTableSunat table = taxTableService.findByAccountigAccountService(prefix);
            if (table == null) {
                continue;
            }
            if (isEligible(f)) {
                processAsset(f, username, accountingPeriod, table);
            }
        }
    }

    /**
     * Determines whether a fixed asset qualifies for the temporary-additions projection.
     *
     * <p>An asset is considered <em>eligible</em> when all of the following are true:</p>
     * <ul>
     *   <li>The acquisition date is not the default placeholder
     *   ({@link com.epsgrau.pe.sicodetri.util.Constants#LOCAL_DATE_DEFAULT}).</li>
     *   <li>The accounting account starts with one of the prefixes {@code 332}, {@code 333},
     *       {@code 334}, {@code 335}, {@code 336}, or {@code 337}.</li>
     *   <li>The “well appraised / pre-investment study” field does <strong>not</strong>
     *       contain any of: {@code "tasacion"}, {@code "valor tasado"}, {@code "valortasado"}
     *       (case-insensitive).</li>
     *   <li>The initial balance is strictly greater than zero.</li>
     * </ul>
     *
     * @param f the fixed asset under evaluation
     * @return {@code true} if the asset meets all criteria; {@code false} otherwise
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
     * Projects a single fixed asset for the temporary-additions scenario and persists results.
     *
     * <p>Processing steps:</p>
     * <ol>
     *   <li>Initialize the first tax-depreciation snapshot (first/last flags, zero net value).</li>
     *   <li>Build a working {@link Projection} model anchored at the supplied period.</li>
     *   <li>Iterate month-by-month across the asset’s useful life (as per SUNAT table),
     *       invoking {@link AbstractProjectionGenerator#processYearDepreciation} with
     *       {@code S_VAL_02} to populate financial/tax depreciation and additions/deductions.</li>
     *   <li>Delete prior persisted calculations for this asset and save the newly computed sets.</li>
     * </ol>
     *
     * <p><b>Side effects:</b> existing financial depreciation, tax depreciation, additions,
     * and deductions tied to the asset are removed and replaced.</p>
     *
     * @param fixedAsset the asset to project
     * @param username   user identifier used for auditing on generated rows
     * @param period     starting accounting period for the projection
     * @param table      SUNAT tax table entry resolved for the asset’s account prefix
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

        int years = 1;
        int remaining = table.getUsefulLifeYears();
        for (int i = 0; i < remaining; i++) {
            proj = processYearDepreciation(fixedAsset, username, proj, table, S_VAL_02, years);
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
