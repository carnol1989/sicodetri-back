package com.epsgrau.pe.sicodetri.dto;

import lombok.*;

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
