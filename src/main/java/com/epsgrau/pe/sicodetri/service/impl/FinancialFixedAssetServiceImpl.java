package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.*;
import com.epsgrau.pe.sicodetri.model.Depreciation;
import com.epsgrau.pe.sicodetri.model.FixedAssetDetail;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.Projection;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetRepository;
import com.epsgrau.pe.sicodetri.service.*;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelper.getValueCell;
import static com.epsgrau.pe.sicodetri.util.Constants.*;

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
@Service
public class FinancialFixedAssetServiceImpl implements FinancialFixedAssetService {

    @Autowired
    private FinancialFixedAssetRepository repository;

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
        List<InputData> listInputData = new ArrayList<InputData>();
        try {
            if (file.isEmpty() || file.getInputStream().available() == 0) {
                return listInputData;
            }

            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
            //System.out.println("physicalNumberOfRows: " + physicalNumberOfRows);

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                int rowNum = currentRow.getRowNum();
                //System.out.println("rowNum: " + rowNum);

                // skip header
                if (rowNum > 10 && (rowNum < physicalNumberOfRows)) {
                    Iterator<Cell> cellsInRow = currentRow.iterator();

                    InputData itemInputData = new InputData();
                    itemInputData.setAccountingPeriod(period);
                    FixedAssetDetail itemInputDataDetail = new FixedAssetDetail();
                    Depreciation itemInputDataDepreciation = new Depreciation();

                    while (cellsInRow.hasNext()) {
                        Cell currentCell = cellsInRow.next();
                        int columnIndex = currentCell.getColumnIndex();
                        //System.out.println("columnIndex: " + columnIndex);
                        //System.out.println("cellType: " + currentCell.getCellType() + "\tvalueCurrentCell: " + String.valueOf(getValueCell(currentCell)));
                        switch (columnIndex) {
                            case 0:
                                String mainFixAssetCode = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : _BLANK);
                                itemInputData.setMainFixedAssetCode(mainFixAssetCode);
                                break;

                            case 1:
                                String fixedAssetCodeComponent = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : _BLANK);
                                itemInputData.setFixedAssetCodeComponent(fixedAssetCodeComponent);
                                break;

                            case 2:
                                String fixedAssetAccountingAccount = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : _BLANK);
                                itemInputData.setFixedAssetAccountingAccount(fixedAssetAccountingAccount);
                                break;

                            case 3:
                                String description = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : _BLANK);
                                itemInputDataDetail.setDescription(description);
                                itemInputData.setFixedAssetDetail(itemInputDataDetail);
                                break;

                            case 4:
                                String fixedAssetBrand = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : _BLANK);
                                itemInputDataDetail.setFixedAssetBrand(fixedAssetBrand);
                                itemInputData.setFixedAssetDetail(itemInputDataDetail);
                                break;

                            case 5:
                                String fixedAssetModel = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : _BLANK);
                                itemInputDataDetail.setFixedAssetModel(fixedAssetModel);
                                itemInputData.setFixedAssetDetail(itemInputDataDetail);
                                break;

                            case 6:
                                String fixedSerialNumberOrPlate = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : _BLANK);
                                itemInputDataDetail.setFixedAssetSerialNumberAndOrPlate(fixedSerialNumberOrPlate);
                                itemInputData.setFixedAssetDetail(itemInputDataDetail);
                                break;

                            case 7:
                                String initialBalance = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setInitialBalance(initialBalance);
                                break;

                            case 8:
                                String acquisitionsAdditions = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setAcquisitionsAdditions(acquisitionsAdditions);
                                break;

                            case 9:
                                String improvements = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setImprovements(improvements);
                                break;

                            case 10:
                                String transfersAndOrReclassifications = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setTransfersAndOrReclassifications(transfersAndOrReclassifications);
                                break;

                            case 11:
                                String withDrawalsAndOrRetirements = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setWithdrawalsAndOrRetirements(withDrawalsAndOrRetirements);
                                break;

                            case 12:
                                String otherAdjustments = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setOtherAdjustments(otherAdjustments);
                                break;

                            case 13:
                                String bookValueOfFixedAssets = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setBookValueOfFixedAssets(bookValueOfFixedAssets);
                                break;

                            case 14:
                                String inflationAdjustment = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setInflationAdjustment(inflationAdjustment);

                            case 15:
                                String adjustedValueOfFixedAssets = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setAdjustedValueOfFixedAssets(adjustedValueOfFixedAssets);
                                break;

                            case 16:
                                String acquisitionDate = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_DATE_DEFAULT);
                                if (acquisitionDate.contains("/")) {
                                    String[] arrayAcquisitionDate = acquisitionDate.split("/");
                                    acquisitionDate = arrayAcquisitionDate[2] + "-" + arrayAcquisitionDate[1] + "-" + arrayAcquisitionDate[0];
                                }
                                itemInputData.setAcquisitionDate(acquisitionDate);
                                break;

                            case 17:
                                String startDateFixedAssetUsage = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_DATE_DEFAULT);
                                if (startDateFixedAssetUsage.contains("/")) {
                                    String[] arrayAcquisitionDate = startDateFixedAssetUsage.split("/");
                                    startDateFixedAssetUsage = arrayAcquisitionDate[2] + "-" + arrayAcquisitionDate[1] + "-" + arrayAcquisitionDate[0];
                                }
                                itemInputData.setStartDateFixedAssetUsage(startDateFixedAssetUsage);
                                break;

                            case 18:
                                String retirementDate = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_DATE_DEFAULT);
                                if (retirementDate.contains("/")) {
                                    String[] arrayRetirementDate = retirementDate.split("/");
                                    retirementDate = arrayRetirementDate[2] + "-" + arrayRetirementDate[1] + "-" + arrayRetirementDate[0];
                                }
                                itemInputData.setRetirementDate(retirementDate);
                                break;

                            case 19:
                                String appliedMethod = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : _BLANK);
                                itemInputDataDepreciation.setAppliedMethod(appliedMethod);
                                itemInputData.setDepreciation(itemInputDataDepreciation);
                                break;

                            case 20:
                                String authorizationDocumentNumber = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : _BLANK);
                                itemInputDataDepreciation.setAuthorizationDocumentNumber(authorizationDocumentNumber);
                                itemInputData.setDepreciation(itemInputDataDepreciation);
                                break;

                            case 21:
                                String useFullLifeYears = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setUsefulLifeYears(useFullLifeYears);
                                break;

                            case 22:
                                String accumulatedDepreciationTheEndPrevious = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setAccumulatedDepreciationTheEndPrevious(accumulatedDepreciationTheEndPrevious);
                                break;

                            case 23:
                                String currentDepreciation = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setCurrentDepreciation(currentDepreciation);
                                break;

                            case 24:
                                String currentDepreciationRelatedWithDrawalsRetirements = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setCurrentDepreciationRelatedWithdrawalsRetirements(currentDepreciationRelatedWithDrawalsRetirements);
                                break;

                            case 25:
                                String currentDepreciationRelatedOtherAdjustments = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setCurrentDepreciationRelatedOtherAdjustments(currentDepreciationRelatedOtherAdjustments);
                                break;

                            case 26:
                                String accumulatedHistoricalDepreciation = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setAccumulatedHistoricalDepreciation(accumulatedHistoricalDepreciation);

                            case 27:
                                String depreciationInflationAdjustment = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setDepreciationInflationAdjustment(depreciationInflationAdjustment);
                                break;

                            case 28:
                                String accumulatedDepreciationAdjustedInflation = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setAccumulatedDepreciationAdjustedInflation(accumulatedDepreciationAdjustedInflation);
                                break;

                            case 30:
                                String netFixedAssets = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_VAL_ZERO);
                                itemInputData.setNetFixedAssets(String.valueOf(netFixedAssets));
                                break;

                            default:
                                break;
                        }
                    }
                    System.out.println(itemInputData);
                    listInputData.add(itemInputData);
                }
            }
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
        return listInputData;
    }

    @Override
    public void saveAllService(String accountingPeriod, List<InputData> listInputData) {
        List<FinancialFixedAsset> list = getListFinancialFixedAsset(accountingPeriod, listInputData);
        repository.saveAll(list);
    }

    public static List<FinancialFixedAsset> getListFinancialFixedAsset(String accountingPeriod, List<InputData> listInputData) {
        List<FinancialFixedAsset> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD);
        int row = 0;
        for (InputData itemInputData : listInputData) {
            //System.out.println("row: "+ row);
            //System.out.println(itemInputData.toString());

            if (accountingPeriod.equals(itemInputData.getAccountingPeriod())) {
                FinancialFixedAsset itemFinancialFixedAsset = new FinancialFixedAsset();

                LocalDate period = (itemInputData.getAccountingPeriod() != null ? LocalDate.parse(itemInputData.getAccountingPeriod(), formatter) :
                        LOCAL_DATE_DEFAULT);
                itemFinancialFixedAsset.setAccountingPeriod(period);

                String mainFixAssetCode = (itemInputData.getMainFixedAssetCode() != null ? itemInputData.getMainFixedAssetCode() : _BLANK);
                itemFinancialFixedAsset.setMainFixedAssetCode(mainFixAssetCode);//0

                String fixedAssetCodeComponent = (itemInputData.getFixedAssetCodeComponent() != null ? itemInputData.getFixedAssetCodeComponent() : _BLANK);
                itemFinancialFixedAsset.setFixedAssetCodeComponent(fixedAssetCodeComponent);//1

                String fixedAssetAccountingAccount = (itemInputData.getFixedAssetAccountingAccount() != null ? itemInputData.getFixedAssetAccountingAccount() : _BLANK);
                itemFinancialFixedAsset.setFixedAssetAccountingAccount(fixedAssetAccountingAccount);//2

                String description = "";
                String fixedAssetBrand = "";
                String fixedAssetModel = "";
                String fixedSerialNumberOrPlate = "";
                if (itemInputData.getFixedAssetDetail() != null) {
                    description = (itemInputData.getFixedAssetDetail().getDescription() != null ? itemInputData.getFixedAssetDetail().getDescription() : _BLANK);
                    fixedAssetBrand = (itemInputData.getFixedAssetDetail().getFixedAssetBrand() != null ? itemInputData.getFixedAssetDetail().getFixedAssetBrand() : _BLANK);
                    fixedAssetModel = (itemInputData.getFixedAssetDetail().getFixedAssetModel() != null ? itemInputData.getFixedAssetDetail().getFixedAssetModel() : _BLANK);
                    fixedSerialNumberOrPlate = (itemInputData.getFixedAssetDetail().getFixedAssetSerialNumberAndOrPlate() != null ?
                            itemInputData.getFixedAssetDetail().getFixedAssetSerialNumberAndOrPlate() : _BLANK);
                }
                itemFinancialFixedAsset.setDescription(description);//3
                itemFinancialFixedAsset.setFixedAssetBrand(fixedAssetBrand);//4
                itemFinancialFixedAsset.setFixedAssetModel(fixedAssetModel);//5
                itemFinancialFixedAsset.setFixedAssetSerialNumberAndOrPlate(fixedSerialNumberOrPlate);//6

                BigDecimal initialBalance = (itemInputData.getInitialBalance() != null ? new BigDecimal(itemInputData.getInitialBalance()) :
                        BigDecimal.ZERO);
                itemFinancialFixedAsset.setInitialBalance(initialBalance);//7

                BigDecimal acquisitionsAdditions = (itemInputData.getAcquisitionsAdditions() != null ? new BigDecimal(itemInputData.getAcquisitionsAdditions()) :
                        BigDecimal.ZERO);
                itemFinancialFixedAsset.setAcquisitionsAdditions(acquisitionsAdditions);//8

                BigDecimal improvements = (itemInputData.getImprovements() != null ? new BigDecimal(itemInputData.getImprovements()) :
                        BigDecimal.ZERO);
                itemFinancialFixedAsset.setImprovements(improvements);//9

                BigDecimal transfersAndOrReclassifications = (itemInputData.getTransfersAndOrReclassifications() != null ?
                        new BigDecimal(itemInputData.getTransfersAndOrReclassifications()) : BigDecimal.ZERO);
                itemFinancialFixedAsset.setTransfersAndOrReclassifications(transfersAndOrReclassifications);//10

                BigDecimal withDrawalsAndOrRetirements = (itemInputData.getWithdrawalsAndOrRetirements() != null ?
                        new BigDecimal(itemInputData.getWithdrawalsAndOrRetirements()) : BigDecimal.ZERO);
                itemFinancialFixedAsset.setWithdrawalsAndOrRetirements(withDrawalsAndOrRetirements);//11

                BigDecimal otherAdjustments = (itemInputData.getOtherAdjustments() != null ? new BigDecimal(itemInputData.getOtherAdjustments()) :
                        BigDecimal.ZERO);
                itemFinancialFixedAsset.setOtherAdjustments(otherAdjustments);//12

                BigDecimal bookValueOfFixedAssets = (itemInputData.getBookValueOfFixedAssets() != null ? new BigDecimal(itemInputData.getBookValueOfFixedAssets()) :
                        BigDecimal.ZERO);
                itemFinancialFixedAsset.setBookValueOfFixedAssets(bookValueOfFixedAssets);//13

                BigDecimal inflationAdjustment = (itemInputData.getInflationAdjustment() != null ? new BigDecimal(itemInputData.getInflationAdjustment()) :
                        BigDecimal.ZERO);
                itemFinancialFixedAsset.setInflationAdjustment(inflationAdjustment);//14

                BigDecimal adjustedValueOfFixedAssets = (itemInputData.getAdjustedValueOfFixedAssets() != null ? new BigDecimal(itemInputData.getAdjustedValueOfFixedAssets()) :
                        BigDecimal.ZERO);
                itemFinancialFixedAsset.setAdjustedValueOfFixedAssets(adjustedValueOfFixedAssets);//15

                LocalDate acquisitionDate = (itemInputData.getAcquisitionDate() != null ? LocalDate.parse(itemInputData.getAcquisitionDate(), formatter) :
                        LOCAL_DATE_DEFAULT);
                itemFinancialFixedAsset.setAcquisitionDate(acquisitionDate);//16

                LocalDate startDateFixedAssetUsage = (itemInputData.getStartDateFixedAssetUsage() != null ?
                        LocalDate.parse(itemInputData.getStartDateFixedAssetUsage(), formatter) : LOCAL_DATE_DEFAULT);
                itemFinancialFixedAsset.setStartDateFixedAssetUsage(startDateFixedAssetUsage);//17

                LocalDate retirementDate = (itemInputData.getRetirementDate() != null ? LocalDate.parse(itemInputData.getRetirementDate(), formatter) :
                        LOCAL_DATE_DEFAULT);
                itemFinancialFixedAsset.setRetirementDate(retirementDate);//18

                String appliedMethod = "";
                String authorizationDocumentNumber = "";
                if (itemInputData.getDepreciation() != null) {
                    appliedMethod = (itemInputData.getDepreciation().getAppliedMethod() != null ? itemInputData.getDepreciation().getAppliedMethod() : _BLANK);
                    authorizationDocumentNumber = (itemInputData.getDepreciation().getAuthorizationDocumentNumber() != null ?
                            itemInputData.getDepreciation().getAuthorizationDocumentNumber() : _BLANK);
                }
                itemFinancialFixedAsset.setAppliedMethod(appliedMethod);//19
                itemFinancialFixedAsset.setAuthorizationDocumentNumber(authorizationDocumentNumber);//20

                Double useFullLifeYears = (itemInputData.getUsefulLifeYears() != null ? Double.parseDouble(itemInputData.getUsefulLifeYears()) :
                        D_VAL_ZERO);
                itemFinancialFixedAsset.setUsefulLifeYears(useFullLifeYears);//21

                BigDecimal accumulatedDepreciationTheEndPrevious = (itemInputData.getAccumulatedDepreciationTheEndPrevious() != null ?
                        new BigDecimal(itemInputData.getAccumulatedDepreciationTheEndPrevious()) : BigDecimal.ZERO);
                itemFinancialFixedAsset.setAccumulatedDepreciationTheEndPrevious(accumulatedDepreciationTheEndPrevious);//22

                BigDecimal currentDepreciation = (itemInputData.getCurrentDepreciation() != null ? new BigDecimal(itemInputData.getCurrentDepreciation()) :
                        BigDecimal.ZERO);
                itemFinancialFixedAsset.setCurrentDepreciation(currentDepreciation);//23

                BigDecimal currentDepreciationRelatedWithDrawalsRetirements = (itemInputData.getCurrentDepreciationRelatedWithdrawalsRetirements() != null ?
                        new BigDecimal(itemInputData.getCurrentDepreciationRelatedWithdrawalsRetirements()) : BigDecimal.ZERO);
                itemFinancialFixedAsset.setCurrentDepreciationRelatedWithdrawalsRetirements(currentDepreciationRelatedWithDrawalsRetirements);//24

                BigDecimal currentDepreciationRelatedOtherAdjustments = (itemInputData.getCurrentDepreciationRelatedOtherAdjustments() != null ?
                        new BigDecimal(itemInputData.getCurrentDepreciationRelatedOtherAdjustments()) : BigDecimal.ZERO);
                itemFinancialFixedAsset.setCurrentDepreciationRelatedOtherAdjustments(currentDepreciationRelatedOtherAdjustments);//25

                BigDecimal accumulatedHistoricalDepreciation = (itemInputData.getAccumulatedHistoricalDepreciation() != null ?
                        new BigDecimal(itemInputData.getAccumulatedHistoricalDepreciation()) : BigDecimal.ZERO);
                itemFinancialFixedAsset.setAccumulatedHistoricalDepreciation(accumulatedHistoricalDepreciation);//26

                BigDecimal depreciationInflationAdjustment = (itemInputData.getDepreciationInflationAdjustment() != null ?
                        new BigDecimal(itemInputData.getDepreciationInflationAdjustment()) : BigDecimal.ZERO);
                itemFinancialFixedAsset.setDepreciationInflationAdjustment(depreciationInflationAdjustment);//27

                BigDecimal accumulatedDepreciationAdjustedInflation = (itemInputData.getAccumulatedDepreciationAdjustedInflation() != null ?
                        new BigDecimal(itemInputData.getAccumulatedDepreciationAdjustedInflation()) :  BigDecimal.ZERO);
                itemFinancialFixedAsset.setAccumulatedDepreciationAdjustedInflation(accumulatedDepreciationAdjustedInflation);//28

                BigDecimal netFixedAssets = (itemInputData.getNetFixedAssets() != null ? new BigDecimal(itemInputData.getNetFixedAssets()) :
                        BigDecimal.ZERO);
                itemFinancialFixedAsset.setNetFixedAssets(netFixedAssets);//30

                itemFinancialFixedAsset.setAddedBy("CARNOL");
                itemFinancialFixedAsset.setAdditionDate(LocalDateTime.now());
                list.add(itemFinancialFixedAsset);
            }

            row++;
        }
        return list;
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

    @Override
    @Transactional
    @Modifying
    public void generateFixedAssetDepreciationService(LocalDate accountingPeriod, FinancialFixedAsset fixedAsset) {
        if (fixedAsset.getFixedAssetAccountingAccount().startsWith(S_VAL_332)) {
            TaxTableSunat taxTable = taxTableService.findByAccountigAccountService(S_VAL_332);
            if (taxTable != null) {
                List<FinancialDepreciation> financialDepreciations = new ArrayList<>();
                List<TaxDepreciation> taxDepreciations = new ArrayList<>();
                FixedAssetDepreciation fixedAssetDepreciation = new FixedAssetDepreciation();
                fixedAssetDepreciation.setFixedAsset(fixedAsset);
                fixedAssetDepreciation.setAccountingPeriod(accountingPeriod);
                fixedAssetDepreciation.setFinancialDepreciations(financialDepreciations);
                fixedAssetDepreciation.setTaxDepreciations(taxDepreciations);
                fixedAssetDepreciation.setAddedBy("CARNOL");
                fixedAssetDepreciation.setAdditionDate(LocalDateTime.now());
                //depreciationService.saveService(fixedAssetDepreciation);

                FinancialDepreciation financialDepreciation = new FinancialDepreciation();
                financialDepreciation.setAccountingPeriod(accountingPeriod);
                financialDepreciation.calculateDepreciation(fixedAsset);
                financialDepreciation.setAddedBy("CARNOL");
                financialDepreciation.setAdditionDate(LocalDateTime.now());
                financialDepreciationService.saveService(financialDepreciation);
                financialDepreciations.add(financialDepreciation);

                List<Addition> additions = new ArrayList<>();
                List<Deduction> deductions = new ArrayList<>();
                TaxDepreciation taxDepreciation = new TaxDepreciation();
                //taxDepreciation.setFixedAsset(fixedAsset);
                taxDepreciation.setAccountingPeriod(accountingPeriod);
                taxDepreciation.calculateDepreciation(fixedAsset, taxTable, null, null);
                //taxDepreciation.setAdditions(additions);
                //taxDepreciation.setDeductions(deductions);
                taxDepreciation.setAddedBy("CARNOL");
                taxDepreciation.setAdditionDate(LocalDateTime.now());
                taxDepreciationService.saveService(taxDepreciation);
                taxDepreciations.add(taxDepreciation);

                financialDepreciation.setModifiedBy("CARNOL");
                financialDepreciation.setModificationDate(LocalDateTime.now());
                //depreciationService.updateService(financialDepreciation);

                Deduction deduction = new Deduction();
                Addition addition = new Addition();
                if (financialDepreciation.getValueDepreciation().compareTo(taxDepreciation.getValueDepreciation()) > N_VAL_1) {
                    addition.setAccountingPeriod(accountingPeriod);
                    addition.setValueAddition(financialDepreciation.getValueDepreciation());
                    addition.setModifiedBy("CARNOL");
                    addition.setModificationDate(LocalDateTime.now());
                    additionService.saveService(addition);
                    additions.add(addition);
                } else {
                    deduction.setAccountingPeriod(accountingPeriod);
                    deduction.setValueDeduction(financialDepreciation.getValueDepreciation().subtract(taxDepreciation.getValueDepreciation()));
                    deduction.setModifiedBy("CARNOL");
                    deduction.setModificationDate(LocalDateTime.now());
                    deductionService.saveService(deduction);
                    deductions.add(deduction);
                }
                //taxDepreciation.setAdditions(additions);
                //taxDepreciation.setDeductions(deductions);
                taxDepreciation.setModifiedBy("CARNOL");
                taxDepreciation.setModificationDate(LocalDateTime.now());
                taxDepreciationService.updateService(taxDepreciation);
            }
        }
    }

    @Transactional
    @Modifying
    @Override
    public void generateProjectionAdditionsAndDeductionsService(LocalDate accountingPeriod, List<FinancialFixedAsset> fixedAssets) {
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
        return !fixedAsset.getAcquisitionDate().equals(LOCAL_DATE_DEFAULT) &&
                fixedAsset.getInitialBalance().compareTo(BigDecimal.ZERO) > N_VAL_0;
    }

    private void processAssetDepreciation(FinancialFixedAsset fixedAsset, LocalDate accountingPeriod, TaxTableSunat taxTable) {
        LocalDate acquisitionDate = fixedAsset.getAcquisitionDate();
        int numberYearsPassed = Period.between(acquisitionDate, accountingPeriod).getYears();
        int remainingYears = fixedAsset.getUsefulLifeYears().intValue() - numberYearsPassed;

        List<FinancialDepreciation> financialDepreciationList = new ArrayList<>();
        List<TaxDepreciation> taxDepreciationList = new ArrayList<>();
        List<Addition> additionList = new ArrayList<>();
        List<Deduction> deductionList = new ArrayList<>();

        TaxDepreciation taxDepreciationMonth = new TaxDepreciation();
        taxDepreciationMonth.setNetTaxValue(BigDecimal.ZERO);
        taxDepreciationMonth.setFirst(true);
        taxDepreciationMonth.setLast(false);

        Projection projection = new Projection();
        projection.setAccountigPeriod(accountingPeriod);
        projection.setAccountigPeriodNext(accountingPeriod);
        projection.setFinancialDepreciationList(financialDepreciationList);
        projection.setTaxDepreciation(taxDepreciationMonth);
        projection.setTaxDepreciationList(taxDepreciationList);
        projection.setAdditionList(additionList);
        projection.setDeductionList(deductionList);

        for (int year = 0; year < remainingYears; year++) {
            projection = processYearDepreciation(fixedAsset, projection, taxTable);
        }

        financialDepreciationService.saveAllService(projection.getFinancialDepreciationList());
        taxDepreciationService.saveAllService(projection.getTaxDepreciationList());
        deductionService.saveAllService(projection.getDeductionList());
        additionService.saveAllService(projection.getAdditionList());
    }

    private Projection processYearDepreciation(FinancialFixedAsset fixedAsset, Projection projection,
                                              TaxTableSunat taxTable) {
        for (int month = 0; month < 12; month++) {
            FinancialDepreciation financialDepreciation = createFinancialDepreciation(fixedAsset, projection.getAccountigPeriodNext());
            projection.getFinancialDepreciationList().add(financialDepreciation);

            TaxDepreciation taxDepreciation = createTaxDepreciation(fixedAsset, projection.getAccountigPeriodNext(),
                    projection.getAccountigPeriod(), projection.getTaxDepreciation(), taxTable);
            projection.getTaxDepreciationList().add(taxDepreciation);

            BigDecimal valueAdditionOrDeduction = BigDecimal.ZERO;
            /*System.out.println("Periodo: " + projection.getAccountigPeriodNext());
            System.out.println("Depreciación Tributaria: " + taxDepreciation.getValueDepreciation() + "\tValor Neto Tributario: "
                    + taxDepreciation.getNetTaxValue() + "\nDepreciación Financiera: " + financialDepreciation.getValueDepreciation());*/
            if (taxDepreciation.getValueDepreciation().compareTo(financialDepreciation.getValueDepreciation()) > 0 &&
                    taxDepreciation.getNetTaxValue().compareTo(BigDecimal.ZERO) > 0) {
                valueAdditionOrDeduction = taxDepreciation.getValueDepreciation().subtract(financialDepreciation.getValueDepreciation());
                Deduction deduction = createDeduction(projection.getAccountigPeriodNext(), taxDepreciation, valueAdditionOrDeduction);
                projection.getDeductionList().add(deduction);
            } else if (projection.getTaxDepreciation().getNetTaxValue().compareTo(BigDecimal.ZERO) > 0 &&
                    taxDepreciation.getNetTaxValue().compareTo(BigDecimal.ZERO) == 0) {
                valueAdditionOrDeduction = taxDepreciation.getValueDepreciation().subtract(financialDepreciation.getValueDepreciation());
                Deduction deduction = createDeduction(projection.getAccountigPeriodNext(), taxDepreciation, valueAdditionOrDeduction);
                projection.getDeductionList().add(deduction);
            } else {
                valueAdditionOrDeduction = financialDepreciation.getValueDepreciation();
                Addition addition = createAddition(projection.getAccountigPeriodNext(), taxDepreciation, valueAdditionOrDeduction);
                projection.getAdditionList().add(addition);
            }

            projection.setAccountigPeriodNext(projection.getAccountigPeriodNext().plusMonths(1));
            projection.setTaxDepreciation(taxDepreciation);
        }
        projection.setAccountigPeriodNext(projection.getAccountigPeriodNext().plusYears(1).minusMonths(12)); // Ajusta el periodo al siguiente año
        return projection;
    }

    private FinancialDepreciation createFinancialDepreciation(FinancialFixedAsset fixedAsset, LocalDate accountingPeriod) {
        FinancialDepreciation financialDepreciation = new FinancialDepreciation();
        financialDepreciation.setFinancialFixedAsset(fixedAsset);
        financialDepreciation.setAccountingPeriod(accountingPeriod);
        financialDepreciation.calculateDepreciation(fixedAsset);
        financialDepreciation.setAddedBy("CARNOL");
        financialDepreciation.setAdditionDate(LocalDateTime.now());
        return financialDepreciation;
    }

    private TaxDepreciation createTaxDepreciation(FinancialFixedAsset fixedAsset, LocalDate accountingPeriodNext,
                                                  LocalDate accountingPeriod, TaxDepreciation taxDepreciationOld,
                                                  TaxTableSunat taxTable) {
        TaxDepreciation taxDepreciation = new TaxDepreciation();
        taxDepreciation.setFinancialFixedAsset(fixedAsset);
        taxDepreciation.setAccountingPeriod(accountingPeriodNext);
        taxDepreciation.calculateDepreciation(fixedAsset, taxTable, accountingPeriod, taxDepreciationOld);
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
    public List<FinancialFixedAsset> findByFixedAssetAccountingAccountService(String accountingAccount) {
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
    public List<FinancialFixedAsset> findByPeriodAndAccountStartingWithService(LocalDate period, String account) {
        return new ArrayList<>(repository.findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(period, account));
    }

}
