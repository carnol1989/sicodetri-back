package com.epsgrau.pe.sicodetri.controller;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.ResponseData;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelper.hasExcelFormat;
import static com.epsgrau.pe.sicodetri.util.Constants.FORMATTER_YYYY_MM_DD;
import static com.epsgrau.pe.sicodetri.util.Constants.LIST_INPUT_DATA;

@RestController
@RequestMapping("/api/v1/financial-fixed-asset")
public class FinancialFixedAssetController {

    @Autowired
    private FinancialFixedAssetService service;

    @PostMapping("/upload")
    public ResponseEntity<ResponseData> uploadInputDataController(@RequestParam("file")MultipartFile file,
                                                                  HttpServletRequest request) {
        String code = "00";
        String message = "";
        String detailError = "";
        HttpStatus statusCode = HttpStatus.OK;
        ResponseData responseData = new ResponseData();
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        List<InputData> listInputData = new ArrayList<>();
        try {
            if(hasExcelFormat(file)) {
                responseData.setTimestamp(LocalDateTime.now());
                listInputData = service.loadExcelFileToObject(file);
                request.getSession().setAttribute(LIST_INPUT_DATA, listInputData);
                message = "Archivo subio correctamente: " + file.getOriginalFilename();
            } else {
                responseData.setTimestamp(LocalDateTime.now());
                code = "01";
                statusCode = HttpStatus.EXPECTATION_FAILED;
                message = "Error al cargar archivo: " + file.getOriginalFilename() + "\nFormato permitido: xlsx";
                exceptionResponse.setMessage(message);
                exceptionResponse.setTimestamp(LocalDateTime.now());
                exceptionResponse.setDetails(detailError);
            }
        } catch (Exception e) {
            responseData.setTimestamp(LocalDateTime.now());
            code = "01";
            statusCode = HttpStatus.EXPECTATION_FAILED;
            e.printStackTrace();
            message = "Error al cargar archivo: " + file.getOriginalFilename();
            detailError = "Mensaje: " + e.getMessage() + "\nCausa: " + e.getCause();
            exceptionResponse.setMessage(message);
            exceptionResponse.setTimestamp(LocalDateTime.now());
            exceptionResponse.setDetails(detailError);
        }
        responseData.setCode(code);
        responseData.setMessage(message);
        responseData.setException(exceptionResponse);
        return new ResponseEntity<ResponseData>(responseData, statusCode);
    }

    @PostMapping
    public ResponseEntity<ResponseData> saveFinancialFixedAssetController(@RequestParam(name = "period") String period,
                                                                          HttpSession session) {
        String code = "00";
        String message = "";
        String detailError = "";
        HttpStatus statusCode = HttpStatus.CREATED;
        ResponseData responseData = new ResponseData();
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD);
        try {
            List<InputData> listInputData = (List<InputData>) session.getAttribute(LIST_INPUT_DATA);
            if (listInputData != null) {
                LocalDate periodDate = LocalDate.parse(period, formatter);
                service.deleteByAccountingPeriod(periodDate);

                service.saveAllService(periodDate, listInputData);

                responseData.setTimestamp(LocalDateTime.now());
                message = "Informacion guardada de manera correcta para el periodo " + period;
            } else {
                responseData.setTimestamp(LocalDateTime.now());
                code = "01";
                statusCode = HttpStatus.EXPECTATION_FAILED;
                message = "Error al guardar informacion para el periodo " + period;
                exceptionResponse.setMessage(message);
                exceptionResponse.setTimestamp(LocalDateTime.now());
                exceptionResponse.setDetails(detailError);
            }
        } catch (Exception e) {
            e.printStackTrace();
            code = "01";
            statusCode = HttpStatus.EXPECTATION_FAILED;
            responseData.setTimestamp(LocalDateTime.now());
            message = "Error al guardar informacion para el periodo " + period;
            detailError = "Mensaje: " + e.getMessage() + "\nCausa: " + e.getCause();
            exceptionResponse.setMessage(message);
            exceptionResponse.setTimestamp(LocalDateTime.now());
            exceptionResponse.setDetails(detailError);
        }
        responseData.setCode(code);
        responseData.setMessage(message);
        responseData.setException(exceptionResponse);
        return new ResponseEntity<ResponseData>(responseData, statusCode);
    }

    @GetMapping
    public ResponseEntity<ResponseData> getAllFinancialFixedController(@RequestParam(name = "period") String period) {
        String code = "00";
        String message = "";
        String detailError = "";
        HttpStatus statusCode = HttpStatus.OK;
        ResponseData responseData = new ResponseData();
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD);
        try {
            LocalDate periodDate = LocalDate.parse(period, formatter);
            responseData.setTimestamp(LocalDateTime.now());
            List<FinancialFixedAsset> listFinancialFixedAsset = service.findByPeriodService(periodDate);
            if (listFinancialFixedAsset != null) {
                responseData.setResponse(listFinancialFixedAsset);
                message = "Se obtuvo informacion para el periodo " + period;
            } else {
                code = "01";
                statusCode = HttpStatus.NOT_FOUND;
                responseData.setTimestamp(LocalDateTime.now());
                message = "Error al obtener informacion para el periodo " + period;
                exceptionResponse.setMessage(message);
                exceptionResponse.setTimestamp(LocalDateTime.now());
                exceptionResponse.setDetails(detailError);
            }
        } catch (Exception e) {
            e.printStackTrace();
            code = "01";
            statusCode = HttpStatus.EXPECTATION_FAILED;
            responseData.setTimestamp(LocalDateTime.now());
            message = "Error al obtener informacion para el periodo " + period;
            detailError = "Mensaje: " + e.getMessage() + "\nCausa: " + e.getCause();
            exceptionResponse.setMessage(message);
            exceptionResponse.setTimestamp(LocalDateTime.now());
            exceptionResponse.setDetails(detailError);
        }
        responseData.setCode(code);
        responseData.setMessage(message);
        responseData.setException(exceptionResponse);
        return new ResponseEntity<ResponseData>(responseData, statusCode);
    }

}
