package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;

import java.util.List;
import java.util.Map;

public interface ReportJasperService {

    byte[] getJasperReportInBytes(String templateReport, String fileType, Map<String, Object> parameters,
                                  List<FinancialFixedAssetDto> dataSource) throws Exception;

}
