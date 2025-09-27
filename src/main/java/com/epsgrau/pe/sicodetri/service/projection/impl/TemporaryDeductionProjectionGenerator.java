package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_0;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_01;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_332;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.model.Projection;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class TemporaryDeductionProjectionGenerator extends AbstractProjectionGenerator {

    @Override
    protected Projection doGenerate(
        String username,
        LocalDate period,
        FinancialFixedAsset asset,
        Map<String, TaxTableSunat> taxParams
    ) {
        if (!isEligible(asset)) {
            return newBaseProjection(period, asset); // empty lists but correct prev state
        }

        final TaxTableSunat table = requireTaxByPrefix(S_VAL_332, taxParams);

        Projection proj = newBaseProjection(period, asset);

        int yearsPassed = Period.between(asset.getAcquisitionDate(), period).getYears();
        int remaining = asset.getUsefulLifeYears().intValue() - yearsPassed;

        for (int i = 0; i < remaining; i++) {
            proj = processYearDepreciation(asset, username, proj, table, S_VAL_01, -1);
        }

        return proj;
    }

    private boolean isEligible(FinancialFixedAsset f) {
        return !f.getAcquisitionDate().equals(LOCAL_DATE_DEFAULT)
            && f.getFixedAssetAccountingAccount().startsWith(S_VAL_332)
            && f.getInitialBalance().compareTo(BigDecimal.ZERO) > N_VAL_0
            && f.getRevaluedAmount().compareTo(BigDecimal.ZERO) == N_VAL_0;
    }

}
