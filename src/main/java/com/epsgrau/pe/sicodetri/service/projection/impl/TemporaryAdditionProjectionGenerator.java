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
import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.model.Projection;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator;
import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class TemporaryAdditionProjectionGenerator extends AbstractProjectionGenerator {

    @Override
    protected Projection doGenerate(
        String username,
        LocalDate period,
        FinancialFixedAsset asset,
        Map<String, TaxTableSunat> taxParams
    ) {
        if (!isEligible(asset)) {
            return newBaseProjection(period, asset);
        }

        // OK logic: key is the first 3 digits (332-337)
        String prefix = asset.getFixedAssetAccountingAccount().substring(0, 3);
        TaxTableSunat table = requireTaxByPrefix(prefix, taxParams);

        Projection proj = newBaseProjection(period, asset);

        int years = 1;
        int remaining = table.getUsefulLifeYears();
        for (int i = 0; i < remaining; i++) {
            proj = processYearDepreciation(asset, username, proj, table, S_VAL_02, years);
            years++;
        }

        return proj;
    }

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

}
