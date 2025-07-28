package com.epsgrau.pe.sicodetri.service.report.impl;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import com.epsgrau.pe.sicodetri.service.report.ReportStrategy;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for generating the "Fixed Asset Tax Book" report.
 * Delegates to {@link FinancialFixedAssetService} to call the stored procedure
 * that retrieves the tax book data and returns it as a list of DTOs.
 * <b>Class</b>: {@link TaxBookReportStrategy}<br/>
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
public class TaxBookReportStrategy implements ReportStrategy {

    private final FinancialFixedAssetService service;

    /**
     * Constructs the strategy with the required financial fixed asset service.
     *
     * @param service the service used to fetch tax book report data
     */
    public TaxBookReportStrategy(FinancialFixedAssetService service) {
        this.service = service;
    }

    /**
     * Returns the report code used to select this strategy.
     *
     * @return the code "1"
     */
    @Override
    public String getCode() {
        return "1";
    }

    /**
     * Returns the name of the JasperReports template file for the tax book.
     *
     * @return the JRXML template filename
     */
    @Override
    public String getTemplate() {
        return "rpt_tax_book.jrxml";
    }

    /**
     * Returns the base filename prefix to use when exporting the tax book report.
     *
     * @return the base file name prefix
     */
    @Override
    public String getBaseFileName() {
        return "Libro_Tributario_Activo_Fijo_";
    }

    /**
     * Generates the tax book report data by calling the corresponding service method.
     *
     * @param params the parameters defining the report (year, codes, dates, etc.)
     * @return a list of {@link FinancialFixedAssetDto} representing the tax book entries
     * @throws SQLException if an error occurs while executing the stored procedure
     */
    @Override
    public List<FinancialFixedAssetDto> generateReport(ParamReportDto params) throws SQLException {
        return service.callGetReportTaxBookService(params);
    }
}