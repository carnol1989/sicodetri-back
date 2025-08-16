package com.epsgrau.pe.sicodetri.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epsgrau.pe.sicodetri.repository.AdditionRepository;
import com.epsgrau.pe.sicodetri.repository.DeductionRepository;
import com.epsgrau.pe.sicodetri.repository.FinancialDepreciationRepository;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetRepository;
import com.epsgrau.pe.sicodetri.repository.TaxDepreciationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for {@link TestCleanupController}.
 *
 * <p>
 * Verifies that:
 * <ul>
 *   <li>{@code cleanup()} deletes all records from the injected repositories
 *       (FinancialDepreciationRepository, TaxDepreciationRepository, AdditionRepository,
 *       DeductionRepository, FinancialFixedAssetRepository) and returns HTTP 200 with no body.</li>
 *   <li>A POST request to "/api/v1/test/cleanup" returns HTTP 200 and triggers the same repository deletions.</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link TestCleanupControllerTest}<br/>
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
 *     <li>Jul 29, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class TestCleanupControllerTest {

  @Mock
  private FinancialDepreciationRepository finDepRepo;
  @Mock
  private TaxDepreciationRepository taxDepRepo;
  @Mock
  private AdditionRepository additionRepo;
  @Mock
  private DeductionRepository deductionRepo;
  @Mock
  private FinancialFixedAssetRepository assetRepo;

  private TestCleanupController controller;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    controller = new TestCleanupController(
        finDepRepo, taxDepRepo, additionRepo, deductionRepo, assetRepo
    );
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  @DisplayName("Delete all repository entries when cleanup is called")
  void deleteAllRepositoriesWhenCleanupCalled() {
    // Act
    ResponseEntity<Void> response = controller.cleanup();

    // Assert
    assertEquals(200, response.getStatusCodeValue());
    assertNull(response.getBody());
    verify(finDepRepo).deleteAll();
    verify(taxDepRepo).deleteAll();
    verify(additionRepo).deleteAll();
    verify(deductionRepo).deleteAll();
    verify(assetRepo).deleteAll();
    verifyNoMoreInteractions(finDepRepo, taxDepRepo, additionRepo, deductionRepo, assetRepo);
  }

  @Test
  @DisplayName("Return OK status when cleanup endpoint is invoked")
  void returnOkStatusWhenCleanupEndpointIsInvoked() throws Exception {
    // Act & Assert
    mockMvc.perform(post("/api/v1/test/cleanup"))
        .andExpect(status().isOk());

    verify(finDepRepo).deleteAll();
    verify(taxDepRepo).deleteAll();
    verify(additionRepo).deleteAll();
    verify(deductionRepo).deleteAll();
    verify(assetRepo).deleteAll();
  }
}