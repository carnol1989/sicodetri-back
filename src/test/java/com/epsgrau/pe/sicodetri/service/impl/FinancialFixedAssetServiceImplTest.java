package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.*;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetRepository;
import com.epsgrau.pe.sicodetri.service.*;
import com.epsgrau.pe.sicodetri.util.DataTest;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.epsgrau.pe.sicodetri.helper.FileExcelHelperTest.*;
import static com.epsgrau.pe.sicodetri.service.impl.FinancialFixedAssetServiceImpl.getListFinancialFixedAsset;
import static com.epsgrau.pe.sicodetri.util.Constants.FORMATTER_YYYY_MM_DD;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.S_VAL_332;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.TYPE_XLSX;
import static com.epsgrau.pe.sicodetri.util.DataTest.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class test for FinancialFixedAssetServiceImpl. <br/>
 * <b>Class</b>: {@link FinancialFixedAssetServiceImplTest}<br/>
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
 *     <li>Ago 17, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
class FinancialFixedAssetServiceImplTest {

    @Mock
    private Workbook mockWorkbook;

    @Mock
    private FinancialFixedAssetRepository repository;

    @Mock
    private TaxTableSunatService taxTableService;

    @Mock
    private FinancialDepreciationService financialDepreciationService;

    @Mock
    private TaxDepreciationService taxDepreciationService;

    @Mock
    private AdditionService additionService;

    @Mock
    private DeductionService deductionService;

    @Spy
    @InjectMocks
    private FinancialFixedAssetServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Return non-empty list when Excel file has content")
    void returnNonEmptyListWhenExcelFileHasContent() throws IOException {
        // Arrange
        String period = "2022-01-01";
        ByteArrayInputStream bais = createMockExcelInputStream();
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                TYPE_XLSX, bais);

        // Act
        List<InputData> result = service.loadExcelFileToObjectService(file, period);

        // Assert
        assertFalse(result.isEmpty(), "The result should not be empty when the file has content.");
        assertEquals(1, result.size(), "The result should contain one InputData object.");
        assertEquals("21.02.010149", result.get(0).getMainFixedAssetCode());
        assertEquals("", result.get(0).getFixedAssetCodeComponent());
    }

    @Test
    @DisplayName("Return empty list when Excel file is empty")
    void returnEmptyListWhenExcelFileIsEmpty() throws IOException {
        // Arrange
        String period = "2022-01-01";
        ByteArrayInputStream bais = createMockExcelEmpty();
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                TYPE_XLSX, bais);

        // Act
        List<InputData> result = service.loadExcelFileToObjectService(file, period);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Throw RuntimeException when IOException occurs during Excel parsing")
    void throwRuntimeExceptionWhenIOExceptionOccursDuringExcelParsing() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Test exception"));
        String period = "2024-01";

        // Act
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            service.loadExcelFileToObjectService(file, period);
        });

        // Assert
        assertTrue(thrown.getMessage().contains("Failed to parse Excel file"), "Exception message should indicate failure to parse Excel file.");
    }

    @Test
    @DisplayName("Save all assets when inputDataList is non-empty")
    void saveAllAssetsWhenInputDataListIsNonEmpty() {
        // Arrange
        String accountingPeriod = "2024-01";
        List<InputData> listInputData = getListInputData();

        // Act
        service.saveAllService(accountingPeriod, listInputData);

        // Assert
        ArgumentCaptor<List<FinancialFixedAsset>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());

        List<FinancialFixedAsset> capturedList = captor.getValue();
        List<FinancialFixedAsset> expectedList = getListFinancialFixedAsset(accountingPeriod, listInputData);

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
    @DisplayName("Save empty list when inputDataList is empty")
    void saveEmptyListWhenInputDataListIsEmpty() {
        // Arrange
        String accountingPeriod = "2022-01-01";
        List<InputData> listInputData = new ArrayList<>();

        // Act
        service.saveAllService(accountingPeriod, listInputData);

        // Assert
        ArgumentCaptor<List<FinancialFixedAsset>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());
        List<FinancialFixedAsset> capturedList = captor.getValue();
        List<FinancialFixedAsset> expectedList = getListFinancialFixedAsset(accountingPeriod, listInputData);
        assertEquals(expectedList.size(), capturedList.size());
    }

    @Test
    @DisplayName("Return matching assets when getListFinancialFixedAsset is called with matching data")
    public void returnMatchingAssetsWhenGetListFinancialFixedAssetIsCalledWithMatchingData() {
        String accountingPeriod = "2022-01-01";
        List<InputData> inputDataList = getListInputData();
        FinancialFixedAsset expectedAsset = getFinancialFixedAsset012();

        // Act
        List<FinancialFixedAsset> result = FinancialFixedAssetServiceImpl.getListFinancialFixedAsset(accountingPeriod, inputDataList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        FinancialFixedAsset actualAsset = result.get(0);
        assertEquals(expectedAsset.getAccountingPeriod(), actualAsset.getAccountingPeriod());
        assertEquals(expectedAsset.getMainFixedAssetCode(), actualAsset.getMainFixedAssetCode());
        assertEquals(expectedAsset.getFixedAssetCodeComponent(), actualAsset.getFixedAssetCodeComponent());
        assertEquals(expectedAsset.getFixedAssetAccountingAccount(), actualAsset.getFixedAssetAccountingAccount());
        assertEquals("CARNOL", actualAsset.getAddedBy());
        assertNotNull(actualAsset.getAdditionDate()); // Check if additionDate is set
    }

    @Test
    @DisplayName("Return empty list when getListFinancialFixedAsset is called with empty inputDataList")
    public void returnEmptyListWhenGetListFinancialFixedAssetIsCalledWithEmptyList() {
        // Arrange
        String accountingPeriod = "2022-01-01";
        List<InputData> inputDataList = new ArrayList<>();

        // Act
        List<FinancialFixedAsset> result = FinancialFixedAssetServiceImpl.getListFinancialFixedAsset(accountingPeriod, inputDataList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Throw IllegalArgumentException when getListFinancialFixedAsset receives null InputData")
    void throwIllegalArgumentExceptionWhenGetListFinancialFixedAssetReceivesNullInputData() {
        // Arrange
        String accountingPeriod = "2022-01-01";
        List<InputData> inputDataList = null;

        // Act
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                getListFinancialFixedAsset(accountingPeriod, inputDataList)
        );

        // Assert
        assertEquals("listInputData nulo", thrown.getMessage());
    }

    @Test
    @DisplayName("Throw RuntimeException when getListFinancialFixedAsset receives null accountingPeriod")
    void throwIllegalArgumentExceptionWhenGetListFinancialFixedAssetReceivesNullAccountingPeriod() {
        // Arrange
        InputData invalid = new InputData();
        invalid.setAccountingPeriod(null);
        List<InputData> inputDataList = List.of(invalid);

        // Act
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                getListFinancialFixedAsset(null, inputDataList)
        );

        // Assert
        assertTrue(thrown.getMessage().contains("Periodo contable nulo"));
        assertTrue(thrown.getCause() instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Throw RuntimeException when date parse fails in getListFinancialFixedAsset")
    void throwIllegalArgumentExceptionWhenDateParseFailsInGetListFinancialFixedAsset() {
        // Arrange
        InputData badDate = getListInputData().get(0);
        badDate.setAcquisitionDate("invalid-date");
        badDate.setAccountingPeriod("2022-01-01");
        List<InputData> inputDataList = List.of(badDate);

        // Act
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                getListFinancialFixedAsset("2022-01-01", inputDataList)
        );

        // Assert
        assertTrue(thrown.getMessage().contains("Formato de fecha inválido"));
        assertTrue(thrown.getCause() instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Throw RuntimeException when BigDecimal parse fails in getListFinancialFixedAsset")
    void throwIllegalArgumentExceptionWhenBigDecimalParseFailsInGetListFinancialFixedAsset() {
        // Arrange
        InputData badNumber = getListInputData().get(0);
        badNumber.setAccountingPeriod("2022-01-01");
        badNumber.setInitialBalance("notANumber");
        List<InputData> inputDataList = List.of(badNumber);

        // Act
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                getListFinancialFixedAsset("2022-01-01", inputDataList)
        );

        // Assert
        assertTrue(thrown.getMessage().contains("Número inválido"));
        assertTrue(thrown.getCause() instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Throw RuntimeException when Double parse fails in getListFinancialFixedAsset")
    void throwIllegalArgumentExceptionWhenDoubleParseFailsInGetListFinancialFixedAsset() {
        // Arrange
        InputData badDouble = getListInputData().get(0);
        badDouble.setAccountingPeriod("2022-01-01");
        badDouble.setUsefulLifeYears("notADouble");
        List<InputData> inputDataList = List.of(badDouble);

        // Act
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                getListFinancialFixedAsset("2022-01-01", inputDataList)
        );

        // Assert
        assertTrue(thrown.getMessage().contains("Número double inválido"));
        assertTrue(thrown.getCause() instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Save asset when updateService is called with valid financialFixedAsset")
    void saveAssetWhenUpdateServiceIsCalledWithValidFinancialFixedAsset() {
        // Arrange
        FinancialFixedAsset asset = getFinancialFixedAsset012();

        // Act
        service.updateService(asset);

        // Assert
        verify(repository, times(1)).save(asset);
    }

    @Test
    @DisplayName("Delete asset when deleteByIdService is called with valid id")
    void deleteAssetWhenDeleteByIdServiceIsCalledWithValidId() {
        // Arrange
        Long id = 1L;

        // Act
        service.deleteByIdService(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Delete assets by accounting period when deleteByAccountingPeriod is called")
    void deleteAssetsByAccountingPeriodWhenDeleteByAccountingPeriodIsCalled() {
        // Arrange
        LocalDate period = LocalDate.of(2022, 1, 1);

        // Act
        service.deleteByAccountingPeriod(period);

        // Assert
        verify(repository, times(1)).deleteByAccountingPeriod(period);
    }

    @Test
    @DisplayName("Persist projection records when tax table is non-null")
    void persistProjectionRecordsWhenTaxTableIsNonNull() {
        // Arrange
        LocalDate accountingPeriod = LocalDate.parse("2022-01-01", DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD));
        List<FinancialFixedAsset> fixedAssets = getListFinancialFixedAsset332();
        TaxTableSunat taxTable = getTaxTable332();
        when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(taxTable);

        // Act
        service.generateTemporayDeductionsService(accountingPeriod, fixedAssets);

        // Assert
        verify(taxTableService).findByAccountigAccountService(S_VAL_332);
        verify(financialDepreciationService, atLeastOnce()).saveAllService(anyList());
        verify(taxDepreciationService, atLeastOnce()).saveAllService(anyList());
        verify(deductionService, atLeastOnce()).saveAllService(anyList());
        verify(additionService, atLeastOnce()).saveAllService(anyList());
    }

    @Test
    @DisplayName("Skip saving when tax table is null in projection generation")
    void skipSavingWhenTaxTableIsNullInProjectionGeneration() {
        // Arrange
        LocalDate accountingPeriod = LocalDate.parse("2022-01-01", DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD));
        FinancialFixedAsset fixedAsset = getFinancialFixedAsset332();
        when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(null);
        List<FinancialFixedAsset> fixedAssets = List.of(fixedAsset);

        // Act
        service.generateTemporayDeductionsService(accountingPeriod, fixedAssets);

        // Assert
        verify(financialDepreciationService, never()).saveService(any(FinancialDepreciation.class));
    }

    @Test
    @DisplayName("Return asset when findByIdService is called with existing ID")
    void returnAssetWhenFindByIdServiceIsCalledWithExistingId() {
        // Arrange
        Long id = 1L;
        FinancialFixedAsset expectedAsset = getFinancialFixedAsset012();
        when(repository.findById(id)).thenReturn(Optional.of(expectedAsset));

        // Act
        FinancialFixedAsset result = service.findByIdService(id);

        // Assert
        assertEquals(expectedAsset.getId(), result.getId());
        assertEquals(expectedAsset.getMainFixedAssetCode(), result.getMainFixedAssetCode());
    }

    @Test
    @DisplayName("Return new asset when findByIdService is called with non-existing ID")
    void returnNewAssetWhenFindByIdServiceIsCalledWithNonExistingId() {
        // Arrange
        Long assetId = 2L;
        when(repository.findById(assetId)).thenReturn(Optional.empty());

        // Act
        FinancialFixedAsset result = service.findByIdService(assetId);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof FinancialFixedAsset);
        verify(repository).findById(assetId);
    }

    @Test
    @DisplayName("Return list when findByMainFixedAssetCodeService is called with non-empty result")
    void returnListWhenFindByMainFixedAssetCodeServiceIsCalledWithNonEmptyResult() {
        // Arrange
        String mainFixedAssetCode = "21.02.010149";
        FinancialFixedAsset fixedAsset1 = getFinancialFixedAsset012();
        FinancialFixedAsset fixedAsset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> financialFixedAssets = DataTest.getListFinancialFixedAsset();
        when(repository.findByMainFixedAssetCode(mainFixedAssetCode)).thenReturn(financialFixedAssets);

        // Act
        List<FinancialFixedAsset> result = service.findByMainFixedAssetCodeService(mainFixedAssetCode);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(result.get(0).getMainFixedAssetCode(), fixedAsset1.getMainFixedAssetCode());
        assertEquals(result.get(1).getFixedAssetAccountingAccount(), fixedAsset2.getFixedAssetAccountingAccount());
        verify(repository).findByMainFixedAssetCode(mainFixedAssetCode);
    }

    @Test
    @DisplayName("Return empty when findByMainFixedAssetCodeService is called with empty result")
    void returnEmptyWhenFindByMainFixedAssetCodeServiceIsCalledWithEmptyResult() {
        // Arrange
        String mainFixedAssetCode = "21.02.010149";
        when(repository.findByMainFixedAssetCode(mainFixedAssetCode)).thenReturn(Collections.emptyList());

        // Act
        List<FinancialFixedAsset> result = service.findByMainFixedAssetCodeService(mainFixedAssetCode);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByMainFixedAssetCode(mainFixedAssetCode);
    }

    @Test
    @DisplayName("Return list when findByFixedAssetAccountingAccountService is called with non-empty result")
    void returnListWhenFindByFixedAssetAccountingAccountServiceIsCalledWithNonEmptyResult() {
        // Arrange
        String accountingAccount = "3321161001";
        FinancialFixedAsset asset1 = getFinancialFixedAsset012();
        FinancialFixedAsset asset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> assetList = List.of(asset1, asset2);
        when(repository.findByFixedAssetAccountingAccount(accountingAccount)).thenReturn(assetList);

        // Act
        List<FinancialFixedAsset> result = service.findByFixedAssetAccountingAccountService(accountingAccount);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(asset1));
        assertTrue(result.contains(asset2));
        verify(repository).findByFixedAssetAccountingAccount(accountingAccount);
    }

    @Test
    @DisplayName("Return empty when findByFixedAssetAccountingAccountService is called with empty result")
    void returnEmptyWhenFindByFixedAssetAccountingAccountServiceIsCalledWithEmptyResult() {
        // Arrange
        String accountingAccount = "3321161001";
        when(repository.findByFixedAssetAccountingAccount(accountingAccount)).thenReturn(Collections.emptyList());

        // Act
        List<FinancialFixedAsset> result = service.findByFixedAssetAccountingAccountService(accountingAccount);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByFixedAssetAccountingAccount(accountingAccount);
    }

    @Test
    @DisplayName("Return list when findAllService is called with non-empty result")
    void returnListWhenFindAllServiceIsCalledWithNonEmptyResult() {
        // Arrange
        FinancialFixedAsset asset1 = getFinancialFixedAsset012();
        FinancialFixedAsset asset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> expectedList = List.of(asset1, asset2);
        when(repository.findAll()).thenReturn(expectedList);

        // Act
        List<FinancialFixedAsset> result = service.findAllService();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(asset1));
        assertTrue(result.contains(asset2));
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Return empty when findAllService is called with empty result")
    void returnEmptyWhenFindAllServiceIsCalledWithEmptyResult() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<FinancialFixedAsset> result = service.findAllService();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Return list when findByPeriodService is called with non-empty result")
    void returnListWhenFindByPeriodServiceIsCalledWithNonEmptyResult() {
        // Arrange
        LocalDate period = LocalDate.parse("2022-01-01");
        FinancialFixedAsset asset1 = getFinancialFixedAsset012();
        FinancialFixedAsset asset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> assetList = List.of(asset1, asset2);
        when(repository.findByAccountingPeriod(period)).thenReturn(assetList);

        // Act
        List<FinancialFixedAsset> result = service.findByPeriodService(period);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(asset1));
        assertTrue(result.contains(asset2));
        verify(repository).findByAccountingPeriod(period);
    }

    @Test
    @DisplayName("Return empty when findByPeriodService is called with empty result")
    void returnEmptyWhenFindByPeriodServiceIsCalledWithEmptyResult() {
        // Arrange
        LocalDate period = LocalDate.now();
        when(repository.findByAccountingPeriod(period)).thenReturn(Collections.emptyList());

        // Act
        List<FinancialFixedAsset> result = service.findByPeriodService(period);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByAccountingPeriod(period);
    }

    @Test
    @DisplayName("Return list when findByPeriodAndAccountStartingWithService is called with non-empty result")
    void returnListWhenFindByPeriodAndAccountStartingWithServiceIsCalledWithNonEmptyResult() {
        // Arrange
        LocalDate period = LocalDate.now();
        String accountPrefix = S_VAL_332;
        FinancialFixedAsset asset1 = getFinancialFixedAsset012();
        FinancialFixedAsset asset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> assetList = List.of(asset1, asset2);
        when(repository.findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(period, accountPrefix))
                .thenReturn(assetList);

        // Act
        List<FinancialFixedAsset> result = service.findByPeriodAndAccountStartingWithService(period, accountPrefix);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(asset1));
        assertTrue(result.contains(asset2));
        verify(repository).findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(period, accountPrefix);
    }

    @Test
    @DisplayName("Return empty when findByPeriodAndAccountStartingWithService is called with empty result")
    void returnEmptyWhenFindByPeriodAndAccountStartingWithServiceIsCalledWithEmptyResult() {
        // Arrange
        LocalDate period = LocalDate.now();
        String accountPrefix = "ACC";
        when(repository.findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(period, accountPrefix))
                .thenReturn(Collections.emptyList());

        // Act
        List<FinancialFixedAsset> result = service.findByPeriodAndAccountStartingWithService(period, accountPrefix);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(period, accountPrefix);
    }

    @Test
    @DisplayName("Return new asset when getListFinancialFixedAsset is called with null-detail fields")
    public void returnNewAssetWhenGetListFinancialFixedAssetIsCalledWithNullFields() {
        // Arrange
        String accountingPeriod = "2022-01-01";
        List<InputData> inputDataList = getListInputData();
        FinancialFixedAsset expectedAsset = getFinancialFixedAsset012();

        // Act
        List<FinancialFixedAsset> result = FinancialFixedAssetServiceImpl.getListFinancialFixedAsset(accountingPeriod, inputDataList);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        FinancialFixedAsset actualAsset = result.get(0);
        assertEquals(expectedAsset.getAccountingPeriod(), actualAsset.getAccountingPeriod());
        assertEquals(expectedAsset.getMainFixedAssetCode(), actualAsset.getMainFixedAssetCode());
        assertEquals(expectedAsset.getFixedAssetCodeComponent(), actualAsset.getFixedAssetCodeComponent());
        assertEquals(expectedAsset.getFixedAssetAccountingAccount(), actualAsset.getFixedAssetAccountingAccount());
        assertEquals("CARNOL", actualAsset.getAddedBy());
        assertNotNull(actualAsset.getAdditionDate());
    }
}