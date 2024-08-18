package com.epsgrau.pe.sicodetri.controller;

import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.DataResponse;
import com.epsgrau.pe.sicodetri.service.FinacialFixedAssetRegisterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelper.hasExcelFormat;
import static com.epsgrau.pe.sicodetri.util.Constants.LIST_INPUT_DATA;

@RestController
@RequestMapping("/api/v1/financial-fixed-asset")
public class FinancialFixedAssetController {

    @Autowired
    private FinacialFixedAssetRegisterService service;

    @PostMapping("/upload")
    public ResponseEntity<DataResponse> uploadInputDataController(@RequestParam("file")MultipartFile file,
                                                                  HttpServletRequest request) {
        String message = "";
        DataResponse responseData = new DataResponse();
        List<InputData> listInputData = new ArrayList<>();
        try {
            if(hasExcelFormat(file)) {
                listInputData = service.loadExcelFileToObject(file);
                request.getSession().setAttribute(LIST_INPUT_DATA, listInputData);
                message = "Archivo subio correctamente: " + file.getOriginalFilename();
                responseData.setMessage(message);
                responseData.setResponse(listInputData);
            } else {
                message = "Error al cargar archivo: " + file.getOriginalFilename() + "\nFormato permitido: xlsx";
                ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), message, "");
                responseData.setMessage(message);
                responseData.setResponse(exceptionResponse);
            }
        } catch (Exception e) {
            message = "Error al cargar archivo: " + file.getOriginalFilename() + "\n" + e.getMessage() + " " + e.getCause();
            ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), message, "");
            responseData.setMessage(message);
            responseData.setResponse(exceptionResponse);
        }
        return new ResponseEntity<DataResponse>(responseData, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<InputData>> getAllTaxesController(HttpSession session) {
        List<InputData> listInputData = (List<InputData>) session.getAttribute(LIST_INPUT_DATA);
        return ResponseEntity.ok(listInputData);
    }

}
