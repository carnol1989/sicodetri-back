package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_0;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_01;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_332;

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
 * This class implements the generation of temporary deductions. <br/>
 * <b>Class</b>: {@link TemporaryDeductionProjectionGenerator}<br/>
 * <b>Copyright</b>: &Copy; 2024 EPS Grau. <br/>
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

    @Override
    public ProjectionType getType() {
        return ProjectionType.TEMPORARY_DEDUCTIONS;
    }

    @Override
    public void generate(LocalDate accountingPeriod, List<FinancialFixedAsset> fixedAssets) {
        TaxTableSunat taxTable = taxTableService.findByAccountigAccountService(S_VAL_332);
        if (taxTable == null) {
            return;
        }
        for (FinancialFixedAsset fixedAsset : fixedAssets) {
            if (isEligible(fixedAsset)) {
                processAsset(fixedAsset, accountingPeriod, taxTable);
            }
        }
    }

    private boolean isEligible(FinancialFixedAsset f) {
        return !f.getAcquisitionDate().equals(LOCAL_DATE_DEFAULT)
            && f.getFixedAssetAccountingAccount().startsWith(S_VAL_332)
            && f.getInitialBalance().compareTo(BigDecimal.ZERO) > N_VAL_0
            && f.getRevaluedAmount().compareTo(BigDecimal.ZERO) == N_VAL_0;
    }

    private void processAsset(FinancialFixedAsset f,
                              LocalDate period,
                              TaxTableSunat table) {
        // inicializar proyección
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

        // cuántos años faltan desde la adquisición
        int yearsPassed = Period.between(f.getAcquisitionDate(), period).getYears();
        int remaining = f.getUsefulLifeYears().intValue() - yearsPassed;
        for (int i = 0; i < remaining; i++) {
            proj = processYearDepreciation(f, proj, table, S_VAL_01, -1);
        }

        // persistir resultados
        financialDepreciationService.saveAllService(proj.getFinancialDepreciationList());
        taxDepreciationService.saveAllService(proj.getTaxDepreciationList());
        deductionService.saveAllService(proj.getDeductionList());
        additionService.saveAllService(proj.getAdditionList());
    }

}
