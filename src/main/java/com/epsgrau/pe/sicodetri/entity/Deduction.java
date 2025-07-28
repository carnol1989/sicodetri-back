package com.epsgrau.pe.sicodetri.entity;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a SUNAT tax deduction record.
 * Maps to the {@code deductions} table and stores information about tax deductions
 * for a given accounting period, including the deduction amount and audit details.
 * <b>Class</b>: {@link Deduction}<br/>
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
@Schema(description = "Información de las deducciones tributarias.")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "deductions")
public class Deduction {

    @Schema(description = "Identificador del modelo", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Periodo contable", example = "2022-01-01")
    @Column
    private LocalDate accountingPeriod;

    @Schema(description = "Valor de la deducción tributaria", example = "38,185.70")
    @Column
    private BigDecimal valueDeduction;

    @ManyToOne
    @JoinColumn(name = "tax_depreciation_id", foreignKey = @ForeignKey(
        name = "FK_tax_depreciation_deductions"))
    private TaxDepreciation taxDepreciation;

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

}
