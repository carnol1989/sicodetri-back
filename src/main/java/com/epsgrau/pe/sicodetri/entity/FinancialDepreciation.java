package com.epsgrau.pe.sicodetri.entity;

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
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "financial_depreciation")
public class FinancialDepreciation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate accountingPeriod;

    @Column(precision = 20, scale = 4)
    private BigDecimal valueDepreciation;

    @Column(precision = 20, scale = 4)
    private BigDecimal annualFinancialRate;

    @ManyToOne
    @JoinColumn(name = "id_financiacl_fixed_asset", foreignKey = @ForeignKey(name = "FK_financial_depreciation_fixed_asset"))
    private FinancialFixedAsset financialFixedAsset;

    @Column
    private String addedBy;

    @Column
    private LocalDateTime additionDate;

    @Column
    private String modifiedBy;

    @Column
    private LocalDateTime modificationDate;

    public void calculateDepreciation(FinancialFixedAsset fixedAsset) {
        // Obtener el saldo inicial
        BigDecimal initialBalance = fixedAsset.getInitialBalance().setScale(N_VAL_2, RoundingMode.HALF_UP);
        //System.out.println("Initial Balance: " + initialBalance);

        // Calcular la tasa anual de depreciación como un valor decimal (no porcentaje)
        this.annualFinancialRate = BD_VAL_100.divide(BigDecimal.valueOf(fixedAsset.getUsefulLifeYears()), N_VAL_9, RoundingMode.HALF_UP)
                .divide(BD_VAL_100, N_VAL_9, RoundingMode.HALF_UP);
        //System.out.println("Annual Financial Rate (Decimal): " + this.annualFinancialRate);

        // Calcular la tasa mensual
        BigDecimal monthlyFinancialRate = this.annualFinancialRate.divide(BD_VAL_12, N_VAL_9, RoundingMode.HALF_UP);
        //System.out.println("Monthly Financial Rate: " + monthlyFinancialRate);

        // Calcular la depreciación mensual
        this.valueDepreciation = initialBalance.multiply(monthlyFinancialRate);
        //System.out.println("Value Depreciation: " + this.valueDepreciation);

        this.valueDepreciation.setScale(N_VAL_2, RoundingMode.DOWN);
    }
}