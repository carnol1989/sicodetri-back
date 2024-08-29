package com.epsgrau.pe.sicodetri.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
@ToString(exclude = {"fixedAssetDepreciation", "additions", "deductions"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tax_depreciation")
public class TaxDepreciation extends FixedAssetDepreciation{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate accountingPeriod;

    @Column
    private BigDecimal annualAccumulatedDepreciationPriorPeriod;

    @Column
    private BigDecimal annualNetTaxValuePriorPeriod;

    @Column
    private BigDecimal valueDepreciation;

    @Column
    private BigDecimal netTaxValue;

    @ManyToOne
    @JoinColumn(name = "fixed_asset_depreciation_id", foreignKey = @ForeignKey(name = "FK_fixed_asset_depreciation_tax_depreciation"))
    private FixedAssetDepreciation fixedAssetDepreciation;

    @OneToMany(mappedBy = "taxDepreciation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Addition> additions;

    @OneToMany(mappedBy = "taxDepreciation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deduction> deductions;

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
        Period period = Period.between(fixedAsset.getAcquisitionDate(), this.accountingPeriod.minusYears(N_VAL_1));
        int numberYears = period.getYears();
        this.annualAccumulatedDepreciationPriorPeriod = fixedAsset.getInitialBalance()
                .multiply(new BigDecimal(taxTable.getAnnualRate()))
                .multiply(new BigDecimal(numberYears));

        this.annualNetTaxValuePriorPeriod = fixedAsset.getInitialBalance().subtract(this.annualAccumulatedDepreciationPriorPeriod);

        this.valueDepreciation = fixedAsset.getInitialBalance().multiply((new BigDecimal(taxTable.getAnnualRate())
                .divide(BD_VAL_12, N_VAL_2, RoundingMode.HALF_UP)));

        this.netTaxValue = this.annualNetTaxValuePriorPeriod.subtract(this.valueDepreciation);

        return this.valueDepreciation.setScale(N_VAL_2, RoundingMode.HALF_UP);
    }
}
