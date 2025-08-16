package com.epsgrau.pe.sicodetri.service.report.impl;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import com.epsgrau.pe.sicodetri.service.report.ReportStrategy;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for generating the "Detail Financial and Tax Depreciation Book" report.
 * This class delegates to {@link FinancialFixedAssetService} to fetch the detailed
 * financial and tax depreciation data, suitable for use in a JasperReports template.
 * <b>Class</b>: {@link DetailFinancialTaxDepreciationStrategy}<br/>
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

    /**
     * Constructs the strategy with the required service dependency.
     *
     * @param service the FinancialFixedAssetService used to fetch report data
     */
    public DetailFinancialTaxDepreciationStrategy(FinancialFixedAssetService service) {
        this.service = service;
    }

    /**
     * Returns the report code used to select this strategy.
     *
     * @return the code "2"
     */
    @Override
    public String getCode() {
        return "2";
    }

    /**
     * Returns the name of the JasperReports template file.
     *
     * @return the JRXML template filename
     */
    @Override
    public String getTemplate() {
        return "rpt_detail_financial_tax_depreciation.jrxml";
    }

    /**
     * Returns the base filename to use when exporting the report.
     *
     * @return the base file name prefix
     */
    @Override
    public String getBaseFileName() {
        return "Libro_Detalle_Financiero_Tributario_Depreciacion_";
    }

    /**
     * Generates the report data by calling the financial fixed asset service.
     *
     * @param params the parameters defining the report (year, codes, dates, etc.)
     * @return a list of {@link FinancialFixedAssetDto} containing the detailed depreciation data
     */
    @Override
    public List<FinancialFixedAssetDto> generateReport(ParamReportDto params) {
        return service.callGetReportDetailFinancialAndTaxDepreciationService(params);
    }
}