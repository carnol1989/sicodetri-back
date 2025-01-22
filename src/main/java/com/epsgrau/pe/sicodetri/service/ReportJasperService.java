package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;

/**
 * This interface for methods report with Jasper. <br/>
 * <b>Class</b>: {@link ReportJasperService}<br/>
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
 *     <li>Ago 08, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public interface ReportJasperService {

    byte[] getJasperReportInBytes(String templateReport, String fileType, Map<String,
        Object> parameters, List<FinancialFixedAssetDto> dataSource) throws FileNotFoundException,
        JRException;

}
