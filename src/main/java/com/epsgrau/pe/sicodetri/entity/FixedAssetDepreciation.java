package com.epsgrau.pe.sicodetri.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class entity for generate the depreciation of a fixed asset. <br/>
 * <b>Class</b>: {@link FixedAssetDepreciation}<br/>
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
@ToString(exclude = {"fixedAsset", "financialDepreciations", "taxDepreciations"})
@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "fixed_asset_depreciation")
public class FixedAssetDepreciation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate accountingPeriod;

    @OneToOne
    @JoinColumn(name = "fixed_asset_financial_id", foreignKey = @ForeignKey(name = "FK_financial_fixed_asset_fixed_asset_depreciation"))
    private FinancialFixedAsset fixedAsset;

    @OneToMany(mappedBy = "fixedAssetDepreciation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinancialDepreciation> financialDepreciations;

    @OneToMany(mappedBy = "fixedAssetDepreciation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaxDepreciation> taxDepreciations;

    public BigDecimal calculateDepreciation(FinancialFixedAsset fixedAsset, TaxTableSunat taxTable) {
        return BigDecimal.ZERO;
    }

    @Column
    private String addedBy;

    @Column
    private LocalDateTime additionDate;

    @Column
    private String modifiedBy;

    @Column
    private LocalDateTime modificationDate;

}
