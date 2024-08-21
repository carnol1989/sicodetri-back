package com.epsgrau.pe.sicodetri.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class is a model for mapping data of excel file. <br/>
 * <b>Class</b>: {@link InputData}<br/>
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
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputData {

    private String mainFixedAssetCode;//0
    private String fixedAssetCodeComponent;//1
    private String fixedAssetAccountingAccount;//2
    private FixedAssetDetail fixedAssetDetail;//3 al 6
    private String initialBalance;//7
    private String acquisitionsAdditions;//8
    private String improvements;//9
    private String transfersAndOrReclassifications;//10
    private String withdrawalsAndOrRetirements;//11
    private String otherAdjustments;//12
    private String bookValueOfFixedAssets;//13
    private String inflationAdjustment;//14
    private String adjustedValueOfFixedAssets;//15
    private String acquisitionDate;//16
    private String startDateFixedAssetUsage;//17
    private String retirementDate;//18
    private Depreciation depreciation;//19 y 20
    private String usefulLifeYears;//21
    private String accumulatedDepreciationTheEndPrevious;//22
    private String currentDepreciation;//23
    private String currentDepreciationRelatedWithdrawalsRetirements;//24
    private String currentDepreciationRelatedOtherAdjustments;//25
    private String accumulatedHistoricalDepreciation;//26
    private String depreciationInflationAdjustment;//27
    private String accumulatedDepreciationAdjustedInflation;//28
    private String netFixedAssets;//30

}
