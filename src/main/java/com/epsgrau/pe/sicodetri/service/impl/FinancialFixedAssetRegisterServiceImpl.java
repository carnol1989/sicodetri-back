package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.model.Depreciation;
import com.epsgrau.pe.sicodetri.model.FixedAssetDetail;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.service.FinacialFixedAssetRegisterService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelper.getCellType;

@Service
public class FinancialFixedAssetRegisterServiceImpl implements FinacialFixedAssetRegisterService {

    static String[] HEADERS = {"CÓDIGO ACTIVO FIJO PRINCIPAL",	"CÓDIGO ACTIVO FIJO COMPONENTE",	"CUENTA CONTABLE DEL ACTIVO FIJO",
            "DESCRIPCIÓN",	"MARCA DEL ACTIVO FIJO",	"MODELO DEL ACTIVO FIJO",	"NÚMERO DE SERIE Y/O PLACA DEL ACTIVO FIJO",
            "SALDO INICIAL AL 31.12.2020",	"ADQUISICIONES ADICIONES",	"MEJORAS",	"TRANSFERENCIAS Y/O RECLASIFICACIONES",
            "RETIROS Y/O BAJAS",	"OTROS AJUSTES",	"VALOR EN LIBROS DEL ACTIVO FIJO AL 31.12.2022",	"AJUSTE POR INFLACIÓN",
            "VALOR AJUSTADO DEL ACTIVO FIJO AL 31.12.2022",	"FECHA DE ADQUISICIÓN",	"FECHA DE INICIO DEL USO DEL ACTIVO FIJO",
            "FECHA DE RETIRO", "MÉTODO APLICADO",	"N° DE DOCUMENTO DE AUTORIZACIÓN",	"AÑOS VIDA UTIL", "DEPRECIACIÓN DE ACUMULADA AL AL CIERRE DEL EJERCICIO ANTERIOR",
            "DEPRECIACIÓN DEL EJERCICIO", "DEPRECIACIÓN RELACIONADA CON LOS RETIROS Y/O BAJAS", "DEPRECIACIÓN DEL EJERCICIO RELACIONADA CON OTROS AJUSTES",
            "DEPRECIACIÓN ACUMULADA HISTÓRICA", "AJUSTE POR INFLACIÓN DE LA DEPRECIACIÓN",	"DEPRECIACIÓN ACUMULADA AJUSTADA POR INFLACIÓN",
            "", "ACTIVO FIJO NETO"};

    //static String SHEET = "Hoja1";

    @Override
    public List<InputData> loadExcelFileToObject(MultipartFile file) {
        List<InputData> listInputData = new ArrayList<InputData>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
            System.out.println("physicalNumberOfRows: " + physicalNumberOfRows);

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                int rowNum = currentRow.getRowNum();
                System.out.println("rowNum: " + rowNum);

                // skip header
                if (rowNum > 11 && (rowNum < (physicalNumberOfRows - 1))) {
                    Iterator<Cell> cellsInRow = currentRow.iterator();

                    InputData itemInputData = new InputData();
                    FixedAssetDetail itemInputDataDetail = new FixedAssetDetail();
                    Depreciation itemInputDataDepreciation = new Depreciation();

                    while (cellsInRow.hasNext()) {
                        Cell currentCell = cellsInRow.next();
                        int columnIndex = currentCell.getColumnIndex();
                        System.out.println("columnIndex: " + columnIndex);

                        switch (columnIndex) {
                            case 0:
                                itemInputData.setMainFixedAssetCode(String.valueOf(getCellType(currentCell)));
                                break;

                            case 1:
                                itemInputData.setFixedAssetCodeComponent(String.valueOf(getCellType(currentCell)));
                                break;

                            case 2:
                                itemInputData.setFixedAssetAccountingAccount(String.valueOf(getCellType(currentCell)));
                                break;

                            case 3:
                                itemInputDataDetail.setDescription(String.valueOf(getCellType(currentCell)));
                                itemInputData.setFixedAssetDetail(itemInputDataDetail);
                                break;

                            case 4:
                                itemInputDataDetail.setFixedAssetBrand(String.valueOf(getCellType(currentCell)));
                                itemInputData.setFixedAssetDetail(itemInputDataDetail);
                                break;

                            case 5:
                                itemInputDataDetail.setFixedAssetModel(String.valueOf(getCellType(currentCell)));
                                itemInputData.setFixedAssetDetail(itemInputDataDetail);
                                break;

                            case 6:
                                itemInputDataDetail.setFixedAssetSerialNumberAndOrPlate(String.valueOf(getCellType(currentCell)));
                                itemInputData.setFixedAssetDetail(itemInputDataDetail);
                                break;

                            case 7:
                                itemInputData.setInitialBalance(String.valueOf(getCellType(currentCell)));
                                break;

                            case 8:
                                itemInputData.setAcquisitionsAdditions(String.valueOf(getCellType(currentCell)));
                                break;

                            case 9:
                                itemInputData.setImprovements(String.valueOf(getCellType(currentCell)));
                                break;

                            case 10:
                                itemInputData.setTransfersAndOrReclassifications(String.valueOf(getCellType(currentCell)));
                                break;

                            case 11:
                                itemInputData.setWithdrawalsAndOrRetirements(String.valueOf(getCellType(currentCell)));
                                break;

                            case 12:
                                itemInputData.setOtherAdjustments(String.valueOf(getCellType(currentCell)));
                                break;

                            case 13:
                                itemInputData.setBookValueOfFixedAssets(String.valueOf(getCellType(currentCell)));
                                break;

                            case 14:
                                itemInputData.setInflationAdjustment(String.valueOf(getCellType(currentCell)));

                            case 15:
                                itemInputData.setAdjustedValueOfFixedAssets(String.valueOf(getCellType(currentCell)));
                                break;

                            case 16:
                                itemInputData.setAcquisitionDate(String.valueOf(getCellType(currentCell)));
                                break;

                            case 17:
                                itemInputData.setStartDateFixedAssetUsage(String.valueOf(getCellType(currentCell)));
                                break;

                            case 18:
                                itemInputData.setRetirementDate(String.valueOf(getCellType(currentCell)));
                                break;

                            case 19:
                                itemInputDataDepreciation.setAppliedMethod(String.valueOf(getCellType(currentCell)));
                                itemInputData.setDepreciation(itemInputDataDepreciation);
                                break;

                            case 20:
                                itemInputDataDepreciation.setAuthorizationDocumentNumber(String.valueOf(getCellType(currentCell)));
                                itemInputData.setDepreciation(itemInputDataDepreciation);
                                break;

                            case 21:
                                itemInputData.setUsefulLifeYears(String.valueOf(getCellType(currentCell)));
                                break;

                            case 22:
                                itemInputData.setAccumulatedDepreciationTheEndPrevious(String.valueOf(getCellType(currentCell)));
                                break;

                            case 23:
                                itemInputData.setCurrentDepreciation(String.valueOf(getCellType(currentCell)));
                                break;

                            case 24:
                                itemInputData.setCurrentDepreciationRelatedWithdrawalsRetirements(String.valueOf(getCellType(currentCell)));
                                break;

                            case 25:
                                itemInputData.setCurrentDepreciationRelatedOtherAdjustments(String.valueOf(getCellType(currentCell)));
                                break;

                            case 26:
                                itemInputData.setAccumulatedHistoricalDepreciation(String.valueOf(getCellType(currentCell)));

                            case 27:
                                itemInputData.setDepreciationInflationAdjustment(String.valueOf(getCellType(currentCell)));
                                break;

                            case 28:
                                itemInputData.setAccumulatedDepreciationAdjustedInflation(String.valueOf(getCellType(currentCell)));
                                break;

                            case 30:
                                itemInputData.setNetFixedAssets(String.valueOf(String.valueOf(getCellType(currentCell))));
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
}
