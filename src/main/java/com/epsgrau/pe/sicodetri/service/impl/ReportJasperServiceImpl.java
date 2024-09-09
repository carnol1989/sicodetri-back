package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.service.ReportJasperService;
import com.epsgrau.pe.sicodetri.helper.JasperReportsHelper;
import com.epsgrau.pe.sicodetri.util.enums.ReportTypeEnum;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.util.List;
import java.util.Map;

@Service
public class ReportJasperServiceImpl implements ReportJasperService {

    @Override
    public byte[] getJasperReportInBytes(String templateReport, String fileType, Map<String, Object> parameters,
                                         List<FinancialFixedAssetDto> dataSource) throws Exception {
        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(dataSource);
        String path = ResourceUtils.getFile("classpath:reports/" + templateReport).getAbsolutePath();
        JasperReport jasperReport = JasperCompileManager.compileReport(path);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanCollectionDataSource);
        ReportTypeEnum reportType = ReportTypeEnum.getReportTypeByCode(fileType);
        return JasperReportsHelper.exportJasperReportBytes(jasperPrint, reportType);
    }

}
