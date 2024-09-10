package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface FinancialFixedAssetJdbcRepository {

    public List<FinancialFixedAssetDto> callGetReportTaxBookJdbc(ParamReportDto params);

}
