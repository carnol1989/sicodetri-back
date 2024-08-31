package com.epsgrau.pe.sicodetri.entity;

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
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tax_depreciation")
public class TaxDepreciation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate accountingPeriod;

    @Column(precision = 25, scale = 15)
    private BigDecimal annualAccumulatedDepreciationPriorPeriod;

    @Column(precision = 25, scale = 15)
    private BigDecimal annualNetTaxValuePriorPeriod;

    @Column(precision = 25, scale = 15)
    private BigDecimal valueDepreciation;

    @Column(precision = 25, scale = 15)
    private BigDecimal netTaxValue;

    @ManyToOne
    @JoinColumn(name = "id_financiacl_fixed_asset", foreignKey = @ForeignKey(name = "FK_tax_depreciation_fixed_asset"))
    private FinancialFixedAsset financialFixedAsset;

    @OneToMany(mappedBy = "taxDepreciation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Addition> additionsList;

    @OneToMany(mappedBy = "taxDepreciation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deduction> deductionsList;

    @Column
    private String addedBy;

    @Column
    private LocalDateTime additionDate;

    @Column
    private String modifiedBy;

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
        //System.out.println("Initial Balance: " + initialBalance);

        Period period = Period.between(fixedAsset.getAcquisitionDate(), accountingPeriod);
        int numberYears = period.getYears();
        //System.out.println("Años transcurridos: " + numberYears);

        BigDecimal rateDecimal = new BigDecimal(taxTable.getAnnualRate()).divide(BD_VAL_100, mathContext);
        //System.out.println("rateDecimal: " + rateDecimal);

        // Calcular la tasa mensual
        BigDecimal monthlyTaxRate = rateDecimal.divide(BD_VAL_12, mathContext);
        //System.out.println("monthlyTaxRate: " + monthlyTaxRate);

        this.annualAccumulatedDepreciationPriorPeriod = initialBalance.multiply(rateDecimal, mathContext)
                .multiply(new BigDecimal(numberYears), mathContext);
        //System.out.println("annualAccumulatedDepreciationPriorPeriod: " + this.annualAccumulatedDepreciationPriorPeriod);

        this.annualNetTaxValuePriorPeriod = initialBalance.subtract(this.annualAccumulatedDepreciationPriorPeriod, mathContext);
        //System.out.println("annualNetTaxValuePriorPeriod: " + this.annualNetTaxValuePriorPeriod);

        this.valueDepreciation = initialBalance.multiply(monthlyTaxRate, mathContext);
        //System.out.println("valueDepreciation: " + this.valueDepreciation);

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

        //System.out.println("netTaxValue: " + this.netTaxValue);

        // Redondear resultados finales
        this.annualAccumulatedDepreciationPriorPeriod = this.annualAccumulatedDepreciationPriorPeriod.setScale(N_VAL_2, RoundingMode.HALF_UP);
        this.netTaxValue = this.netTaxValue.setScale(N_VAL_2, RoundingMode.HALF_UP);
        this.valueDepreciation = this.valueDepreciation.setScale(N_VAL_2, RoundingMode.HALF_UP);
        this.annualNetTaxValuePriorPeriod = this.annualNetTaxValuePriorPeriod.setScale(N_VAL_2, RoundingMode.HALF_UP);
    }
}
