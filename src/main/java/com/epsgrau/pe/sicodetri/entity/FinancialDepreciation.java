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
@ToString(exclude = "fixedAssetDepreciation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "financial_depreciation")
public class FinancialDepreciation extends FixedAssetDepreciation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate accountingPeriod;

    @Column
    private BigDecimal valueDepreciation;

    @Column
    private BigDecimal annualFinancialRate;

    @ManyToOne
    @JoinColumn(name = "fixed_asset_depreciation_id", foreignKey = @ForeignKey(name = "FK_fixed_asset_depreciation_financial_depreciation"))
    private FixedAssetDepreciation fixedAssetDepreciation;

    @Column
    private String addedBy;

    @Column
    private LocalDateTime additionDate;

    @Column
    private String modifiedBy;

    @Column
    private LocalDateTime modificationDate;

    @Override
    public BigDecimal calculateDepreciation(FinancialFixedAsset fixedAsset, TaxTableSunat taxTable) {
        this.annualFinancialRate = BD_VAL_100.divide(BigDecimal.valueOf(fixedAsset.getUsefulLifeYears()), N_VAL_2,
                RoundingMode.HALF_UP);
        this.valueDepreciation = fixedAsset.getInitialBalance().multiply((this.annualFinancialRate.divide(BD_VAL_12, N_VAL_2,
                RoundingMode.HALF_UP)));
        return this.valueDepreciation.setScale(N_VAL_2, RoundingMode.HALF_UP);
    }
}
