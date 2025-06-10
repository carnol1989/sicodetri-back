package com.epsgrau.pe.sicodetri.service.report;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import java.sql.SQLException;
import java.util.List;

/**
 * This interface provide type report for generate. <br/>
 * <b>Class</b>: {@link ReportStrategy}<br/>
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
public interface ReportStrategy {

    String getCode();

    String getTemplate();

    String getBaseFileName();

    List<FinancialFixedAssetDto> generateReport(ParamReportDto params) throws SQLException;

}
