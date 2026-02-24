package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_0;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_03;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_332;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.model.Projection;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;
import java.util.Map;

import com.epsgrau.pe.sicodetri.service.tax.TaxTableCatalog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Generates projections for <em>permanent additions</em> based on SUNAT tax table
 * account <strong>"332"</strong>. This component filters eligible fixed assets and, for each
 * asset, iterates over the remaining useful life to build monthly financial depreciation,
 * tax depreciation, and the corresponding additions/deductions. Once the projection is
 * produced, existing persisted records for the asset are removed and the newly generated
 * records are stored.
 *
 * <p><strong>Responsibilities</strong></p>
 * <ul>
 *   <li>Identify eligible assets for permanent additions (SUNAT account {@code "332"}).</li>
 *   <li>Compute monthly financial and tax depreciation for the remaining useful life.</li>
 *   <li>Derive additions/deductions and persist the results atomically for the asset.</li>
 * </ul>
 *
 * <p><strong>Operational notes</strong></p>
 * <ul>
 *   <li>This generator performs <strong>destructive updates</strong> for a given asset and period
 *       (delete-then-insert of related records). It should be invoked inside a transactional
 *       boundary provided by the service layer.</li>
 *   <li>Given identical inputs (asset state, tax table, and accounting period), repeated runs are
 *       effectively idempotent with respect to the resulting persisted data.</li>
 *   <li>Thread-safety is delegated to the caller; instances are Spring-managed and stateless.</li>
 * </ul>
 *
 * <p><strong>Class</strong>: {@link PermanentAdditionProjectionGenerator}<br/>
 * <strong>Company</strong>: EPS Grau S.A.<br/>
 * <strong>Copyright</strong>: &copy; 2025 EPS Grau S.A. All rights reserved.</p>
 *
 * <p><strong>Authors</strong>:</p>
 * <ul>
 *   <li>Carlos Augusto Nole Machaca</li>
 * </ul>
 *
 * <p><strong>Change log</strong>:</p>
 * <ul>
 *   <li>Jun 11, 2025 — Initial version.</li>
 * </ul>
 *
 * @since 1.0
 * @see ProjectionGenerator
 * @see TaxTableSunatService#findByAccountigAccountService(String)
 */
@Slf4j
@Component
public class PermanentAdditionProjectionGenerator extends AbstractProjectionGenerator {

    @Override
    protected Projection doGenerate(
        String username,
        LocalDate period,
        FinancialFixedAsset asset,
        TaxTableCatalog taxCatalog
    ) {
        if (!isEligible(asset)) {
            return newBaseProjection(period, asset);
        }

        final TaxTableSunat table = requireTaxByPrefix(S_VAL_332, asset, taxCatalog);

        Projection proj = newBaseProjection(period, asset);

        int yearsPassed = Period.between(asset.getAcquisitionDate(), period).getYears();
        int remaining = asset.getUsefulLifeYears().intValue() - yearsPassed;

        for (int i = 0; i < remaining; i++) {
            proj = processYearDepreciation(asset, username, proj, table, S_VAL_03, -1);
        }

        return proj;
    }

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

}
