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
import org.springframework.transaction.annotation.Transactional;
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
import static com.epsgrau.pe.sicodetri.util.DataTest.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinancialFixedAssetServiceImplTest {

    @Mock
    private Workbook mockWorkbook;

    @Mock
    private FinancialFixedAssetRepository repository;

    @Mock
    private TaxTableSunatService taxTableService;

    @Mock
    private FixedAssetDepreciationService depreciationService;

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
    @DisplayName("Test when file has content")
    void testLoadExcelFileToObjectWhenFileHasContent() throws IOException {
        // Arrange
        String period = "2022-01-01";
        ByteArrayInputStream bais = createMockExcelInputStream();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(bais);

        // Act
        List<InputData> result = service.loadExcelFileToObjectService(file, period);

        // Assert
        assertFalse(result.isEmpty(), "The result should not be empty when the file has content.");
        assertEquals(1, result.size(), "The result should contain one InputData object.");
        assertEquals("21.02.010149", result.get(0).getMainFixedAssetCode());
        assertEquals("", result.get(0).getFixedAssetCodeComponent());
    }

    @Test
    @DisplayName("Test when file is empty")
    void testLoadExcelFileToObjectWhenFileIsEmpty() throws IOException {
        // Arrange
        String period = "2022-01-01";
        MultipartFile file = mock(MultipartFile.class);
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        when(file.getInputStream()).thenReturn(bais);
        when(file.isEmpty()).thenReturn(true);

        // Act
        List<InputData> result = service.loadExcelFileToObjectService(file, period);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test when an IOException is thrown")
    void testLoadExcelFileToObjectThrowsIOException() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Test exception"));
        String period = "2024-01";

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            service.loadExcelFileToObjectService(file, period);
        });

        assertTrue(thrown.getMessage().contains("fail to parse Excel file"), "Exception message should indicate failure to parse Excel file.");
    }

    @Test
    @DisplayName("Test saveAllService with non-empty list")
    void testSaveAllServiceWithNonEmptyList() {
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
    @DisplayName("Test saveAllService with empty list")
    void testSaveAllServiceWithEmptyList() {
        // Arrange
        String accountingPeriod = "2022-01-01";
        List<InputData> listInputData = new ArrayList<>();
        List<FinancialFixedAsset> listFinancialFixedAssets = new ArrayList<>();

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
    public void testGetListFinancialFixedAsset() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD);
        String accountingPeriod = "2022-01-01";
        List<InputData> inputDataList = getListInputData();

        // Expected output
        FinancialFixedAsset expectedAsset = getFinancialFixedAsset012();

        // Execute method
        List<FinancialFixedAsset> result = FinancialFixedAssetServiceImpl.getListFinancialFixedAsset(accountingPeriod, inputDataList);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size()); // Only one matching record
        FinancialFixedAsset actualAsset = result.get(0);

        assertEquals(expectedAsset.getAccountingPeriod(), actualAsset.getAccountingPeriod());
        assertEquals(expectedAsset.getMainFixedAssetCode(), actualAsset.getMainFixedAssetCode());
        assertEquals(expectedAsset.getFixedAssetCodeComponent(), actualAsset.getFixedAssetCodeComponent());
        assertEquals(expectedAsset.getFixedAssetAccountingAccount(), actualAsset.getFixedAssetAccountingAccount());
        // Assert other fields...

        assertEquals("CARNOL", actualAsset.getAddedBy());
        assertNotNull(actualAsset.getAdditionDate()); // Check if additionDate is set
    }

    @Test
    public void testGetListFinancialFixedAssetWithNullFields() {
        String accountingPeriod = "2022-01-01";
        List<InputData> inputDataList = getListInputData();

        FinancialFixedAsset expectedAsset = getFinancialFixedAsset012();

        List<FinancialFixedAsset> result = FinancialFixedAssetServiceImpl.getListFinancialFixedAsset(accountingPeriod, inputDataList);

        assertNotNull(result);
        assertEquals(1, result.size());
        FinancialFixedAsset actualAsset = result.get(0);

        assertEquals(expectedAsset.getAccountingPeriod(), actualAsset.getAccountingPeriod());
        assertEquals(expectedAsset.getMainFixedAssetCode(), actualAsset.getMainFixedAssetCode());
        assertEquals(expectedAsset.getFixedAssetCodeComponent(), actualAsset.getFixedAssetCodeComponent());
        assertEquals(expectedAsset.getFixedAssetAccountingAccount(), actualAsset.getFixedAssetAccountingAccount());
        // Assert other fields...

        assertEquals("CARNOL", actualAsset.getAddedBy());
        assertNotNull(actualAsset.getAdditionDate());
    }

    @Test
    public void testGetListFinancialFixedAssetWithEmptyList() {
        String accountingPeriod = "2022-01-01";
        List<InputData> inputDataList = new ArrayList<>();

        List<FinancialFixedAsset> result = FinancialFixedAssetServiceImpl.getListFinancialFixedAsset(accountingPeriod, inputDataList);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test updateService with a valid FinancialFixedAsset")
    void testUpdateService() {
        // Arrange
        FinancialFixedAsset asset = getFinancialFixedAsset012();

        // Act
        service.updateService(asset);

        // Assert
        verify(repository, times(1)).save(asset);
    }

    @Test
    @DisplayName("Test deleteByIdService")
    void testDeleteByIdService() {
        // Arrange
        Long id = 1L;

        // Act
        service.deleteByIdService(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Test deleteByAccountingPeriod")
    void testDeleteByAccountingPeriod() {
        // Arrange
        LocalDate period = LocalDate.of(2022, 1, 1);

        // Act
        service.deleteByAccountingPeriod(period);

        // Assert
        verify(repository, times(1)).deleteByAccountingPeriod(period);
    }

    @Test
    @Transactional
    @DisplayName("testGenerateFixedAssetDepreciationService")
    void testGenerateFixedAssetDepreciationService() {
        LocalDate accountingPeriod = LocalDate.now();
        FinancialFixedAsset fixedAsset = getFinancialFixedAsset332();

        // Mock behavior for taxTableService
        TaxTableSunat taxTable = getTaxTable332();
        when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(taxTable);

        // Mock behavior for financialDepreciationService and taxDepreciationService
        FinancialDepreciation financialDepreciation = new FinancialDepreciation();
        TaxDepreciation taxDepreciation = new TaxDepreciation();
        List<Addition> additions = new ArrayList<>();
        Addition addition = new Addition();
        additions.add(addition);
        List<Deduction> deductions = new ArrayList<>();
        Deduction deduction = new Deduction();
        deductions.add(deduction);
        taxDepreciation.setAdditions(additions);
        taxDepreciation.setDeductions(deductions);

        // Call the method under test
        service.generateFixedAssetDepreciationService(accountingPeriod, fixedAsset);

        // Verify interactions and state changes
        verify(taxTableService).findByAccountigAccountService(S_VAL_332);
        verify(depreciationService).saveService(any(FixedAssetDepreciation.class));
        verify(financialDepreciationService).saveService(any(FinancialDepreciation.class));
        verify(taxDepreciationService).saveService(any(TaxDepreciation.class));
    }

    @Test
    @DisplayName("testGenerateProjectionAdditionsAndDeductions")
    void testGenerateProjectionAdditionsAndDeductions() {
        // Arrange
        String accountingPeriod = "2024-01-01";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD);
        LocalDate period = LocalDate.parse(accountingPeriod, formatter);
        List<FinancialFixedAsset> financialFixedAssets = new ArrayList<>();
        FinancialFixedAsset fixedAsset = getFinancialFixedAsset332();
        financialFixedAssets.add(fixedAsset);
        TaxTableSunat taxTable = getTaxTable332();

        when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(taxTable);

        // Act
        service.generateProjectionAdditionsAndDeductionsService(period, financialFixedAssets);

        // Assert
        verify(taxTableService, times(1)).findByAccountigAccountService(S_VAL_332);
        verify(depreciationService, atLeast(1)).saveService(any(FixedAssetDepreciation.class));
        verify(financialDepreciationService, atLeast(1)).saveService(any(FinancialDepreciation.class));
    }

    @Test
    @DisplayName("testGenerateProjectionAdditionsAndDeductionsService with Non-Null Tax Table")
    void testGenerateProjectionAdditionsAndDeductionsServiceWithNonNullTaxTable() {
        LocalDate accountingPeriod = LocalDate.parse("2022-01-01");
        FinancialFixedAsset fixedAsset = getFinancialFixedAsset332();
        List<FinancialFixedAsset> fixedAssets = List.of(fixedAsset);
        TaxTableSunat taxTable = getTaxTable332();

        // Setup mock behaviors
        when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(taxTable);

        // Call the method under test
        service.generateProjectionAdditionsAndDeductionsService(accountingPeriod, fixedAssets);

        // Verify interactions with mocks
        verify(taxTableService).findByAccountigAccountService(S_VAL_332);
        verify(depreciationService, atLeastOnce()).saveService(any(FixedAssetDepreciation.class));
        verify(financialDepreciationService, atLeastOnce()).saveService(any(FinancialDepreciation.class));
    }

    @Test
    @DisplayName("testGenerateProjectionAdditionsAndDeductionsService with Null Tax Table")
    void testGenerateProjectionAdditionsAndDeductionsServiceWithNullTaxTable() {
        LocalDate accountingPeriod = LocalDate.parse("2022-01-01");
        FinancialFixedAsset fixedAsset = getFinancialFixedAsset332();

        // Setup mock behaviors
        when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(null);

        List<FinancialFixedAsset> fixedAssets = List.of(fixedAsset);

        // Call the method under test
        service.generateProjectionAdditionsAndDeductionsService(accountingPeriod, fixedAssets);

        // Verify that saveService was not called since taxTable is null
        verify(depreciationService, never()).saveService(any(FixedAssetDepreciation.class));
        verify(financialDepreciationService, never()).saveService(any(FinancialDepreciation.class));
    }

    @Test
    @DisplayName("testFindByIdService with Existing Asset")
    void testFindByIdServiceWithExistingAsset() {
        // Arrange
        Long id = 1L;
        FinancialFixedAsset expectedAsset = getFinancialFixedAsset012();
        // Populate expectedAsset with necessary fields
        when(repository.findById(id)).thenReturn(Optional.of(expectedAsset));

        // Act
        FinancialFixedAsset result = service.findByIdService(id);

        // Assert
        assertEquals(expectedAsset.getId(), result.getId());
        assertEquals(expectedAsset.getMainFixedAssetCode(), result.getMainFixedAssetCode());
    }

    @Test
    @DisplayName("testFindByIdService with Non-Existing Asset")
    void testFindByIdServiceWithNonExistingAsset() {
        Long assetId = 2L;

        // Mock the repository behavior
        when(repository.findById(assetId)).thenReturn(Optional.empty());

        // Call the method under test
        FinancialFixedAsset result = service.findByIdService(assetId);

        // Assert the result is a new instance of FinancialFixedAsset
        assertNotNull(result);
        assertTrue(result instanceof FinancialFixedAsset);

        // Verify that repository.findById was called with the correct ID
        verify(repository).findById(assetId);
    }

    @Test
    @DisplayName("testFindByMainFixedAssetCodeService with Non-Empty List")
    void testFindByMainFixedAssetCodeServiceWithNonEmptyList() {
        String mainFixedAssetCode = "21.02.010149";
        FinancialFixedAsset fixedAsset1 = getFinancialFixedAsset012();
        FinancialFixedAsset fixedAsset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> financialFixedAssets = DataTest.getListFinancialFixedAsset();

        // Mock the repository behavior
        when(repository.findByMainFixedAssetCode(mainFixedAssetCode)).thenReturn(financialFixedAssets);

        // Call the method under test
        List<FinancialFixedAsset> result = service.findByMainFixedAssetCodeService(mainFixedAssetCode);

        // Assert the result is as expected
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(result.get(0).getMainFixedAssetCode(), fixedAsset1.getMainFixedAssetCode());
        assertEquals(result.get(1).getFixedAssetAccountingAccount(), fixedAsset2.getFixedAssetAccountingAccount());

        // Verify that repository.findByMainFixedAssetCode was called with the correct code
        verify(repository).findByMainFixedAssetCode(mainFixedAssetCode);
    }

    @Test
    @DisplayName("testFindByMainFixedAssetCodeService with Empty List")
    void testFindByMainFixedAssetCodeServiceWithEmptyList() {
        String mainFixedAssetCode = "21.02.010149";

        // Mock the repository behavior
        when(repository.findByMainFixedAssetCode(mainFixedAssetCode)).thenReturn(Collections.emptyList());

        // Call the method under test
        List<FinancialFixedAsset> result = service.findByMainFixedAssetCodeService(mainFixedAssetCode);

        // Assert the result is as expected
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify that repository.findByMainFixedAssetCode was called with the correct code
        verify(repository).findByMainFixedAssetCode(mainFixedAssetCode);
    }

    @Test
    @DisplayName("testFindByFixedAssetAccountingAccountService with Non-Empty List")
    void testFindByFixedAssetAccountingAccountServiceWithNonEmptyList() {
        String accountingAccount = "3321161001";
        FinancialFixedAsset asset1 = getFinancialFixedAsset012();
        FinancialFixedAsset asset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> assetList = List.of(asset1, asset2);

        // Mock the repository behavior
        when(repository.findByFixedAssetAccountingAccount(accountingAccount)).thenReturn(assetList);

        // Call the method under test
        List<FinancialFixedAsset> result = service.findByFixedAssetAccountingAccountService(accountingAccount);

        // Assert the result is as expected
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(asset1));
        assertTrue(result.contains(asset2));

        // Verify that repository.findByFixedAssetAccountingAccount was called with the correct accounting account
        verify(repository).findByFixedAssetAccountingAccount(accountingAccount);
    }

    @Test
    @DisplayName("testFindByFixedAssetAccountingAccountService with Empty List")
    void testFindByFixedAssetAccountingAccountServiceWithEmptyList() {
        String accountingAccount = "3321161001";

        // Mock the repository behavior
        when(repository.findByFixedAssetAccountingAccount(accountingAccount)).thenReturn(Collections.emptyList());

        // Call the method under test
        List<FinancialFixedAsset> result = service.findByFixedAssetAccountingAccountService(accountingAccount);

        // Assert the result is as expected
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify that repository.findByFixedAssetAccountingAccount was called with the correct accounting account
        verify(repository).findByFixedAssetAccountingAccount(accountingAccount);
    }

    @Test
    @DisplayName("testFindAllService with Non-Empty List")
    void testFindAllServiceWithNonEmptyList() {
        // Arrange
        FinancialFixedAsset asset1 = getFinancialFixedAsset012();
        FinancialFixedAsset asset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> expectedList = List.of(asset1, asset2);

        // Mock the repository behavior
        when(repository.findAll()).thenReturn(expectedList);

        // Call the method under test
        List<FinancialFixedAsset> result = service.findAllService();

        // Assert the result is as expected
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(asset1));
        assertTrue(result.contains(asset2));

        // Verify that repository.findAll was called
        verify(repository).findAll();
    }

    @Test
    @DisplayName("testFindAllService with Empty List")
    void testFindAllServiceWithEmptyList() {
        // Mock the repository behavior
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Call the method under test
        List<FinancialFixedAsset> result = service.findAllService();

        // Assert the result is as expected
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify that repository.findAll was called
        verify(repository).findAll();
    }

    @Test
    @DisplayName("testFindByPeriodService with Non-Empty List")
    void testFindByPeriodServiceWithNonEmptyList() {
        LocalDate period = LocalDate.parse("2022-01-01");
        FinancialFixedAsset asset1 = getFinancialFixedAsset012();
        FinancialFixedAsset asset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> assetList = List.of(asset1, asset2);

        // Mock the repository behavior
        when(repository.findByAccountingPeriod(period)).thenReturn(assetList);

        // Call the method under test
        List<FinancialFixedAsset> result = service.findByPeriodService(period);

        // Assert the result is as expected
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(asset1));
        assertTrue(result.contains(asset2));

        // Verify that repository.findByAccountingPeriod was called with the correct period
        verify(repository).findByAccountingPeriod(period);
    }

    @Test
    @DisplayName("testFindByPeriodService with Empty List")
    void testFindByPeriodServiceWithEmptyList() {
        LocalDate period = LocalDate.now();

        // Mock the repository behavior
        when(repository.findByAccountingPeriod(period)).thenReturn(Collections.emptyList());

        // Call the method under test
        List<FinancialFixedAsset> result = service.findByPeriodService(period);

        // Assert the result is as expected
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify that repository.findByAccountingPeriod was called with the correct period
        verify(repository).findByAccountingPeriod(period);
    }

    @Test
    @DisplayName("testFindByPeriodAndAccountStartingWithService with Non-Empty List")
    void testFindByPeriodAndAccountStartingWithServiceWithNonEmptyList() {
        LocalDate period = LocalDate.now();
        String accountPrefix = S_VAL_332;
        FinancialFixedAsset asset1 = getFinancialFixedAsset012();
        FinancialFixedAsset asset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> assetList = List.of(asset1, asset2);

        // Mock the repository behavior
        when(repository.findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(period, accountPrefix))
                .thenReturn(assetList);

        // Call the method under test
        List<FinancialFixedAsset> result = service.findByPeriodAndAccountStartingWithService(period, accountPrefix);

        // Assert the result is as expected
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(asset1));
        assertTrue(result.contains(asset2));

        // Verify that repository.findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith was called with the correct parameters
        verify(repository).findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(period, accountPrefix);
    }

    @Test
    @DisplayName("testFindByPeriodAndAccountStartingWithService with Empty List")
    void testFindByPeriodAndAccountStartingWithServiceWithEmptyList() {
        LocalDate period = LocalDate.now();
        String accountPrefix = "ACC";

        // Mock the repository behavior
        when(repository.findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(period, accountPrefix))
                .thenReturn(Collections.emptyList());

        // Call the method under test
        List<FinancialFixedAsset> result = service.findByPeriodAndAccountStartingWithService(period, accountPrefix);

        // Assert the result is as expected
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify that repository.findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith was called with the correct parameters
        verify(repository).findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(period, accountPrefix);
    }

}