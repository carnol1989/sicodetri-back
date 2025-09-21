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
 * Service contract for managing Financial Fixed Assets and related reporting/projection workflows.
 *
 * <p><b>Scope</b>:</p>
 * <ul>
 *   <li>Bulk import (Excel) and persistence of assets</li>
 *   <li>CRUD-style operations filtered by accounting period and user context</li>
 *   <li>Generation of depreciation/additions projections</li>
 *   <li>Stored-procedure driven reporting</li>
 * </ul>
 *
 * <b>Concurrency &amp; transactions</b>: Implementations are expected to be thread-safe,
 * leverage Spring’s transactional boundaries where appropriate, and consistently honor the
 * {@code username} (added-by) filter parameters.
 * <b>Class</b>: {@link FinancialFixedAssetService}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <br/><u>Service Provider</u>: Consultor TI
 *     <br/><u>Developed by</u>:
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *       <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 * @since 1.0
 */
public interface FinancialFixedAssetService {

    /**
     * Parses the provided Excel file (.xlsx) and maps each valid row to an {@link InputData} instance.
     * <b>Validation</b>:
     * Implementations should validate file structure, required columns, and data types;
     * invalid rows may be ignored or reported according to business rules.
     *
     * @param file      uploaded Excel file (format: .xlsx); must not be {@code null}
     * @param period    accounting period (format defined by the domain,
     *                  commonly {@code YYYY-MM}); must not be {@code null}
     * @param username  user identifier used for auditing/ownership attribution; must not be {@code null}
     * @return list of {@link InputData} extracted from the file (never {@code null}, possibly empty)
     */
    List<InputData> loadExcelFileToObjectService(MultipartFile file, String period,
                                                 String username);

    /**
     * Persists all parsed {@link InputData} as {@link FinancialFixedAsset} entities bound to the given period.
     * It is recommended that implementations handle the operation atomically (transactional bulk insert/update)
     * and record the {@code username} as the creator/owner (added-by).
     *
     * @param accountingPeriod accounting period (e.g., {@code 2025-06})
     * @param listInputData    parsed input rows to persist (must not be {@code null})
     * @param username         user identifier used for auditing; must not be {@code null}
     */
    void saveAllService(String accountingPeriod, List<InputData> listInputData, String username);

    /**
     * Updates an existing {@link FinancialFixedAsset}.
     *
     * <p>Implementations should validate that the target entity exists and enforce
     * optimistic/pessimistic concurrency controls as required.</p>
     *
     * @param obj entity carrying updated fields (must include the identifier)
     */
    void updateService(FinancialFixedAsset obj);

    /**
     * Deletes a {@link FinancialFixedAsset} by its identifier.
     *
     * @param id unique identifier of the asset to delete (must not be {@code null})
     */
    void deleteByIdService(Long id);

    /**
     * Deletes all {@link FinancialFixedAsset} records for the specified period that belong to the given user.
     *
     * @param period   accounting period to purge
     * @param username user identifier (added-by) used as ownership filter
     */
    void deleteByAccountingPeriodAndAddedByService(LocalDate period, String username);

    /**
     * Deletes the provided list of assets.
     *
     * <p>This method is intended for efficient bulk deletion when the caller already
     * resolved the entities to remove.</p>
     *
     * @param fixedAssets list of entities to delete (never {@code null}, may be empty)
     */
    void deleteAllFinancialFixedAsset(List<FinancialFixedAsset> fixedAssets);

    /**
     * Generates a projection (e.g., depreciation, additions/deductions) over the supplied assets.
     * <b>Behavior</b>:
     * <ul>
     *   <li>Projection semantics are dictated by {@link ProjectionType}.</li>
     *   <li>{@code accountingAccount} may be used as a prefix filter, depending on business rules.</li>
     *   <li>Implementations should record the user context for auditing.</li>
     * </ul>
     *
     * @param type               projection type to execute
     * @param accountingPeriod   base accounting period for the projection window
     * @param fixedAssets        assets to project over (never {@code null}, may be empty)
     * @param accountingAccount  optional account prefix used for filtering (may be {@code null} or empty)
     * @param username           user identifier used for auditing; must not be {@code null}
     */
    void generateProjectionService(ProjectionType type,
                                   LocalDate accountingPeriod,
                                   List<FinancialFixedAsset> fixedAssets,
                                   String accountingAccount,
                                   String username);

    /**
     * Finds a {@link FinancialFixedAsset} by its identifier.
     * <b>Note</b>: Implementations may return a new empty instance when not found
     * (instead of {@code null}) as per existing business contract.
     *
     * @param id asset identifier
     * @return the found entity or a new instance if not found (never {@code null})
     */
    FinancialFixedAsset findByIdService(Long id);

    /**
     * Retrieves assets matching the provided main fixed asset code for the given user (added-by).
     *
     * @param mainFixedAssetCode main/parent fixed asset code
     * @param username           user identifier used as ownership filter
     * @return matching assets (never {@code null}, possibly empty)
     */
    List<FinancialFixedAsset> findByMainFixedAssetCodeAndAddedByService(String mainFixedAssetCode,
                                                                        String username);

    /**
     * Retrieves assets whose accounting account starts with the given prefix for the given user (added-by).
     *
     * @param accountingAccount account code prefix
     * @param username          user identifier used as ownership filter
     * @return matching assets (never {@code null}, possibly empty)
     */
    List<FinancialFixedAsset> findByFixedAssetAccountingAccountAndAddedByService(String accountingAccount,
                                                                                 String username);

    /**
     * Retrieves assets for a user when the accounting account begins with any of the provided prefixes.
     *
     * @param username user identifier used as ownership filter
     * @param prefixes collection of account code prefixes (never {@code null}, may be empty)
     * @return matching assets (never {@code null}, possibly empty)
     */
    List<FinancialFixedAsset> findByAddedByAndAccountingAccountPrefixesService(String username,
                                                                               List<String> prefixes);

    /**
     * Retrieves all assets.
     *
     * @return all persisted assets (never {@code null}, possibly empty)
     */
    List<FinancialFixedAsset> findAllService();

    /**
     * Retrieves assets for the specified accounting period for the given user (added-by).
     *
     * @param period   accounting period filter
     * @param username user identifier used as ownership filter
     * @return matching assets (never {@code null}, possibly empty)
     */
    List<FinancialFixedAsset> findByPeriodAndAddedByService(LocalDate period, String username);

    /**
     * Retrieves assets for the specified period whose account starts with the given value
     * for the given user (added-by).
     *
     * @param period   accounting period filter
     * @param account  account code prefix
     * @param username user identifier used as ownership filter
     * @return matching assets (never {@code null}, possibly empty)
     */
    List<FinancialFixedAsset> findByPeriodAndAddedByAndAccountStartingWithService(LocalDate period,
                                                                                  String account,
                                                                                  String username);

    /**
     * Invokes the stored procedure responsible for the Fixed Asset Tax Book report.
     *
     * @param params report parameters (must not be {@code null})
     * @return DTOs containing the report dataset (never {@code null}, possibly empty)
     * @throws SQLException if an error occurs during stored-procedure execution
     */
    List<FinancialFixedAssetDto> callGetReportTaxBookService(ParamReportDto params)
        throws SQLException;

    /**
     * Invokes the stored procedure responsible for the Financial and Tax Depreciation Detail report.
     *
     * @param params report parameters (must not be {@code null})
     * @return DTOs containing the detailed depreciation dataset (never {@code null}, possibly empty)
     */
    List<FinancialFixedAssetDto> callGetReportDetailFinancialAndTaxDepreciationService(
        ParamReportDto params);

}
