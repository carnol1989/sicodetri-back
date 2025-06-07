package com.epsgrau.pe.sicodetri.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class dto for params report. <br/>
 * <b>Class</b>: {@link ParamReportDto}<br/>
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
 *     <li>Sep 09, 2024 (JAR) Creation class.</li>
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
