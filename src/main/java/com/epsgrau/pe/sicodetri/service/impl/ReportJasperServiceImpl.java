package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.helper.JasperReportsHelper;
import com.epsgrau.pe.sicodetri.service.ReportJasperService;
import com.epsgrau.pe.sicodetri.util.enums.ReportTypeEnum;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

/**
 * Service implementation for generating JasperReports.
 * This class compiles JasperReport templates, fills them with data and parameters,
 * and exports the result to a byte array in the requested format (PDF, XLSX, CSV, etc.).
 * <b>Class</b>: {@link ReportJasperServiceImpl}<br/>
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
@Service
public class ReportJasperServiceImpl implements ReportJasperService {

    /**
     * Compiles the specified JasperReports template, fills it with the provided
     * parameters and data source, and exports the filled report to a byte array
     * in the given file type.
     *
     * @param optionReport   the report option code (e.g., "1" for detail, "2" for summary)
     * @param templateReport the filename of the JRXML template under classpath:reports/
     * @param fileType       the output file type code (e.g., "pdf", "xlsx", "csv")
     * @param parameters     a map of report parameters to pass to the report
     * @param dataSource     the data to populate the report (list of DTOs)
     * @return the generated report content as a byte array
     * @throws FileNotFoundException if the report template file cannot be found
     * @throws JRException           if an error occurs during report compilation, filling,
     *                               or exporting
     */
    @Override
    public byte[] getJasperReportInBytes(String optionReport, String templateReport,
        String fileType, Map<String, Object> parameters, List<FinancialFixedAssetDto> dataSource)
        throws FileNotFoundException, JRException {
        JRBeanCollectionDataSource beanCollectionDataSource =
            new JRBeanCollectionDataSource(dataSource);
        String path = ResourceUtils.getFile("classpath:reports/"
            + templateReport).getAbsolutePath();
        JasperReport jasperReport = JasperCompileManager.compileReport(path);
        JasperPrint jasperPrint;
        if (optionReport.equals("2")) {
            parameters.put("financialFixedAssetList", beanCollectionDataSource);
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
                new JREmptyDataSource());
        } else {
            jasperPrint = JasperFillManager.fillReport(jasperReport,
                parameters, beanCollectionDataSource);
        }
        ReportTypeEnum reportType = ReportTypeEnum.getReportTypeByCode(fileType);
        return JasperReportsHelper.exportJasperReportBytes(jasperPrint, reportType);
    }

}
