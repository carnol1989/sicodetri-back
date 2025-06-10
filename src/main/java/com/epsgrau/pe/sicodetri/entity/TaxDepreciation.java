package com.epsgrau.pe.sicodetri.entity;

import static com.epsgrau.pe.sicodetri.util.Constants.BD_VAL_100;
import static com.epsgrau.pe.sicodetri.util.Constants.BD_VAL_12;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_0;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_15;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_2;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_01;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_02;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
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

    @Schema(description = "Depreciación tributaria acumulada al cierre del periodo anterior",
        example = "19,245,590.46")
    @Column(precision = 25, scale = 15)
    private BigDecimal annualAccumulatedDepreciationPriorPeriod;

    @Schema(description = "Valor neto tributario al cierre del periodo anterior",
        example = "8,248,110.20")
    @Column(precision = 25, scale = 15)
    private BigDecimal annualNetTaxValuePriorPeriod;

    @Schema(description = "Valor depreciación tributaria", example = "114,557.09")
    @Column(precision = 25, scale = 15)
    private BigDecimal valueDepreciation;

    @Schema(description = "Valor neto tributario", example = "7,560,767.68")
    @Column(precision = 25, scale = 15)
    private BigDecimal netTaxValue;

    @ManyToOne
    @JoinColumn(name = "id_financial_fixed_asset", foreignKey = @ForeignKey(
        name = "FK_tax_depreciation_fixed_asset"))
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

    /**
     * Calculate tax depreciation.
     *
     * @param fixedAsset FinacialFixedAsset
     * @param taxTable TaxTableSunat
     * @param accountingPeriod Accountig period
     * @param taxDepreciationOld Tax depreciation old
     */
    public void calculateDepreciation(FinancialFixedAsset fixedAsset, TaxTableSunat taxTable,
        LocalDate accountingPeriod, TaxDepreciation taxDepreciationOld, String typeProjection) {
        MathContext mathContext = new MathContext(N_VAL_15, RoundingMode.HALF_UP);

        BigDecimal initialBalance = fixedAsset.getInitialBalance()
            .setScale(N_VAL_2, RoundingMode.HALF_UP);

        Period period = Period.between(fixedAsset.getAcquisitionDate(), accountingPeriod);
        int numberYears = period.getYears();

        BigDecimal rateDecimal = new BigDecimal(taxTable.getAnnualRate())
            .divide(BD_VAL_100, mathContext);

        BigDecimal monthlyTaxRate = rateDecimal.divide(BD_VAL_12, mathContext);

        // Temporary Deduction
        this.annualAccumulatedDepreciationPriorPeriod = initialBalance
            .multiply(rateDecimal, mathContext)
            .multiply(new BigDecimal(numberYears), mathContext);

        this.annualNetTaxValuePriorPeriod = initialBalance
            .subtract(this.annualAccumulatedDepreciationPriorPeriod, mathContext);

        this.valueDepreciation = initialBalance.multiply(monthlyTaxRate, mathContext);

        if (taxDepreciationOld.getNetTaxValue().compareTo(BigDecimal.ZERO) > N_VAL_0) {
            this.netTaxValue = taxDepreciationOld.getNetTaxValue().subtract(this.valueDepreciation);
        } else if (taxDepreciationOld.isFirst) {
            if (typeProjection.equals(S_VAL_02)) {
                this.netTaxValue = initialBalance
                    .subtract(this.valueDepreciation, mathContext);
            } else {
                if (typeProjection.equals(S_VAL_01)) {
                    this.netTaxValue = this.annualNetTaxValuePriorPeriod
                        .subtract(this.valueDepreciation, mathContext);
                } else if (taxDepreciationOld.getNetTaxValue().compareTo(BigDecimal.ZERO)
                    == N_VAL_0) {
                    this.netTaxValue = BigDecimal.ZERO;
                } else {
                    this.netTaxValue = this.annualNetTaxValuePriorPeriod
                        .subtract(this.valueDepreciation, mathContext);
                }
            }
        } else {
            this.netTaxValue = BigDecimal.ZERO;
        }

        if (this.netTaxValue.setScale(N_VAL_0, RoundingMode.DOWN)
            .compareTo(BigDecimal.ZERO) == N_VAL_0) {
            this.netTaxValue = BigDecimal.ZERO;
        }

        this.annualAccumulatedDepreciationPriorPeriod =
            this.annualAccumulatedDepreciationPriorPeriod.setScale(N_VAL_2, RoundingMode.HALF_UP);
        this.netTaxValue = this.netTaxValue.setScale(N_VAL_2, RoundingMode.HALF_UP);
        this.valueDepreciation = this.valueDepreciation.setScale(N_VAL_2, RoundingMode.HALF_UP);
        this.annualNetTaxValuePriorPeriod = this.annualNetTaxValuePriorPeriod
            .setScale(N_VAL_2, RoundingMode.HALF_UP);
    }
}
