package com.epsgrau.pe.sicodetri.helper;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.model.Depreciation;
import com.epsgrau.pe.sicodetri.model.FixedAssetDetail;
import com.epsgrau.pe.sicodetri.model.InputData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.epsgrau.pe.sicodetri.util.Constants.*;

public class ExcelHelperTest {

    public static ByteArrayInputStream createMockExcelInputStream() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Sheet sheet = workbook.createSheet("CTA33");

        createRowHeaderFile(sheet);
        Row row = sheet.createRow(12);
        row.createCell(0).setCellValue("21.02.010149");
        row.createCell(1).setCellValue("");
        row.createCell(2).setCellValue("01261110039");
        row.createCell(3).setCellValue("TERRENO LAGUNA OXIDACION PAITA");
        row.createCell(4).setCellValue("NINGUNA");
        row.createCell(5).setCellValue("NINGUNO");
        row.createCell(6).setCellValue("/");
        row.createCell(7).setCellValue(0);
        row.createCell(8).setCellValue(0);
        row.createCell(9).setCellValue(0);
        row.createCell(10).setCellValue(0);
        row.createCell(11).setCellValue(0);
        row.createCell(12).setCellValue(0);
        row.createCell(13).setCellValue(0);
        row.createCell(14).setCellValue(0);
        row.createCell(15).setCellValue(0);
        row.createCell(16).setCellValue("26/09/2012");
        row.createCell(17).setCellValue("26/09/2012");
        row.createCell(18).setCellValue("");
        row.createCell(19).setCellValue("LINEA RECTA");
        row.createCell(20).setCellValue("310-1997-1998");
        row.createCell(21).setCellValue(0);
        row.createCell(22).setCellValue(0);
        row.createCell(23).setCellValue(0);
        row.createCell(24).setCellValue(0);
        row.createCell(25).setCellValue(0);
        row.createCell(26).setCellValue(0);
        row.createCell(27).setCellValue(0);
        row.createCell(28).setCellValue(0);
        row.createCell(30).setCellValue(0);

        workbook.write(out);
        workbook.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());

        out.close();

        return bais;
    }

    public static List<InputData> getListInputData() {
        List<InputData> listInputData = new ArrayList<>();
        InputData itemInputData = new InputData();
        itemInputData.setMainFixedAssetCode("21.02.010149");//0
        itemInputData.setFixedAssetCodeComponent("");//1
        itemInputData.setFixedAssetAccountingAccount("01261110039");//2
        FixedAssetDetail itemInputDataDetail = new FixedAssetDetail();
        itemInputDataDetail.setDescription("TERRENO LAGUNA OXIDACION PAITA");//3
        itemInputDataDetail.setFixedAssetBrand("NINGUNA");//4
        itemInputDataDetail.setFixedAssetModel("NINGUNO");//5
        itemInputDataDetail.setFixedAssetSerialNumberAndOrPlate("/");//6
        itemInputData.setFixedAssetDetail(itemInputDataDetail);
        itemInputData.setInitialBalance("0");//7
        itemInputData.setAcquisitionsAdditions("0");//8
        itemInputData.setImprovements("0");//9
        itemInputData.setTransfersAndOrReclassifications("0");//10
        itemInputData.setWithdrawalsAndOrRetirements("0");//11
        itemInputData.setOtherAdjustments("0");//12
        itemInputData.setBookValueOfFixedAssets("0");//13
        itemInputData.setInflationAdjustment("0");//14
        itemInputData.setAdjustedValueOfFixedAssets("0");//15
        itemInputData.setAcquisitionDate("26/09/2012");//16
        itemInputData.setStartDateFixedAssetUsage("26/09/2012");//17
        itemInputData.setRetirementDate("01/01/1900");//18
        Depreciation depreciation = new Depreciation();
        depreciation.setAppliedMethod("LINEA RECTA");//19
        depreciation.setAuthorizationDocumentNumber("310-1997-1998");//20
        itemInputData.setDepreciation(depreciation);
        itemInputData.setUsefulLifeYears("0");//21
        itemInputData.setAccumulatedDepreciationTheEndPrevious("0");//22
        itemInputData.setCurrentDepreciation("0");//23
        itemInputData.setCurrentDepreciationRelatedWithdrawalsRetirements("0");//24
        itemInputData.setCurrentDepreciationRelatedOtherAdjustments("0");//25
        itemInputData.setAccumulatedHistoricalDepreciation("0");//26
        itemInputData.setDepreciationInflationAdjustment("0");//27
        itemInputData.setAccumulatedDepreciationAdjustedInflation("0");//28
        itemInputData.setNetFixedAssets("0");//30
        listInputData.add(itemInputData);
        return listInputData;
    }

    public static List<FinancialFixedAsset> getListFinancialFixedAsset() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER_DD_MM_YYYY);
        List<FinancialFixedAsset> listFinancialFixedAsset = new ArrayList<>();
        FinancialFixedAsset itemInputFinacialFixedAsset = new FinancialFixedAsset();
        itemInputFinacialFixedAsset.setMainFixedAssetCode("21.02.010149");//0
        itemInputFinacialFixedAsset.setFixedAssetCodeComponent("");//1
        itemInputFinacialFixedAsset.setFixedAssetAccountingAccount("01261110039");//2
        itemInputFinacialFixedAsset.setDescription("TERRENO LAGUNA OXIDACION PAITA");//3
        itemInputFinacialFixedAsset.setFixedAssetBrand("NINGUNA");//4
        itemInputFinacialFixedAsset.setFixedAssetModel("NINGUNO");//5
        itemInputFinacialFixedAsset.setFixedAssetSerialNumberAndOrPlate("/");//6
        itemInputFinacialFixedAsset.setInitialBalance(BigDecimal.ZERO);//7
        itemInputFinacialFixedAsset.setAcquisitionsAdditions(BigDecimal.ZERO);//8
        itemInputFinacialFixedAsset.setImprovements(BigDecimal.ZERO);//9
        itemInputFinacialFixedAsset.setTransfersAndOrReclassifications(BigDecimal.ZERO);//10
        itemInputFinacialFixedAsset.setWithdrawalsAndOrRetirements(BigDecimal.ZERO);//11
        itemInputFinacialFixedAsset.setOtherAdjustments(BigDecimal.ZERO);//12
        itemInputFinacialFixedAsset.setBookValueOfFixedAssets(BigDecimal.ZERO);//13
        itemInputFinacialFixedAsset.setInflationAdjustment(BigDecimal.ZERO);//14
        itemInputFinacialFixedAsset.setAdjustedValueOfFixedAssets(BigDecimal.ZERO);//15
        itemInputFinacialFixedAsset.setAcquisitionDate(LocalDate.parse("26/09/2012", formatter));//16
        itemInputFinacialFixedAsset.setStartDateFixedAssetUsage(LocalDate.parse("26/09/2012", formatter));//17
        itemInputFinacialFixedAsset.setRetirementDate(LocalDate.parse("01/01/1900", formatter));//18
        Depreciation depreciation = new Depreciation();
        depreciation.setAppliedMethod("LINEA RECTA");//19
        depreciation.setAuthorizationDocumentNumber("310-1997-1998");//20
        itemInputFinacialFixedAsset.setUsefulLifeYears(D_VAL_ZERO);//21
        itemInputFinacialFixedAsset.setAccumulatedDepreciationTheEndPrevious(BigDecimal.ZERO);//22
        itemInputFinacialFixedAsset.setCurrentDepreciation(BigDecimal.ZERO);//23
        itemInputFinacialFixedAsset.setCurrentDepreciationRelatedWithdrawalsRetirements(BigDecimal.ZERO);//24
        itemInputFinacialFixedAsset.setCurrentDepreciationRelatedOtherAdjustments(BigDecimal.ZERO);//25
        itemInputFinacialFixedAsset.setAccumulatedHistoricalDepreciation(BigDecimal.ZERO);//26
        itemInputFinacialFixedAsset.setDepreciationInflationAdjustment(BigDecimal.ZERO);//27
        itemInputFinacialFixedAsset.setAccumulatedDepreciationAdjustedInflation(BigDecimal.ZERO);//28
        itemInputFinacialFixedAsset.setNetFixedAssets(BigDecimal.ZERO);//30
        listFinancialFixedAsset.add(itemInputFinacialFixedAsset);
        return listFinancialFixedAsset;
    }

    public static FinancialFixedAsset getFinancialFixedAsset() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER_DD_MM_YYYY);
        FinancialFixedAsset itemInputFinacialFixedAsset = new FinancialFixedAsset();
        itemInputFinacialFixedAsset.setMainFixedAssetCode("21.02.010149");//0
        itemInputFinacialFixedAsset.setFixedAssetCodeComponent("");//1
        itemInputFinacialFixedAsset.setFixedAssetAccountingAccount("01261110039");//2
        itemInputFinacialFixedAsset.setDescription("TERRENO LAGUNA OXIDACION PAITA");//3
        itemInputFinacialFixedAsset.setFixedAssetBrand("NINGUNA");//4
        itemInputFinacialFixedAsset.setFixedAssetModel("NINGUNO");//5
        itemInputFinacialFixedAsset.setFixedAssetSerialNumberAndOrPlate("/");//6
        itemInputFinacialFixedAsset.setInitialBalance(BigDecimal.ZERO);//7
        itemInputFinacialFixedAsset.setAcquisitionsAdditions(BigDecimal.ZERO);//8
        itemInputFinacialFixedAsset.setImprovements(BigDecimal.ZERO);//9
        itemInputFinacialFixedAsset.setTransfersAndOrReclassifications(BigDecimal.ZERO);//10
        itemInputFinacialFixedAsset.setWithdrawalsAndOrRetirements(BigDecimal.ZERO);//11
        itemInputFinacialFixedAsset.setOtherAdjustments(BigDecimal.ZERO);//12
        itemInputFinacialFixedAsset.setBookValueOfFixedAssets(BigDecimal.ZERO);//13
        itemInputFinacialFixedAsset.setInflationAdjustment(BigDecimal.ZERO);//14
        itemInputFinacialFixedAsset.setAdjustedValueOfFixedAssets(BigDecimal.ZERO);//15
        itemInputFinacialFixedAsset.setAcquisitionDate(LocalDate.parse("26/09/2012", formatter));//16
        itemInputFinacialFixedAsset.setStartDateFixedAssetUsage(LocalDate.parse("26/09/2012", formatter));//17
        itemInputFinacialFixedAsset.setRetirementDate(LocalDate.parse("01/01/1900", formatter));//18
        Depreciation depreciation = new Depreciation();
        depreciation.setAppliedMethod("LINEA RECTA");//19
        depreciation.setAuthorizationDocumentNumber("310-1997-1998");//20
        itemInputFinacialFixedAsset.setUsefulLifeYears(D_VAL_ZERO);//21
        itemInputFinacialFixedAsset.setAccumulatedDepreciationTheEndPrevious(BigDecimal.ZERO);//22
        itemInputFinacialFixedAsset.setCurrentDepreciation(BigDecimal.ZERO);//23
        itemInputFinacialFixedAsset.setCurrentDepreciationRelatedWithdrawalsRetirements(BigDecimal.ZERO);//24
        itemInputFinacialFixedAsset.setCurrentDepreciationRelatedOtherAdjustments(BigDecimal.ZERO);//25
        itemInputFinacialFixedAsset.setAccumulatedHistoricalDepreciation(BigDecimal.ZERO);//26
        itemInputFinacialFixedAsset.setDepreciationInflationAdjustment(BigDecimal.ZERO);//27
        itemInputFinacialFixedAsset.setAccumulatedDepreciationAdjustedInflation(BigDecimal.ZERO);//28
        itemInputFinacialFixedAsset.setNetFixedAssets(BigDecimal.ZERO);//30
        return itemInputFinacialFixedAsset;
    }

    public static void createRowHeaderFile(Sheet sheet) {
        for (int i = 0; i<12; i++) {
            Row row = sheet.createRow(i);
        }
    }

}
