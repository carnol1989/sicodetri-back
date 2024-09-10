package com.epsgrau.pe.sicodetri.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import static com.epsgrau.pe.sicodetri.util.Constants.*;

/**
 * This class entity for generate the tax depreciation. <br/>
 * <b>Class</b>: {@link TaxDepreciation}<br/>
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
@Schema(description = "Información de la Depreciación Tributaria.")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tax_depreciation")
public class TaxDepreciation {

    @Schema(description = "Identificador del modelo", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Periodo contable", example = "2022-01-01")
    @Column
    private LocalDate accountingPeriod;

    @Schema(description = "Depreciación tributaria acumulada al cierre del periodo anterior", example = "19,245,590.46")
    @Column(precision = 25, scale = 15)
    private BigDecimal annualAccumulatedDepreciationPriorPeriod;

    @Schema(description = "Valor neto tributario al cierre del periodo anterior", example = "8,248,110.20")
    @Column(precision = 25, scale = 15)
    private BigDecimal annualNetTaxValuePriorPeriod;

    @Schema(description = "Valor depreciación tributaria", example = "114,557.09")
    @Column(precision = 25, scale = 15)
    private BigDecimal valueDepreciation;

    @Schema(description = "Valor neto tributario", example = "7,560,767.68")
    @Column(precision = 25, scale = 15)
    private BigDecimal netTaxValue;

    @ManyToOne
    @JoinColumn(name = "id_financial_fixed_asset", foreignKey = @ForeignKey(name = "FK_tax_depreciation_fixed_asset"))
    private FinancialFixedAsset financialFixedAsset;

    @OneToMany(mappedBy = "taxDepreciation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Addition> additionsList;

    @OneToMany(mappedBy = "taxDepreciation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deduction> deductionsList;

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

    @Transient
    private boolean isFirst;

    @Transient
    private boolean isLast;

    public void calculateDepreciation(FinancialFixedAsset fixedAsset, TaxTableSunat taxTable, LocalDate accountingPeriod,
                                      TaxDepreciation taxDepreciationOld) {
        MathContext mathContext = new MathContext(N_VAL_15, RoundingMode.HALF_UP);

        // Obtener el saldo inicial
        BigDecimal initialBalance = fixedAsset.getInitialBalance().setScale(N_VAL_2, RoundingMode.HALF_UP);

        Period period = Period.between(fixedAsset.getAcquisitionDate(), accountingPeriod);
        int numberYears = period.getYears();

        BigDecimal rateDecimal = new BigDecimal(taxTable.getAnnualRate()).divide(BD_VAL_100, mathContext);

        // Calcular la tasa mensual
        BigDecimal monthlyTaxRate = rateDecimal.divide(BD_VAL_12, mathContext);

        this.annualAccumulatedDepreciationPriorPeriod = initialBalance.multiply(rateDecimal, mathContext)
                .multiply(new BigDecimal(numberYears), mathContext);

        this.annualNetTaxValuePriorPeriod = initialBalance.subtract(this.annualAccumulatedDepreciationPriorPeriod, mathContext);

        this.valueDepreciation = initialBalance.multiply(monthlyTaxRate, mathContext);

        if (taxDepreciationOld.getNetTaxValue().compareTo(BigDecimal.ZERO) > N_VAL_0) {
            this.netTaxValue = taxDepreciationOld.getNetTaxValue().subtract(this.valueDepreciation);
        } else if (taxDepreciationOld.isFirst) {
            this.netTaxValue = this.annualNetTaxValuePriorPeriod.subtract(this.valueDepreciation, mathContext);
        } else {
            this.netTaxValue = BigDecimal.ZERO;
        }

        if (this.netTaxValue.setScale(N_VAL_0, RoundingMode.DOWN).compareTo(BigDecimal.ZERO) == N_VAL_0) {
            this.netTaxValue = BigDecimal.ZERO;
        }

        // Redondear resultados finales
        this.annualAccumulatedDepreciationPriorPeriod = this.annualAccumulatedDepreciationPriorPeriod.setScale(N_VAL_2, RoundingMode.HALF_UP);
        this.netTaxValue = this.netTaxValue.setScale(N_VAL_2, RoundingMode.HALF_UP);
        this.valueDepreciation = this.valueDepreciation.setScale(N_VAL_2, RoundingMode.HALF_UP);
        this.annualNetTaxValuePriorPeriod = this.annualNetTaxValuePriorPeriod.setScale(N_VAL_2, RoundingMode.HALF_UP);
    }
}
