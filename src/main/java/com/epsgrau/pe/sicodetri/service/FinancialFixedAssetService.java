package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for managing financial fixed assets and related operations.
 * Provides methods to:
 * <ul>
 *   <li>Import asset data from Excel</li>
 *   <li>Persist and update fixed asset entities</li>
 *   <li>Delete assets by ID or accounting period</li>
 *   <li>Generate depreciation projections</li>
 *   <li>Query assets by various criteria</li>
 *   <li>Invoke stored procedures for report data retrieval</li>
 * </ul>
 * <b>Class</b>: {@link FinancialFixedAssetService}<br/>
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
public interface FinancialFixedAssetService {

    /**
     * Parses the given Excel file and maps its rows into a list of {@link InputData}.
     *
     * @param file   the uploaded Excel file (.xlsx)
     * @param period the accounting period to associate with the data
     * @return a list of InputData representing each valid row
     */
    List<InputData> loadExcelFileToObjectService(MultipartFile file, String period,
                                                 String username);

    /**
     * Saves all parsed input data as {@link FinancialFixedAsset} entities for the given period.
     *
     * @param accountingPeriod the accounting period (YYYY-MM-DD)
     * @param listInputData    the list of parsed InputData to persist
     */
    void saveAllService(String accountingPeriod, List<InputData> listInputData, String username);

    /**
     * Updates an existing {@link FinancialFixedAsset} record.
     *
     * @param obj the FinancialFixedAsset entity with updated fields
     */
    void updateService(FinancialFixedAsset obj);

    /**
     * Deletes the FinancialFixedAsset with the specified ID.
     *
     * @param id the identifier of the asset to delete
     */
    void deleteByIdService(Long id);

    /**
     * Deletes all FinancialFixedAsset records for the specified accounting period.
     *
     * @param period the accounting period whose assets should be removed
     */
    void deleteByAccountingPeriodAndAddedByService(LocalDate period, String username);

    void deleteAllFinancialFixedAsset(List<FinancialFixedAsset> fixedAssets);

    /**
     * Generates a projection of depreciation or additions/deductions for a list of assets.
     *
     * @param type             the type of projection to perform
     * @param accountingPeriod the starting accounting period for projection
     * @param fixedAssets      the list of FinancialFixedAsset to project
     * @param accountingAccount the accounting account prefix used for filtering
     */
    void generateProjectionService(ProjectionType type,
                                   LocalDate accountingPeriod,
                                   List<FinancialFixedAsset> fixedAssets,
                                   String accountingAccount,
                                   String username);

    /**
     * Finds a FinancialFixedAsset by its identifier.
     *
     * @param id the asset ID
     * @return the found FinancialFixedAsset, or a new instance if not found
     */
    FinancialFixedAsset findByIdService(Long id);

    /**
     * Retrieves all assets matching the given main fixed asset code.
     *
     * @param mainFixedAssetCode the primary asset code to search for
     * @return a list of matching FinancialFixedAsset entities
     */
    List<FinancialFixedAsset> findByMainFixedAssetCodeAndAddedByService(String mainFixedAssetCode,
                                                                        String username);

    /**
     * Retrieves all assets whose accounting account starts with the given prefix.
     *
     * @param accountingAccount the account code prefix
     * @return a list of matching FinancialFixedAsset entities
     */
    List<FinancialFixedAsset> findByFixedAssetAccountingAccountAndAddedByService(String accountingAccount,
                                                                                 String username);

    /**
     * Retrieves assets whose accounting account begins with any of the provided prefixes.
     *
     * @param prefixes the list of account code prefixes
     * @return a list of matching FinancialFixedAsset entities
     */
    List<FinancialFixedAsset> findByAddedByAndAccountingAccountPrefixesService(String username,
                                                                               List<String> prefixes);

    /**
     * Retrieves all FinancialFixedAsset records.
     *
     * @return a list of all assets
     */
    List<FinancialFixedAsset> findAllService();

    /**
     * Retrieves assets for the specified accounting period.
     *
     * @param period the accounting period to filter by
     * @return a list of FinancialFixedAsset entities
     */
    List<FinancialFixedAsset> findByPeriodAndAddedByService(LocalDate period, String username);

    /**
     * Retrieves assets for the specified period whose account starts with the given value.
     *
     * @param period  the accounting period
     * @param account the account code prefix
     * @return a list of matching FinancialFixedAsset entities
     */
    List<FinancialFixedAsset> findByPeriodAndAddedByAndAccountStartingWithService(LocalDate period,
                                                                                  String account,
                                                                                  String username);

    /**
     * Calls the stored procedure to generate the Fixed Asset Tax Book report data.
     *
     * @param params the report parameters
     * @return a list of DTOs containing the report data
     * @throws SQLException if an error occurs during procedure execution
     */
    List<FinancialFixedAssetDto> callGetReportTaxBookService(ParamReportDto params)
        throws SQLException;

    /**
     * Calls the stored procedure to generate the Financial and Tax Depreciation Detail report data.
     *
     * @param params the report parameters
     * @return a list of DTOs containing the detailed depreciation report data
     */
    List<FinancialFixedAssetDto> callGetReportDetailFinancialAndTaxDepreciationService(
        ParamReportDto params);

}
