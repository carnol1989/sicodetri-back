package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import java.sql.SQLException;
import java.util.List;

/**
 * This interface for methods with Jdbc. <br/>
 * <b>Class</b>: {@link FinancialFixedAssetJdbcRepository}<br/>
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
 *     <li>Sep 08, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public interface FinancialFixedAssetJdbcRepository {

    public List<FinancialFixedAssetDto> callGetReportTaxBookJdbc(ParamReportDto params)
        throws SQLException;

    List<FinancialFixedAssetDto> callGetReportDetailFinancialAndTaxDepreciationJdbc(
        ParamReportDto params);

}
