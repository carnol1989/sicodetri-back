package com.epsgrau.pe.sicodetri.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataResponse {

    private LocalDateTime timestamp;
    private String message;
    private Object response;

}
