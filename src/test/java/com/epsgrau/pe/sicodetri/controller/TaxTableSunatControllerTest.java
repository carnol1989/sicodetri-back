package com.epsgrau.pe.sicodetri.controller;

import static com.epsgrau.pe.sicodetri.util.ConstantsTest.L_VAL_1;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.L_VAL_2;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.L_VAL_3;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.N_VAL_1;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.N_VAL_2;
import static com.epsgrau.pe.sicodetri.util.DataTest.getListTableSunats;
import static com.epsgrau.pe.sicodetri.util.DataTest.getTaxTable332;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.exception.GlobalExceptionHandler;
import com.epsgrau.pe.sicodetri.exception.ResourceNotFoundException;
import com.epsgrau.pe.sicodetri.model.ResponseData;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for {@link TaxTableSunatController}.
 *
 * <p>
 * Verifies behavior of the TaxTableSunatController endpoints:
 * <ul>
 *   <li>Retrieval by ID or accounting account</li>
 *   <li>Error handling when data is missing or exceptions occur</li>
 *   <li>Retrieval of all records</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link TaxTableSunatControllerTest}<br/>
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
class TaxTableSunatControllerTest {

    public static String BASE_URI = "/api/v1/tax-tables";

    private MockMvc mockMvc;

    @Mock
    private TaxTableSunatService service;

    @InjectMocks
    private TaxTableSunatController controller;

    /**
     * Initializes mocks and configures MockMvc with the controller
     * and global exception handler before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())  // Configurar el manejador global de excepciones
                .build();
    }

    /**
     * Provides parameter combinations for testing retrieval by option:
     * 1 = by ID, 2 = by accounting account, 3 = invalid option.
     *
     * @return a stream of arguments: (option, id, accountingAccount, expectedEntity, expectedStatus)
     */
    static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(L_VAL_1, 1L, null, getTaxTable332(), HttpStatus.OK),
                Arguments.of(L_VAL_1, 2L, "accountingAccount", new TaxTableSunat(), HttpStatus.NOT_FOUND),
                Arguments.of(L_VAL_2, 0L, "332", getTaxTable332(), HttpStatus.OK),
                Arguments.of(L_VAL_2, 0L, "invalidAccount", new TaxTableSunat(), HttpStatus.NOT_FOUND),
                Arguments.of(L_VAL_3, 0L, "invalidAccount", new TaxTableSunat(), HttpStatus.EXPECTATION_FAILED)
        );
    }

    /**
     * Parameterized test that verifies:
     * <ul>
     *   <li>Successful retrieval when valid parameters are provided</li>
     *   <li>HTTP 404 when entity is not found</li>
     *   <li>HTTP 417 for invalid option</li>
     * </ul>
     *
     * @param option           retrieval option code
     * @param id               ID parameter for option 1
     * @param accountingAccount account parameter for option 2
     * @param expected         expected TaxTableSunat entity or empty instance
     * @param expectedStatus   expected HTTP status
     * @throws Exception if MockMvc request fails
     */
    @ParameterizedTest
    @MethodSource("provideParameters")
    @DisplayName("Return taxTableSunat when option and parameters are valid or handle not found properly")
    void returnTaxTableSunatWhenOptionAndParametersAreValidOrNotFound(Long option, Long id, String accountingAccount,
                                                                      TaxTableSunat expected, HttpStatus expectedStatus) throws Exception {
        // Arrange
        if (option == N_VAL_1) {
            if (expectedStatus == HttpStatus.NOT_FOUND) {
                when(service.findByIdService(id)).thenReturn(expected)
                        .thenThrow(new ResourceNotFoundException("Error al obtener informacion con el ID: " + id));
            } else {
                when(service.findByIdService(id)).thenReturn(expected);
            }
        } else if (option == N_VAL_2) {
            if (expectedStatus == HttpStatus.NOT_FOUND) {
                when(service.findByAccountigAccountService(accountingAccount)).thenReturn(expected)
                        .thenThrow(new ResourceNotFoundException("Error al obtener informacion para cuenta contable: " + accountingAccount));
            } else {
                when(service.findByAccountigAccountService(accountingAccount)).thenReturn(expected);
            }
        }

        // Act
        MvcResult result = mockMvc.perform(get(BASE_URI + "/{option}", option)
                        .param("id", id != null ? id.toString() : "")
                        .param("accountingAccount", accountingAccount != null ? accountingAccount : "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(expectedStatus.value()))
                .andReturn();

        // Assert
        if (option == N_VAL_1) {
            if (expectedStatus != HttpStatus.NOT_FOUND) {
                verify(service, times(1)).findByIdService(id);
            }
        } else if (option == N_VAL_2) {
            if (expectedStatus != HttpStatus.NOT_FOUND) {
                verify(service, times(1)).findByAccountigAccountService(accountingAccount);
            }
        }

        if (expectedStatus == HttpStatus.NOT_FOUND || expectedStatus == HttpStatus.EXPECTATION_FAILED) {
            String responseBody = result.getResponse().getContentAsString();
            System.out.println(responseBody);
            assertTrue(responseBody.contains("Error al obtener informacion"));
        }
    }

    /**
     * Verifies that an unexpected runtime exception in the controller
     * returns HTTP 417 with a ResponseData containing error details.
     */
    @Test
    @DisplayName("Return expectation failed when unexpected exception is thrown in controller")
    void returnExpectationFailedWhenUnexpectedExceptionIsThrownInController() {
        // Arrange
        Long option = 1L;
        Long id = 123L;
        String accountingAccount = "account";

        when(service.findByIdService(anyLong())).thenThrow(new RuntimeException("Error al obtener informacion del activo fijo: account"));

        // Act
        ResponseEntity<ResponseData> response = controller.getTaxTableSunatController(option, id, accountingAccount);

        // Assert
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        ResponseData responseData = response.getBody();
        assertNotNull(responseData);
        assertEquals("01", responseData.getCode());
        assertTrue(responseData.getMessage().startsWith("Error al obtener informacion del activo fijo: "));
        assertTrue(responseData.getException().getMessage().contains("Error al obtener informacion"));
        assertNotNull(responseData.getException().getTimestamp());
        assertTrue(responseData.getException().getDetails().contains("Error al obtener informacion"));
    }

    /**
     * Tests retrieval of all TaxTableSunat records:
     * <ul>
     *   <li>HTTP 200 and correct JSON array length when records exist</li>
     * </ul>
     *
     * @throws Exception if MockMvc request fails
     */
    @Test
    @DisplayName("Return all taxTableSunat records when data exists")
    void returnAllTaxTableSunatRecordsWhenDataExists() throws Exception {
        // Arrange
        List<TaxTableSunat> taxes = getListTableSunats();
        when(service.findAllService()).thenReturn(taxes);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Act
        mockMvc.perform(get(BASE_URI + "/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(taxes.size()));

        // Assert
        verify(service, atLeastOnce()).findAllService();
    }

    /**
     * Tests retrieval of all TaxTableSunat records when none exist:
     * <ul>
     *   <li>HTTP 200 and zero-length JSON array</li>
     * </ul>
     *
     * @throws Exception if MockMvc request fails
     */
    @Test
    @DisplayName("Return empty list when no taxTableSunat records exist")
    void returnEmptyListWhenNoTaxTableSunatRecordsExist() throws Exception {
        // Arrange
        when(service.findAllService()).thenReturn(new ArrayList<>());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Act
        mockMvc.perform(get(BASE_URI + "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // Assert
        verify(service, times(1)).findAllService();
    }

}