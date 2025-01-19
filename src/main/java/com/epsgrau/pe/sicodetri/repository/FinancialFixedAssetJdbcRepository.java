package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;

import java.util.List;

public interface FinancialFixedAssetJdbcRepository {

    public List<FinancialFixedAssetDto> callGetReportTaxBookJdbc(ParamReportDto params);

}
