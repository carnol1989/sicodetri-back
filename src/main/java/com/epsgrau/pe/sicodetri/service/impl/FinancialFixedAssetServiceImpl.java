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

    private final Map<ProjectionType, ProjectionGenerator> projectionGenerators;

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
          case 33 -> inputData.setWellAppraisedPreInvestmentStudyDry(cellValue);
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

    @Override
    @Transactional
    public void updateService(FinancialFixedAsset obj) {
        repository.save(obj);
    }

    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByAccountingPeriod(LocalDate period) {
        repository.deleteByAccountingPeriod(period);
    }

    @Transactional
    @Modifying
    @Override
    public void generateProjectionService(ProjectionType type,
                                          LocalDate accountingPeriod,
                                          List<FinancialFixedAsset> fixedAssets,
                                          String accountingAccount) {
        ProjectionGenerator gen = projectionGenerators.get(type);
        if (gen == null) {
            throw new IllegalArgumentException("Tipo de proyección no soportado: " + type);
        }
        gen.generate(accountingPeriod, fixedAssets);
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialFixedAsset findByIdService(Long id) {
        Optional<FinancialFixedAsset> optionalFinancialFixedAsset = repository.findById(id);
        return optionalFinancialFixedAsset.orElseGet(FinancialFixedAsset::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByMainFixedAssetCodeService(String mainFixedAssetCode) {
        return new ArrayList<>(repository.findByMainFixedAssetCode(mainFixedAssetCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByFixedAssetAccountingAccountService(
        String accountingAccount) {
        return new ArrayList<>(repository.findByFixedAssetAccountingAccount(accountingAccount));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findAllService() {
        return new ArrayList<>(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByPeriodService(LocalDate period) {
        return new ArrayList<>(repository.findByAccountingPeriod(period));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByPeriodAndAccountStartingWithService(LocalDate period,
                                                                               String account) {
        return new ArrayList<>(repository
            .findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(period, account));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAsset> findByAccountingAccountPrefixesService(List<String> prefixes) {
        return new ArrayList<>(repository.findByAccountingAccountPrefixes(prefixes));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAssetDto> callGetReportTaxBookService(ParamReportDto params)
        throws SQLException {
        return repositoryJdbc.callGetReportTaxBookJdbc(params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialFixedAssetDto> callGetReportDetailFinancialAndTaxDepreciationService(
        ParamReportDto params) {
        return repositoryJdbc.callGetReportDetailFinancialAndTaxDepreciationJdbc(params);
    }

}
