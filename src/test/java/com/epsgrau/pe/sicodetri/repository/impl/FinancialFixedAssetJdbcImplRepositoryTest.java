package com.epsgrau.pe.sicodetri.repository.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.CALL_SQL;
import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants._BLANK;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.P_ACCOUNT;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.P_END_ACQ;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.P_END_USE;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.P_MAIN_CODE;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.P_OUT_CODE;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.P_OUT_CUR;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.P_OUT_MSG;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.P_START_ACQ;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.P_START_USE;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.P_YEAR;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.SP_TAX_BOOK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

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

        // Act & Assert: debe saltar IllegalStateException con el mensaje exacto
        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> repository.callGetReportTaxBookJdbc(params),
            "Cuando DataSource es null, debe lanzarse IllegalStateException"
        );

        // Comprobamos el mensaje completo para evitar problemas de tildes o espacios
        assertTrue(ex.getMessage().contains("DataSource no configurado"),
            "El mensaje de la excepción debe coincidir con el definido en el método"
        );

        // Verificamos que realmente consultó al menos una vez el DataSource
        verify(jdbcTemplate, times(1)).getDataSource();
        // No debería haber interactuado con la conexión, CallableStatement ni ResultSet
        verifyNoInteractions(dataSource, connection, callableStatement, resultSet);
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

        // Act & Assert: al fallar getConnection, debe lanzarse RuntimeException con el mensaje esperado
        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> repository.callGetReportTaxBookJdbc(params),
            "Cuando getConnection() falla, debe envolverse en RuntimeException"
        );

        // El mensaje interno de la RuntimeException es exactamente:
        assertEquals(
            "Fallo al obtener datos de SP",
            ex.getMessage(),
            "El RuntimeException debe llevar el mensaje de fallo en el SP"
        );

        // Verificamos que intentó abrir la conexión y no tocó ni callableStatement ni resultSet
        verify(dataSource, times(1)).getConnection();
        verifyNoInteractions(callableStatement, resultSet);
    }

    @Test
    @DisplayName("Call StoredProcedure Successfully maps non‐null and null columns into DTO list")
    void callStoredProcedureSuccessfullyMapsRowsIntoDto() throws Exception {
        // Arrange
        // 1) Preparamos el datasource y la llamada al SP real de la clase
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        // Fíjate aquí: ahora usamos exactamente la constante que tu repo usa internamente
        when(connection.prepareCall(SP_TAX_BOOK)).thenReturn(callableStatement);

        // 2) OUT parameters simulados
        when(callableStatement.getString(P_OUT_CODE)).thenReturn("E001");
        when(callableStatement.getString(P_OUT_MSG)).thenReturn("NoError");

        // 3) El cursor OUT devuelve nuestro ResultSet mockeado
        when(callableStatement.getObject(P_OUT_CUR)).thenReturn(resultSet);

        // 4) El ResultSet tiene una sola fila
        when(resultSet.next()).thenReturn(true, false);

        // 5) Stub de todos los getters: algunas columnas null → deben convertirse
        when(resultSet.getString("CODIGO_ACTIVO_FIJO_PRINCIPAL")).thenReturn("PRINC123");
        when(resultSet.getString("CODIGO_ACTIVO_FIJO_COMPONENTE")).thenReturn(null);
        when(resultSet.getString("CUENTA_CONTABLE_ACTIVO_FIJO")).thenReturn("ACC001");
        when(resultSet.getString("DESCRIPTION")).thenReturn(null);
        when(resultSet.getString("MARCA_ACTIVO_FIJO")).thenReturn("BRAND");
        when(resultSet.getString("MODELO_ACTIVO_FIJO")).thenReturn(null);
        when(resultSet.getString("NUMERO_SERIE_PLACA_ACTIVO_FIJO")).thenReturn("SN-001");
        when(resultSet.getBigDecimal("COSTO_HISTORICO"))
            .thenReturn(new BigDecimal("1000.50"));
        when(resultSet.getBigDecimal("VALOR_REVALUADO")).thenReturn(null);
        when(resultSet.getBigDecimal("VALOR_LIBROS_ACTIVO_FIJO")).thenReturn(null);
        when(resultSet.getBigDecimal("ADQUISICIONES_ADICIONES"))
            .thenReturn(new BigDecimal("200.00"));
        when(resultSet.getBigDecimal("RETIROS_BAJAS")).thenReturn(null);
        when(resultSet.getBigDecimal("OTROS_AJUSTES"))
            .thenReturn(new BigDecimal("50.75"));
        when(resultSet.getDate("FECHA_RETIRO"))
            .thenReturn(Date.valueOf(LocalDate.of(2022, 2, 15)));
        when(resultSet.getInt("ANIOS_VIDA_UTIL_TRIBUTARIO")).thenReturn(5);
        when(resultSet.getString("TASA_ANUAL_TRIBUTARIO")).thenReturn(null);
        when(resultSet.getBigDecimal("DEPRECIACION_EJERCICIO"))
            .thenReturn(new BigDecimal("120.00"));
        when(resultSet.getBigDecimal("DEPRECIACION_EJERCICIO_RELACIONADA_RETIROS_BAJAS"))
            .thenReturn(null);
        when(resultSet.getBigDecimal("DEPRECIACION_RELACIONADA_OTROS_AJUSTES"))
            .thenReturn(new BigDecimal("10.25"));
        when(resultSet.getBigDecimal("DEPRECIACION_ACUMULADA_HISTORICA"))
            .thenReturn(null);
        when(resultSet.getBigDecimal("ACTIVO_FIJO_NETO"))
            .thenReturn(new BigDecimal("800.00"));

        // 6) Ejecutamos el método bajo prueba
        ParamReportDto params = new ParamReportDto();
        params.setYear(2022);
        params.setMainCode("MC");
        params.setAccountingAccount("ACC");
        params.setStartAcquisitionDate("2022-01-01");
        params.setEndAcquisitionDate("2022-12-31");
        params.setStartDateUse("2022-02-01");
        params.setEndDateUse("2022-11-30");

        List<FinancialFixedAssetDto> output = repository.callGetReportTaxBookJdbc(params);

        // 7) Assertions
        assertNotNull(output);
        assertEquals(1, output.size());
        FinancialFixedAssetDto dto = output.get(0);

        assertEquals("PRINC123", dto.getMainFixedAssetCode());
        assertEquals("",         dto.getFixedAssetCodeComponent());     // default ""
        assertEquals("ACC001",   dto.getFixedAssetAccountingAccount());
        assertEquals("",         dto.getDescription());
        assertEquals("BRAND",    dto.getFixedAssetBrand());
        assertEquals("",         dto.getFixedAssetModel());
        assertEquals("SN-001",   dto.getFixedAssetSerialNumberAndOrPlate());

        assertEquals(new BigDecimal("1000.50"), dto.getInitialBalance());
        assertEquals(BigDecimal.ZERO,          dto.getRevaluedAmount());      // <-- antes era null, ahora es ZERO
        assertEquals(new BigDecimal("200.00"), dto.getAcquisitionsAdditions());
        assertEquals(BigDecimal.ZERO,          dto.getWithdrawalsAndOrRetirements());
        assertEquals(new BigDecimal("50.75"),  dto.getOtherAdjustments());
        assertEquals(BigDecimal.ZERO,          dto.getBookValueOfFixedAssets());

        assertEquals(LocalDate.of(1900,1,1), dto.getAcquisitionDate());
        assertEquals(LocalDate.of(1900,1,1), dto.getStartDateFixedAssetUsage());
        assertEquals(LocalDate.of(2022,2,15), dto.getRetirementDate());

        assertEquals("",       dto.getAppliedMethod());
        assertEquals("", dto.getAuthorizationDocumentNumber());
        assertEquals(5,        dto.getUsefulLifeYearsTax());
        assertEquals("",       dto.getAnnualRateTax());
        assertEquals(new BigDecimal("120.00"),dto.getCurrentDepreciation());
        assertEquals(BigDecimal.ZERO,         dto.getCurrentDepreciationRelatedWithdrawalsRetirements());
        assertEquals(new BigDecimal("10.25"), dto.getCurrentDepreciationRelatedOtherAdjustments());
        assertEquals(BigDecimal.ZERO,         dto.getHistoricalAccumulatedDepreciation());
        assertEquals(new BigDecimal("800.00"),dto.getNetFixedAssets());

        // 8) Verificamos que cerramos ResultSet y CallableStatement
        verify(resultSet).close();
        verify(callableStatement).close();
        verify(connection).close();
    }

    @Test
    @DisplayName("Call StoredProcedure DetailDepreciation successfully maps non-null and null columns into DTO list")
    void callStoredProcedureDetailDepreciationSuccessfullyMapsRowsIntoDto() throws Exception {
        // Arrange: idéntico al test de TaxBook, salvo que usamos el SQL del detail SP
        String detailSql = "CALL get_report_detail_financial_and_tax_depreciation(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareCall(detailSql)).thenReturn(callableStatement);

        // OUT params
        when(callableStatement.getString(P_OUT_CODE)).thenReturn("C002");
        when(callableStatement.getString(P_OUT_MSG)).thenReturn("OK");

        // Cursor con 1 fila
        when(callableStatement.getObject(P_OUT_CUR)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);

        // mapCommonFields
        when(resultSet.getString("CODIGO_ACTIVO_FIJO_PRINCIPAL")).thenReturn("CFP123");
        when(resultSet.getString("CODIGO_ACTIVO_FIJO_COMPONENTE")).thenReturn(null);
        when(resultSet.getString("CUENTA_CONTABLE_ACTIVO_FIJO")).thenReturn("ACC123");
        when(resultSet.getString("DESCRIPTION")).thenReturn("Desc");
        when(resultSet.getString("MARCA_ACTIVO_FIJO")).thenReturn(null);
        when(resultSet.getString("MODELO_ACTIVO_FIJO")).thenReturn("Model");
        when(resultSet.getString("NUMERO_SERIE_PLACA_ACTIVO_FIJO")).thenReturn(null);
        when(resultSet.getBigDecimal("COSTO_HISTORICO")).thenReturn(new BigDecimal("500"));
        when(resultSet.getBigDecimal("VALOR_REVALUADO")).thenReturn(null);
        when(resultSet.getBigDecimal("VALOR_LIBROS_ACTIVO_FIJO")).thenReturn(new BigDecimal("450"));
        Date dt = Date.valueOf(LocalDate.of(2020,1,1));
        when(resultSet.getDate("FECHA_ADQUISICION")).thenReturn(dt);
        when(resultSet.getDate("FECHA_INICIO_USO_ACTIVO_FIJO")).thenReturn(null);
        when(resultSet.getString("METODO_APLICADO")).thenReturn("M1");
        when(resultSet.getString("N_DOCUMENTO_AUTORIZACION")).thenReturn(null);

        // detail-specific
        when(resultSet.getInt("ID")).thenReturn(99);
        when(resultSet.getDate("PERIODO_CONTABLE")).thenReturn(Date.valueOf(LocalDate.of(2019,12,31)));
        when(resultSet.getInt("ANIOS_VIDA_UTIL_FINANCIERO")).thenReturn(8);
        when(resultSet.getBigDecimal("DEPRECIACION_FINANCIERA")).thenReturn(new BigDecimal("50.5"));
        when(resultSet.getBigDecimal("DEPRECIACION_ACUMULADA_CIERRE_EJERCICIO_ANTERIOR")).thenReturn(null);
        when(resultSet.getBigDecimal("VALOR_NETO_TRIBUTARIO_CIERRE_EJERCICIO_ANTERIOR")).thenReturn(new BigDecimal("400"));
        when(resultSet.getBigDecimal("DEPRECIACION_TRIBUTARIA")).thenReturn(null);
        when(resultSet.getBigDecimal("VALOR_NETO_TRIBUTARIO")).thenReturn(new BigDecimal("350"));
        when(resultSet.getBigDecimal("ADICION")).thenReturn(null);
        when(resultSet.getBigDecimal("DEDUCCION")).thenReturn(new BigDecimal("10"));

        // stub voids & execute()
        doNothing().when(callableStatement).setInt(eq(P_YEAR), anyInt());
        doNothing().when(callableStatement).setString(eq(P_MAIN_CODE), anyString());
        doNothing().when(callableStatement).setString(eq(P_ACCOUNT), anyString());
        doNothing().when(callableStatement).setString(eq(P_START_ACQ), anyString());
        doNothing().when(callableStatement).setString(eq(P_END_ACQ), anyString());
        doNothing().when(callableStatement).setString(eq(P_START_USE), anyString());
        doNothing().when(callableStatement).setString(eq(P_END_USE), anyString());
        doNothing().when(callableStatement).registerOutParameter(eq(P_OUT_CODE), anyInt());
        doNothing().when(callableStatement).registerOutParameter(eq(P_OUT_MSG), anyInt());
        doNothing().when(callableStatement).registerOutParameter(eq(P_OUT_CUR), anyInt());
        when(callableStatement.execute()).thenReturn(true);

        ParamReportDto params = new ParamReportDto(2022, "MC", "ACC","2022-01-01","2022-12-31","2022-02-01","2022-11-30");

        // Act
        List<FinancialFixedAssetDto> list = repository.callGetReportDetailFinancialAndTaxDepreciationJdbc(params);

        // Assert
        assertEquals(1, list.size());
        FinancialFixedAssetDto dto = list.get(0);
        // common
        assertEquals("CFP123", dto.getMainFixedAssetCode());
        assertEquals(_BLANK, dto.getFixedAssetCodeComponent());
        assertEquals("ACC123", dto.getFixedAssetAccountingAccount());
        assertEquals("Desc", dto.getDescription());
        assertEquals(_BLANK, dto.getFixedAssetBrand());
        assertEquals("Model", dto.getFixedAssetModel());
        assertEquals(_BLANK, dto.getFixedAssetSerialNumberAndOrPlate());
        assertEquals(new BigDecimal("500"), dto.getInitialBalance());
        assertEquals(BigDecimal.ZERO, dto.getRevaluedAmount());
        assertEquals(new BigDecimal("450"), dto.getBookValueOfFixedAssets());
        assertEquals(LocalDate.of(2020,1,1), dto.getAcquisitionDate());
        assertEquals(LOCAL_DATE_DEFAULT, dto.getStartDateFixedAssetUsage());
        assertEquals("M1", dto.getAppliedMethod());
        assertEquals(_BLANK, dto.getAuthorizationDocumentNumber());
        // detail-specific
        assertEquals(99, dto.getId());
        assertEquals(LocalDate.of(2019,12,31), dto.getAccountingPeriod());
        assertEquals(8, dto.getUsefulLifeYearsFinancial());
        assertEquals(new BigDecimal("50.5"), dto.getValueDepreciationFinancial());
        assertEquals(BigDecimal.ZERO, dto.getAnnualAccumulatedDepreciationPriorPeriod());
        assertEquals(new BigDecimal("400"), dto.getAnnualNetTaxValuePriorPeriod());
        assertEquals(BigDecimal.ZERO, dto.getValueDepreciationTax());
        assertEquals(new BigDecimal("350"), dto.getNetTaxValue());
        assertEquals(BigDecimal.ZERO, dto.getValueAddition());
        assertEquals(new BigDecimal("10"), dto.getValueDeduction());
        // errorCode/message
        assertEquals("C002", dto.getErrorCode());
        assertEquals("OK", dto.getErrorMessage());
    }

    @Test
    @DisplayName("Throw RuntimeException when mapping TaxBook row fails")
    void throwRuntimeExceptionWhenMappingTaxBookRowFails() throws Exception {
        // Arrange: misma configuración de SP_TAX_BOOK pero forzamos SQLException en un getter
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareCall(CALL_SQL)).thenReturn(callableStatement);
        when(callableStatement.getString(P_OUT_CODE)).thenReturn("X");
        when(callableStatement.getString(P_OUT_MSG)).thenReturn("Y");
        when(callableStatement.getObject(P_OUT_CUR)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        // fuerza la excepción al mapear
        when(resultSet.getBigDecimal("ADQUISICIONES_ADICIONES"))
            .thenThrow(new SQLException("oops"));

        // stub de execute
        doNothing().when(callableStatement).setInt(anyInt(), anyInt());
        doNothing().when(callableStatement).registerOutParameter(anyInt(), anyInt());
        when(callableStatement.execute()).thenReturn(true);

        ParamReportDto params = new ParamReportDto(0, "", "", "", "", "", "");

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> repository.callGetReportTaxBookJdbc(params));
        assertTrue(ex.getMessage().contains("Error mapeando fila TaxBook"),
            "Debe indicar el mapper TaxBook");
    }

    @Test
    @DisplayName("Throw RuntimeException when mapping DetailDepreciation row fails")
    void throwRuntimeExceptionWhenMappingDetailDepreciationRowFails() throws Exception {
        // Arrange: igual al anterior pero detalle SP
        String detailSql = "CALL get_report_detail_financial_and_tax_depreciation(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareCall(detailSql)).thenReturn(callableStatement);
        when(callableStatement.getString(P_OUT_CODE)).thenReturn("A");
        when(callableStatement.getString(P_OUT_MSG)).thenReturn("B");
        when(callableStatement.getObject(P_OUT_CUR)).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        // fuerza SQLException en el mapper detalle
        when(resultSet.getInt("ID")).thenThrow(new SQLException("bad"));

        doNothing().when(callableStatement).setInt(anyInt(), anyInt());
        doNothing().when(callableStatement).registerOutParameter(anyInt(), anyInt());
        when(callableStatement.execute()).thenReturn(true);

        ParamReportDto params = new ParamReportDto(0, "", "", "", "", "", "");

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> repository.callGetReportDetailFinancialAndTaxDepreciationJdbc(params));
        assertTrue(ex.getMessage().contains("Error mapeando fila DetailDepreciation"),
            "Debe indicar el mapper DetailDepreciation");
    }
}