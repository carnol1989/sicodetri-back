package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.model.Depreciation;
import com.epsgrau.pe.sicodetri.model.FixedAssetDetail;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetRepository;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelper.getValueCell;
import static com.epsgrau.pe.sicodetri.util.Constants.*;

@Service
public class FinancialFixedAssetServiceImpl implements FinancialFixedAssetService {

    @Autowired
    private FinancialFixedAssetRepository repository;

    @Override
    public List<InputData> loadExcelFileToObject(MultipartFile file) {
        List<InputData> listInputData = new ArrayList<InputData>();
        try {
            if (file.isEmpty() || file.getInputStream().available() == 0) {
                return listInputData;
            }

            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
            System.out.println("physicalNumberOfRows: " + physicalNumberOfRows);

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                int rowNum = currentRow.getRowNum();

                // skip header
                if (rowNum > 11 && (rowNum < physicalNumberOfRows)) {
                    System.out.println("rowNum: " + rowNum);

                    Iterator<Cell> cellsInRow = currentRow.iterator();

                    InputData itemInputData = new InputData();
                    FixedAssetDetail itemInputDataDetail = new FixedAssetDetail();
                    Depreciation itemInputDataDepreciation = new Depreciation();

                    while (cellsInRow.hasNext()) {
                        Cell currentCell = cellsInRow.next();
                        int columnIndex = currentCell.getColumnIndex();
                        System.out.println("columnIndex: " + columnIndex);
                        System.out.println("cellType: " + currentCell.getCellType() + "\tvalueCurrentCell: " + String.valueOf(getValueCell(currentCell)));
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
                                itemInputData.setAcquisitionDate(acquisitionDate);
                                break;

                            case 17:
                                String startDateFixedAssetUsage = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_DATE_DEFAULT);
                                itemInputData.setStartDateFixedAssetUsage(startDateFixedAssetUsage);
                                break;

                            case 18:
                                String retirementDate = (getValueCell(currentCell) != null ? String.valueOf(getValueCell(currentCell)) : S_DATE_DEFAULT);
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
    public void saveAllService(LocalDate accountingPeriod, List<InputData> listInputData) {
        List<FinancialFixedAsset> list = getListFinancialFixedAsset(accountingPeriod, listInputData);
        repository.saveAll(list);
    }

    public static List<FinancialFixedAsset> getListFinancialFixedAsset(LocalDate accountingPeriod, List<InputData> listInputData) {
        List<FinancialFixedAsset> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER_DD_MM_YYYY);
        int row = 0;
        for (InputData itemInputData : listInputData) {
            System.out.println("row: "+ row);
            System.out.println(itemInputData.toString());

            FinancialFixedAsset itemFinancialFixedAsset = new FinancialFixedAsset();

            LocalDate accountPeriodAux = (accountingPeriod != null ? accountingPeriod : LOCAL_DATE_DEFAULT);
            itemFinancialFixedAsset.setAccountingPeriod(accountPeriodAux);

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
    public FinancialFixedAsset findByIdService(Long id) {
        Optional<FinancialFixedAsset> optionalFinancialFixedAsset = repository.findById(id);
        return optionalFinancialFixedAsset.orElseGet(FinancialFixedAsset::new);
    }

    @Override
    public List<FinancialFixedAsset> findAllService() {
        return new ArrayList<>(repository.findAll());
    }

    @Override
    public List<FinancialFixedAsset> findByPeriodService(LocalDate period) {
        return new ArrayList<>(repository.findByAccountingPeriod(period));
    }

}
