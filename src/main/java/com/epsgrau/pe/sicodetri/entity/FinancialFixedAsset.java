package com.epsgrau.pe.sicodetri.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class entity for Financial Fixed Asset. <br/>
 * <b>Class</b>: {@link FinancialFixedAsset}<br/>
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
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "financial_fixed_asset")
public class FinancialFixedAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate accountingPeriod;

    @Column
    private String mainFixedAssetCode;//0

    @Column
    private String fixedAssetCodeComponent;//1

    @Column
    private String fixedAssetAccountingAccount;//2

    @Column
    private String description;//3

    @Column
    private String fixedAssetBrand;//4

    @Column
    private String fixedAssetModel;//5

    @Column
    private String fixedAssetSerialNumberAndOrPlate;//6

    @Column(precision = 20, scale = 4)
    private BigDecimal initialBalance;//7

    @Column(precision = 20, scale = 4)
    private BigDecimal acquisitionsAdditions;//8

    @Column(precision = 20, scale = 4)
    private BigDecimal improvements;//9

    @Column(precision = 20, scale = 4)
    private BigDecimal transfersAndOrReclassifications;//10

    @Column(precision = 20, scale = 4)
    private BigDecimal withdrawalsAndOrRetirements;//11

    @Column(precision = 20, scale = 4)
    private BigDecimal otherAdjustments;//12

    @Column(precision = 20, scale = 4)
    private BigDecimal bookValueOfFixedAssets;//13

    @Column(precision = 20, scale = 4)
    private BigDecimal inflationAdjustment;//14

    @Column(precision = 20, scale = 4)
    private BigDecimal adjustedValueOfFixedAssets;//15

    @Column
    private LocalDate acquisitionDate;//16

    @Column
    private LocalDate startDateFixedAssetUsage;//17

    @Column
    private LocalDate retirementDate;//18

    @Column
    private String appliedMethod;//19

    @Column
    private String authorizationDocumentNumber;//20

    @Column
    private Double usefulLifeYears;//21

    @Column(precision = 20, scale = 4)
    private BigDecimal accumulatedDepreciationTheEndPrevious;//22

    @Column(precision = 20, scale = 4)
    private BigDecimal currentDepreciation;//23

    @Column(precision = 20, scale = 4)
    private BigDecimal currentDepreciationRelatedWithdrawalsRetirements;//24

    @Column(precision = 20, scale = 4)
    private BigDecimal currentDepreciationRelatedOtherAdjustments;//25

    @Column(precision = 20, scale = 4)
    private BigDecimal accumulatedHistoricalDepreciation;//26

    @Column(precision = 20, scale = 4)
    private BigDecimal depreciationInflationAdjustment;//27

    @Column(precision = 20, scale = 4)
    private BigDecimal accumulatedDepreciationAdjustedInflation;//28

    @Column(precision = 20, scale = 4)
    private BigDecimal netFixedAssets;//30

    @OneToMany(mappedBy = "financialFixedAsset", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<FinancialDepreciation> depreciacionFinancieraList;

    @OneToMany(mappedBy = "financialFixedAsset", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<TaxDepreciation> depreciacionTributariaList;

    @Column
    private String addedBy;

    @Column
    private LocalDateTime additionDate;

    @Column
    private String modifiedBy;

    @Column
    private LocalDateTime modificationDate;

}
