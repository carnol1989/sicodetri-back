package com.epsgrau.pe.sicodetri.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a SUNAT tax rate entry.
 * Maps to the {@code tax_table_sunat} domain model, storing the accounting account,
 * description, annual rate, useful life, status, and audit metadata.
 * <b>Class</b>: {@link TaxTableSunat}<br/>
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
@Schema(description = "Información de la Tasas de Sunat.")
@Entity
@Table(
    name = "tax_table_sunat",
    indexes = {
        @Index(name = "idx_tax_table_sunat_account", columnList = "accounting_account"),
        @Index(name = "idx_tax_table_sunat_effective", columnList = "accounting_account,effective_from,effective_to")
    }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxTableSunat {

    @Schema(description = "Identificador del modelo", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Cuenta contable", example = "332")
    @Column
    private String accountingAccount;

    @Schema(description = "Descripción de tasa tributaria",
        example = "Edificaciones y Otras Construcciones")
    @Column
    @NotNull
    @Size(max = 255, message = "Descripción debe ser máximo de 255 caracteres.")
    private String description;

    @Schema(description = "Tasa anual tributaria", example = "5%")
    @Column
    @NotNull
    private Integer annualRate;

    @Schema(description = "Años de vida util tributaria", example = "20")
    @Column
    @NotNull
    private Integer usefulLifeYears;

    @Schema(description = "Indicador de estado del registro", example = "S")
    @Column
    @NotNull
    @Size(max = 1)
    private String status;

    @Column(length = 50)
    private String addedBy;

    @Column
    private LocalDateTime additionDate;

    @Column(length = 50)
    private String modifiedBy;

    @Column
    private LocalDateTime modificationDate;

    @Column
    private LocalDate effectiveFrom;

    @Column
    private LocalDate effectiveTo;

    /**
     * Checks if the tax rate is effective on the given date.
     * A tax rate is considered effective if:
     * - The date is not null.
     * - The date is on or after the effectiveFrom date (if specified).
     * - The date is on or before the effectiveTo date (if specified).
     *
     * @param date the date to check for effectiveness
     * @return true if the tax rate is effective on the given date, false otherwise
     */
    public boolean isEffectiveOn(LocalDate date) {
        if (date == null) {
            return false;
        }

        boolean afterFrom = (effectiveFrom == null) || !date.isBefore(effectiveFrom);
        boolean beforeTo  = (effectiveTo == null) || !date.isAfter(effectiveTo);
        return afterFrom && beforeTo;
    }

}
