package com.epsgrau.pe.sicodetri.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Size(max = 255)
    private String description;

    @Column
    @NotNull
    private Integer rate;

    @Column
    @NotNull
    @Size(max = 1)
    private String status;

}
