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
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
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
public class TemporaryDeductionProjectionGenerator extends AbstractProjectionGenerator
    implements ProjectionGenerator {

    @Autowired
    private TaxTableSunatService taxTableService;

    /**
     * Returns the projection type handled by this generator.
     *
     * @return the {@link ProjectionType} constant for temporary deductions
     */
    @Override
    public ProjectionType getType() {
        return ProjectionType.TEMPORARY_DEDUCTIONS;
    }

    /**
     * Generates temporary-deduction projections for the provided assets and accounting period.
     * The SUNAT tax table is resolved for account {@code "332"}; if unavailable, the method
     * exits without side effects. For each asset that passes the eligibility check, the asset
     * is processed and its financial/tax depreciation plus deductions (and additions, if any)
     * are (re)persisted.
     *
     * <p><strong>Side effects</strong>: For each eligible asset this method deletes existing
     * financial depreciation, tax depreciation, additions, and deductions and persists the
     * newly computed ones.</p>
     *
     * @param username          application user performing the operation (propagated for audit)
     * @param accountingPeriod  period from which the projection starts (e.g., month start)
     * @param fixedAssets       candidate assets to evaluate and project
     *
     * @apiNote Should run within a {@code @Transactional} context at the service layer to ensure
     *           atomicity of delete/insert operations per asset.
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
     * Determines whether a fixed asset qualifies for temporary-deduction projection.
     *
     * <p><strong>Eligibility criteria</strong>:</p>
     * <ul>
     *   <li>Acquisition date is not the default placeholder
     *   ({@link com.epsgrau.pe.sicodetri.util.Constants#LOCAL_DATE_DEFAULT}).</li>
     *   <li>Accounting account starts with {@code "332"} (SUNAT account).</li>
     *   <li>{@code initialBalance} &gt; 0.</li>
     *   <li>{@code revaluedAmount} == 0.</li>
     * </ul>
     *
     * @param f the fixed asset under evaluation
     * @return {@code true} if the asset meets all criteria; {@code false} otherwise
     */
    private boolean isEligible(FinancialFixedAsset f) {
        return !f.getAcquisitionDate().equals(LOCAL_DATE_DEFAULT)
            && f.getFixedAssetAccountingAccount().startsWith(S_VAL_332)
            && f.getInitialBalance().compareTo(BigDecimal.ZERO) > N_VAL_0
            && f.getRevaluedAmount().compareTo(BigDecimal.ZERO) == N_VAL_0;
    }

    /**
     * Processes a single eligible asset by.:
     * <ol>
     *   <li>Initializing a first-period {@link TaxDepreciation} record.</li>
     *   <li>Creating a {@link Projection} model seeded with the requested period.</li>
     *   <li>Iterating over the remaining useful life to append monthly financial/tax depreciation
     *       and to derive deductions (and additions, if applicable).</li>
     *   <li>Deleting any previously persisted projection records for the asset.</li>
     *   <li>Persisting the newly generated financial/tax depreciation, deductions, and additions.</li>
     * </ol>
     *
     * <p><strong>Parameters</strong>:</p>
     * <ul>
     *   <li><b>fixedAsset</b> — the asset being projected.</li>
     *   <li><b>username</b> — propagated to downstream services for auditing.</li>
     *   <li><b>period</b> — accounting period that anchors the projection.</li>
     *   <li><b>table</b> — SUNAT tax table entry resolved for account {@code "332"}.</li>
     * </ul>
     *
     * @param fixedAsset the fixed asset to project
     * @param username   the user performing the operation (used for audit trails)
     * @param period     starting accounting period of the projection
     * @param table      the resolved SUNAT tax table row (account {@code "332"})
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
