package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.model.InputData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

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

    List<InputData> loadExcelFileToObjectService(MultipartFile file, String period);

    void saveAllService(String accountingPeriod, List<InputData> listInputData);

    void updateService(FinancialFixedAsset obj);

    void deleteByIdService(Long id);

    void deleteByAccountingPeriod(LocalDate period);

    void generateTemporayDeductionsService(LocalDate accountingPeriod,
                                           List<FinancialFixedAsset> fixedAsset);

    FinancialFixedAsset findByIdService(Long id);

    List<FinancialFixedAsset> findByMainFixedAssetCodeService(String mainFixedAssetCode);

    List<FinancialFixedAsset> findByFixedAssetAccountingAccountService(String accountingAccount);

    List<FinancialFixedAsset> findAllService();

    List<FinancialFixedAsset> findByPeriodService(LocalDate period);

    List<FinancialFixedAsset> findByPeriodAndAccountStartingWithService(LocalDate period,
                                                                        String account);

    List<FinancialFixedAssetDto> callGetReportTaxBookService(ParamReportDto params)
        throws SQLException;

    List<FinancialFixedAssetDto> callGetReportDetailFinancialAndTaxDepreciationService(
        ParamReportDto params);

}
