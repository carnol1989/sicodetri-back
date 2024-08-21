package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.service.impl.TaxTableSunatServiceImpl;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * This class is the interface of the service for Financial Fixed Asset. <br/>
 * <b>Class</b>: {@link FinancialFixedAssetService}<br/>
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
 *     <li>Ago 15, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
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
