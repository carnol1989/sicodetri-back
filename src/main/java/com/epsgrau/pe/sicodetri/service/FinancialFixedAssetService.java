package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.model.InputData;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface FinancialFixedAssetService {

    List<InputData> loadExcelFileToObject(MultipartFile file);

    void saveAllService(LocalDate accountingPeriod, List<InputData> listInputData);

    void updateService(FinancialFixedAsset obj);

    void deleteByIdService(Long id);

    void deleteByAccountingPeriod(LocalDate period);

    FinancialFixedAsset findByIdService(Long id);

    List<FinancialFixedAsset> findAllService();

    List<FinancialFixedAsset> findByPeriodService(LocalDate period);

}
