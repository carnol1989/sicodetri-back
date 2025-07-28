package com.epsgrau.pe.sicodetri.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a financial fixed asset, storing all relevant details
 * such as identification, valuation, acquisition and usage dates, depreciation
 * data, and audit information.
 * Maps to the {@code financial_fixed_asset} table. Associated depreciation
 * entries (both financial and tax) are maintained via one-to-many relationships.
 * Transient fields support runtime metadata (e.g., row numbers for import).
 * <b>Class</b>: {@link FinancialFixedAsset}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
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
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Schema(description = "Información de los Activos Fijos.")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "financial_fixed_asset")
public class FinancialFixedAsset {

    @Schema(description = "Identificador del modelo", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Periodo contable", example = "2022-01-01")
    @Column
    private LocalDate accountingPeriod;

    @Schema(description = "Código activo fijo principal", example = "22.01.010862")
    @Column
    private String mainFixedAssetCode; //0

    @Schema(description = "Código activo fijo componente", example = "")
    @Column
    private String fixedAssetCodeComponent; //1

    @Schema(description = "Código contable del activo fijo", example = "3321161001")
    @Column
    private String fixedAssetAccountingAccount; //2

    @Schema(description = "Detalle del Activo Fijo: Descripción del activo fijo",
        example = "CONSTRUCCION DE EDIFICIO PTAP CURUMUY")
    @Column(length = 500)
    private String description; //3

    @Schema(description = "Detalle del Activo Fijo: Marca del activo fijo", example = "NINGUNA")
    @Column(length = 500)
    private String fixedAssetBrand; //4

    @Schema(description = "Detalle del Activo Fijo: Modelo del activo fijo",
        example = "NINGUNO")
    @Column(length = 500)
    private String fixedAssetModel; //5

    @Schema(description = "Detalle del Activo Fijo: Número de serie y/o placa "
        + "del activo fijo", example = "/")
    @Column(length = 500)
    private String fixedAssetSerialNumberAndOrPlate; //6

    @Schema(description = "Saldo inicial", example = "27,493,700.65")
    @Column(precision = 20, scale = 4)
    private BigDecimal initialBalance; //7

    @Schema(description = "Adquisiciones adiciones", example = "0")
    @Column(precision = 20, scale = 4)
    private BigDecimal acquisitionsAdditions; //8

    @Schema(description = "Mejoras", example = "0")
    @Column(precision = 20, scale = 4)
    private BigDecimal improvements; //9

    @Schema(description = "Transferencias y/o reclasificaciones", example = "0")
    @Column(precision = 20, scale = 4)
    private BigDecimal transfersAndOrReclassifications; //10

    @Schema(description = "Retiros y/o bajas", example = "0")
    @Column(precision = 20, scale = 4)
    private BigDecimal withdrawalsAndOrRetirements; //11

    @Schema(description = "Otros ajustes", example = "0")
    @Column(precision = 20, scale = 4)
    private BigDecimal otherAdjustments; //12

    @Schema(description = "Valor en libros del Activo Fijo", example = "27,493,700.65")
    @Column(precision = 20, scale = 4)
    private BigDecimal bookValueOfFixedAssets; //13

    @Schema(description = "Ajuste por inflación", example = "0")
    @Column(precision = 20, scale = 4)
    private BigDecimal inflationAdjustment; //14

    @Schema(description = "Valor ajustado del activo fijo", example = "27,493,700.65")
    @Column(precision = 20, scale = 4)
    private BigDecimal adjustedValueOfFixedAssets; //15

    @Schema(description = "Fecha de adquisición", example = "2007-12-31")
    @Column
    private LocalDate acquisitionDate; //16

    @Schema(description = "Fecha de inicio del uso del Activo Fijo", example = "2007-12-31")
    @Column
    private LocalDate startDateFixedAssetUsage; //17

    @Schema(description = "Fecha de retiro")
    @Column
    private LocalDate retirementDate; //18

    @Schema(description = "Depreciación: Método aplicado", example = "LINEA RECTA")
    @Column
    private String appliedMethod; //19

    @Schema(description = "Depreciación: N° de documento de autorización", example = "1")
    @Column
    private String authorizationDocumentNumber; //20

    @Schema(description = "Años de vida util", example = "30")
    @Column
    private Double usefulLifeYears; //21

    @Schema(description = "Depreciación acumulada al cierre del ejercicio anterior",
        example = "12,830,393.57")
    @Column(precision = 20, scale = 4)
    private BigDecimal accumulatedDepreciationTheEndPrevious; //22

    @Schema(description = "Depreciación del ejercicio", example = "916,456.68")
    @Column(precision = 20, scale = 4)
    private BigDecimal currentDepreciation; //23

    @Schema(description = "Depreciación del ejercicio relacionada con los "
        + "retiros y/o bajas", example = "0")
    @Column(precision = 20, scale = 4)
    private BigDecimal currentDepreciationRelatedWithdrawalsRetirements; //24

    @Schema(description = "Depreciación del ejercicio relacionada con "
        + "otros ajuste", example = "0")
    @Column(precision = 20, scale = 4)
    private BigDecimal currentDepreciationRelatedOtherAdjustments; //25

    @Schema(description = "Depreciación acumulada histórica", example = "13,746,850.25")
    @Column(precision = 20, scale = 4)
    private BigDecimal accumulatedHistoricalDepreciation; //26

    @Schema(description = "Ajuste por inflación de la depreciación", example = "0")
    @Column(precision = 20, scale = 4)
    private BigDecimal depreciationInflationAdjustment; //27

    @Schema(description = "Depreciación acumulada ajustada por inflación",
        example = "13,746,850.25")
    @Column(precision = 20, scale = 4)
    private BigDecimal accumulatedDepreciationAdjustedInflation; //28

    @Schema(description = "Activo Fijo Neto", example = "13,746,850.40")
    @Column(precision = 20, scale = 4)
    private BigDecimal netFixedAssets; //30

    @Schema(description = "Costo histórico", example = "20,143,512.00")
    @Column(precision = 20, scale = 4)
    private BigDecimal historicalCost; //31

    @Schema(description = "Valor revaluado", example = "7,350,188.65")
    @Column(precision = 20, scale = 4)
    private BigDecimal revaluedAmount; //32


    @Schema(description = "Bien tasado estudio de preinversion seco", example = "tasacion 2013")
    @Column
    private String wellAppraisedPreInvestmentStudyDry; //33

    @Transient
    private int rowNum;

    @Transient
    private int physicalNumberOfRows;

    @OneToMany(mappedBy = "financialFixedAsset", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<FinancialDepreciation> depreciacionFinancieraList;

    @OneToMany(mappedBy = "financialFixedAsset", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<TaxDepreciation> depreciacionTributariaList;

    @Schema(description = "Usuario que realizó el registro", example = "CANO")
    @Column(length = 50)
    private String addedBy;

    @Schema(description = "Fecha y hora del registro", example = "2018-02-27T12:26:30.107")
    @Column
    private LocalDateTime additionDate;

    @Schema(description = "Usuario que realizó una modificación", example = "WIJI")
    @Column(length = 50)
    private String modifiedBy;

    @Schema(description = "Fecha y hora de la modificación", example = "2018-02-28T12:26:30.107")
    @Column
    private LocalDateTime modificationDate;

    @Override
    public String toString() {
        return "FinancialFixedAsset{"
            + "id=" + id
            + ", accountingPeriod=" + accountingPeriod
            + ", mainFixedAssetCode='" + mainFixedAssetCode + '\''
            + ", fixedAssetCodeComponent='" + fixedAssetCodeComponent + '\''
            + ", fixedAssetAccountingAccount='" + fixedAssetAccountingAccount + '\''
            + ", description='" + description + '\''
            + ", fixedAssetBrand='" + fixedAssetBrand + '\''
            + ", fixedAssetModel='" + fixedAssetModel + '\''
            + ", fixedAssetSerialNumberAndOrPlate='" + fixedAssetSerialNumberAndOrPlate + '\''
            + ", initialBalance=" + initialBalance
            + ", acquisitionsAdditions=" + acquisitionsAdditions
            + ", improvements=" + improvements
            + ", transfersAndOrReclassifications=" + transfersAndOrReclassifications
            + ", withdrawalsAndOrRetirements=" + withdrawalsAndOrRetirements
            + ", otherAdjustments=" + otherAdjustments
            + ", bookValueOfFixedAssets=" + bookValueOfFixedAssets
            + ", inflationAdjustment=" + inflationAdjustment
            + ", adjustedValueOfFixedAssets=" + adjustedValueOfFixedAssets
            + ", acquisitionDate=" + acquisitionDate
            + ", startDateFixedAssetUsage=" + startDateFixedAssetUsage
            + ", retirementDate=" + retirementDate
            + ", appliedMethod='" + appliedMethod + '\''
            + ", authorizationDocumentNumber='" + authorizationDocumentNumber + '\''
            + ", usefulLifeYears=" + usefulLifeYears
            + ", accumulatedDepreciationTheEndPrevious=" + accumulatedDepreciationTheEndPrevious
            + ", currentDepreciation=" + currentDepreciation
            + ", currentDepreciationRelatedWithdrawalsRetirements="
            + currentDepreciationRelatedWithdrawalsRetirements
            + ", currentDepreciationRelatedOtherAdjustments="
            + currentDepreciationRelatedOtherAdjustments
            + ", accumulatedHistoricalDepreciation="
            + accumulatedHistoricalDepreciation
            + ", depreciationInflationAdjustment=" + depreciationInflationAdjustment
            + ", accumulatedDepreciationAdjustedInflation="
            + accumulatedDepreciationAdjustedInflation
            + ", netFixedAssets=" + netFixedAssets
            + ", historicalCost=" + historicalCost
            + ", revaluedAmount=" + revaluedAmount
            + ", wellAppraisedPreInvestmentStudyDry=" + wellAppraisedPreInvestmentStudyDry
            + ", rowNum=" + rowNum
            + ", physicalNumberOfRows=" + physicalNumberOfRows + '}';
    }
}
