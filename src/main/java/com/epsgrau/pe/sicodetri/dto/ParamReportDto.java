package com.epsgrau.pe.sicodetri.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object encapsulating parameters for report generation.
 * Carries all necessary filters and date ranges used by reporting strategies
 * to produce the desired report output.
 * <ul>
 *   <li><strong>year</strong>: the accounting year for the report</li>
 *   <li><strong>mainCode</strong>: the main fixed asset or tax book code</li>
 *   <li><strong>accountingAccount</strong>: the accounting account filter</li>
 *   <li><strong>startAcquisitionDate</strong>: start date of asset acquisition (YYYY-MM-DD)</li>
 *   <li><strong>endAcquisitionDate</strong>: end date of asset acquisition (YYYY-MM-DD)</li>
 *   <li><strong>startDateUse</strong>: start date of asset usage (YYYY-MM-DD)</li>
 *   <li><strong>endDateUse</strong>: end date of asset usage (YYYY-MM-DD)</li>
 * </ul>
 * <b>Class</b>: {@link ParamReportDto}<br/>
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
 *     <li>Jun 06, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ParamReportDto {

    private int year;
    private String mainCode;
    private String accountingAccount;
    private String startAcquisitionDate;
    private String endAcquisitionDate;
    private String startDateUse;
    private String endDateUse;

}
