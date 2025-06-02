package com.epsgrau.pe.sicodetri.repository.impl;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants._BLANK;
import static com.epsgrau.pe.sicodetri.util.Constants.CALL_SQL;
import static com.epsgrau.pe.sicodetri.util.Constants.IDX_OUT_ERROR_CODE;
import static com.epsgrau.pe.sicodetri.util.Constants.IDX_OUT_ERROR_MESSAGE;
import static com.epsgrau.pe.sicodetri.util.Constants.IDX_OUT_RESULT_SET_CURSOR;
import static com.epsgrau.pe.sicodetri.util.Constants.IDX_YEAR;
import static com.epsgrau.pe.sicodetri.util.Constants.IDX_MAIN_CODE;
import static com.epsgrau.pe.sicodetri.util.Constants.IDX_ACCOUNTING_ACCOUNT;
import static com.epsgrau.pe.sicodetri.util.Constants.IDX_START_ACQ_DATE;
import static com.epsgrau.pe.sicodetri.util.Constants.IDX_END_ACQ_DATE;
import static com.epsgrau.pe.sicodetri.util.Constants.IDX_START_DATE_USE;
import static com.epsgrau.pe.sicodetri.util.Constants.IDX_END_DATE_USE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("FinancialFixedAssetJdbcImplRepository Tests")
class FinancialFixedAssetJdbcImplRepositoryTest {

    @InjectMocks
    private FinancialFixedAssetJdbcImplRepository repository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private CallableStatement callableStatement;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Return IllegalStateException when DataSource is null")
    void returnIllegalStateExceptionWhenDataSourceIsNull() {
        // Arrange: jdbcTemplate.getDataSource() returns null
        when(jdbcTemplate.getDataSource()).thenReturn(null);
        ParamReportDto params = new ParamReportDto();
        params.setYear(2022);
        params.setMainCode("MC");
        params.setAccountingAccount("ACC");
        params.setStartAcquisitionDate("2022-01-01");
        params.setEndAcquisitionDate("2022-12-31");
        params.setStartDateUse("2022-02-01");
        params.setEndDateUse("2022-11-30");

        // Act & Assert
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> repository.callGetReportTaxBookJdbc(params),
                "When DataSource is null, should throw IllegalStateException"
        );
        assertTrue(ex.getMessage().contains("DataSource no está configurado"));
        verify(jdbcTemplate, times(1)).getDataSource();
    }

    @Test
    @DisplayName("Wrap SQLException as RuntimeException when getConnection fails")
    void wrapSQLExceptionAsRuntimeExceptionWhenGetConnectionFails() throws Exception {
        // Arrange: a DataSource is present but getConnection() throws SQLException
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenThrow(new SQLException("DB down"));

        ParamReportDto params = new ParamReportDto();
        params.setYear(2022);
        params.setMainCode("MC");
        params.setAccountingAccount("ACC");
        params.setStartAcquisitionDate("2022-01-01");
        params.setEndAcquisitionDate("2022-12-31");
        params.setStartDateUse("2022-02-01");
        params.setEndDateUse("2022-11-30");

        // Act & Assert
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> repository.callGetReportTaxBookJdbc(params),
                "When getConnection() fails, should wrap in RuntimeException"
        );
        assertTrue(ex.getMessage().contains("Error al obtener información Libro Tributario"),
                "Exception message should indicate stored‐procedure failure");
        verify(dataSource, times(1)).getConnection();
        verifyNoMoreInteractions(callableStatement, resultSet);
    }

    @Test
    @DisplayName("CallStoredProcedure_Successfully maps non‐null and null columns into DTO list")
    void callStoredProcedureSuccessfullyMapsRowsIntoDto() throws Exception {
        // Arrange
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareCall(CALL_SQL)).thenReturn(callableStatement);

        // a) OUT parameters simulados
        when(callableStatement.getString(IDX_OUT_ERROR_CODE)).thenReturn("E001");
        when(callableStatement.getString(IDX_OUT_ERROR_MESSAGE)).thenReturn("NoError");

        // b) El cursor OUT (índice 10) devuelve nuestro mock ResultSet
        when(callableStatement.getObject(IDX_OUT_RESULT_SET_CURSOR)).thenReturn(resultSet);

        // c) El ResultSet tiene exactamente una fila:
        when(resultSet.next()).thenReturn(true, false);

        // d) Columnas STRING (algunas no‐null, otras null → debe aplicar default “”):
        when(resultSet.getString("CODIGO_ACTIVO_FIJO_PRINCIPAL")).thenReturn("PRINC123");
        when(resultSet.getString("CODIGO_ACTIVO_FIJO_COMPONENTE")).thenReturn(null);
        when(resultSet.getString("CUENTA_CONTABLE_ACTIVO_FIJO")).thenReturn("ACC001");
        when(resultSet.getString("DESCRIPTION")).thenReturn(null);
        when(resultSet.getString("MARCA_ACTIVO_FIJO")).thenReturn("BRAND");
        when(resultSet.getString("MODELO_ACTIVO_FIJO")).thenReturn(null);
        when(resultSet.getString("NUMERO_SERIE_PLACA_ACTIVO_FIJO")).thenReturn("SN-001");

        // e) Columnas BigDecimal (unas no‐null, otras null → debe aplicar BigDecimal.ZERO):
        when(resultSet.getBigDecimal("COSTO_HISTORICO")).thenReturn(new BigDecimal("1000.50"));
        when(resultSet.getBigDecimal("VALOR_REVALUADO")).thenReturn(null);
        when(resultSet.getBigDecimal("ADQUISICIONES_ADICIONES")).thenReturn(new BigDecimal("200.00"));
        when(resultSet.getBigDecimal("RETIROS_BAJAS")).thenReturn(null);
        when(resultSet.getBigDecimal("OTROS_AJUSTES")).thenReturn(new BigDecimal("50.75"));
        when(resultSet.getBigDecimal("VALOR_LIBROS_ACTIVO_FIJO")).thenReturn(null);

        // f) Columnas DATE (unas no‐null, otras null → debe aplicar LOCAL_DATE_DEFAULT):
        Date sqlDate = Date.valueOf(LocalDate.of(2021, 5, 20));
        when(resultSet.getDate("FECHA_ADQUISICION")).thenReturn(sqlDate);
        when(resultSet.getDate("FECHA_INICIO_USO_ACTIVO_FIJO")).thenReturn(null);
        when(resultSet.getDate("FECHA_RETIRO")).thenReturn(Date.valueOf(LocalDate.of(2022, 2, 15)));

        // g) Columnas adicionales:
        when(resultSet.getString("METODO_APLICADO")).thenReturn(null);
        when(resultSet.getString("N_DOCUMENTO_AUTORIZACION")).thenReturn("DOC123");
        when(resultSet.getInt("ANIOS_VIDA_UTIL_TRIBUTARIO")).thenReturn(5);
        when(resultSet.getString("TASA_ANUAL_TRIBUTARIO")).thenReturn(null);

        when(resultSet.getBigDecimal("DEPRECIACION_ACUMULADA_CIERRE_EJERCICIO_ANTERIOR"))
                .thenReturn(null);
        when(resultSet.getBigDecimal("DEPRECIACION_EJERCICIO")).thenReturn(new BigDecimal("120.00"));
        when(resultSet.getBigDecimal("DEPRECIACION_EJERCICIO_RELACIONADA_RETIROS_BAJAS"))
                .thenReturn(null);
        when(resultSet.getBigDecimal("DEPRECIACION_RELACIONADA_OTROS_AJUSTES"))
                .thenReturn(new BigDecimal("10.25"));
        when(resultSet.getBigDecimal("DEPRECIACION_ACUMULADA_HISTORICA"))
                .thenReturn(null);
        when(resultSet.getBigDecimal("ACTIVO_FIJO_NETO")).thenReturn(new BigDecimal("800.00"));

        // h) stub para los métodos void de CallableStatement:
        doNothing().when(callableStatement).setInt(eq(IDX_YEAR), anyInt());
        doNothing().when(callableStatement).setString(eq(IDX_MAIN_CODE), anyString());
        doNothing().when(callableStatement).setString(eq(IDX_ACCOUNTING_ACCOUNT), anyString());
        doNothing().when(callableStatement).setString(eq(IDX_START_ACQ_DATE), anyString());
        doNothing().when(callableStatement).setString(eq(IDX_END_ACQ_DATE), anyString());
        doNothing().when(callableStatement).setString(eq(IDX_START_DATE_USE), anyString());
        doNothing().when(callableStatement).setString(eq(IDX_END_DATE_USE), anyString());

        doNothing().when(callableStatement).registerOutParameter(eq(IDX_OUT_ERROR_CODE), anyInt());
        doNothing().when(callableStatement).registerOutParameter(eq(IDX_OUT_ERROR_MESSAGE), anyInt());
        doNothing().when(callableStatement).registerOutParameter(eq(IDX_OUT_RESULT_SET_CURSOR), anyInt());

        // i) Stub para execute() → devuelve true, porque execute() no es void:
        when(callableStatement.execute()).thenReturn(true);

        // Act
        ParamReportDto params = new ParamReportDto();
        params.setYear(2022);
        params.setMainCode("MC");
        params.setAccountingAccount("ACC");
        params.setStartAcquisitionDate("2022-01-01");
        params.setEndAcquisitionDate("2022-12-31");
        params.setStartDateUse("2022-02-01");
        params.setEndDateUse("2022-11-30");

        List<FinancialFixedAssetDto> output = repository.callGetReportTaxBookJdbc(params);

        // Assert: debe obtener exactamente una fila convertida a DTO
        assertNotNull(output, "Returned list should not be null");
        assertEquals(1, output.size(), "Should have mapped exactly one DTO");

        FinancialFixedAssetDto dto = output.get(0);

        // Verificamos cada campo: las columnas no-null se conservan, las null se convierten al valor por defecto
        assertEquals("PRINC123", dto.getMainFixedAssetCode());
        assertEquals(_BLANK, dto.getFixedAssetCodeComponent());           // null → ""
        assertEquals("ACC001", dto.getFixedAssetAccountingAccount());
        assertEquals(_BLANK, dto.getDescription());                        // null → ""
        assertEquals("BRAND", dto.getFixedAssetBrand());
        assertEquals(_BLANK, dto.getFixedAssetModel());                    // null → ""
        assertEquals("SN-001", dto.getFixedAssetSerialNumberAndOrPlate());

        assertEquals(new BigDecimal("1000.50"), dto.getInitialBalance());
        assertEquals(BigDecimal.ZERO, dto.getRevaluedAmount());             // null → ZERO
        assertEquals(new BigDecimal("200.00"), dto.getAcquisitionsAdditions());
        assertEquals(BigDecimal.ZERO, dto.getWithdrawalsAndOrRetirements()); // null → ZERO
        assertEquals(new BigDecimal("50.75"), dto.getOtherAdjustments());
        assertEquals(BigDecimal.ZERO, dto.getBookValueOfFixedAssets());      // null → ZERO

        assertEquals(LocalDate.of(2021, 5, 20), dto.getAcquisitionDate());
        assertEquals(LOCAL_DATE_DEFAULT, dto.getStartDateFixedAssetUsage()); // null → LOCAL_DATE_DEFAULT
        assertEquals(LocalDate.of(2022, 2, 15), dto.getRetirementDate());

        assertEquals(_BLANK, dto.getAppliedMethod());                        // null → ""
        assertEquals("DOC123", dto.getAuthorizationDocumentNumber());
        assertEquals(5, dto.getUsefulLifeYearsTax());
        assertEquals(_BLANK, dto.getAnnualRateTax());                       // null → ""

        assertEquals(BigDecimal.ZERO, dto.getAnnualAccumulatedDepreciationPriorPeriod()); // null → ZERO
        assertEquals(new BigDecimal("120.00"), dto.getCurrentDepreciation());
        assertEquals(BigDecimal.ZERO, dto.getCurrentDepreciationRelatedWithdrawalsRetirements()); // null → ZERO
        assertEquals(new BigDecimal("10.25"), dto.getCurrentDepreciationRelatedOtherAdjustments());
        assertEquals(BigDecimal.ZERO, dto.getHistoricalAccumulatedDepreciation());          // null → ZERO
        assertEquals(new BigDecimal("800.00"), dto.getNetFixedAssets());

        // Verificamos que se hayan leído correctamente los OUT parameters
        verify(callableStatement).getString(IDX_OUT_ERROR_CODE);
        verify(callableStatement).getString(IDX_OUT_ERROR_MESSAGE);

        // Verificamos que se cerraron recursos en orden
        verify(resultSet).close();
        verify(callableStatement).close();
        verify(connection).close();
    }
}