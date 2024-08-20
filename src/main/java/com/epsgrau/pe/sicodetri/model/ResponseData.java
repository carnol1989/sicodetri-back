package com.epsgrau.pe.sicodetri.model;

import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData {

    private LocalDateTime timestamp;
    private String code;
    private String message;
    private Object response;
    private ExceptionResponse exception;

}
