package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC repository interface for generating financial fixed asset reports.
 * Defines methods to invoke stored procedures for:
 * <ul>
 *   <li>Tax book report</li>
 *   <li>Detailed financial and tax depreciation report</li>
 * </ul>
 * Each method accepts a {@link ParamReportDto} containing the report parameters
 * and returns a list of {@link FinancialFixedAssetDto} representing the report rows.
 * <b>Class</b>: {@link FinancialFixedAssetJdbcRepository}<br/>
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
public interface FinancialFixedAssetJdbcRepository {

    /**
     * Calls the stored procedure to generate the tax book report.
     *
     * @param params the parameters for the tax book report
     * @return a list of {@link FinancialFixedAssetDto} for each row in the tax book
     * @throws SQLException if a database access error occurs during the procedure call
     */
    public List<FinancialFixedAssetDto> callGetReportTaxBookJdbc(ParamReportDto params)
        throws SQLException;

    /**
     * Calls the stored procedure to generate detailed financial and tax depreciation data.
     *
     * @param params the parameters for the detail depreciation report
     * @return a list of {@link FinancialFixedAssetDto} for each row in the detailed report
     */
    List<FinancialFixedAssetDto> callGetReportDetailFinancialAndTaxDepreciationJdbc(
        ParamReportDto params);

}
