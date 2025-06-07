package com.epsgrau.pe.sicodetri.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class dto for financial fixed asset. <br/>
 * <b>Class</b>: {@link FinancialFixedAssetDto}<br/>
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
 *     <li>Sep 08, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FinancialFixedAssetDto {

    private Integer id;
    private LocalDate accountingPeriod;
    private String mainFixedAssetCode;
    private String fixedAssetCodeComponent;
    private String fixedAssetAccountingAccount;
    private String description;
    private String fixedAssetBrand;
    private String fixedAssetModel;
    private String fixedAssetSerialNumberAndOrPlate;
    private BigDecimal initialBalance;
    private BigDecimal revaluedAmount;
    private BigDecimal acquisitionsAdditions;
    private BigDecimal withdrawalsAndOrRetirements;
    private BigDecimal otherAdjustments;
    private BigDecimal bookValueOfFixedAssets;
    private LocalDate acquisitionDate;
    private LocalDate startDateFixedAssetUsage;
    private LocalDate retirementDate;
    private String appliedMethod;
    private String authorizationDocumentNumber;
    private Integer usefulLifeYearsFinancial;
    private String annualRateFinancial;
    private Integer usefulLifeYearsTax;
    private String annualRateTax;
    private BigDecimal annualAccumulatedDepreciationPriorPeriod;
    private BigDecimal currentDepreciation;
    private BigDecimal currentDepreciationRelatedWithdrawalsRetirements;
    private BigDecimal currentDepreciationRelatedOtherAdjustments;
    private BigDecimal historicalAccumulatedDepreciation;
    private BigDecimal valueDepreciationFinancial;
    private BigDecimal valueDepreciationTax;
    private BigDecimal annualNetTaxValuePriorPeriod;
    private BigDecimal netTaxValue;
    private BigDecimal valueAddition;
    private BigDecimal valueDeduction;
    private BigDecimal netFixedAssets;

    private String errorCode;
    private String errorMessage;

}
