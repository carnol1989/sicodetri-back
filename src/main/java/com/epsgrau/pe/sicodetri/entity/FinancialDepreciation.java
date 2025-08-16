package com.epsgrau.pe.sicodetri.entity;

import static com.epsgrau.pe.sicodetri.util.Constants.BD_VAL_100;
import static com.epsgrau.pe.sicodetri.util.Constants.BD_VAL_12;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_2;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_9;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_01;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_02;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_03;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing the financial depreciation record for a fixed asset.
 * Maps to the {@code financial_depreciation} table and stores the depreciation
 * value calculated per period, the applied annual rate, and audit information.
 * <b>Class</b>: {@link FinancialDepreciation}<br/>
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
    @JoinColumn(name = "id_financial_fixed_asset", foreignKey = @ForeignKey(name =
        "FK_financial_depreciation_fixed_asset"))
    private FinancialFixedAsset financialFixedAsset;

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

    /**
     * Calculates the periodic depreciation value based on the asset's initial balance,
     * useful life, and projection type.
     * - Computes the annual rate as 1 / usefulLifeYears.<br>
     * - Converts to a monthly rate by dividing by 12.<br>
     * - Depending on the projection type:
     *   <ul>
     *     <li>For regular or full-life projections (S_VAL_01, S_VAL_03), multiplies balance by
     *     monthly rate.</li>
     *     <li>For year-limited projections (S_VAL_02), only applies if numberYears ≤
     *     usefulLifeYears.</li>
     *   </ul>
     * - Rounds the result down to 2 decimal places.
     *
     * @param fixedAsset     the {@link FinancialFixedAsset} containing balance and useful life
     * @param typeProjection the projection type constant (e.g. S_VAL_01, S_VAL_02, S_VAL_03)
     * @param numberYears    the number of years elapsed for limited projections
     */
    public void calculateDepreciation(FinancialFixedAsset fixedAsset, String typeProjection,
        int numberYears) {
        // Get the initial balance
        BigDecimal initialBalance = fixedAsset.getInitialBalance().setScale(N_VAL_2,
            RoundingMode.HALF_UP);

        // Calculate the annual depreciation rate as a decimal value (not a percentage)
        this.annualFinancialRate = BD_VAL_100.divide(BigDecimal.valueOf(
            fixedAsset.getUsefulLifeYears()), N_VAL_9, RoundingMode.HALF_UP).divide(BD_VAL_100,
            N_VAL_9, RoundingMode.HALF_UP);

        // Calculate the monthly rate
        BigDecimal monthlyFinancialRate = this.annualFinancialRate.divide(BD_VAL_12, N_VAL_9,
            RoundingMode.HALF_UP);

        // Calculate monthly depreciation
        if (typeProjection.equals(S_VAL_01)
            || typeProjection.equals(S_VAL_03)) {
            this.valueDepreciation = initialBalance.multiply(monthlyFinancialRate);
        } else if (typeProjection.equals(S_VAL_02)) {
            if (numberYears <= fixedAsset.getUsefulLifeYears().intValue()) {
                this.valueDepreciation = initialBalance.multiply(monthlyFinancialRate);
            } else {
                this.valueDepreciation = BigDecimal.ZERO;
            }
        }

        // Assign the 'value Depreciation' value with the new scale
        this.valueDepreciation = this.valueDepreciation.setScale(N_VAL_2, RoundingMode.DOWN);
    }
}