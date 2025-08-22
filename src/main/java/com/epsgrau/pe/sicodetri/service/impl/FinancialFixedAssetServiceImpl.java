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
import com.epsgrau.pe.sicodetri.service.projection.ProjectionType;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * Implementation of {@link FinancialFixedAssetService} that handles the import,
 * persistence, and querying of financial fixed assets and their related depreciation,
 * additions, deductions, and reporting.
 * Key responsibilities include:
 * <ul>
 *   <li>Loading and parsing Excel files into {@link InputData} models</li>
 *   <li>Converting {@link InputData} into {@link FinancialFixedAsset} entities and saving them</li>
 *   <li>Deleting assets by period or by ID</li>
 *   <li>Generating depreciation projections via {@link ProjectionGenerator}</li>
 *   <li>Delegating to JDBC repository for stored‐procedure reports</li>
 * </ul>
 * <b>Class</b>: {@link FinancialFixedAssetServiceImpl}<br/>
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
@Slf4j
@Service
public class FinancialFixedAssetServiceImpl implements FinancialFixedAssetService {

    private final Map<ProjectionType, ProjectionGenerator> projectionGenerators;

    /**
     * Constructs the service implementation, building a map of projection generators
     * keyed by their {@link ProjectionType}.
     *
     * @param generators the list of ProjectionGenerator implementations to support various
     *                   projection types
     */
    @Autowired
    public FinancialFixedAssetServiceImpl(List<ProjectionGenerator> generators) {
        this.projectionGenerators = generators.stream()
            .collect(Collectors.toMap(ProjectionGenerator::getType, g -> g));
    }

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

    /**
     * Reads the first sheet of the provided Excel {@code file} and maps each data row
     * (skipping headers) into an {@link InputData} object, preserving row numbers
     * and total row count.
     *
     * @param file   the uploaded Excel file (XLSX)
     * @param period the accounting period string (format YYYY-MM-DD)
     * @return a list of {@link InputData} instances representing each valid row
     * @throws RuntimeException if file cannot be read or a row mapping fails
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
     * Processes a single Excel {@link Cell}, extracting its value and assigning it
     * to the corresponding field in the given models.
     *
     * @param cell        the Excel cell to process
     * @param inputData   the InputData instance to populate with raw cell values
     * @param assetDetail the FixedAssetDetail instance to populate with descriptive fields
     * @param depreciation the Depreciation instance to populate with method and document
     *                     information
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
     * Returns the string representation of the given Excel {@link Cell}'s value,
     * or an empty string if the cell is null or has no content.
     *
     * @param cell the Excel cell from which to extract the value
     * @return the cell's value as a trimmed string, or an empty string if the cell is null/empty
     */
    private String getCellValue(Cell cell) {
        Object value = getValueCell(cell);
        return value != null ? String.valueOf(value) : _BLANK;
    }

    /**
     * Normalizes a date string into ISO format (YYYY-MM-DD). Accepts input in either
     * "DD/MM/YYYY" or "YYYY-MM-DD" form; if the string cannot be parsed, returns a default date.
     *
     * @param date the input date string
     * @return the formatted date in "YYYY-MM-DD" format, or {@code S_DATE_DEFAULT} if parsing fails
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
     * Persists all {@link FinancialFixedAsset} entities converted from the given list
     * of {@link InputData} for the specified accountingPeriod.
     *
     * @param accountingPeriod the accounting period for which to save assets
     * @param listInputData    the list of input data to transform and save
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
     * Get List of financial fixed asset.
     *
     * @param accountingPeriod Accounting period
     * @param listInputData List input data
     * @return List of FinancialFixedAsset
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
     * Updates an existing {@link FinancialFixedAsset} in the database.
     *
     * @param obj the FinancialFixedAsset to update
     */
    @Override
    @Transactional
    public void updateService(FinancialFixedAsset obj) {
        repository.save(obj);
    }

    /**
     * Deletes the {@link FinancialFixedAsset} identified by the given ID.
     *
     * @param id the ID of the FinancialFixedAsset to delete
     */
    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    /**
     * Deletes all {@link FinancialFixedAsset} records for the given accounting period.
     *
     * @param period the accounting period whose assets should be deleted
     */
    @Override
    @Transactional
    public void deleteByAccountingPeriodAndAddedByService(LocalDate period, String username) {
        repository.deleteByAccountingPeriodAndAddedBy(period, username);
    }

    /**
     * Generates a depreciation projection of type {@code type} for the given list
     * of fixed assets and accounting period, delegating to the appropriate generator.
     *
     * @param type              the projection type (e.g., TEMPORARY_DEDUCTION)
     * @param accountingPeriod  the period for which to generate projections
     * @param fixedAssets       the list of assets to project
     * @param accountingAccount the accounting account filter for projection
     * @throws IllegalArgumentException if no generator exists for the specified type
     */
    @Transactional
    @Modifying
    @Override
    public void generateProjectionService(ProjectionType type,
                                          LocalDate accountingPeriod,
                                          List<FinancialFixedAsset> fixedAssets,
                                          String accountingAccount,
                                          String username) {
        ProjectionGenerator gen = projectionGenerators.get(type);
        if (gen == null) {
            throw new IllegalArgumentException("Tipo de proyección no soportado: " + type);
        }
        gen.generate(username, accountingPeriod, fixedAssets);
    }

    /**
     * Retrieves a single {@link FinancialFixedAsset} by its ID.
     *
     * @param id the identifier of the FinancialFixedAsset
     * @return the found entity, or a new empty instance if not present
     */
    @Override
    @Transactional(readOnly = true)
    public FinancialFixedAsset findByIdService(Long id) {
        Optional<FinancialFixedAsset> optionalFinancialFixedAsset = repository.findById(id);
        return optionalFinancialFixedAsset.orElseGet(FinancialFixedAsset::new);
    }

    /**
     * Finds all {@link FinancialFixedAsset} entities matching the given main fixed asset code.
     *
     * @param mainFixedAssetCode the main code to filter by
     * @return list of matching FinancialFixedAsset instances
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByMainFixedAssetCodeAndAddedByService(
        String mainFixedAssetCode, String username) {
        return new ArrayList<>(repository.findByMainFixedAssetCodeAndAddedBy(mainFixedAssetCode,
            username));
    }

    /**
     * Finds all {@link FinancialFixedAsset} entities whose accounting account starts
     * with the given prefix.
     *
     * @param accountingAccount the account code prefix
     * @return list of matching FinancialFixedAsset instances
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByFixedAssetAccountingAccountAndAddedByService(
        String accountingAccount, String username) {
        return new ArrayList<>(repository.findByFixedAssetAccountingAccountAndAddedBy(
            accountingAccount, username));
    }

    /**
     * Retrieves all {@link FinancialFixedAsset} entities.
     *
     * @return list of all FinancialFixedAsset instances
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findAllService() {
        return new ArrayList<>(repository.findAll());
    }

    /**
     * Finds {@link FinancialFixedAsset} entities for the specified accounting period.
     *
     * @param period the accounting period filter
     * @return list of matching FinancialFixedAsset instances
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByPeriodAndAddedByService(LocalDate period, String username) {
        return new ArrayList<>(repository.findByAccountingPeriodAndAddedBy(period, username));
    }

    /**
     * Finds {@link FinancialFixedAsset} entities for the specified period whose
     * accounting account starts with the given prefix.
     *
     * @param period the accounting period filter
     * @param account the accounting account prefix
     * @return list of matching FinancialFixedAsset instances
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByPeriodAndAddedByAndAccountStartingWithService(LocalDate period,
                                                                                         String account,
                                                                                         String username) {
        return new ArrayList<>(repository.findByAccountingPeriodAndAddedByAndFixedAssetAccountingAccountStartingWith(
            period, username, account));
    }

    /**
     * Finds {@link FinancialFixedAsset} entities whose accounting account code prefix
     * is in the given list.
     *
     * @param prefixes list of 3-character prefixes
     * @return list of matching FinancialFixedAsset instances
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByAddedByAndAccountingAccountPrefixesService(String username,
                                                                                      List<String> prefixes) {
        return new ArrayList<>(repository.findByAddedByAndAccountingAccountPrefixes(username, prefixes));
    }

    /**
     * Calls the JDBC stored procedure to generate the tax book report.
     *
     * @param params the report parameters
     * @return a list of {@link FinancialFixedAssetDto} rows
     * @throws SQLException if the procedure call fails
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAssetDto> callGetReportTaxBookService(ParamReportDto params)
        throws SQLException {
        return repositoryJdbc.callGetReportTaxBookJdbc(params);
    }

    /**
     * Calls the JDBC stored procedure to generate the detailed financial and
     * tax depreciation report.
     *
     * @param params the report parameters
     * @return a list of {@link FinancialFixedAssetDto} rows
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAssetDto> callGetReportDetailFinancialAndTaxDepreciationService(
        ParamReportDto params) {
        return repositoryJdbc.callGetReportDetailFinancialAndTaxDepreciationJdbc(params);
    }

}
