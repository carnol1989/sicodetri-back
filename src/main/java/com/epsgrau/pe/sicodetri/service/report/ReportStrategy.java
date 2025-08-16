package com.epsgrau.pe.sicodetri.service.report;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import java.sql.SQLException;
import java.util.List;

/**
 * Strategy interface for generating different types of fixed asset reports.
 * Implementations provide:
 * <ul>
 *   <li>a unique report code (used for dispatching requests)</li>
 *   <li>the JasperReports template file name</li>
 *   <li>a base filename prefix for exported reports</li>
 *   <li>the logic to fetch and prepare report data</li>
 * </ul>
 * <b>Class</b>: {@link ReportStrategy}<br/>
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
public interface ReportStrategy {

    /**
     * Returns the unique code identifying this report type.
     * This code is used to select the appropriate {@link ReportStrategy}
     * implementation for a given report request.
     *
     * @return the report type code
     */
    String getCode();

    /**
     * Returns the filename of the JasperReports template (JRXML) used to render this report.
     *
     * @return the template filename (e.g., "rpt_tax_book.jrxml")
     */
    String getTemplate();

    /**
     * Returns the base filename prefix to use when exporting the report output.
     * The final filename typically appends a timestamp and file extension
     * to this base name.
     *
     * @return the base file name prefix
     */
    String getBaseFileName();

    /**
     * Fetches and prepares the data required by the report.
     * Implementations typically delegate to service-layer methods
     * that call stored procedures or perform business logic to
     * assemble a list of {@link FinancialFixedAssetDto}.
     *
     * @param params the report parameters (e.g., year, codes, date ranges)
     * @return a list of DTOs containing the report data
     * @throws SQLException if a database access error occurs during data retrieval
     */
    List<FinancialFixedAssetDto> generateReport(ParamReportDto params) throws SQLException;

}
