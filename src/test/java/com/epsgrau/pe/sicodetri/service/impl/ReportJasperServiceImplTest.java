package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.helper.JasperReportsHelper;
import com.epsgrau.pe.sicodetri.util.enums.ReportTypeEnum;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

/**
 * Tests para ReportJasperServiceImpl, cubriendo:
 *  1) Éxito cuando el template existe, la compilación y llenado funcionan, y JasperReportsHelper devuelve bytes.
 *  2) FileNotFoundException si ResourceUtils.getFile falla.
 *  3) JRException si JasperCompileManager.compileReport falla.
 */
@DisplayName("ReportJasperServiceImpl Tests")
class ReportJasperServiceImplTest {

    private ReportJasperServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReportJasperServiceImpl();
    }

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

    @Test
    @DisplayName("Throw JRException when compilation fails")
    void throwJrExceptionWhenCompilationFails() {
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

    @Test
    @DisplayName("Throw JRException when fillReport fails")
    void throwJrExceptionWhenFillReportFails() {
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
}