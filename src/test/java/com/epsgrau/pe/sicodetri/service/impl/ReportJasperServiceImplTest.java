package com.epsgrau.pe.sicodetri.service.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.helper.JasperReportsHelper;
import com.epsgrau.pe.sicodetri.util.enums.ReportTypeEnum;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.util.ResourceUtils;

/**
 * Unit tests for {@link ReportJasperServiceImpl}.
 *
 * <p>
 * Verifies the behavior of {@code getJasperReportInBytes} including:
 * <ul>
 *   <li>Successful report generation for different file types and options</li>
 *   <li>Propagation of FileNotFoundException when the JRXML is missing</li>
 *   <li>Propagation of JRException on compile or fill failures</li>
 *   <li>IllegalArgumentException for unsupported file types</li>
 *   <li>Injection of data source beans into report parameters for option "2"</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link ReportJasperServiceImplTest}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@DisplayName("ReportJasperServiceImpl Tests")
class ReportJasperServiceImplTest {

    private ReportJasperServiceImpl service;

    /**
     * Initializes a fresh instance of the service before each test.
     */
    @BeforeEach
    void setUp() {
        service = new ReportJasperServiceImpl();
    }

    /**
     * Verifies that {@code getJasperReportInBytes} returns the exact byte array
     * provided by {@link JasperReportsHelper} when the template compiles and fills successfully.
     */
    @Test
    @DisplayName("Return report bytes when template is valid and data provided")
    void returnReportBytesWhenTemplateIsValid() throws Exception {
        // Arrange
        String option = "1";
        String templateName = "dummy.jrxml";
        String fileType = "PDF";
        Map<String, Object> parameters = Collections.singletonMap("PARAM_KEY", "PARAM_VALUE");
        List<FinancialFixedAssetDto> dataSource = List.of(new FinancialFixedAssetDto());

        // Preparamos mocks de los métodos estáticos involucrados:
        try (MockedStatic<ResourceUtils> resourceUtilsMock = Mockito.mockStatic(ResourceUtils.class);
             MockedStatic<JasperCompileManager> compileMock = Mockito.mockStatic(JasperCompileManager.class);
             MockedStatic<JasperFillManager> fillMock = Mockito.mockStatic(JasperFillManager.class);
             MockedStatic<JasperReportsHelper> helperMock = Mockito.mockStatic(JasperReportsHelper.class)) {

            // a) ResourceUtils.getFile debe devolver un File “falso”
            File fakeFile = new File("/path/to/dummy.jrxml");
            resourceUtilsMock
                    .when(() -> ResourceUtils.getFile("classpath:reports/" + templateName))
                    .thenReturn(fakeFile);

            // b) JasperCompileManager.compileReport retorna un JasperReport “mock”
            JasperReport jasperReportMock = mock(JasperReport.class);
            compileMock
                    .when(() -> JasperCompileManager.compileReport(fakeFile.getAbsolutePath()))
                    .thenReturn(jasperReportMock);

            // c) JasperFillManager.fillReport retorna un JasperPrint “mock”
            JasperPrint jasperPrintMock = mock(JasperPrint.class);
            fillMock
                    .when(() -> JasperFillManager.fillReport(
                            eq(jasperReportMock),
                            eq(parameters),
                            any(JRBeanCollectionDataSource.class)))
                    .thenReturn(jasperPrintMock);

            // d) JasperReportsHelper.exportJasperReportBytes retorna un arreglo de bytes “esperado”
            byte[] expectedBytes = new byte[]{0x01, 0x02, 0x03};
            helperMock
                    .when(() -> JasperReportsHelper.exportJasperReportBytes(
                            eq(jasperPrintMock),
                            eq(ReportTypeEnum.PDF)))
                    .thenReturn(expectedBytes);

            // Act
            byte[] actualBytes = service.getJasperReportInBytes(
                option,
                templateName,
                fileType,
                parameters,
                dataSource
            );

            // Assert
            assertArrayEquals(expectedBytes, actualBytes, "Los bytes devueltos deben coincidir con los proporcionados por JasperReportsHelper");

            // Verificar que cada estático fue invocado en el flujo esperado:
            resourceUtilsMock.verify(() -> ResourceUtils.getFile("classpath:reports/" + templateName));
            compileMock.verify(() -> JasperCompileManager.compileReport(fakeFile.getAbsolutePath()));
            fillMock.verify(() -> JasperFillManager.fillReport(
                    eq(jasperReportMock),
                    eq(parameters),
                    any(JRBeanCollectionDataSource.class)));
            helperMock.verify(() -> JasperReportsHelper.exportJasperReportBytes(jasperPrintMock, ReportTypeEnum.PDF));
        }
    }

    /**
     * Verifies that {@code getJasperReportInBytes} propagates a FileNotFoundException
     * when the specified JRXML template cannot be found in the classpath.
     */
    @Test
    @DisplayName("Throw FileNotFoundException when template not found")
    void throwFileNotFoundWhenTemplateMissing() {
        // Arrange
        String missingTemplate = "does_not_exist.jrxml";
        try (MockedStatic<ResourceUtils> resourceUtilsMock = Mockito.mockStatic(ResourceUtils.class)) {
            resourceUtilsMock
                    .when(() -> ResourceUtils.getFile("classpath:reports/" + missingTemplate))
                    .thenThrow(new FileNotFoundException("File not found in classpath"));

            // Act & Assert
            FileNotFoundException ex = assertThrows(
                    FileNotFoundException.class,
                    () -> service.getJasperReportInBytes(
                        "1",
                            missingTemplate,
                            "PDF",
                            Collections.emptyMap(),
                            Collections.emptyList()
                    ),
                    "Cuando ResourceUtils.getFile lanza FileNotFoundException, el servicio debe propagarla"
            );
            assertTrue(ex.getMessage().contains("File not found"));
        } catch (Exception e) {
            fail("No se esperaba otra excepción: " + e.getMessage());
        }
    }

    /**
     * Verifies that a JRException thrown during report compilation
     * is propagated by {@code getJasperReportInBytes}.
     */
    @Test
    @DisplayName("Throw JRException when compilation fails")
    void throwJrexceptionWhenCompilationFails() {
        // Arrange
        String badTemplate = "broken.jrxml";
        try (MockedStatic<ResourceUtils> resourceUtilsMock = Mockito.mockStatic(ResourceUtils.class);
             MockedStatic<JasperCompileManager> compileMock = Mockito.mockStatic(JasperCompileManager.class)) {

            File fakeFile = new File("/path/to/broken.jrxml");
            resourceUtilsMock
                    .when(() -> ResourceUtils.getFile("classpath:reports/" + badTemplate))
                    .thenReturn(fakeFile);

            compileMock
                    .when(() -> JasperCompileManager.compileReport(fakeFile.getAbsolutePath()))
                    .thenThrow(new JRException("Compilation error"));

            // Act & Assert
            JRException ex = assertThrows(
                    JRException.class,
                    () -> service.getJasperReportInBytes(
                        "1",
                            badTemplate,
                            "PDF",
                            Collections.emptyMap(),
                            Collections.emptyList()
                    ),
                    "Si JasperCompileManager.compileReport falla, el JRException debe propagarse"
            );
            assertTrue(ex.getMessage().contains("Compilation error"));
        } catch (Exception e) {
            fail("No se esperaba otra excepción: " + e.getMessage());
        }
    }

    /**
     * Verifies that a JRException thrown during report filling
     * is propagated by {@code getJasperReportInBytes}.
     */
    @Test
    @DisplayName("Throw JRException when fillReport fails")
    void throwJrexceptionWhenFillReportFails() {
        // Arrange
        String templateName = "fill_error.jrxml";
        try (MockedStatic<ResourceUtils> resourceUtilsMock = Mockito.mockStatic(ResourceUtils.class);
             MockedStatic<JasperCompileManager> compileMock = Mockito.mockStatic(JasperCompileManager.class);
             MockedStatic<JasperFillManager> fillMock = Mockito.mockStatic(JasperFillManager.class)) {

            File fakeFile = new File("/path/to/fill_error.jrxml");
            resourceUtilsMock
                    .when(() -> ResourceUtils.getFile("classpath:reports/" + templateName))
                    .thenReturn(fakeFile);

            JasperReport jasperReportMock = mock(JasperReport.class);
            compileMock
                    .when(() -> JasperCompileManager.compileReport(fakeFile.getAbsolutePath()))
                    .thenReturn(jasperReportMock);

            fillMock
                    .when(() -> JasperFillManager.fillReport(
                            eq(jasperReportMock),
                            anyMap(),
                            any(JRBeanCollectionDataSource.class)))
                    .thenThrow(new JRException("Fill error"));

            // Act & Assert
            JRException ex = assertThrows(
                    JRException.class,
                    () -> service.getJasperReportInBytes(
                        "1",
                            templateName,
                            "PDF",
                            Collections.emptyMap(),
                            Collections.emptyList()
                    ),
                    "Si JasperFillManager.fillReport falla, el JRException debe propagarse"
            );
            assertTrue(ex.getMessage().contains("Fill error"));
        } catch (Exception e) {
            fail("No se esperaba otra excepción: " + e.getMessage());
        }
    }

    /**
     * Verifies that {@code getJasperReportInBytes} throws IllegalArgumentException
     * when given an unsupported fileType code.
     */
    @Test
    @DisplayName("Throw IllegalArgumentException when fileType is invalid")
    void throwIllegalArgumentExceptionWhenFileTypeInvalid() {
        // Arrange
        String templateName = "dummy.jrxml";
        try (MockedStatic<ResourceUtils> resourceUtilsMock = Mockito.mockStatic(ResourceUtils.class);
             MockedStatic<JasperCompileManager> compileMock = Mockito.mockStatic(JasperCompileManager.class);
             MockedStatic<JasperFillManager> fillMock = Mockito.mockStatic(JasperFillManager.class)) {

            File fakeFile = new File("/path/to/dummy.jrxml");
            resourceUtilsMock
                    .when(() -> ResourceUtils.getFile("classpath:reports/" + templateName))
                    .thenReturn(fakeFile);

            JasperReport jasperReportMock = mock(JasperReport.class);
            compileMock
                    .when(() -> JasperCompileManager.compileReport(fakeFile.getAbsolutePath()))
                    .thenReturn(jasperReportMock);

            JasperPrint jasperPrintMock = mock(JasperPrint.class);
            fillMock
                    .when(() -> JasperFillManager.fillReport(
                            eq(jasperReportMock),
                            anyMap(),
                            any(JRBeanCollectionDataSource.class)))
                    .thenReturn(jasperPrintMock);

            // fileType inválido ‒ ReportTypeEnum.getReportTypeByCode lanzará IllegalArgumentException
            String invalidFileType = "INVALID_TYPE";

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.getJasperReportInBytes(
                        "1",
                            templateName,
                            invalidFileType,
                            Collections.emptyMap(),
                            Collections.emptyList()
                    ),
                    "Si el fileType no corresponde a ningún ReportTypeEnum, debe lanzarse IllegalArgumentException"
            );
            assertTrue(ex.getMessage().contains("Tipo de reporte desconocido"));
        } catch (Exception e) {
            fail("No se esperaba otra excepción: " + e.getMessage());
        }
    }

    /**
     * Verifies support for option "2", ensuring that the data list is wrapped
     * in a {@link JREmptyDataSource} or {@link JRBeanCollectionDataSource} and
     * included in the report parameters before exporting.
     */
    @Test
    @DisplayName("Return report bytes when optionReport equals '2'")
    void returnReportBytesWhenOption2() throws Exception {
        // Arrange
        String option = "2";
        String templateName = "dummy2.jrxml";
        String fileType = "XLSX";
        // Uso de HashMap para poder verificar la mutación
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("INITIAL", "VALUE");
        List<FinancialFixedAssetDto> dataSource = List.of(new FinancialFixedAssetDto());

        try (MockedStatic<ResourceUtils> resourceUtilsMock = Mockito.mockStatic(ResourceUtils.class);
             MockedStatic<JasperCompileManager> compileMock = Mockito.mockStatic(JasperCompileManager.class);
             MockedStatic<JasperFillManager> fillMock = Mockito.mockStatic(JasperFillManager.class);
             MockedStatic<JasperReportsHelper> helperMock = Mockito.mockStatic(JasperReportsHelper.class)) {

            File fakeFile = new File("/path/to/" + templateName);
            resourceUtilsMock
                .when(() -> ResourceUtils.getFile("classpath:reports/" + templateName))
                .thenReturn(fakeFile);

            JasperReport jrMock = mock(JasperReport.class);
            compileMock
                .when(() -> JasperCompileManager.compileReport(fakeFile.getAbsolutePath()))
                .thenReturn(jrMock);

            JasperPrint jpMock = mock(JasperPrint.class);
            fillMock
                .when(() -> JasperFillManager.fillReport(
                    eq(jrMock),
                    eq(parameters),
                    any(JREmptyDataSource.class)
                ))
                .thenReturn(jpMock);

            byte[] expected = new byte[]{0x0A, 0x0B, 0x0C};
            helperMock
                .when(() -> JasperReportsHelper.exportJasperReportBytes(
                    eq(jpMock),
                    eq(ReportTypeEnum.XLSX)
                ))
                .thenReturn(expected);

            // Act
            byte[] actual = service.getJasperReportInBytes(
                option, templateName, fileType, parameters, dataSource
            );

            // Assert
            assertArrayEquals(expected, actual, "Debe devolver los bytes generados por JasperReportsHelper");
            // Verificar que se haya añadido el bean en parámetros
            assertTrue(parameters.containsKey("financialFixedAssetList"));
            assertTrue(parameters.get("financialFixedAssetList") instanceof JRBeanCollectionDataSource);
            // Verificar llamadas estáticas
            resourceUtilsMock.verify(() -> ResourceUtils.getFile("classpath:reports/" + templateName));
            compileMock.verify(() -> JasperCompileManager.compileReport(fakeFile.getAbsolutePath()));
            fillMock.verify(() -> JasperFillManager.fillReport(
                eq(jrMock), eq(parameters), any(JREmptyDataSource.class)
            ));
            helperMock.verify(() -> JasperReportsHelper.exportJasperReportBytes(jpMock, ReportTypeEnum.XLSX));
        }
    }

    /**
     * Verifies that a JRException thrown by {@link JasperFillManager}
     * for option "2" is propagated and that the data source bean
     * was added to the parameters prior to failure.
     */
    @Test
    @DisplayName("Throw JRException when fillReport fails for optionReport '2'")
    void throwJrexceptionWhenFillReportFailsOption2() {
        // Arrange
        String option = "2";
        String templateName = "broken2.jrxml";
        String fileType = "PDF";
        HashMap<String, Object> parameters = new HashMap<>();
        List<FinancialFixedAssetDto> dataSource = List.of(new FinancialFixedAssetDto());

        try (MockedStatic<ResourceUtils> resourceUtilsMock = Mockito.mockStatic(ResourceUtils.class);
             MockedStatic<JasperCompileManager> compileMock = Mockito.mockStatic(JasperCompileManager.class);
             MockedStatic<JasperFillManager> fillMock = Mockito.mockStatic(JasperFillManager.class)) {

            File fakeFile = new File("/path/to/" + templateName);
            resourceUtilsMock
                .when(() -> ResourceUtils.getFile("classpath:reports/" + templateName))
                .thenReturn(fakeFile);

            JasperReport jrMock = mock(JasperReport.class);
            compileMock
                .when(() -> JasperCompileManager.compileReport(fakeFile.getAbsolutePath()))
                .thenReturn(jrMock);

            fillMock
                .when(() -> JasperFillManager.fillReport(
                    eq(jrMock),
                    anyMap(),
                    any(JREmptyDataSource.class)
                ))
                .thenThrow(new JRException("Empty-DS fill failure"));

            // Act & Assert
            JRException ex = assertThrows(
                JRException.class,
                () -> service.getJasperReportInBytes(
                    option, templateName, fileType, parameters, dataSource
                ),
                "Debe propagarse el JRException lanzado por JasperFillManager"
            );
            assertTrue(ex.getMessage().contains("Empty-DS fill failure"));
            // Verificar que el bean también se haya agregado antes de fallar
            assertTrue(parameters.containsKey("financialFixedAssetList"));
        }
    }
}