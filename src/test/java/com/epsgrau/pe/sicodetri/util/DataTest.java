package com.epsgrau.pe.sicodetri.util;

import static com.epsgrau.pe.sicodetri.util.ConstantsTest.D_VAL_ZERO;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.FORMATTER_DD_MM_YYYY;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.FORMATTER_YYYY_MM_DD;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.model.Depreciation;
import com.epsgrau.pe.sicodetri.model.FixedAssetDetail;
import com.epsgrau.pe.sicodetri.model.InputData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.type.ModeEnum;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * Utility class providing sample data objects for unit tests.
 *
 * <p>
 * Contains factory methods to create pre-configured instances of data transfer
 * and entity classes, covering various scenarios for testing service logic,
 * report generation, and Excel file processing.
 * </p>
 *
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public class DataTest {

    /**
     * Creates a list of {@link InputData} populated with default test values.
     *
     * @return non-null List of sample InputData objects
     */
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
        itemInputData.setUsername("katherine_gutierrez");
        listInputData.add(itemInputData);
        return listInputData;
    }

    /**
     * Returns a list of two sample FinancialFixedAsset instances (codes 012 and 332).
     *
     * @return non-null List of FinancialFixedAsset
     */
    public static List<FinancialFixedAsset> getListFinancialFixedAsset() {
        List<FinancialFixedAsset> financialFixedAssets = new ArrayList<>();
        financialFixedAssets.add(getFinancialFixedAsset012());
        financialFixedAssets.add(getFinancialFixedAsset332());
        return financialFixedAssets;
    }

    /**
     * Returns a list containing the sample FinancialFixedAsset with code 012.
     *
     * @return non-null List containing one FinancialFixedAsset
     */
    public static List<FinancialFixedAsset> getListFinancialFixedAsset012() {
        List<FinancialFixedAsset> listFinancialFixedAsset = new ArrayList<>();
        listFinancialFixedAsset.add(getFinancialFixedAsset012());
        return listFinancialFixedAsset;
    }

    /**
     * Creates a sample FinancialFixedAsset for code 012 with zero balances and default dates.
     *
     * @return pre-configured FinancialFixedAsset instance
     */
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
        itemInputFinacialFixedAsset.setAddedBy("katherine_gutierrez");
        return itemInputFinacialFixedAsset;
    }

    /**
     * Returns a list containing the sample FinancialFixedAsset with code 332.
     *
     * @return non-null List containing one FinancialFixedAsset
     */
    public static List<FinancialFixedAsset> getListFinancialFixedAsset332() {
        List<FinancialFixedAsset> financialFixedAssets = new ArrayList<>();
        financialFixedAssets.add(getFinancialFixedAsset332());
        return financialFixedAssets;
    }

    /**
     * Creates a sample FinancialFixedAsset for code 332 used in general tests.
     *
     * @return pre-configured FinancialFixedAsset instance
     */
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
        itemInputFinacialFixedAsset.setHistoricalCost(BigDecimal.ZERO);
        itemInputFinacialFixedAsset.setRevaluedAmount(BigDecimal.ZERO);
        itemInputFinacialFixedAsset.setAddedBy("katherine_gutierrez");
        return itemInputFinacialFixedAsset;
    }

    /**
     * Creates a sample FinancialFixedAsset with code 332 for temporary addition projections.
     *
     * @return pre-configured FinancialFixedAsset instance
     */
    public static FinancialFixedAsset getFinancialFixedAsset332ForTemporaryAddition() {
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
        itemInputFinacialFixedAsset.setHistoricalCost(BigDecimal.ZERO);
        itemInputFinacialFixedAsset.setRevaluedAmount(BigDecimal.ZERO);
        itemInputFinacialFixedAsset.setWellAppraisedPreInvestmentStudyDry("");
        return itemInputFinacialFixedAsset;
    }

    /**
     * Creates a sample FinancialFixedAsset with code 332 for permanent addition projections.
     *
     * @return pre-configured FinancialFixedAsset instance
     */
    public static FinancialFixedAsset getFinancialFixedAsset332ForPermanentAddition() {
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
        itemInputFinacialFixedAsset.setHistoricalCost(new BigDecimal("20143512.0000"));
        itemInputFinacialFixedAsset.setRevaluedAmount(new BigDecimal("7350188.6500"));
        itemInputFinacialFixedAsset.setWellAppraisedPreInvestmentStudyDry("tasacion 2013");
        return itemInputFinacialFixedAsset;
    }

    /**
     * Returns a list of two sample TaxTableSunat entries (accounts 331 and 332).
     *
     * @return non-null List of TaxTableSunat
     */
    public static List<TaxTableSunat> getListTableSunats() {
        List<TaxTableSunat> taxTableSunats = new ArrayList<>();
        taxTableSunats.add(getTaxTable331());
        taxTableSunats.add(getTaxTable332());
        return taxTableSunats;
    }

    /**
     * Creates a sample TaxTableSunat for account 332 with sample rates and life years.
     *
     * @return pre-configured TaxTableSunat instance
     */
    public static TaxTableSunat getTaxTable332() {
        TaxTableSunat taxTable = new TaxTableSunat();
        taxTable.setId(1L);
        taxTable.setAccountingAccount("332");
        taxTable.setDescription("Edificaciones y Otras Construcciones");
        taxTable.setAnnualRate(5);
        taxTable.setUsefulLifeYears(20);
        taxTable.setStatus("S");
        taxTable.setAddedBy("katherine_gutierrez");
        return taxTable;
    }

    /**
     * Creates a sample TaxTableSunat for account 331 with sample rates.
     *
     * @return pre-configured TaxTableSunat instance
     */
    public static TaxTableSunat getTaxTable331() {
        TaxTableSunat taxTable = new TaxTableSunat();
        taxTable.setId(2L);
        taxTable.setAccountingAccount("331");
        taxTable.setDescription("Terrenos");
        taxTable.setAnnualRate(0);
        taxTable.setStatus("S");
        taxTable.setAddedBy("katherine_gutierrez");
        return taxTable;
    }

    /**
     * Returns an empty list of FinancialDepreciation for tests.
     *
     * @return empty List of FinancialDepreciation
     */
    public static List<FinancialDepreciation> getListFinancialDepreciations() {
        List<FinancialDepreciation> financialDepreciations = new ArrayList<>();
        return financialDepreciations;
    }

    /**
     * Returns an empty list of TaxDepreciation for tests.
     *
     * @return empty List of TaxDepreciation
     */
    public static List<TaxDepreciation> getListTaxDepreciations() {
        List<TaxDepreciation> taxDepreciations = new ArrayList<>();
        return taxDepreciations;
    }

    /**
     * Creates a sample TaxDepreciation populated with default financial values.
     *
     * @return pre-configured TaxDepreciation instance
     */
    public static TaxDepreciation getTaxDepreciation() {
        TaxDepreciation taxDepreciation = new TaxDepreciation();
        taxDepreciation.setId(1L);
        taxDepreciation.setAccountingPeriod(LocalDate.parse("2022-01-01"));
        taxDepreciation.setAnnualAccumulatedDepreciationPriorPeriod(new BigDecimal("19245590.46"));
        taxDepreciation.setAnnualNetTaxValuePriorPeriod(new BigDecimal("8248110.195"));
        taxDepreciation.setValueDepreciation(new BigDecimal("114557.086"));
        taxDepreciation.setNetTaxValue(new BigDecimal("8133553.109"));
        taxDepreciation.setAddedBy("katherine_gutierrez");
        return taxDepreciation;
    }

    /**
     * Builds a JasperPrint with one page and a test text element, for report tests.
     *
     * @return JasperPrint containing a single text element
     */
    public static JasperPrint createJasperPrintWithOneEmptyPage() {
        // 1) Creamos el JasperPrint
        JasperPrint jasperPrint = new JasperPrint();
        jasperPrint.setName("Prueba con texto");

        // 2) Añadimos una página vacía (JRBasePrintPage)
        JRBasePrintPage page = new JRBasePrintPage();
        jasperPrint.addPage(page);

        // 3) Creamos un elemento JRPrintText y lo configuramos
        JRBasePrintText text = new JRBasePrintText(null);
        text.setX(0);
        text.setY(0);
        text.setWidth(200);
        text.setHeight(20);
        text.setForecolor(java.awt.Color.BLACK);
        text.setBackcolor(java.awt.Color.WHITE);
        text.setMode(ModeEnum.OPAQUE);
        text.setText("TEXTO DE PRUEBA");

        // 4) Lo agregamos a la página
        page.addElement(text);

        return jasperPrint;
    }

    /**
     * Creates a sample FinancialDepreciation for report and projection tests.
     *
     * @return pre-configured FinancialDepreciation instance
     */
    public static FinancialDepreciation getFinancialDepreciation() {
        FinancialDepreciation financialDepreciation = new FinancialDepreciation();
        financialDepreciation.setId(1L);
        financialDepreciation.setAccountingPeriod(LocalDate.parse("2022-01-01"));
        financialDepreciation.setAddedBy("CARNOL");
        financialDepreciation.setAdditionDate(LocalDateTime.now());
        financialDepreciation.setModifiedBy("CARNOL");
        financialDepreciation.setModificationDate(LocalDateTime.now());
        financialDepreciation.setAnnualFinancialRate(BigDecimal.valueOf(0.0333));
        financialDepreciation.setValueDepreciation(BigDecimal.valueOf(76371.3900));
        financialDepreciation.setFinancialFixedAsset(getFinancialFixedAsset332());
        return financialDepreciation;
    }

    /**
     * Creates a sample Addition entity for tests of addition services.
     *
     * @return pre-configured Addition instance
     */
    public static Addition getAddition() {
        Addition addition = new Addition();
        addition.setId(1L);
        addition.setAccountingPeriod(LocalDate.parse("2028-01-01"));
        addition.setAddedBy("CARNOL");
        addition.setAdditionDate(LocalDateTime.now());
        addition.setModifiedBy("TEST");
        addition.setModificationDate(LocalDateTime.now());
        addition.setValueAddition(BigDecimal.valueOf(76371.390000000000000));
        addition.setTaxDepreciation(getTaxDepreciation());
        return addition;
    }

    /**
     * Creates a sample Deduction entity for tests of deduction services.
     *
     * @return pre-configured Deduction instance
     */
    public static Deduction getDeduction() {
        Deduction deduction = new Deduction();
        deduction.setId(1L);
        deduction.setAccountingPeriod(LocalDate.parse("2022-01-01"));
        deduction.setAddedBy("CARNOL");
        deduction.setAdditionDate(LocalDateTime.now());
        deduction.setModifiedBy("TEST");
        deduction.setModificationDate(LocalDateTime.now());
        deduction.setValueDeduction(BigDecimal.valueOf(38185.70));
        deduction.setTaxDepreciation(getTaxDepreciation());
        return deduction;
    }

    /**
     * Creates a sample FinancialFixedAssetDto for report strategy tests.
     *
     * @return pre-configured FinancialFixedAssetDto instance
     */
    public static FinancialFixedAssetDto getFinancialFixedAssetDto() {
        FinancialFixedAssetDto dto = new FinancialFixedAssetDto();
        dto.setId(77);
        dto.setMainFixedAssetCode("MFC77");
        dto.setFixedAssetAccountingAccount("3321161001");
        dto.setNetFixedAssets(new BigDecimal("12345.67"));
        dto.setErrorCode("E01");
        dto.setErrorMessage("Mensaje de error");
        return dto;
    }

    public static Authentication auth(String user, String role) {
        TestingAuthenticationToken t = new TestingAuthenticationToken(user, "pwd", role);
        t.setAuthenticated(true);
        return t;
    }

}
