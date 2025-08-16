package com.epsgrau.pe.sicodetri.config;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetJdbcRepository;
import com.epsgrau.pe.sicodetri.service.ReportJasperService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Spring Test configuration for integration tests.
 *
 * <p>
 * Provides primary beans that override the real JDBC repository and Jasper report service
 * in the "integration" profile, returning fixed stub data for FinancialFixedAsset reports
 * and minimal Excel/PDF responses.
 * </p>
 *
 * <b>Class</b>: {@link IntegrationTestConfig}<br/>
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
 *     <li>Jul 08, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@TestConfiguration
@Profile("integration")
public class IntegrationTestConfig {

  /**
   * Provides a stubbed implementation of {@link FinancialFixedAssetJdbcRepository}
   * that returns predefined report data when the year equals 2022, otherwise returns an empty list.
   *
   * @return a primary {@code FinancialFixedAssetJdbcRepository} bean for integration tests
   */
  @Bean
  @Primary
  public FinancialFixedAssetJdbcRepository financialFixedAssetJdbcRepository() {
    return new FinancialFixedAssetJdbcRepository() {
      @Override
      public List<FinancialFixedAssetDto> callGetReportTaxBookJdbc(ParamReportDto params) {
        if (params.getYear() == 2022) {
          FinancialFixedAssetDto dto = new FinancialFixedAssetDto();
          dto.setMainFixedAssetCode("22.01.010862");
          dto.setFixedAssetCodeComponent("");
          dto.setFixedAssetAccountingAccount("3321161001");
          dto.setDescription("CONSTRUCCION DE EDIFICIO PTAP CURUMUY");
          dto.setFixedAssetBrand("NINGUNA");
          dto.setFixedAssetModel("NINGUNO");
          dto.setFixedAssetSerialNumberAndOrPlate("/");
          dto.setInitialBalance(new BigDecimal("27493700.65"));
          dto.setRevaluedAmount(BigDecimal.ZERO);
          dto.setBookValueOfFixedAssets(new BigDecimal("27493700.6500"));
          dto.setAcquisitionDate(LocalDate.of(2007, 12, 31));
          dto.setStartDateFixedAssetUsage(LocalDate.of(2007, 12, 31));
          dto.setAppliedMethod("LINEA RECTA");
          dto.setAuthorizationDocumentNumber("1");
          dto.setAcquisitionsAdditions(BigDecimal.ZERO);
          dto.setWithdrawalsAndOrRetirements(BigDecimal.ZERO);
          dto.setOtherAdjustments(BigDecimal.ZERO);
          dto.setRetirementDate(LocalDate.of(1900, 1, 1));
          dto.setUsefulLifeYearsTax(20);
          dto.setAnnualRateTax("5%");
          dto.setCurrentDepreciation(BigDecimal.ZERO);
          dto.setCurrentDepreciationRelatedWithdrawalsRetirements(BigDecimal.ZERO);
          dto.setCurrentDepreciationRelatedOtherAdjustments(BigDecimal.ZERO);
          dto.setHistoricalAccumulatedDepreciation(BigDecimal.ZERO);
          dto.setNetFixedAssets(BigDecimal.ZERO);
          dto.setErrorCode("00");
          dto.setErrorMessage("Execute ok.");
          return List.of(dto);
        }
        return Collections.emptyList();
      }

      @Override
      public List<FinancialFixedAssetDto> callGetReportDetailFinancialAndTaxDepreciationJdbc(ParamReportDto params) {
        if (params.getYear() == 2022) {
          FinancialFixedAssetDto dto = new FinancialFixedAssetDto();
          dto.setMainFixedAssetCode("22.01.010862");
          dto.setFixedAssetCodeComponent("");
          dto.setFixedAssetAccountingAccount("3321161001");
          dto.setDescription("CONSTRUCCION DE EDIFICIO PTAP CURUMUY");
          dto.setFixedAssetBrand("NINGUNA");
          dto.setFixedAssetModel("NINGUNO");
          dto.setFixedAssetSerialNumberAndOrPlate("/");
          dto.setInitialBalance(new BigDecimal("27493700.65"));
          dto.setRevaluedAmount(BigDecimal.ZERO);
          dto.setBookValueOfFixedAssets(new BigDecimal("27493700.6500"));
          dto.setAcquisitionDate(LocalDate.of(2007, 12, 31));
          dto.setStartDateFixedAssetUsage(LocalDate.of(2007, 12, 31));
          dto.setAppliedMethod("LINEA RECTA");
          dto.setAuthorizationDocumentNumber("1");
          dto.setId(1);
          dto.setAccountingPeriod(LocalDate.of(2022, 1, 1));
          dto.setUsefulLifeYearsFinancial(30);
          dto.setValueDepreciationFinancial(new BigDecimal("76371.39"));
          dto.setAnnualAccumulatedDepreciationPriorPeriod(new BigDecimal("14100458.40"));
          dto.setAnnualNetTaxValuePriorPeriod(new BigDecimal("6043053.60"));
          dto.setValueDepreciationTax(new BigDecimal("83931.30"));
          dto.setNetTaxValue(new BigDecimal("5959122.30"));
          dto.setValueAddition(BigDecimal.ZERO);
          dto.setValueDeduction(new BigDecimal("7559.91"));
          dto.setErrorCode("00");
          dto.setErrorMessage("Execute ok.");
          return List.of(dto);
        }
        return Collections.emptyList();
      }
    };
  }

  /**
   * Provides a stubbed {@link ReportJasperService} that:
   * <ul>
   *   <li>Generates a minimal XLSX byte array when {@code fileType} equals "xlsx".</li>
   *   <li>Returns a simple PDF header byte array for other formats.</li>
   * </ul>
   *
   * @return a primary {@code ReportJasperService} bean for integration tests
   */
  @Bean
  @Primary
  public ReportJasperService reportJasperService() {
    return (optionReport, templateReport, fileType, parameters, dataSource) -> {
      if ("xlsx".equalsIgnoreCase(fileType)) {
        // Genera un Excel mínimo con Apache POI
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
          wb.createSheet("Sheet1");
          wb.write(os);
          return os.toByteArray();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else {
        // para otros formatos, devuelve un PDF “stub”
        return new byte[]{'%', 'P', 'D', 'F', '\n'};
      }
    };
  }
}
