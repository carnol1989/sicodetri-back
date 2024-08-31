package com.epsgrau.pe.sicodetri.model;

import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Projection {

    private LocalDate accountigPeriod;
    private LocalDate accountigPeriodNext;
    private FinancialDepreciation financialDepreciation;
    private TaxDepreciation taxDepreciation;

}
