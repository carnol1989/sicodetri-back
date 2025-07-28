package com.epsgrau.pe.sicodetri.model;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data model representing a full projection period for fixed asset depreciation,
 * including financial and tax depreciation entries as well as related additions
 * and deductions.
 * Contains both the current and next accounting periods, individual and list
 * forms of depreciation records, and the corresponding additions and deductions.
 * <b>Class</b>: {@link Projection}<br/>
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
