package com.epsgrau.pe.sicodetri.service.impl;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelper.getValueCell;
import static com.epsgrau.pe.sicodetri.util.Constants.D_VAL_ZERO;
import static com.epsgrau.pe.sicodetri.util.Constants.FORMATTER_YYYY_MM_DD;
import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_0;
import static com.epsgrau.pe.sicodetri.util.Constants.S_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_332;
import static com.epsgrau.pe.sicodetri.util.Constants._BLANK;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.model.Depreciation;
import com.epsgrau.pe.sicodetri.model.FixedAssetDetail;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.Projection;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetJdbcRepository;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetRepository;
import com.epsgrau.pe.sicodetri.service.AdditionService;
import com.epsgrau.pe.sicodetri.service.DeductionService;
import com.epsgrau.pe.sicodetri.service.FinancialDepreciationService;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import com.epsgrau.pe.sicodetri.service.TaxDepreciationService;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
 * This class is the implementation of the service for Financial Fixed Asset. <br/>
 * <b>Class</b>: {@link FinancialFixedAssetServiceImpl}<br/>
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

    @Override
    public List<InputData> loadExcelFileToObjectService(MultipartFile file, String period) {
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
          default -> {
          }
        }
    }

    private String getCellValue(Cell cell) {
        Object value = getValueCell(cell);
        return value != null ? String.valueOf(value) : _BLANK;
    }

    private String formatDate(String date) {
        if (date.contains("/")) {
            String[] dateParts = date.split("/");
            return dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
        } else if (date.contains("-")) {
            return date;
        }
        return S_DATE_DEFAULT;
    }

    @Override
    @Transactional
    public void saveAllService(String accountingPeriod, List<InputData> listInputData) {
        List<FinancialFixedAsset> list = getListFinancialFixedAsset(accountingPeriod,
            listInputData);
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
        List<InputData> listInputData) {
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

                if (itemInputData.getAccountingPeriod() != null
                    && accountingPeriod.equals(itemInputData.getAccountingPeriod())) {
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
                    itemFinancialFixedAsset.setAddedBy("CARNOL");
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
     * Validate and analyze dates.
     *
     * @param dateStr Date string
     * @param formatter Formatter
     * @param rowNum Row num
     * @param fieldName Field name
     * @return LocalDate
     */
    private static LocalDate parseDate(String dateStr, DateTimeFormatter formatter,
                                       int rowNum, String fieldName) {
        if (dateStr == null || dateStr.isBlank()) {
            return LOCAL_DATE_DEFAULT;
        }
        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido en '" + fieldName
                + "' (fila " + rowNum + "): '" + dateStr + "'");
        }
    }

    /**
     * Numerical (BigDecimal) parsing with validation.
     *
     * @param value String for parse
     * @param rowNum Row num
     * @param fieldName Field name
     * @return BigDecimal
     */
    private static BigDecimal parseBigDecimal(String value, int rowNum, String fieldName) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            // 1. Elimina separadores de miles (comas)
            String normalizedValue = value.replace(",", "");

            // 2. Reemplaza "- 0" o cualquier "- <espacio><dígitos>" por "-<dígitos>"
            normalizedValue = normalizedValue.replaceAll("-\\s+(\\d+)", "-$1");

            return new BigDecimal(normalizedValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inválido en '" + fieldName + "' (fila "
                + rowNum + "): '" + value + "'");
        }
    }

    /**
     * Numerical (Double) parsing with validation.
     *
     * @param value String
     * @param rowNum Row num
     * @param fieldName Field name
     * @return Double
     */
    private static Double parseDouble(String value, int rowNum, String fieldName) {
        if (value == null || value.isBlank()) {
            return D_VAL_ZERO;
        }
        try {
            String normalizedValue = value.replace(",", "");

            normalizedValue = normalizedValue.replaceAll("-\\s+(\\d+(\\.\\d+)?)", "-$1");

            return Double.parseDouble(normalizedValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número double inválido en '" + fieldName
                + "' (fila " + rowNum + "): '" + value + "'");
        }
    }

    /**
     * Validate string and return default value.
     *
     * @param value String
     * @return String
     */
    private static String defaultString(String value) {
        return (value != null) ? value : _BLANK;
    }

    @Override
    public void updateService(FinancialFixedAsset obj) {
        repository.save(obj);
    }

    @Override
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteByAccountingPeriod(LocalDate period) {
        repository.deleteByAccountingPeriod(period);
    }

    @Transactional
    @Modifying
    @Override
    public void generateTemporayDeductionsService(LocalDate accountingPeriod,
                                                  List<FinancialFixedAsset> fixedAssets) {
        TaxTableSunat taxTable = taxTableService.findByAccountigAccountService(S_VAL_332);
        if (taxTable == null) {
            return; // Si no hay tabla de impuestos, no se puede proceder
        }

        for (FinancialFixedAsset fixedAsset : fixedAssets) {
            if (isEligibleForDepreciation(fixedAsset)) {
                processAssetDepreciation(fixedAsset, accountingPeriod, taxTable);
            }
        }
    }

    private boolean isEligibleForDepreciation(FinancialFixedAsset fixedAsset) {
        return !fixedAsset.getAcquisitionDate().equals(LOCAL_DATE_DEFAULT)
            && fixedAsset.getInitialBalance().compareTo(BigDecimal.ZERO) > N_VAL_0
            && fixedAsset.getRevaluedAmount().compareTo(BigDecimal.ZERO) == N_VAL_0;
    }

    private void processAssetDepreciation(FinancialFixedAsset fixedAsset,
        LocalDate accountingPeriod, TaxTableSunat taxTable) {
        TaxDepreciation taxDepreciationMonth = new TaxDepreciation();
        taxDepreciationMonth.setNetTaxValue(BigDecimal.ZERO);
        taxDepreciationMonth.setFirst(true);
        taxDepreciationMonth.setLast(false);

        Projection projection = new Projection();
        projection.setAccountigPeriod(accountingPeriod);
        projection.setAccountigPeriodNext(accountingPeriod);
        List<FinancialDepreciation> financialDepreciationList = new ArrayList<>();
        projection.setFinancialDepreciationList(financialDepreciationList);
        projection.setTaxDepreciation(taxDepreciationMonth);
        List<TaxDepreciation> taxDepreciationList = new ArrayList<>();
        projection.setTaxDepreciationList(taxDepreciationList);
        List<Addition> additionList = new ArrayList<>();
        projection.setAdditionList(additionList);
        List<Deduction> deductionList = new ArrayList<>();
        projection.setDeductionList(deductionList);

        LocalDate acquisitionDate = fixedAsset.getAcquisitionDate();
        int numberYearsPassed = Period.between(acquisitionDate, accountingPeriod).getYears();
        int remainingYears = fixedAsset.getUsefulLifeYears().intValue() - numberYearsPassed;
        for (int year = 0; year < remainingYears; year++) {
            projection = processYearDepreciation(fixedAsset, projection, taxTable);
        }

        financialDepreciationService.saveAllService(projection.getFinancialDepreciationList());
        taxDepreciationService.saveAllService(projection.getTaxDepreciationList());
        deductionService.saveAllService(projection.getDeductionList());
        additionService.saveAllService(projection.getAdditionList());
    }

    private Projection processYearDepreciation(FinancialFixedAsset fixedAsset,
                                               Projection projection,
                                              TaxTableSunat taxTable) {
        for (int month = 0; month < 12; month++) {
            FinancialDepreciation financialDepreciation = createFinancialDepreciation(fixedAsset,
                projection.getAccountigPeriodNext());
            projection.getFinancialDepreciationList().add(financialDepreciation);

            TaxDepreciation taxDepreciation = createTaxDepreciation(fixedAsset,
                projection.getAccountigPeriodNext(),
                    projection.getAccountigPeriod(), projection.getTaxDepreciation(), taxTable);
            projection.getTaxDepreciationList().add(taxDepreciation);

            BigDecimal valueAdditionOrDeduction = BigDecimal.ZERO;

            if (taxDepreciation.getValueDepreciation()
                .compareTo(financialDepreciation.getValueDepreciation()) > 0
                && taxDepreciation.getNetTaxValue().compareTo(BigDecimal.ZERO) > 0) {
                valueAdditionOrDeduction = taxDepreciation.getValueDepreciation()
                    .subtract(financialDepreciation.getValueDepreciation());
                Deduction deduction = createDeduction(projection.getAccountigPeriodNext(),
                    taxDepreciation, valueAdditionOrDeduction);
                projection.getDeductionList().add(deduction);
            } else if (projection.getTaxDepreciation().getNetTaxValue()
                .compareTo(BigDecimal.ZERO) > 0
                && taxDepreciation.getNetTaxValue().compareTo(BigDecimal.ZERO) == 0) {
                valueAdditionOrDeduction = taxDepreciation.getValueDepreciation()
                    .subtract(financialDepreciation.getValueDepreciation());
                Deduction deduction = createDeduction(projection.getAccountigPeriodNext(),
                    taxDepreciation, valueAdditionOrDeduction);
                projection.getDeductionList().add(deduction);
            } else {
                valueAdditionOrDeduction = financialDepreciation.getValueDepreciation();
                Addition addition = createAddition(projection.getAccountigPeriodNext(),
                    taxDepreciation, valueAdditionOrDeduction);
                projection.getAdditionList().add(addition);
            }

            projection.setAccountigPeriodNext(projection.getAccountigPeriodNext().plusMonths(1));
            projection.setTaxDepreciation(taxDepreciation);
        }
        projection.setAccountigPeriodNext(projection.getAccountigPeriodNext()
            .plusYears(1).minusMonths(12)); // Ajusta el periodo al siguiente año
        return projection;
    }

    private FinancialDepreciation createFinancialDepreciation(FinancialFixedAsset fixedAsset,
                                                              LocalDate accountingPeriod) {
        FinancialDepreciation financialDepreciation = new FinancialDepreciation();
        financialDepreciation.setFinancialFixedAsset(fixedAsset);
        financialDepreciation.setAccountingPeriod(accountingPeriod);
        financialDepreciation.calculateDepreciation(fixedAsset);
        financialDepreciation.setAddedBy("CARNOL");
        financialDepreciation.setAdditionDate(LocalDateTime.now());
        return financialDepreciation;
    }

    private TaxDepreciation createTaxDepreciation(FinancialFixedAsset fixedAsset,
                                                  LocalDate accountingPeriodNext,
                                                  LocalDate accountingPeriod,
                                                  TaxDepreciation taxDepreciationOld,
                                                  TaxTableSunat taxTable) {
        TaxDepreciation taxDepreciation = new TaxDepreciation();
        taxDepreciation.setFinancialFixedAsset(fixedAsset);
        taxDepreciation.setAccountingPeriod(accountingPeriodNext);
        taxDepreciation.calculateDepreciation(fixedAsset, taxTable,
            accountingPeriod, taxDepreciationOld);
        taxDepreciation.setAddedBy("CARNOL");
        taxDepreciation.setAdditionDate(LocalDateTime.now());
        taxDepreciation.setFirst(false);
        return taxDepreciation;
    }

    private Addition createAddition(LocalDate accountingPeriod, TaxDepreciation taxDepreciation,
                                    BigDecimal valueAddition) {
        Addition addition = new Addition();
        addition.setAccountingPeriod(accountingPeriod);
        addition.setTaxDepreciation(taxDepreciation);
        addition.setValueAddition(valueAddition);
        addition.setAddedBy("CARNOL");
        addition.setAdditionDate(LocalDateTime.now());
        return addition;
    }

    private Deduction createDeduction(LocalDate accountingPeriod, TaxDepreciation taxDepreciation,
                                      BigDecimal valueDeduction) {
        Deduction deduction = new Deduction();
        deduction.setAccountingPeriod(accountingPeriod);
        deduction.setTaxDepreciation(taxDepreciation);
        deduction.setValueDeduction(valueDeduction);
        deduction.setAddedBy("CARNOL");
        deduction.setAdditionDate(LocalDateTime.now());
        return deduction;
    }

    @Override
    public FinancialFixedAsset findByIdService(Long id) {
        Optional<FinancialFixedAsset> optionalFinancialFixedAsset = repository.findById(id);
        return optionalFinancialFixedAsset.orElseGet(FinancialFixedAsset::new);
    }

    @Override
    public List<FinancialFixedAsset> findByMainFixedAssetCodeService(String mainFixedAssetCode) {
        return new ArrayList<>(repository.findByMainFixedAssetCode(mainFixedAssetCode));
    }

    @Override
    public List<FinancialFixedAsset> findByFixedAssetAccountingAccountService(
        String accountingAccount) {
        return new ArrayList<>(repository.findByFixedAssetAccountingAccount(accountingAccount));
    }

    @Override
    public List<FinancialFixedAsset> findAllService() {
        return new ArrayList<>(repository.findAll());
    }

    @Override
    public List<FinancialFixedAsset> findByPeriodService(LocalDate period) {
        return new ArrayList<>(repository.findByAccountingPeriod(period));
    }

    @Override
    public List<FinancialFixedAsset> findByPeriodAndAccountStartingWithService(LocalDate period,
                                                                               String account) {
        return new ArrayList<>(repository
            .findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(period, account));
    }

    @Override
    public List<FinancialFixedAssetDto> callGetReportTaxBookService(ParamReportDto params)
        throws SQLException {
        return repositoryJdbc.callGetReportTaxBookJdbc(params);
    }

    @Override
    public List<FinancialFixedAssetDto> callGetReportDetailFinancialAndTaxDepreciationService(
        ParamReportDto params) {
        return repositoryJdbc.callGetReportDetailFinancialAndTaxDepreciationJdbc(params);
    }

}
