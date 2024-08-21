package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelperTest.*;
import static com.epsgrau.pe.sicodetri.util.Constants.FORMATTER_YYYY_MM_DD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinancialFixedAssetRegisterServiceImplTest {

    @InjectMocks
    private FinancialFixedAssetServiceImpl service;

    @Mock
    private FinancialFixedAssetRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("saveAllService_ShouldCallSaveAllWithCorrectData")
    void saveAllService_ShouldCallSaveAllWithCorrectData() throws IOException {
        // Arrange
        String accountingPeriod = "2024-01-01";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD);
        LocalDate period = LocalDate.parse(accountingPeriod, formatter);
        List<InputData> inputDataList = getListInputData();

        // Act
        service.saveAllService(period, inputDataList);

        // Assert
        ArgumentCaptor<List<FinancialFixedAsset>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());

        List<FinancialFixedAsset> capturedList = captor.getValue();
        List<FinancialFixedAsset> expectedList = FinancialFixedAssetServiceImpl.getListFinancialFixedAsset(period, inputDataList);

        assertEquals(expectedList.size(), capturedList.size());

        for (int i = 0; i < expectedList.size(); i++) {
            FinancialFixedAsset expected = expectedList.get(i);
            FinancialFixedAsset actual = capturedList.get(i);

            assertEquals(expected.getAccountingPeriod(), actual.getAccountingPeriod());
            assertEquals(expected.getMainFixedAssetCode(), actual.getMainFixedAssetCode());
            assertEquals(expected.getAddedBy(), actual.getAddedBy());
            assertEquals(expected.getModifiedBy(), actual.getModifiedBy());
            assertEquals(expected.getModificationDate(), actual.getModificationDate());
        }
    }

    @Test
    @DisplayName("updateService_ShouldCallSaveWithCorrectData")
    void updateService_ShouldCallSaveWithCorrectData() {
        // Arrange
        FinancialFixedAsset asset = getFinancialFixedAsset();

        // Act
        service.updateService(asset);

        // Assert
        verify(repository, times(1)).save(asset);
    }

    @Test
    @DisplayName("deleteByIdService_ShouldCallDeleteByIdWithCorrectId")
    void deleteByIdService_ShouldCallDeleteByIdWithCorrectId() {
        // Arrange
        Long id = 1L;

        // Act
        service.deleteByIdService(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("findByIdService_ShouldReturnCorrectData")
    void findByIdService_ShouldReturnCorrectData() {
        // Arrange
        Long id = 1L;
        FinancialFixedAsset expectedAsset = getFinancialFixedAsset();
        // Populate expectedAsset with necessary fields
        when(repository.findById(id)).thenReturn(Optional.of(expectedAsset));

        // Act
        FinancialFixedAsset result = service.findByIdService(id);

        // Assert
        assertEquals(expectedAsset.getId(), result.getId());
        assertEquals(expectedAsset.getMainFixedAssetCode(), result.getMainFixedAssetCode());
    }

    @Test
    @DisplayName("findAllService_ShouldReturnAllAssets")
    void findAllService_ShouldReturnAllAssets() {
        // Arrange
        List<FinancialFixedAsset> expectedList = new ArrayList<>();
        FinancialFixedAsset asset = new FinancialFixedAsset();
        expectedList.add(asset);
        when(repository.findAll()).thenReturn(expectedList);

        // Act
        List<FinancialFixedAsset> result = service.findAllService();

        // Assert
        assertEquals(expectedList, result);
    }


    @Test
    @DisplayName("loadExcelFileToObject_ShouldProcessFileCorrectly")
    void loadExcelFileToObject_ShouldProcessFileCorrectly() throws IOException {
        // Arrange
        FinancialFixedAssetServiceImpl service = new FinancialFixedAssetServiceImpl();
        MultipartFile file = mock(MultipartFile.class);

        // Mock file input stream
        ByteArrayInputStream bais = createMockExcelInputStream();
        when(file.getInputStream()).thenReturn(bais);

        // Act
        List<InputData> result = service.loadExcelFileToObject(file);

        // Assert
        assertEquals(1, result.size());
        InputData inputData = result.get(0);
        assertEquals("21.02.010149", inputData.getMainFixedAssetCode());
        assertEquals("TERRENO LAGUNA OXIDACION PAITA", inputData.getFixedAssetDetail().getDescription());
    }

    @Test
    @DisplayName("loadExcelFileToObject_ShouldHandleEmptyFile")
    void loadExcelFileToObject_ShouldHandleEmptyFile() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        when(file.getInputStream()).thenReturn(bais);
        when(file.isEmpty()).thenReturn(true);

        // Act
        List<InputData> result = service.loadExcelFileToObject(file);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("loadExcelFileToObject_ShouldHandleMalformedExcelFile")
    void loadExcelFileToObject_ShouldHandleMalformedExcelFile() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        ByteArrayInputStream bais = new ByteArrayInputStream("malformed content".getBytes());
        when(file.getInputStream()).thenReturn(bais);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            service.loadExcelFileToObject(file);
        });
    }

}