package com.epsgrau.pe.sicodetri.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This class entity for tax deductions. <br/>
 * <b>Class</b>: {@link Addition}<br/>
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
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "deductions")
public class Deduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate accountingPeriod;

    @Column
    private BigDecimal valueDeduction;

    @ManyToOne
    @JoinColumn(name = "tax_depreciation_id", foreignKey = @ForeignKey(name = "FK_tax_depreciation_deductions"))
    private TaxDepreciation taxDepreciation;

    @Column
    private String addedBy;

    @Column
    private LocalDateTime additionDate;

    @Column
    private String modifiedBy;

    @Column
    private LocalDateTime modificationDate;

}
