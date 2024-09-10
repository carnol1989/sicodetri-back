package com.epsgrau.pe.sicodetri.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.epsgrau.pe.sicodetri.util.Constants.*;

/**
 * This class entity for financial depreciation. <br/>
 * <b>Class</b>: {@link FinancialDepreciation}<br/>
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
 *     <li>Ago 25, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Schema(description = "Información de la Depreciación Financiera.")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "financial_depreciation")
public class FinancialDepreciation {

    @Schema(description = "Identificador del modelo", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Periodo contable", example = "2022-01-01")
    @Column
    private LocalDate accountingPeriod;

    @Schema(description = "Valor de la depreciación financiera", example = "76,371.39")
    @Column(precision = 20, scale = 4)
    private BigDecimal valueDepreciation;

    @Schema(description = "Tasa anual financiera", example = "3%")
    @Column(precision = 20, scale = 4)
    private BigDecimal annualFinancialRate;

    @ManyToOne
    @JoinColumn(name = "id_financial_fixed_asset", foreignKey = @ForeignKey(name = "FK_financial_depreciation_fixed_asset"))
    private FinancialFixedAsset financialFixedAsset;

    @Schema(description = "Usuario que realizó el registro", example = "CANO")
    @Column
    private String addedBy;

    @Schema(description = "Fecha y hora del registro", example = "2018-02-27T12:26:30.107")
    @Column
    private LocalDateTime additionDate;

    @Schema(description = "Usuario que realizó una modificación", example = "WIJI")
    @Column
    private String modifiedBy;

    @Schema(description = "Fecha y hora de la modificación", example = "2018-02-28T12:26:30.107")
    @Column
    private LocalDateTime modificationDate;

    /***
     * Method that allows the calculation of financial depreciation.
     * @param fixedAsset Fixed Asset
     */
    public void calculateDepreciation(FinancialFixedAsset fixedAsset) {
        // Obtener el saldo inicial
        BigDecimal initialBalance = fixedAsset.getInitialBalance().setScale(N_VAL_2, RoundingMode.HALF_UP);

        // Calcular la tasa anual de depreciación como un valor decimal (no porcentaje)
        this.annualFinancialRate = BD_VAL_100.divide(BigDecimal.valueOf(fixedAsset.getUsefulLifeYears()), N_VAL_9, RoundingMode.HALF_UP)
                .divide(BD_VAL_100, N_VAL_9, RoundingMode.HALF_UP);

        // Calcular la tasa mensual
        BigDecimal monthlyFinancialRate = this.annualFinancialRate.divide(BD_VAL_12, N_VAL_9, RoundingMode.HALF_UP);

        // Calcular la depreciación mensual
        this.valueDepreciation = initialBalance.multiply(monthlyFinancialRate);

        this.valueDepreciation.setScale(N_VAL_2, RoundingMode.DOWN);
    }
}