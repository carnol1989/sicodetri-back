package com.epsgrau.pe.sicodetri.service.impl;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelper.getValueCell;
import static com.epsgrau.pe.sicodetri.util.Constants.FORMATTER_YYYY_MM_DD;
import static com.epsgrau.pe.sicodetri.util.Constants.S_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants._BLANK;
import static com.epsgrau.pe.sicodetri.util.Functions.defaultString;
import static com.epsgrau.pe.sicodetri.util.Functions.parseBigDecimal;
import static com.epsgrau.pe.sicodetri.util.Functions.parseDate;
import static com.epsgrau.pe.sicodetri.util.Functions.parseDouble;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.model.Depreciation;
import com.epsgrau.pe.sicodetri.model.FixedAssetDetail;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetJdbcRepository;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetRepository;
import com.epsgrau.pe.sicodetri.service.AdditionService;
import com.epsgrau.pe.sicodetri.service.DeductionService;
import com.epsgrau.pe.sicodetri.service.FinancialDepreciationService;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import com.epsgrau.pe.sicodetri.service.TaxDepreciationService;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionJobService;
import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Concrete implementation of {@link FinancialFixedAssetService}.
 *
 * <p><b>Responsibilities</b>:</p>
 * <ul>
 *   <li>Import and parsing of Excel sources into {@link InputData}.</li>
 *   <li>Transformation and persistence of {@link FinancialFixedAsset} entities.</li>
 *   <li>Query and deletion operations scoped by accounting period and user context (added-by).</li>
 *   <li>Projection workflows via pluggable {@link ProjectionGenerator} strategies.</li>
 *   <li>Stored-procedure backed reporting through JDBC repositories.</li>
 * </ul>
 *
 * <p><b>Transactions</b>: Write operations are demarcated with {@link Transactional};
 * read paths are {@code readOnly=true} to optimize persistence behavior. Methods explicitly
 * flush where immediate visibility is required.</p>
 *
 * <b>Class</b>: {@link FinancialFixedAssetServiceImpl}<br/>
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
@Slf4j
@Service
public class FinancialFixedAssetServiceImpl implements FinancialFixedAssetService {

    @Autowired
    private FinancialFixedAssetRepository repository;

    @Autowired
    private FinancialFixedAssetJdbcRepository repositoryJdbc;

    @Autowired
    private FinancialDepreciationService financialDepreciationService;

    @Autowired
    private TaxDepreciationService taxDepreciationService;

    @Autowired
    private AdditionService additionService;

    @Autowired
    private DeductionService deductionService;

    @Autowired
    private TaxTableSunatService taxTableService;

    @Autowired
    private FinancialFixedAssetRepository assetRepository;

    @Autowired
    private ProjectionJobService projectionJobService;

    /**
     * New preferred API: run projection by chunks for ALL assets.
     *
     * @param username user who triggered the job
     * @param period   accounting period
     * @param type     projection type
     * @param pageSize chunk size (e.g., 1000)
     * @return job summary with counters and duration
     */
    public ProjectionJobService.ProjectionJobSummary generateProjectionBatch(
        String username, LocalDate period, ProjectionType type, int pageSize) {

        Objects.requireNonNull(period, "period");
        Objects.requireNonNull(type, "type");
        if (pageSize <= 0) throw new IllegalArgumentException("pageSize must be > 0");

        log.info("Starting batch projection. user={}, period={}, type={}, pageSize={}",
            username, period, type, pageSize);

        ProjectionJobService.ProjectionJobSummary summary =
            projectionJobService.generate(username, period, type, pageSize);

        log.info("Batch projection finished. {}", summary);
        return summary;
    }

    /**
     * Legacy API (backward compatibility): previously computed projection in-memory and persisted
     * inside this service. Now it delegates to the batch job for robustness and performance.
     *
     * @param type     projection type
     * @param period   accounting period
     * @param assets   (ignored) kept for backward compatibility; batch runs over all assets
     * @param account  (ignored) was used to filter; batch runs globally
     * @param username user who triggered the job
     */
    @Override
    public void generateProjectionService(ProjectionType type,
                                          LocalDate period,
                                          List<FinancialFixedAsset> assets,
                                          String account,
                                          String username) {
        // Decide a sane default for chunk size. You can externalize it (application.yml)
        final int defaultPageSize = 1000;
        log.warn("generateProjectionService(...) is deprecated. Delegating to batch job. "
                + "type={}, period={}, username={}, legacyAssetsSize={}, legacyAccount={}",
            type, period, username, assets != null ? assets.size() : 0, account);

        projectionJobService.generate(username, period, type, defaultPageSize);
    }

    /**
     * Compatibility helper used by older controllers to get a subset of assets.
     * With the batch approach, we no longer need to pre-filter in the service; the job
     * iterates all assets. Keep this only if your UI still expects it.
     *
     * @param period   (kept for signature compatibility; not used here)
     * @param account  prefix of the accounting account
     * @param username (kept for signature compatibility; not used here)
     * @return assets whose accounting account starts with the given prefix
     */
    @Override
    public List<FinancialFixedAsset> findByPeriodAndAddedByAndAccountStartingWithService(
        LocalDate period, String account, String username) {

        if (account == null || account.isBlank()) {
            return Collections.emptyList();
        }
        final String prefix = account.trim();

        // Minimal-impact approach to avoid changing repository: in-memory filter.
        // If volume is large, add a repository method:
        // List<FinancialFixedAsset> findByFixedAssetAccountingAccountStartingWith(String prefix);
        List<FinancialFixedAsset> all = assetRepository.findAll();
        return all.stream()
            .filter(a -> a != null && a.getFixedAssetAccountingAccount() != null)
            .filter(a -> a.getFixedAssetAccountingAccount().startsWith(prefix))
            .collect(Collectors.toList());
    }

    /**
     * Reads the first sheet of the provided Excel file and maps each data row
     * (header rows are skipped) into {@link InputData}, preserving row numbers and total row count.
     *
     * <p><b>Notes</b>:</p>
     * <ul>
     *   <li>Only the first sheet is processed.</li>
     *   <li>Rows with index {@code &le; 10} are considered headers and ignored.</li>
     *   <li>When the file is empty, an empty list is returned.</li>
     * </ul>
     *
     * @param file      uploaded Excel file (.xlsx), must not be {@code null}
     * @param period    accounting period to attach to each row (e.g., {@code 2025-06-01})
     * @param username  user identifier to be captured as owner/creator
     * @return non-null list of {@link InputData} instances (possibly empty)
     * @throws RuntimeException if the file cannot be read or a row mapping fails
     */
    @Override
    public List<InputData> loadExcelFileToObjectService(MultipartFile file, String period,
                                                        String username) {
        List<InputData> listInputData = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            if (file.isEmpty() || file.getInputStream().available() == 0) {
                return listInputData;
            }

            Sheet sheet = workbook.getSheetAt(0);
            int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();

            for (Row row : sheet) {
                int rowNum = row.getRowNum();
                try {
                    // Omitir encabezado y filas fuera de rango
                    if (rowNum <= 10 || rowNum >= physicalNumberOfRows) {
                        continue;
                    }

                    InputData inputData = new InputData();
                    inputData.setAccountingPeriod(period);
                    FixedAssetDetail assetDetail = new FixedAssetDetail();
                    Depreciation depreciation = new Depreciation();

                    for (Cell cell : row) {
                        inputData.setRowNum(rowNum);
                        inputData.setPhysicalNumberOfRows(physicalNumberOfRows);
                        processCell(cell, inputData, assetDetail, depreciation);
                    }

                    inputData.setFixedAssetDetail(assetDetail);
                    inputData.setDepreciation(depreciation);
                    inputData.setUsername(username);
                    listInputData.add(inputData);
                } catch (Exception e) {
                    String msgError = "Error al obtener información de la fila (" + rowNum
                        + "): \nCausa: " + e.getCause() + "\nMensaje: " + e.getMessage();
                    log.error(msgError);
                    throw new RuntimeException(msgError);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }
        return listInputData;
    }

    /**
     * Processes a single Excel {@link Cell} and assigns its value to the corresponding field(s)
     * in {@link InputData}, {@link FixedAssetDetail}, and/or {@link Depreciation}.
     *
     * @param cell         cell to process
     * @param inputData    container for raw values
     * @param assetDetail  descriptive attributes of the fixed asset
     * @param depreciation depreciation metadata and authorization info
     */
    private void processCell(Cell cell, InputData inputData, FixedAssetDetail assetDetail,
                             Depreciation depreciation) {
        int columnIndex = cell.getColumnIndex();
        String cellValue = getCellValue(cell);

        switch (columnIndex) {
          case 0 -> inputData.setMainFixedAssetCode(cellValue);
          case 1 -> inputData.setFixedAssetCodeComponent(cellValue);
          case 2 -> inputData.setFixedAssetAccountingAccount(cellValue);
          case 3 -> assetDetail.setDescription(cellValue);
          case 4 -> assetDetail.setFixedAssetBrand(cellValue);
          case 5 -> assetDetail.setFixedAssetModel(cellValue);
          case 6 -> assetDetail.setFixedAssetSerialNumberAndOrPlate(cellValue);
          case 7 -> inputData.setInitialBalance(cellValue);
          case 8 -> inputData.setAcquisitionsAdditions(cellValue);
          case 9 -> inputData.setImprovements(cellValue);
          case 10 -> inputData.setTransfersAndOrReclassifications(cellValue);
          case 11 -> inputData.setWithdrawalsAndOrRetirements(cellValue);
          case 12 -> inputData.setOtherAdjustments(cellValue);
          case 13 -> inputData.setBookValueOfFixedAssets(cellValue);
          case 14 -> inputData.setInflationAdjustment(cellValue);
          case 15 -> inputData.setAdjustedValueOfFixedAssets(cellValue);
          case 16 -> inputData.setAcquisitionDate(formatDate(cellValue));
          case 17 -> inputData.setStartDateFixedAssetUsage(formatDate(cellValue));
          case 18 -> inputData.setRetirementDate(formatDate(cellValue));
          case 19 -> depreciation.setAppliedMethod(cellValue);
          case 20 -> depreciation.setAuthorizationDocumentNumber(cellValue);
          case 21 -> inputData.setUsefulLifeYears(cellValue);
          case 22 -> inputData.setAccumulatedDepreciationTheEndPrevious(cellValue);
          case 23 -> inputData.setCurrentDepreciation(cellValue);
          case 24 -> inputData.setCurrentDepreciationRelatedWithdrawalsRetirements(cellValue);
          case 25 -> inputData.setCurrentDepreciationRelatedOtherAdjustments(cellValue);
          case 26 -> inputData.setAccumulatedHistoricalDepreciation(cellValue);
          case 27 -> inputData.setDepreciationInflationAdjustment(cellValue);
          case 28 -> inputData.setAccumulatedDepreciationAdjustedInflation(cellValue);
          case 30 -> inputData.setNetFixedAssets(cellValue);
          case 31 -> inputData.setHistoricalCost(cellValue);
          case 32 -> inputData.setRevaluedAmount(cellValue);
          case 33 -> inputData.setWellAppraisedPreInvestmentStudyDry(cellValue);
          default -> {
          }
        }
    }

    /**
     * Extracts a trimmed string value from an Excel {@link Cell}, returning {@code ""} for null/blank cells.
     *
     * @param cell source cell
     * @return non-null string (may be empty)
     */
    private String getCellValue(Cell cell) {
        Object value = getValueCell(cell);
        return value != null ? String.valueOf(value) : _BLANK;
    }

    /**
     * Normalizes a date string to ISO format {@code yyyy-MM-dd}.
     *
     * <p>Accepted inputs: {@code dd/MM/yyyy} or already-ISO strings; otherwise falls back to
     * {@link com.epsgrau.pe.sicodetri.util.Constants#S_DATE_DEFAULT}.</p>
     *
     * @param date raw date text
     * @return normalized ISO string or the default date value
     */
    private String formatDate(String date) {
        if (date.contains("/")) {
            String[] dateParts = date.split("/");
            return dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
        } else if (date.contains("-")) {
            return date;
        }
        return S_DATE_DEFAULT;
    }

    /**
     * Persists all {@link FinancialFixedAsset} entities produced from the supplied {@link InputData}.
     *
     * <p>Delegates to {@link #getListFinancialFixedAsset(String, List, String)} for transformation,
     * then performs a bulk save.</p>
     *
     * @param accountingPeriod accounting period that will be persisted on each entity
     * @param listInputData    input rows to transform and persist
     * @param username         user identifier recorded as {@code addedBy}
     */
    @Override
    @Transactional
    public void saveAllService(String accountingPeriod, List<InputData> listInputData,
                               String username) {
        List<FinancialFixedAsset> list = getListFinancialFixedAsset(accountingPeriod,
            listInputData, username);
        repository.saveAll(list);
    }

    /**
     * Transforms {@link InputData} rows into {@link FinancialFixedAsset} entities, enforcing
     * basic invariants and field parsing.
     *
     * <p><b>Validation</b>:</p>
     * <ul>
     *   <li>{@code listInputData} must not be {@code null}; each element must not be {@code null}.</li>
     *   <li>{@code accountingPeriod} on each {@link InputData} must be non-null and equal to the method argument.</li>
     *   <li>{@code username} on each {@link InputData} must equal the method argument.</li>
     * </ul>
     *
     * <p>Parsing of dates/numbers is delegated to shared utility functions; parsing failures are wrapped
     * in a {@link RuntimeException} enriched with the row number.</p>
     *
     * @param accountingPeriod expected accounting period (ISO {@code yyyy-MM-dd})
     * @param listInputData    source rows to convert
     * @param username         expected owner (added-by)
     * @return list of populated entities ready for persistence
     * @throws IllegalArgumentException on null inputs or missing required fields
     */
    public static List<FinancialFixedAsset> getListFinancialFixedAsset(String accountingPeriod,
        List<InputData> listInputData, String username) {
        List<FinancialFixedAsset> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD);
        if (listInputData == null) {
            throw new IllegalArgumentException("listInputData nulo");
        }
        for (InputData itemInputData : listInputData) {
            if (itemInputData == null) {
                throw new IllegalArgumentException("InputData nulo");
            }
            try {
                if (itemInputData.getAccountingPeriod() == null) {
                    throw new IllegalArgumentException("Periodo contable nulo");
                }

                if (accountingPeriod.equals(itemInputData.getAccountingPeriod())
                    && username.equals(itemInputData.getUsername())) {
                    FinancialFixedAsset itemFinancialFixedAsset = new FinancialFixedAsset();
                    int rowNum = itemInputData.getRowNum();

                    // Validar y parsear fechas
                    itemFinancialFixedAsset.setAccountingPeriod(
                        parseDate(itemInputData.getAccountingPeriod(), formatter,
                            rowNum, "accountingPeriod"));
                    itemFinancialFixedAsset.setAcquisitionDate(
                        parseDate(itemInputData.getAcquisitionDate(), formatter,
                            rowNum, "acquisitionDate")); //16
                    itemFinancialFixedAsset.setStartDateFixedAssetUsage(
                        parseDate(itemInputData.getStartDateFixedAssetUsage(), formatter,
                            rowNum, "startDateFixedAssetUsage")); //17
                    itemFinancialFixedAsset.setRetirementDate(
                        parseDate(itemInputData.getRetirementDate(), formatter,
                            rowNum, "retirementDate")); //18

                    // Parseo numérico (BigDecimal) con validación
                    itemFinancialFixedAsset.setInitialBalance(
                        parseBigDecimal(itemInputData.getInitialBalance(),
                            rowNum, "initialBalance")); //7
                    itemFinancialFixedAsset.setAcquisitionsAdditions(
                        parseBigDecimal(itemInputData.getAcquisitionsAdditions(),
                            rowNum, "acquisitionsAdditions")); //8
                    itemFinancialFixedAsset.setImprovements(
                        parseBigDecimal(itemInputData.getImprovements(),
                            rowNum, "improvements")); //9
                    itemFinancialFixedAsset.setTransfersAndOrReclassifications(
                        parseBigDecimal(itemInputData.getTransfersAndOrReclassifications(),
                            rowNum, "transfersAndOrReclassifications")); //10
                    itemFinancialFixedAsset.setWithdrawalsAndOrRetirements(
                        parseBigDecimal(itemInputData.getWithdrawalsAndOrRetirements(),
                            rowNum, "withDrawalsAndOrRetirements")); //11
                    itemFinancialFixedAsset.setOtherAdjustments(
                        parseBigDecimal(itemInputData.getOtherAdjustments(),
                            rowNum, "otherAdjustments")); //12
                    itemFinancialFixedAsset.setBookValueOfFixedAssets(
                        parseBigDecimal(itemInputData.getBookValueOfFixedAssets(),
                            rowNum, "bookValueOfFixedAssets")); //13
                    itemFinancialFixedAsset.setInflationAdjustment(
                        parseBigDecimal(itemInputData.getInflationAdjustment(),
                            rowNum, "inflationAdjustment")); //14
                    itemFinancialFixedAsset.setAdjustedValueOfFixedAssets(
                        parseBigDecimal(itemInputData.getAdjustedValueOfFixedAssets(),
                            rowNum, "adjustedValueOfFixedAssets")); //15
                    itemFinancialFixedAsset.setAccumulatedDepreciationTheEndPrevious(
                        parseBigDecimal(itemInputData.getAccumulatedDepreciationTheEndPrevious(),
                            rowNum, "accumulatedDepreciationTheEndPrevious")); //22
                    itemFinancialFixedAsset.setCurrentDepreciation(parseBigDecimal(
                        itemInputData.getCurrentDepreciation(),
                        rowNum, "currentDepreciation")); //23
                    itemFinancialFixedAsset.setCurrentDepreciationRelatedWithdrawalsRetirements(
                        parseBigDecimal(
                            itemInputData.getCurrentDepreciationRelatedWithdrawalsRetirements(),
                            rowNum, "currentDepreciationRelatedWithDrawalsRetirements")); //24
                    itemFinancialFixedAsset.setCurrentDepreciationRelatedOtherAdjustments(
                        parseBigDecimal(
                            itemInputData.getCurrentDepreciationRelatedOtherAdjustments(),
                            rowNum, "currentDepreciationRelatedOtherAdjustments")); //25
                    itemFinancialFixedAsset.setAccumulatedHistoricalDepreciation(parseBigDecimal(
                        itemInputData.getAccumulatedHistoricalDepreciation(), rowNum,
                        "accumulatedHistoricalDepreciation")); //26
                    itemFinancialFixedAsset.setDepreciationInflationAdjustment(parseBigDecimal(
                        itemInputData.getDepreciationInflationAdjustment(), rowNum,
                        "depreciationInflationAdjustment")); //27
                    itemFinancialFixedAsset.setAccumulatedDepreciationAdjustedInflation(
                        parseBigDecimal(
                            itemInputData.getAccumulatedDepreciationAdjustedInflation(),
                            rowNum, "accumulatedDepreciationAdjustedInflation")); //28
                    itemFinancialFixedAsset.setNetFixedAssets(parseBigDecimal(
                        itemInputData.getNetFixedAssets(), rowNum, "netFixedAssets")); //30
                    itemFinancialFixedAsset.setHistoricalCost(parseBigDecimal(
                        itemInputData.getHistoricalCost(), rowNum, "historicalCost")); //31
                    itemFinancialFixedAsset.setRevaluedAmount(parseBigDecimal(
                        itemInputData.getRevaluedAmount(), rowNum, "revaluedAmount")); //32

                    // Parseo numérico (Double) con validación
                    itemFinancialFixedAsset.setUsefulLifeYears(parseDouble(
                        itemInputData.getUsefulLifeYears(), rowNum, "usefulLifeYears")); //21

                    itemFinancialFixedAsset.setMainFixedAssetCode(defaultString(
                        itemInputData.getMainFixedAssetCode())); //0
                    itemFinancialFixedAsset.setFixedAssetCodeComponent(defaultString(
                        itemInputData.getFixedAssetCodeComponent())); //1
                    itemFinancialFixedAsset.setFixedAssetAccountingAccount(defaultString(
                        itemInputData.getFixedAssetAccountingAccount())); //2
                    itemFinancialFixedAsset.setWellAppraisedPreInvestmentStudyDry(defaultString(
                        itemInputData.getWellAppraisedPreInvestmentStudyDry())); //33

                    // Anidado: Detalle
                    if (itemInputData.getFixedAssetDetail() != null) {
                        itemFinancialFixedAsset.setDescription(defaultString(
                            itemInputData.getFixedAssetDetail().getDescription())); //3
                        itemFinancialFixedAsset.setFixedAssetBrand(defaultString(
                            itemInputData.getFixedAssetDetail().getFixedAssetBrand())); //4
                        itemFinancialFixedAsset.setFixedAssetModel(defaultString(
                            itemInputData.getFixedAssetDetail().getFixedAssetModel())); //5
                        itemFinancialFixedAsset.setFixedAssetSerialNumberAndOrPlate(defaultString(
                            itemInputData.getFixedAssetDetail()
                                .getFixedAssetSerialNumberAndOrPlate())); //6
                    }

                    // Anidado: Método de depreciación
                    if (itemInputData.getDepreciation() != null) {
                        itemFinancialFixedAsset.setAppliedMethod(defaultString(
                            itemInputData.getDepreciation().getAppliedMethod())); //19
                        itemFinancialFixedAsset.setAuthorizationDocumentNumber(defaultString(
                            itemInputData.getDepreciation().getAuthorizationDocumentNumber())); //20
                    }

                    itemFinancialFixedAsset.setRowNum(rowNum);
                    itemFinancialFixedAsset.setPhysicalNumberOfRows(
                        itemInputData.getPhysicalNumberOfRows());
                    itemFinancialFixedAsset.setAddedBy(itemInputData.getUsername());
                    itemFinancialFixedAsset.setAdditionDate(LocalDateTime.now());
                    list.add(itemFinancialFixedAsset);
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Error en la fila " + itemInputData.getRowNum()
                    + ": " + e.getMessage(), e);
            }
        }
        return list;
    }

    /**
     * Updates an existing {@link FinancialFixedAsset}.
     *
     * @param obj entity with updated state (must contain a valid id)
     */
    @Override
    @Transactional
    public void updateService(FinancialFixedAsset obj) {
        repository.save(obj);
    }

    /**
     * Deletes a {@link FinancialFixedAsset} by id.
     *
     * @param id entity identifier
     */
    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    /**
     * Deletes all assets for a given period and user, then flushes to ensure immediate persistence.
     *
     * @param period   accounting period to match
     * @param username owner (added-by) filter
     */
    @Override
    @Transactional
    public void deleteByAccountingPeriodAndAddedByService(LocalDate period, String username) {
        repository.deleteByAccountingPeriodAndAddedBy(period, username);
        repository.flush();
    }

    /**
     * Bulk deletes the provided assets and flushes the persistence context.
     *
     * @param fixedAssets assets to remove
     */
    @Override
    @Transactional
    public void deleteAllFinancialFixedAsset(List<FinancialFixedAsset> fixedAssets) {
        repository.deleteAll(fixedAssets);
        repository.flush();
    }

    /**
     * Retrieves a {@link FinancialFixedAsset} by id; returns a new empty instance if not found.
     *
     * @param id entity identifier
     * @return existing entity or a new placeholder instance (never {@code null})
     */
    @Override
    @Transactional(readOnly = true)
    public FinancialFixedAsset findByIdService(Long id) {
        Optional<FinancialFixedAsset> optionalFinancialFixedAsset = repository.findById(id);
        return optionalFinancialFixedAsset.orElseGet(FinancialFixedAsset::new);
    }

    /**
     * Finds assets by main fixed asset code and owner (added-by).
     *
     * @param mainFixedAssetCode main code to match
     * @param username           owner filter
     * @return matching assets (never {@code null})
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByMainFixedAssetCodeAndAddedByService(
        String mainFixedAssetCode, String username) {
        return new ArrayList<>(repository.findByMainFixedAssetCodeAndAddedBy(mainFixedAssetCode,
            username));
    }

    /**
     * Finds assets whose accounting account starts with the given prefix for a specific owner.
     *
     * @param accountingAccount account prefix
     * @param username          owner filter
     * @return matching assets (never {@code null})
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByFixedAssetAccountingAccountAndAddedByService(
        String accountingAccount, String username) {
        return new ArrayList<>(repository.findByFixedAssetAccountingAccountAndAddedBy(
            accountingAccount, username));
    }

    /**
     * Returns all assets.
     *
     * @return all persisted assets (never {@code null})
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findAllService() {
        return new ArrayList<>(repository.findAll());
    }

    /**
     * Finds assets for a given period and owner.
     *
     * @param period   accounting period
     * @param username owner filter
     * @return matching assets (never {@code null})
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByPeriodAndAddedByService(LocalDate period, String username) {
        return new ArrayList<>(repository.findByAccountingPeriodAndAddedBy(period, username));
    }

    /**
     * Finds assets whose accounting account prefix is contained in the provided list for the specified owner.
     *
     * @param username owner filter
     * @param prefixes list of prefixes to match
     * @return matching assets (never {@code null})
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByAddedByAndAccountingAccountPrefixesService(String username,
                                                                                      List<String> prefixes) {
        return new ArrayList<>(repository.findByAddedByAndAccountingAccountPrefixes(username, prefixes));
    }

    /**
     * Executes the stored procedure that generates the Fixed Asset Tax Book dataset.
     *
     * @param params report parameters (must not be {@code null})
     * @return non-null list of {@link FinancialFixedAssetDto} rows (possibly empty)
     * @throws SQLException if the underlying JDBC call fails
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAssetDto> callGetReportTaxBookService(ParamReportDto params)
        throws SQLException {
        return repositoryJdbc.callGetReportTaxBookJdbc(params);
    }

    /**
     * Executes the stored procedure that generates the Financial &amp; Tax Depreciation Detail dataset.
     *
     * @param params report parameters (must not be {@code null})
     * @return non-null list of {@link FinancialFixedAssetDto} rows (possibly empty)
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAssetDto> callGetReportDetailFinancialAndTaxDepreciationService(
        ParamReportDto params) {
        return repositoryJdbc.callGetReportDetailFinancialAndTaxDepreciationJdbc(params);
    }

}
