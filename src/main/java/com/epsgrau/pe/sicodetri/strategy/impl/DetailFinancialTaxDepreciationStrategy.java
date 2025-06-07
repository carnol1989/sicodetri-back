package com.epsgrau.pe.sicodetri.strategy.impl;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import com.epsgrau.pe.sicodetri.strategy.ReportStrategy;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * This class is to obtain the data from the Financial Tax Depreciation Detail Book report. <br/>
 * <b>Class</b>: {@link DetailFinancialTaxDepreciationStrategy}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "El servicio inyectado es un bean singleton thread-safe, "
        + "no es mutable por el consumidor."
)
@Component
public class DetailFinancialTaxDepreciationStrategy implements ReportStrategy {

    private final FinancialFixedAssetService service;

    public DetailFinancialTaxDepreciationStrategy(FinancialFixedAssetService service) {
        this.service = service;
    }

    @Override
    public String getCode() {
        return "2";
    }

    @Override
    public String getTemplate() {
        return "rpt_detail_financial_tax_depreciation.jrxml";
    }

    @Override
    public String getBaseFileName() {
        return "Libro_Detalle_Financiero_Tributario_Depreciacion_";
    }

    @Override
    public List<FinancialFixedAssetDto> generateReport(ParamReportDto params) {
        return service.callGetReportDetailFinancialAndTaxDepreciationService(params);
    }
}