package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.model.InputData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelperTest.createMockExcelInputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FinancialFixedAssetRegisterServiceImplTest {

    @Test
    @DisplayName("loadExcelFileToObject_ShouldProcessFileCorrectly")
    void loadExcelFileToObject_ShouldProcessFileCorrectly() throws IOException {
        // Arrange
        FinancialFixedAssetRegisterServiceImpl service = new FinancialFixedAssetRegisterServiceImpl();
        MultipartFile file = mock(MultipartFile.class);

        // Mock file input stream
        ByteArrayInputStream bais = createMockExcelInputStream();
        when(file.getInputStream()).thenReturn(bais);

        // Act
        List<InputData> result = service.loadExcelFileToObject(file);

        // Assert
        assertEquals(1, result.size());  // Adjust based on the mock data
        InputData inputData = result.get(0);
        assertEquals("21.02.010149", inputData.getMainFixedAssetCode());
        assertEquals("TERRENO LAGUNA OXIDACION PAITA", inputData.getFixedAssetDetail().getDescription());

    }

}