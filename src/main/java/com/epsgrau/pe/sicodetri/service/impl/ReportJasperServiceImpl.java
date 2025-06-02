package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.helper.JasperReportsHelper;
import com.epsgrau.pe.sicodetri.service.ReportJasperService;
import com.epsgrau.pe.sicodetri.util.enums.ReportTypeEnum;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

/**
 * This class implementation for ReportJasperService. <br/>
 * <b>Class</b>: {@link ReportJasperServiceImpl}<br/>
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
@Service
public class ReportJasperServiceImpl implements ReportJasperService {

    @Override
    public byte[] getJasperReportInBytes(String templateReport, String fileType,
        Map<String, Object> parameters, List<FinancialFixedAssetDto> dataSource)
        throws FileNotFoundException, JRException {
        JRBeanCollectionDataSource beanCollectionDataSource =
            new JRBeanCollectionDataSource(dataSource);
        String path = ResourceUtils.getFile("classpath:reports/"
            + templateReport).getAbsolutePath();
        JasperReport jasperReport = JasperCompileManager.compileReport(path);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
            beanCollectionDataSource);
        ReportTypeEnum reportType = ReportTypeEnum.getReportTypeByCode(fileType);
        return JasperReportsHelper.exportJasperReportBytes(jasperPrint, reportType);
    }

}
