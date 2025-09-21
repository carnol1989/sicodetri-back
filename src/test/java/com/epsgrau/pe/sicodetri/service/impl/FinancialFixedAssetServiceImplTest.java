package com.epsgrau.pe.sicodetri.service.impl;

import static com.epsgrau.pe.sicodetri.helper.FileExcelHelperTest.createMockExcelEmpty;
import static com.epsgrau.pe.sicodetri.helper.FileExcelHelperTest.createMockExcelInputStream;
import static com.epsgrau.pe.sicodetri.service.impl.FinancialFixedAssetServiceImpl.getListFinancialFixedAsset;
import static com.epsgrau.pe.sicodetri.util.Constants.S_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.S_VAL_332;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.TYPE_XLSX;
import static com.epsgrau.pe.sicodetri.util.DataTest.getFinancialFixedAsset012;
import static com.epsgrau.pe.sicodetri.util.DataTest.getFinancialFixedAsset332;
import static com.epsgrau.pe.sicodetri.util.DataTest.getFinancialFixedAssetDto;
import static com.epsgrau.pe.sicodetri.util.DataTest.getListInputData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetJdbcRepository;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetRepository;
import com.epsgrau.pe.sicodetri.service.AdditionService;
import com.epsgrau.pe.sicodetri.service.DeductionService;
import com.epsgrau.pe.sicodetri.service.FinancialDepreciationService;
import com.epsgrau.pe.sicodetri.service.TaxDepreciationService;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator;
import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import com.epsgrau.pe.sicodetri.util.DataTest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Test suite for {@link FinancialFixedAssetServiceImpl}.
 *
 * <p><b>Objective</b>: Validate the service’s observable behavior across import/parsing,
 * transformation, persistence, querying, projection delegation, and JDBC-based reporting.</p>
 *
 * <p><b>Coverage</b>:</p>
 * <ul>
 *   <li><b>Excel ingestion</b>: Parses the first sheet, skips header rows, maps all expected
 *       columns, and exercises {@code formatDate} branches (slash, dash, default).</li>
 *   <li><b>Transformation</b>: {@link FinancialFixedAssetServiceImpl#getListFinancialFixedAsset(String, java.util.List, String)}
 *       enforces invariants (non-null inputs, period and user matching) and surfaces parsing errors
 *       for dates, {@code BigDecimal}, and {@code Double} with row context.</li>
 *   <li><b>Persistence</b>: Verifies {@code saveAll}, {@code save} (update), deletions by id/period,
 *       bulk deletes, and explicit {@code flush()} calls.</li>
 *   <li><b>Queries</b>: Asserts repository delegation for lookups by main code, account prefix,
 *       period + owner, and account-prefix lists; validates empty/non-empty paths.</li>
 *   <li><b>Projection</b>: Delegates to the correct {@link com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator}
 *       based on {@link com.epsgrau.pe.sicodetri.util.enums.ProjectionType}; throws on unsupported types.</li>
 *   <li><b>Reporting</b>: Delegates tax book and financial/tax depreciation detail to the JDBC repository.</li>
 *   <li><b>POJOs</b>: Sanity checks for DTO/entity getters/setters, all-args constructors, and {@code toString()}.</li>
 *   <li><b>Edge cases</b>: Handles empty {@link org.springframework.web.multipart.MultipartFile},
 *       IO exceptions during parsing, and null/empty collections.</li>
 * </ul>
 *
 * <p><b>Tooling & Fixtures</b>: JUnit Jupiter, Mockito (stubs, mocks, argument capture), Apache POI (in-memory XLSX),
 * and Spring Test’s {@code ReflectionTestUtils} for field injection.</p>
 *
 * <p><b>Design notes</b>: Tests are interaction-focused and do not bootstrap a Spring context; repository and
 * collaborator interactions are verified via Mockito. Assertions emphasize contract guarantees and error propagation.</p>
 *
 * <b>Class</b>: {@link FinancialFixedAssetServiceImplTest}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <br/><u>Service Provider</u>: Consultor TI
 *     <br/><u>Developed by</u>:
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *       <li>Jun 06, 2025 (JAR) Class creation.</li>
 *     </ul>
 * @version 1.0
 * @since 1.0
 * @see FinancialFixedAssetServiceImpl
 * @see com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator
 * @see com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetRepository
 * @see com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetJdbcRepository
 */
class FinancialFixedAssetServiceImplTest {

    @Mock
    private Workbook mockWorkbook;

    @Mock
    private FinancialFixedAssetRepository repository;

    @Mock
    private FinancialFixedAssetJdbcRepository repositoryJdbc;

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

    @Mock
    ProjectionGenerator generateTemporaryDeductions;

    @Mock
    ProjectionGenerator generateTemporaryAdditions;

    @Mock
    List<ProjectionGenerator> projectionGenerators;

    private FinancialFixedAssetServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(generateTemporaryDeductions.getType()).thenReturn(ProjectionType.TEMPORARY_DEDUCTIONS);
        when(generateTemporaryAdditions.getType()).thenReturn(ProjectionType.TEMPORARY_ADDITIONS);

        service = new FinancialFixedAssetServiceImpl(List.of(generateTemporaryDeductions, generateTemporaryAdditions));

        ReflectionTestUtils.setField(service, "repository", repository);
        ReflectionTestUtils.setField(service, "repositoryJdbc", repositoryJdbc);
        ReflectionTestUtils.setField(service, "financialDepreciationService", financialDepreciationService);
        ReflectionTestUtils.setField(service, "taxDepreciationService",    taxDepreciationService);
        ReflectionTestUtils.setField(service, "additionService",           additionService);
        ReflectionTestUtils.setField(service, "deductionService",          deductionService);
        ReflectionTestUtils.setField(service, "taxTableService",           taxTableService);
    }

    @Test
    @DisplayName("Return non-empty list when Excel file has content")
    void returnNonEmptyListWhenExcelFileHasContent() throws IOException {
        // Arrange
        String period = "2022-01-01";
        String username = "TEST";
        ByteArrayInputStream bais = createMockExcelInputStream();
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
            TYPE_XLSX, bais);

        // Act
        List<InputData> result = service.loadExcelFileToObjectService(file, period, username);

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
        String username = "TEST";
        ByteArrayInputStream bais = createMockExcelEmpty();
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
            TYPE_XLSX, bais);

        // Act
        List<InputData> result = service.loadExcelFileToObjectService(file, period, username);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Throw RuntimeException when IOException occurs during Excel parsing")
    void throwRuntimeExceptionWhenIOExceptionOccursDuringExcelParsing() throws IOException {
        // Arrange
        String username = "TEST";
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Test exception"));
        String period = "2024-01";

        // Act
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            service.loadExcelFileToObjectService(file, period, username);
        });

        // Assert
        assertTrue(thrown.getMessage().contains("Failed to parse Excel file"), "Exception message should indicate failure to parse Excel file.");
    }

    @Test
    @DisplayName("Mapear todas las columnas y cubrir ramas de formatDate ('/', '-', default)")
    void mapAllColumnsAndCoverFormatDateBranches() throws Exception {
        // Arrange: workbook con filas 0..10 (encabezado) y la fila 11 con todas las columnas que usa processCell
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Hoja1");
        for (int i = 0; i <= 10; i++) {
            sheet.createRow(i);
        }
        var row = sheet.createRow(11);

        // columnas 0..33 (sin 29)
        String[] vals = new String[34];
        vals[0]  = "MAIN-001";
        vals[1]  = "COMP-001";
        vals[2]  = "ACC-001";
        vals[3]  = "Descripción";
        vals[4]  = "Marca";
        vals[5]  = "Modelo";
        vals[6]  = "SN-001";
        vals[7]  = "100.00";
        vals[8]  = "10.00";
        vals[9]  = "1.00";
        vals[10] = "2.00";
        vals[11] = "0.50";
        vals[12] = "0.25";
        vals[13] = "900.00";
        vals[14] = "0.10";
        vals[15] = "900.10";
        vals[16] = "01/02/2020";   // con "/"
        vals[17] = "2020-03-01";   // con "-"
        vals[18] = "NA";           // ni "/" ni "-" => default
        vals[19] = "LINEA";
        vals[20] = "AUTHDOC";
        vals[21] = "5.0";
        vals[22] = "100.00";
        vals[23] = "20.00";
        vals[24] = "1.00";
        vals[25] = "2.00";
        vals[26] = "130.00";
        vals[27] = "3.00";
        vals[28] = "133.00";
        // 29 no se usa
        vals[30] = "400.00";
        vals[31] = "600.00";
        vals[32] = "700.00";
        vals[33] = "tasacion";

        for (int c = 0; c < vals.length; c++) {
            if (vals[c] != null) {
                row.createCell(c).setCellValue(vals[c]);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        MockMultipartFile file = new MockMultipartFile("file", "full.xlsx", TYPE_XLSX, baos.toByteArray());
        String period = "2020-01-01";
        String username = "USR";

        // Act
        List<InputData> out = service.loadExcelFileToObjectService(file, period, username);

        // Assert
        assertEquals(1, out.size());
        InputData d = out.get(0);
        assertEquals("MAIN-001", d.getMainFixedAssetCode());
        assertEquals("COMP-001", d.getFixedAssetCodeComponent());
        assertEquals("ACC-001",  d.getFixedAssetAccountingAccount());
        assertEquals("Descripción", d.getFixedAssetDetail().getDescription());
        assertEquals("Marca",       d.getFixedAssetDetail().getFixedAssetBrand());
        assertEquals("Modelo",      d.getFixedAssetDetail().getFixedAssetModel());
        assertEquals("SN-001",      d.getFixedAssetDetail().getFixedAssetSerialNumberAndOrPlate());
        assertEquals("100.00", d.getInitialBalance());
        assertEquals("10.00",  d.getAcquisitionsAdditions());
        assertEquals("1.00",   d.getImprovements());
        assertEquals("2.00",   d.getTransfersAndOrReclassifications());
        assertEquals("0.50",   d.getWithdrawalsAndOrRetirements());
        assertEquals("0.25",   d.getOtherAdjustments());
        assertEquals("900.00", d.getBookValueOfFixedAssets());
        assertEquals("0.10",   d.getInflationAdjustment());
        assertEquals("900.10", d.getAdjustedValueOfFixedAssets());

        // formatDate branches
        assertEquals("2020-02-01", d.getAcquisitionDate());            // "/" → yyyy-MM-dd
        assertEquals("2020-03-01", d.getStartDateFixedAssetUsage());   // "-" → unchanged
        assertEquals(S_DATE_DEFAULT, d.getRetirementDate());           // default branch

        assertEquals("LINEA",   d.getDepreciation().getAppliedMethod());
        assertEquals("AUTHDOC", d.getDepreciation().getAuthorizationDocumentNumber());
        assertEquals("5.0",     d.getUsefulLifeYears());
        assertEquals("100.00",  d.getAccumulatedDepreciationTheEndPrevious());
        assertEquals("20.00",   d.getCurrentDepreciation());
        assertEquals("1.00",    d.getCurrentDepreciationRelatedWithdrawalsRetirements());
        assertEquals("2.00",    d.getCurrentDepreciationRelatedOtherAdjustments());
        assertEquals("130.00",  d.getAccumulatedHistoricalDepreciation());
        assertEquals("3.00",    d.getDepreciationInflationAdjustment());
        assertEquals("133.00",  d.getAccumulatedDepreciationAdjustedInflation());
        assertEquals("400.00",  d.getNetFixedAssets());
        assertEquals("600.00",  d.getHistoricalCost());
        assertEquals("700.00",  d.getRevaluedAmount());
        assertEquals("tasacion",d.getWellAppraisedPreInvestmentStudyDry());
    }

    @Test
    @DisplayName("Save all assets when inputDataList is non-empty")
    void saveAllAssetsWhenInputDataListIsNonEmpty() {
        // Arrange
        String accountingPeriod = "2024-01";
        String username = "TEST";
        List<InputData> listInputData = getListInputData();

        // Act
        service.saveAllService(accountingPeriod, listInputData, username);

        // Assert
        ArgumentCaptor<List<FinancialFixedAsset>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());

        List<FinancialFixedAsset> capturedList = captor.getValue();
        List<FinancialFixedAsset> expectedList = getListFinancialFixedAsset(accountingPeriod, listInputData, username);

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
        String username = "TEST";
        List<InputData> listInputData = new ArrayList<>();

        // Act
        service.saveAllService(accountingPeriod, listInputData, username);

        // Assert
        ArgumentCaptor<List<FinancialFixedAsset>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository, times(1)).saveAll(captor.capture());
        List<FinancialFixedAsset> capturedList = captor.getValue();
        List<FinancialFixedAsset> expectedList = getListFinancialFixedAsset(accountingPeriod, listInputData, username);
        assertEquals(expectedList.size(), capturedList.size());
    }

    @Test
    @DisplayName("Return matching assets when getListFinancialFixedAsset is called with matching data")
    void returnMatchingAssetsWhenGetListFinancialFixedAssetIsCalledWithMatchingData() {
        String accountingPeriod = "2022-01-01";
        String username = "katherine_gutierrez";
        List<InputData> inputDataList = getListInputData();
        FinancialFixedAsset expectedAsset = getFinancialFixedAsset012();

        // Act
        List<FinancialFixedAsset> result = FinancialFixedAssetServiceImpl.getListFinancialFixedAsset(accountingPeriod,
            inputDataList, username);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        FinancialFixedAsset actualAsset = result.get(0);
        assertEquals(expectedAsset.getAccountingPeriod(), actualAsset.getAccountingPeriod());
        assertEquals(expectedAsset.getMainFixedAssetCode(), actualAsset.getMainFixedAssetCode());
        assertEquals(expectedAsset.getFixedAssetCodeComponent(), actualAsset.getFixedAssetCodeComponent());
        assertEquals(expectedAsset.getFixedAssetAccountingAccount(), actualAsset.getFixedAssetAccountingAccount());
        assertEquals("katherine_gutierrez", actualAsset.getAddedBy());
        assertNotNull(actualAsset.getAdditionDate()); // Check if additionDate is set
    }

    @Test
    @DisplayName("Return empty list when getListFinancialFixedAsset is called with empty inputDataList")
    void returnEmptyListWhenGetListFinancialFixedAssetIsCalledWithEmptyList() {
        // Arrange
        String accountingPeriod = "2022-01-01";
        String username = "TEST";
        List<InputData> inputDataList = new ArrayList<>();

        // Act
        List<FinancialFixedAsset> result = FinancialFixedAssetServiceImpl.getListFinancialFixedAsset(accountingPeriod,
            inputDataList, username);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Throw IllegalArgumentException when getListFinancialFixedAsset receives null InputData")
    void throwIllegalArgumentExceptionWhenGetListFinancialFixedAssetReceivesNullInputData() {
        // Arrange
        String accountingPeriod = "2022-01-01";
        String username = "TEST";
        List<InputData> inputDataList = null;

        // Act
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
            getListFinancialFixedAsset(accountingPeriod, inputDataList, username)
        );

        // Assert
        assertEquals("listInputData nulo", thrown.getMessage());
    }

    @Test
    @DisplayName("Throw IllegalArgumentException when list contains null element")
    void throwIllegalArgumentExceptionWhenListContainsNullInputData() {
        // Arrange
        String accountingPeriod = "2022-01-01";
        String username = "TEST";
        List<InputData> list = new ArrayList<>();
        list.add(null);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> getListFinancialFixedAsset(accountingPeriod, list, username));
        assertEquals("InputData nulo", ex.getMessage());
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
            getListFinancialFixedAsset(null, inputDataList, null)
        );

        // Assert
        assertTrue(thrown.getMessage().contains("Periodo contable nulo"));
        assertTrue(thrown.getCause() instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Throw RuntimeException when date parse fails in getListFinancialFixedAsset")
    void throwIllegalArgumentExceptionWhenDateParseFailsInGetListFinancialFixedAsset() {
        // Arrange
        String username = "katherine_gutierrez";
        InputData badDate = getListInputData().get(0);
        badDate.setAcquisitionDate("invalid-date");
        badDate.setAccountingPeriod("2022-01-01");
        List<InputData> inputDataList = List.of(badDate);

        // Act
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
            getListFinancialFixedAsset("2022-01-01", inputDataList, username)
        );

        // Assert
        assertTrue(thrown.getMessage().contains("Formato de fecha inválido"));
        assertTrue(thrown.getCause() instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Throw RuntimeException when BigDecimal parse fails in getListFinancialFixedAsset")
    void throwIllegalArgumentExceptionWhenBigDecimalParseFailsInGetListFinancialFixedAsset() {
        // Arrange
        String username = "katherine_gutierrez";
        InputData badNumber = getListInputData().get(0);
        badNumber.setAccountingPeriod("2022-01-01");
        badNumber.setInitialBalance("notANumber");
        List<InputData> inputDataList = List.of(badNumber);

        // Act
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
            getListFinancialFixedAsset("2022-01-01", inputDataList, username)
        );

        // Assert
        assertTrue(thrown.getMessage().contains("Número inválido"));
        assertTrue(thrown.getCause() instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Throw RuntimeException when Double parse fails in getListFinancialFixedAsset")
    void throwIllegalArgumentExceptionWhenDoubleParseFailsInGetListFinancialFixedAsset() {
        // Arrange
        String username = "katherine_gutierrez";
        InputData badDouble = getListInputData().get(0);
        badDouble.setAccountingPeriod("2022-01-01");
        badDouble.setUsefulLifeYears("notADouble");
        List<InputData> inputDataList = List.of(badDouble);

        // Act
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
            getListFinancialFixedAsset("2022-01-01", inputDataList, username)
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
        String username = "TEST";
        LocalDate period = LocalDate.of(2022, 1, 1);

        // Act
        service.deleteByAccountingPeriodAndAddedByService(period, username);

        // Assert
        verify(repository, times(1)).deleteByAccountingPeriodAndAddedBy(period, username);
        verify(repository, times(1)).flush(); // cubrir flush()
    }

    @Test
    @DisplayName("deleteAllFinancialFixedAsset borra y hace flush")
    void deleteAllFinancialFixedAssetBorraYFlush() {
        // Arrange
        List<FinancialFixedAsset> list = List.of(new FinancialFixedAsset());

        // Act
        service.deleteAllFinancialFixedAsset(list);

        // Assert
        verify(repository, times(1)).deleteAll(list);
        verify(repository, times(1)).flush();
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
        String username = "TEST";
        FinancialFixedAsset fixedAsset1 = getFinancialFixedAsset012();
        FinancialFixedAsset fixedAsset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> financialFixedAssets = DataTest.getListFinancialFixedAsset();
        when(repository.findByMainFixedAssetCodeAndAddedBy(mainFixedAssetCode, username)).thenReturn(financialFixedAssets);

        // Act
        List<FinancialFixedAsset> result = service.findByMainFixedAssetCodeAndAddedByService(mainFixedAssetCode, username);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(result.get(0).getMainFixedAssetCode(), fixedAsset1.getMainFixedAssetCode());
        assertEquals(result.get(1).getFixedAssetAccountingAccount(), fixedAsset2.getFixedAssetAccountingAccount());
        verify(repository).findByMainFixedAssetCodeAndAddedBy(mainFixedAssetCode, username);
    }

    @Test
    @DisplayName("Return empty when findByMainFixedAssetCodeService is called with empty result")
    void returnEmptyWhenFindByMainFixedAssetCodeServiceIsCalledWithEmptyResult() {
        // Arrange
        String mainFixedAssetCode = "21.02.010149";
        String username = "TEST";
        when(repository.findByMainFixedAssetCodeAndAddedBy(mainFixedAssetCode, username)).thenReturn(Collections.emptyList());

        // Act
        List<FinancialFixedAsset> result = service.findByMainFixedAssetCodeAndAddedByService(mainFixedAssetCode, username);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByMainFixedAssetCodeAndAddedBy(mainFixedAssetCode, username);
    }

    @Test
    @DisplayName("Return list when findByFixedAssetAccountingAccountService is called with non-empty result")
    void returnListWhenFindByFixedAssetAccountingAccountServiceIsCalledWithNonEmptyResult() {
        // Arrange
        String accountingAccount = "3321161001";
        String username = "TEST";
        FinancialFixedAsset asset1 = getFinancialFixedAsset012();
        FinancialFixedAsset asset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> assetList = List.of(asset1, asset2);
        when(repository.findByFixedAssetAccountingAccountAndAddedBy(accountingAccount, username)).thenReturn(assetList);

        // Act
        List<FinancialFixedAsset> result = service.findByFixedAssetAccountingAccountAndAddedByService(accountingAccount, username);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(asset1));
        assertTrue(result.contains(asset2));
        verify(repository).findByFixedAssetAccountingAccountAndAddedBy(accountingAccount, username);
    }

    @Test
    @DisplayName("Return empty when findByFixedAssetAccountingAccountService is called with empty result")
    void returnEmptyWhenFindByFixedAssetAccountingAccountServiceIsCalledWithEmptyResult() {
        // Arrange
        String accountingAccount = "3321161001";
        String username = "TEST";
        when(repository.findByFixedAssetAccountingAccountAndAddedBy(accountingAccount, username)).thenReturn(Collections.emptyList());

        // Act
        List<FinancialFixedAsset> result = service.findByFixedAssetAccountingAccountAndAddedByService(accountingAccount, username);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByFixedAssetAccountingAccountAndAddedBy(accountingAccount, username);
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
        String username = "TEST";
        FinancialFixedAsset asset1 = getFinancialFixedAsset012();
        FinancialFixedAsset asset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> assetList = List.of(asset1, asset2);
        when(repository.findByAccountingPeriodAndAddedBy(period, username)).thenReturn(assetList);

        // Act
        List<FinancialFixedAsset> result = service.findByPeriodAndAddedByService(period, username);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(asset1));
        assertTrue(result.contains(asset2));
        verify(repository).findByAccountingPeriodAndAddedBy(period, username);
    }

    @Test
    @DisplayName("Return empty when findByPeriodService is called with empty result")
    void returnEmptyWhenFindByPeriodServiceIsCalledWithEmptyResult() {
        // Arrange
        LocalDate period = LocalDate.now();
        String username = "TEST";
        when(repository.findByAccountingPeriodAndAddedBy(period, username)).thenReturn(Collections.emptyList());

        // Act
        List<FinancialFixedAsset> result = service.findByPeriodAndAddedByService(period, username);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByAccountingPeriodAndAddedBy(period, username);
    }

    @Test
    @DisplayName("Return list when findByPeriodAndAccountStartingWithService is called with non-empty result")
    void returnListWhenFindByPeriodAndAccountStartingWithServiceIsCalledWithNonEmptyResult() {
        // Arrange
        String username = "katherine_gutierrez";
        LocalDate period = LocalDate.of(2022,1,1);
        String accountPrefix = S_VAL_332;
        FinancialFixedAsset asset1 = getFinancialFixedAsset012();
        FinancialFixedAsset asset2 = getFinancialFixedAsset332();
        List<FinancialFixedAsset> assetList = List.of(asset1, asset2);
        when(repository.findByAccountingPeriodAndAddedByAndFixedAssetAccountingAccountStartingWith(period, username, accountPrefix))
            .thenReturn(assetList);

        // Act
        List<FinancialFixedAsset> result = service.findByPeriodAndAddedByAndAccountStartingWithService(period, accountPrefix, username);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(asset1));
        assertTrue(result.contains(asset2));
        verify(repository).findByAccountingPeriodAndAddedByAndFixedAssetAccountingAccountStartingWith(period, username, accountPrefix);
    }

    @Test
    @DisplayName("Return empty when findByPeriodAndAccountStartingWithService is called with empty result")
    void returnEmptyWhenFindByPeriodAndAccountStartingWithServiceIsCalledWithEmptyResult() {
        // Arrange
        String username = "TEST";
        LocalDate period = LocalDate.now();
        String accountPrefix = "ACC";
        when(repository.findByAccountingPeriodAndAddedByAndFixedAssetAccountingAccountStartingWith(period, username,
            accountPrefix)).thenReturn(Collections.emptyList());

        // Act
        List<FinancialFixedAsset> result = service.findByPeriodAndAddedByAndAccountStartingWithService(period, accountPrefix,
            username);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByAccountingPeriodAndAddedByAndFixedAssetAccountingAccountStartingWith(period, username,
            accountPrefix);
    }

    @Test
    @DisplayName("Return new asset when getListFinancialFixedAsset is called with null-detail fields")
    void returnNewAssetWhenGetListFinancialFixedAssetIsCalledWithNullFields() {
        // Arrange
        String accountingPeriod = "2022-01-01";
        String username = "katherine_gutierrez";
        List<InputData> inputDataList = getListInputData();
        FinancialFixedAsset expectedAsset = getFinancialFixedAsset012();

        // Act
        List<FinancialFixedAsset> result = FinancialFixedAssetServiceImpl.getListFinancialFixedAsset(accountingPeriod,
            inputDataList, username);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        FinancialFixedAsset actualAsset = result.get(0);
        assertEquals(expectedAsset.getAccountingPeriod(), actualAsset.getAccountingPeriod());
        assertEquals(expectedAsset.getMainFixedAssetCode(), actualAsset.getMainFixedAssetCode());
        assertEquals(expectedAsset.getFixedAssetCodeComponent(), actualAsset.getFixedAssetCodeComponent());
        assertEquals(expectedAsset.getFixedAssetAccountingAccount(), actualAsset.getFixedAssetAccountingAccount());
        assertEquals("katherine_gutierrez", actualAsset.getAddedBy());
        assertNotNull(actualAsset.getAdditionDate());
    }

    @Test
    @DisplayName("generate projection service throws on unsupported type")
    void generateProjectionServiceThrowsOnUnsupportedType() {
        List<FinancialFixedAsset> fixedAssets = Collections.emptyList();
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> service.generateProjectionService(
                null,
                LocalDate.now(),
                fixedAssets,
                "ACC",
                null
            ),
            "Cuando se pase un tipo de proyección no soportado (null), debe lanzarse IllegalArgumentException"
        );
        assertTrue(ex.getMessage().contains("no soportado"));
    }

    @Test
    @DisplayName("generate projection service delegates to correct generator")
    void generateProjectionServiceDelegatesToGenerator() {
        String username = "katherine_gutierrez";
        LocalDate date = LocalDate.of(2022,1,1);
        List<FinancialFixedAsset> assets = List.of(new FinancialFixedAsset());
        service.generateProjectionService(ProjectionType.TEMPORARY_DEDUCTIONS, date, assets, "ACC", username);
        verify(generateTemporaryDeductions).generate(username, date, assets);
        // no invoca al otro
        verify(generateTemporaryAdditions, never()).generate(any(), any(), any());
    }

    @Test
    @DisplayName("findByAccountingAccountPrefixesService delegates to repository")
    void findByAccountingAccountPrefixesServiceDelegates() {
        String username = "TEST";
        List<String> prefixes = List.of("332","333");
        when(repository.findByAddedByAndAccountingAccountPrefixes(username, prefixes))
            .thenReturn(DataTest.getListFinancialFixedAsset());
        var result = service.findByAddedByAndAccountingAccountPrefixesService(username, prefixes);
        assertEquals(2, result.size());
        verify(repository).findByAddedByAndAccountingAccountPrefixes(username, prefixes);
    }

    @Test
    @DisplayName("call get report tax book service delegates to JDBC repo")
    void callGetReportTaxBookServiceDelegates() throws Exception {
        ParamReportDto prm = new ParamReportDto();
        when(repositoryJdbc.callGetReportTaxBookJdbc(prm))
            .thenReturn(List.of(new FinancialFixedAssetDto()));
        var out = service.callGetReportTaxBookService(prm);
        assertEquals(1, out.size());
        verify(repositoryJdbc).callGetReportTaxBookJdbc(prm);
    }

    @Test
    @DisplayName("call get report detail financial and tax depreciation service delegates")
    void callGetReportDetailFinancialAndTaxDepreciationServiceDelegates() {
        ParamReportDto prm = new ParamReportDto();
        when(repositoryJdbc.callGetReportDetailFinancialAndTaxDepreciationJdbc(prm))
            .thenReturn(List.of(new FinancialFixedAssetDto()));
        var out = service.callGetReportDetailFinancialAndTaxDepreciationService(prm);
        assertEquals(1, out.size());
        verify(repositoryJdbc).callGetReportDetailFinancialAndTaxDepreciationJdbc(prm);
    }

    @Test
    @DisplayName("Return empty list when MultipartFile.isEmpty() == true")
    void returnEmptyListWhenMultipartFileIsEmpty() throws IOException {
        // Arrange
        String period = "2022-01-01";
        String username = "TEST";
        // Creamos un workbook válido en memoria
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Hoja1");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        byte[] workbookBytes = baos.toByteArray();

        // Mock del MultipartFile que indica isEmpty == true
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(workbookBytes));

        // Act
        List<InputData> result = service.loadExcelFileToObjectService(file, period, username);

        // Assert
        assertNotNull(result, "No debe ser null aunque isEmpty sea true");
        assertTrue(result.isEmpty(), "Debe devolver lista vacía cuando el archivo está vacío");
    }

    @Test
    @DisplayName("generate projection service delegates to additions generator")
    void generateProjectionServiceDelegatesToAdditionsGenerator() {
        // Arrange
        String username = "katherine_gutierrez";
        LocalDate date = LocalDate.of(2023, 6, 1);
        List<FinancialFixedAsset> assets = List.of(new FinancialFixedAsset());

        // Act
        service.generateProjectionService(
            ProjectionType.TEMPORARY_ADDITIONS,
            date,
            assets,
            "ACC",
            username
        );

        // Assert
        verify(generateTemporaryAdditions).generate(username, date, assets);
        verify(generateTemporaryDeductions, never()).generate(any(), any(), any());
    }

    @Test
    @DisplayName("Return empty list when findByAccountingAccountPrefixesService returns empty result")
    void returnEmptyListWhenFindByAccountingAccountPrefixesServiceReturnsEmptyResult() {
        // Arrange
        String username = "TEST";
        List<String> prefixes = List.of("999", "888");
        when(repository.findByAddedByAndAccountingAccountPrefixes(username, prefixes))
            .thenReturn(Collections.emptyList());

        // Act
        List<FinancialFixedAsset> result = service.findByAddedByAndAccountingAccountPrefixesService(username, prefixes);

        // Assert
        assertNotNull(result, "No debe ser null aunque no haya coincidencias");
        assertTrue(result.isEmpty(), "Debe devolver lista vacía cuando el repositorio no encuentra nada");
        verify(repository).findByAddedByAndAccountingAccountPrefixes(username, prefixes);
    }

    @Test
    @DisplayName("Entity InputData getters/setters and toString")
    void inputDataGettersSettersAndToString() {
        InputData d = new InputData();
        d.setAccountingPeriod("2022-01-01");
        d.setMainFixedAssetCode("CODE1");
        d.setFixedAssetCodeComponent("COMP1");
        d.setRowNum(7);
        d.setPhysicalNumberOfRows(42);

        assertEquals("2022-01-01", d.getAccountingPeriod());
        assertEquals("CODE1",      d.getMainFixedAssetCode());
        assertEquals("COMP1",      d.getFixedAssetCodeComponent());
        assertEquals(7,            d.getRowNum());
        assertEquals(42,           d.getPhysicalNumberOfRows());
        String s = d.toString();
        assertTrue(s.contains("accountingPeriod='2022-01-01'"));
        assertTrue(s.contains("mainFixedAssetCode='CODE1'"));
        assertTrue(s.contains("rowNum=7"));
    }

    @Test
    @DisplayName("Entity FinancialFixedAsset getters/setters and toString")
    void financialFixedAssetGettersSettersAndToString() {
        FinancialFixedAsset f = new FinancialFixedAsset();

        // básicos
        f.setId(555L);
        LocalDate ap = LocalDate.of(2025, 6, 28);
        f.setAccountingPeriod(ap);
        f.setMainFixedAssetCode("MFC555");
        f.setFixedAssetCodeComponent("COMP555");
        f.setFixedAssetAccountingAccount("332000000");
        f.setDescription("Desc");
        f.setFixedAssetBrand("BrandX");
        f.setFixedAssetModel("ModelY");
        f.setFixedAssetSerialNumberAndOrPlate("SN123");

        // BigDecimals
        f.setInitialBalance(new BigDecimal("1000.01"));
        f.setAcquisitionsAdditions(new BigDecimal("10.00"));
        f.setImprovements(new BigDecimal("5.00"));
        f.setTransfersAndOrReclassifications(new BigDecimal("2.00"));
        f.setWithdrawalsAndOrRetirements(new BigDecimal("1.00"));
        f.setOtherAdjustments(new BigDecimal("0.50"));
        f.setBookValueOfFixedAssets(new BigDecimal("800.00"));
        f.setInflationAdjustment(new BigDecimal("0.10"));
        f.setAdjustedValueOfFixedAssets(new BigDecimal("800.10"));

        // fechas
        LocalDate d1 = LocalDate.of(2020,1,1);
        LocalDate d2 = LocalDate.of(2020,2,1);
        LocalDate d3 = LocalDate.of(2030,1,1);
        f.setAcquisitionDate(d1);
        f.setStartDateFixedAssetUsage(d2);
        f.setRetirementDate(d3);

        // strings
        f.setAppliedMethod("LINEA RECTA");
        f.setAuthorizationDocumentNumber("AUTH123");
        f.setUsefulLifeYears(30.0);

        // más BigDecimals
        f.setAccumulatedDepreciationTheEndPrevious(new BigDecimal("100.00"));
        f.setCurrentDepreciation(new BigDecimal("50.00"));
        f.setCurrentDepreciationRelatedWithdrawalsRetirements(new BigDecimal("5.00"));
        f.setCurrentDepreciationRelatedOtherAdjustments(new BigDecimal("2.00"));
        f.setAccumulatedHistoricalDepreciation(new BigDecimal("150.00"));
        f.setDepreciationInflationAdjustment(new BigDecimal("1.00"));
        f.setAccumulatedDepreciationAdjustedInflation(new BigDecimal("151.00"));
        f.setNetFixedAssets(new BigDecimal("649.00"));
        f.setHistoricalCost(new BigDecimal("1200.00"));
        f.setRevaluedAmount(new BigDecimal("1300.00"));
        f.setWellAppraisedPreInvestmentStudyDry("estudio tasacion");

        // transients
        f.setRowNum(9);
        f.setPhysicalNumberOfRows(99);

        // listas
        FinancialDepreciation fd = new FinancialDepreciation();
        TaxDepreciation td = new TaxDepreciation();
        f.setDepreciacionFinancieraList(List.of(fd));
        f.setDepreciacionTributariaList(List.of(td));

        // auditoría
        f.setAddedBy("USR_ADD");
        LocalDateTime dt1 = LocalDateTime.of(2025,6,28,12,0);
        LocalDateTime dt2 = dt1.plusHours(2);
        f.setAdditionDate(dt1);
        f.setModifiedBy("USR_MOD");
        f.setModificationDate(dt2);

        // asserts
        assertEquals(555L, f.getId());
        assertEquals(ap,   f.getAccountingPeriod());
        assertEquals("MFC555", f.getMainFixedAssetCode());
        assertEquals("COMP555", f.getFixedAssetCodeComponent());
        assertEquals("332000000", f.getFixedAssetAccountingAccount());
        assertEquals("Desc", f.getDescription());
        assertEquals("BrandX", f.getFixedAssetBrand());
        assertEquals("ModelY", f.getFixedAssetModel());
        assertEquals("SN123", f.getFixedAssetSerialNumberAndOrPlate());

        assertEquals(new BigDecimal("1000.01"), f.getInitialBalance());
        assertEquals(new BigDecimal("10.00"), f.getAcquisitionsAdditions());
        assertEquals(new BigDecimal("5.00"), f.getImprovements());
        assertEquals(new BigDecimal("2.00"), f.getTransfersAndOrReclassifications());
        assertEquals(new BigDecimal("1.00"), f.getWithdrawalsAndOrRetirements());
        assertEquals(new BigDecimal("0.50"), f.getOtherAdjustments());
        assertEquals(new BigDecimal("800.00"), f.getBookValueOfFixedAssets());
        assertEquals(new BigDecimal("0.10"), f.getInflationAdjustment());
        assertEquals(new BigDecimal("800.10"), f.getAdjustedValueOfFixedAssets());

        assertEquals(d1, f.getAcquisitionDate());
        assertEquals(d2, f.getStartDateFixedAssetUsage());
        assertEquals(d3, f.getRetirementDate());

        assertEquals("LINEA RECTA", f.getAppliedMethod());
        assertEquals("AUTH123", f.getAuthorizationDocumentNumber());
        assertEquals(30.0, f.getUsefulLifeYears());

        assertEquals(new BigDecimal("100.00"), f.getAccumulatedDepreciationTheEndPrevious());
        assertEquals(new BigDecimal("50.00"), f.getCurrentDepreciation());
        assertEquals(new BigDecimal("5.00"), f.getCurrentDepreciationRelatedWithdrawalsRetirements());
        assertEquals(new BigDecimal("2.00"), f.getCurrentDepreciationRelatedOtherAdjustments());
        assertEquals(new BigDecimal("150.00"), f.getAccumulatedHistoricalDepreciation());
        assertEquals(new BigDecimal("1.00"), f.getDepreciationInflationAdjustment());
        assertEquals(new BigDecimal("151.00"), f.getAccumulatedDepreciationAdjustedInflation());
        assertEquals(new BigDecimal("649.00"), f.getNetFixedAssets());
        assertEquals(new BigDecimal("1200.00"), f.getHistoricalCost());
        assertEquals(new BigDecimal("1300.00"), f.getRevaluedAmount());
        assertEquals("estudio tasacion", f.getWellAppraisedPreInvestmentStudyDry());

        assertEquals(9, f.getRowNum());
        assertEquals(99, f.getPhysicalNumberOfRows());

        assertSame(fd, f.getDepreciacionFinancieraList().get(0));
        assertSame(td, f.getDepreciacionTributariaList().get(0));

        assertEquals("USR_ADD", f.getAddedBy());
        assertEquals(dt1, f.getAdditionDate());
        assertEquals("USR_MOD", f.getModifiedBy());
        assertEquals(dt2, f.getModificationDate());

        // toString
        String str = f.toString();
        System.out.println("str: " + str);
        assertTrue(str.contains("id=555"));
        assertTrue(str.contains("mainFixedAssetCode='MFC555'"));
        assertTrue(str.contains("netFixedAssets=649.00"));
        assertTrue(str.contains("wellAppraisedPreInvestmentStudyDry=estudio tasacion"));
        assertTrue(str.contains("rowNum=9"));
    }

    @Test
    @DisplayName("Entity ParamReportDto all-args constructor, getters and toString")
    void paramReportDtoAllArgsAndToString() {
        ParamReportDto p = new ParamReportDto(
            2024,
            "MAIN",
            "ACC001",
            "2020-01-01",
            "2020-12-31",
            "2021-01-01",
            "2021-12-31"
        );
        assertEquals(2024,    p.getYear());
        assertEquals("MAIN",  p.getMainCode());
        assertEquals("ACC001",p.getAccountingAccount());

        String s = p.toString();
        assertTrue(s.contains("year=2024"));
        assertTrue(s.contains("mainCode=MAIN"));
        assertTrue(s.contains("accountingAccount=ACC001"));
    }

    @Test
    @DisplayName("Entity FinancialFixedAssetDto getters/setters and toString")
    void financialFixedAssetDtoGettersSettersAndToString() {
        FinancialFixedAssetDto dto = getFinancialFixedAssetDto();

        assertEquals(77,  dto.getId());
        assertEquals("MFC77", dto.getMainFixedAssetCode());
        assertEquals("3321161001", dto.getFixedAssetAccountingAccount());
        assertEquals(new BigDecimal("12345.67"), dto.getNetFixedAssets());
        assertEquals("E01", dto.getErrorCode());
        assertEquals("Mensaje de error", dto.getErrorMessage());

        String s = dto.toString();
        assertTrue(s.contains("id=77"));
        assertTrue(s.contains("mainFixedAssetCode=MFC77"));
        assertTrue(s.contains("netFixedAssets=12345.67"));
        assertTrue(s.contains("errorCode=E01"));
    }

    @Test
    @DisplayName("FinancialFixedAsset AllArgsConstructor sets all fields")
    void financialFixedAssetAllArgsConstructor() {
        // Arrange: crear datos para cada campo, en el mismo orden que el AllArgsConstructor
        Long id = 1L;
        LocalDate accountingPeriod = LocalDate.of(2025, 7, 1);
        String mainCode      = "MFC001";
        String compCode      = "COMP001";
        String acctAccount   = "332000111";
        String desc          = "Descripción X";
        String brand         = "MarcaY";
        String model         = "ModeloZ";
        String serial        = "SN-123";
        BigDecimal initial   = new BigDecimal("1000.00");
        BigDecimal adds      = new BigDecimal("50.00");
        BigDecimal improves  = new BigDecimal("10.00");
        BigDecimal transfers = new BigDecimal("5.00");
        BigDecimal withdrawals= new BigDecimal("2.50");
        BigDecimal otherAdj  = new BigDecimal("1.25");
        BigDecimal bookValue = new BigDecimal("900.00");
        BigDecimal inflation = new BigDecimal("0.10");
        BigDecimal adjusted  = new BigDecimal("900.10");
        LocalDate acqDate    = LocalDate.of(2020,1,1);
        LocalDate startUsage = LocalDate.of(2020,2,1);
        LocalDate retireDate = LocalDate.of(2030,1,1);
        String method        = "LINEA RECTA";
        String authDoc       = "AUTH-001";
        Double usefulYears   = 20.0;
        BigDecimal accDepPrev= new BigDecimal("100.00");
        BigDecimal currDep   = new BigDecimal("50.00");
        BigDecimal currDepW  = new BigDecimal("2.00");
        BigDecimal currDepO  = new BigDecimal("1.00");
        BigDecimal histAccDep= new BigDecimal("150.00");
        BigDecimal depInflAdj= new BigDecimal("0.50");
        BigDecimal accDepInfAdj= new BigDecimal("150.50");
        BigDecimal netFixed  = new BigDecimal("849.50");
        BigDecimal histCost  = new BigDecimal("1200.00");
        BigDecimal revalued  = new BigDecimal("1300.00");
        String wellApp      = "tasacion 2021";
        int rowNum           = 7;
        int physRows         = 99;
        List<FinancialDepreciation> finList = List.of(new FinancialDepreciation());
        List<TaxDepreciation> taxList = List.of(new TaxDepreciation());
        String addedBy       = "USER_ADD";
        LocalDateTime addDate= LocalDateTime.of(2025,7,2,9,0);
        String modifiedBy    = "USER_MOD";
        LocalDateTime modDate= addDate.plusHours(3);

        // Act
        FinancialFixedAsset f = new FinancialFixedAsset(
            id,
            accountingPeriod,
            mainCode,
            compCode,
            acctAccount,
            desc,
            brand,
            model,
            serial,
            initial,
            adds,
            improves,
            transfers,
            withdrawals,
            otherAdj,
            bookValue,
            inflation,
            adjusted,
            acqDate,
            startUsage,
            retireDate,
            method,
            authDoc,
            usefulYears,
            accDepPrev,
            currDep,
            currDepW,
            currDepO,
            histAccDep,
            depInflAdj,
            accDepInfAdj,
            netFixed,
            histCost,
            revalued,
            wellApp,
            rowNum,
            physRows,
            finList,
            taxList,
            addedBy,
            addDate,
            modifiedBy,
            modDate
        );

        // Assert
        assertEquals(id,             f.getId());
        assertEquals(accountingPeriod, f.getAccountingPeriod());
        assertEquals(mainCode,       f.getMainFixedAssetCode());
        assertEquals(compCode,       f.getFixedAssetCodeComponent());
        assertEquals(acctAccount,    f.getFixedAssetAccountingAccount());
        assertEquals(desc,           f.getDescription());
        assertEquals(brand,          f.getFixedAssetBrand());
        assertEquals(model,          f.getFixedAssetModel());
        assertEquals(serial,         f.getFixedAssetSerialNumberAndOrPlate());
        assertEquals(initial,        f.getInitialBalance());
        assertEquals(adds,           f.getAcquisitionsAdditions());
        assertEquals(improves,       f.getImprovements());
        assertEquals(transfers,      f.getTransfersAndOrReclassifications());
        assertEquals(withdrawals,    f.getWithdrawalsAndOrRetirements());
        assertEquals(otherAdj,       f.getOtherAdjustments());
        assertEquals(bookValue,      f.getBookValueOfFixedAssets());
        assertEquals(inflation,      f.getInflationAdjustment());
        assertEquals(adjusted,       f.getAdjustedValueOfFixedAssets());
        assertEquals(acqDate,        f.getAcquisitionDate());
        assertEquals(startUsage,     f.getStartDateFixedAssetUsage());
        assertEquals(retireDate,     f.getRetirementDate());
        assertEquals(method,         f.getAppliedMethod());
        assertEquals(authDoc,        f.getAuthorizationDocumentNumber());
        assertEquals(usefulYears,    f.getUsefulLifeYears());
        assertEquals(accDepPrev,     f.getAccumulatedDepreciationTheEndPrevious());
        assertEquals(currDep,        f.getCurrentDepreciation());
        assertEquals(currDepW,       f.getCurrentDepreciationRelatedWithdrawalsRetirements());
        assertEquals(currDepO,       f.getCurrentDepreciationRelatedOtherAdjustments());
        assertEquals(histAccDep,     f.getAccumulatedHistoricalDepreciation());
        assertEquals(depInflAdj,     f.getDepreciationInflationAdjustment());
        assertEquals(accDepInfAdj,   f.getAccumulatedDepreciationAdjustedInflation());
        assertEquals(netFixed,       f.getNetFixedAssets());
        assertEquals(histCost,       f.getHistoricalCost());
        assertEquals(revalued,       f.getRevaluedAmount());
        assertEquals(wellApp,        f.getWellAppraisedPreInvestmentStudyDry());
        assertEquals(rowNum,         f.getRowNum());
        assertEquals(physRows,       f.getPhysicalNumberOfRows());
        assertSame(finList,          f.getDepreciacionFinancieraList());
        assertSame(taxList,          f.getDepreciacionTributariaList());
        assertEquals(addedBy,        f.getAddedBy());
        assertEquals(addDate,        f.getAdditionDate());
        assertEquals(modifiedBy,     f.getModifiedBy());
        assertEquals(modDate,        f.getModificationDate());

        // también cubrimos toString()
        String repr = f.toString();
        assertTrue(repr.contains("id=1"));
        assertTrue(repr.contains("mainFixedAssetCode='MFC001'"));
        assertTrue(repr.contains("wellAppraisedPreInvestmentStudyDry=tasacion 2021"));
    }

}