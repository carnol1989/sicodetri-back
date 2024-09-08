package com.epsgrau.pe.sicodetri.util;

import com.epsgrau.pe.sicodetri.entity.*;
import com.epsgrau.pe.sicodetri.model.Depreciation;
import com.epsgrau.pe.sicodetri.model.FixedAssetDetail;
import com.epsgrau.pe.sicodetri.model.InputData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.epsgrau.pe.sicodetri.util.ConstantsTest.*;

/**
 * Util class for test data. <br/>
 * <b>Class</b>: {@link DataTest}<br/>
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
 *     <li>Ago 27, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public class DataTest {

    public static List<InputData> getListInputData() {
        List<InputData> listInputData = new ArrayList<>();
        InputData itemInputData = new InputData();
        itemInputData.setAccountingPeriod("2022-01-01");
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
        itemInputData.setAcquisitionDate("2012-09-26");//16
        itemInputData.setStartDateFixedAssetUsage("2012-09-26");//17
        itemInputData.setRetirementDate("1900-01-01");//18
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
        List<FinancialFixedAsset> financialFixedAssets = new ArrayList<>();
        financialFixedAssets.add(getFinancialFixedAsset012());
        financialFixedAssets.add(getFinancialFixedAsset332());
        return financialFixedAssets;
    }

    public static List<FinancialFixedAsset> getListFinancialFixedAsset012() {
        List<FinancialFixedAsset> listFinancialFixedAsset = new ArrayList<>();
        listFinancialFixedAsset.add(getFinancialFixedAsset012());
        return listFinancialFixedAsset;
    }

    public static FinancialFixedAsset getFinancialFixedAsset012() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER_DD_MM_YYYY);
        FinancialFixedAsset itemInputFinacialFixedAsset = new FinancialFixedAsset();
        itemInputFinacialFixedAsset.setAccountingPeriod(LocalDate.parse("2022-01-01"));
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

    public static List<FinancialFixedAsset> getListFinancialFixedAsset332() {
        List<FinancialFixedAsset> financialFixedAssets = new ArrayList<>();
        financialFixedAssets.add(getFinancialFixedAsset332());
        return financialFixedAssets;
    }

    public static FinancialFixedAsset getFinancialFixedAsset332() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD);
        FinancialFixedAsset itemInputFinacialFixedAsset = new FinancialFixedAsset();
        itemInputFinacialFixedAsset.setAccountingPeriod(LocalDate.parse("2022-01-01"));
        itemInputFinacialFixedAsset.setMainFixedAssetCode("22.01.010862");//0
        itemInputFinacialFixedAsset.setFixedAssetCodeComponent("");//1
        itemInputFinacialFixedAsset.setFixedAssetAccountingAccount("3321161001");//2
        itemInputFinacialFixedAsset.setDescription("CONSTRUCCION DE EDIFICIO PTAP CURUMUY");//3
        itemInputFinacialFixedAsset.setFixedAssetBrand("NINGUNA");//4
        itemInputFinacialFixedAsset.setFixedAssetModel("NINGUNO");//5
        itemInputFinacialFixedAsset.setFixedAssetSerialNumberAndOrPlate("/");//6
        itemInputFinacialFixedAsset.setInitialBalance(new BigDecimal("27493700.65"));//7
        itemInputFinacialFixedAsset.setAcquisitionsAdditions(BigDecimal.ZERO);//8
        itemInputFinacialFixedAsset.setImprovements(BigDecimal.ZERO);//9
        itemInputFinacialFixedAsset.setTransfersAndOrReclassifications(BigDecimal.ZERO);//10
        itemInputFinacialFixedAsset.setWithdrawalsAndOrRetirements(BigDecimal.ZERO);//11
        itemInputFinacialFixedAsset.setOtherAdjustments(BigDecimal.ZERO);//12
        itemInputFinacialFixedAsset.setBookValueOfFixedAssets(BigDecimal.ZERO);//13
        itemInputFinacialFixedAsset.setInflationAdjustment(BigDecimal.ZERO);//14
        itemInputFinacialFixedAsset.setAdjustedValueOfFixedAssets(new BigDecimal("27493700.65"));//15
        itemInputFinacialFixedAsset.setAcquisitionDate(LocalDate.parse("2007-12-31", formatter));//16
        itemInputFinacialFixedAsset.setStartDateFixedAssetUsage(LocalDate.parse("2007-12-31", formatter));//17
        itemInputFinacialFixedAsset.setRetirementDate(LocalDate.parse("1900-01-01", formatter));//18
        Depreciation depreciation = new Depreciation();
        depreciation.setAppliedMethod("LINEA RECTA");//19
        depreciation.setAuthorizationDocumentNumber("1");//20
        itemInputFinacialFixedAsset.setUsefulLifeYears(30.00);//21
        itemInputFinacialFixedAsset.setAccumulatedDepreciationTheEndPrevious(new BigDecimal("12830393.57"));//22
        itemInputFinacialFixedAsset.setCurrentDepreciation(new BigDecimal("916456.68"));//23
        itemInputFinacialFixedAsset.setCurrentDepreciationRelatedWithdrawalsRetirements(BigDecimal.ZERO);//24
        itemInputFinacialFixedAsset.setCurrentDepreciationRelatedOtherAdjustments(BigDecimal.ZERO);//25
        itemInputFinacialFixedAsset.setAccumulatedHistoricalDepreciation(new BigDecimal("13746850.25"));//26
        itemInputFinacialFixedAsset.setDepreciationInflationAdjustment(BigDecimal.ZERO);//27
        itemInputFinacialFixedAsset.setAccumulatedDepreciationAdjustedInflation(new BigDecimal("13746850.25"));//28
        itemInputFinacialFixedAsset.setNetFixedAssets(new BigDecimal("13746850.40"));//30
        return itemInputFinacialFixedAsset;
    }

    public static List<TaxTableSunat> getListTableSunats() {
        List<TaxTableSunat> taxTableSunats = new ArrayList<>();
        taxTableSunats.add(getTaxTable331());
        taxTableSunats.add(getTaxTable332());
        return taxTableSunats;
    }

    public static TaxTableSunat getTaxTable332() {
        TaxTableSunat taxTable = new TaxTableSunat();
        taxTable.setId(1L);
        taxTable.setAccountingAccount("332");
        taxTable.setDescription("Edificaciones y Otras Construcciones");
        taxTable.setAnnualRate(5);
        taxTable.setStatus("S");
        return taxTable;
    }

    public static TaxTableSunat getTaxTable331() {
        TaxTableSunat taxTable = new TaxTableSunat();
        taxTable.setId(2L);
        taxTable.setAccountingAccount("331");
        taxTable.setDescription("Terrenos");
        taxTable.setAnnualRate(0);
        taxTable.setStatus("S");
        return taxTable;
    }

    public static List<FinancialDepreciation> getListFinancialDepreciations() {
        List<FinancialDepreciation> financialDepreciations = new ArrayList<>();
        return financialDepreciations;
    }

    public static List<TaxDepreciation> getListTaxDepreciations() {
        List<TaxDepreciation> taxDepreciations = new ArrayList<>();
        return taxDepreciations;
    }

    public static TaxDepreciation getTaxDepreciation() {
        TaxDepreciation taxDepreciation = new TaxDepreciation();
        taxDepreciation.setId(1L);
        taxDepreciation.setAccountingPeriod(LocalDate.parse("2022-01-01"));
        taxDepreciation.setAnnualAccumulatedDepreciationPriorPeriod(new BigDecimal("19245590.46"));
        taxDepreciation.setAnnualNetTaxValuePriorPeriod(new BigDecimal("8248110.195"));
        taxDepreciation.setValueDepreciation(new BigDecimal("114557.086"));
        taxDepreciation.setNetTaxValue(new BigDecimal("8133553.109"));
        return taxDepreciation;
    }

}
