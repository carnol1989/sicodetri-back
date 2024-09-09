package com.epsgrau.pe.sicodetri.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FinancialFixedAssetDto {

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
    private Integer usefulLifeYears;
    private String annualRate;
    private BigDecimal annualAccumulatedDepreciationPriorPeriod;
    private BigDecimal currentDepreciation;
    private BigDecimal currentDepreciationRelatedWithdrawalsRetirements;
    private BigDecimal currentDepreciationRelatedOtherAdjustments;
    private BigDecimal historicalAccumulatedDepreciation;
    private BigDecimal netFixedAssets;
    
}
