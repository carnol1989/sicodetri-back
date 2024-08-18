package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.model.InputData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FinacialFixedAssetRegisterService {

    List<InputData> loadExcelFileToObject(MultipartFile file);

}
