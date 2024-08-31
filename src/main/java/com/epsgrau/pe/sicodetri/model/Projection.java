package com.epsgrau.pe.sicodetri.model;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Projection {

    private LocalDate accountigPeriod;
    private LocalDate accountigPeriodNext;
    private FinancialDepreciation financialDepreciation;
    private List<FinancialDepreciation> financialDepreciationList;
    private TaxDepreciation taxDepreciation;
    private List<TaxDepreciation> taxDepreciationList;
    private Addition addition;
    private List<Addition> additionList;
    private Deduction deduction;
    private List<Deduction> deductionList;

}
