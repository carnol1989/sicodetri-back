package com.epsgrau.pe.sicodetri.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class entity for Tax Table SUNAT. <br/>
 * <b>Class</b>: {@link TaxTableSunat}<br/>
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
 *     <li>Ago 15, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxTableSunat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String accountingAccount;

    @Column
    @NotNull
    @Size(max = 255, message = "Descripción debe ser máximo de 255 caracteres.")
    private String description;

    @Column
    @NotNull
    private Integer annualRate;

    @Column
    @NotNull
    @Size(max = 1)
    private String status;

}
