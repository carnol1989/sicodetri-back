package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;

/**
 * Service interface for generating reports using JasperReports.
 * Provides a method to compile, fill, and export a Jasper template
 * into a byte array in the requested file format.
 * <b>Class</b>: {@link ReportJasperService}<br/>
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
public interface ReportJasperService {

    /**
     * Compiles the specified JasperReports template, fills it with the provided parameters
     * and data source, and exports the result as a byte array in the requested format.
     *
     * @param optionReport    a code identifying the report variant (e.g. "1", "2")
     * @param templateReport  the filename of the .jrxml template under classpath:reports/
     * @param fileType        the desired output format code (e.g. "pdf", "xlsx", "csv")
     * @param parameters      a map of named parameters to pass into the report
     * @param dataSource      a list of DTOs serving as the report’s data source
     * @return a byte array representing the generated report file
     * @throws FileNotFoundException if the template file cannot be found
     * @throws JRException           if an error occurs during compilation, filling, or export
     */
    byte[] getJasperReportInBytes(String optionReport, String templateReport, String fileType,
        Map<String, Object> parameters, List<FinancialFixedAssetDto> dataSource)
        throws FileNotFoundException, JRException;

}
