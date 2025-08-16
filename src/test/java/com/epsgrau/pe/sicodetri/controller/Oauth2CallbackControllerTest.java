package com.epsgrau.pe.sicodetri.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for {@link Oauth2CallbackController}.
 *
 * <p>
 * Verifies that:
 * <ul>
 *   <li>{@code callback(String code, String state)} returns HTTP 200 with a JSON body containing
 *       the "authorization_code" and "state" parameters.</li>
 *   <li>The GET "/v1/callback" endpoint responds with HTTP 200 and JSON content type,
 *       correctly mapping request parameters "code" and "state" to the response body.</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link Oauth2CallbackControllerTest}<br/>
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
class Oauth2CallbackControllerTest {

  private final Oauth2CallbackController controller = new Oauth2CallbackController();
  private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

  @Test
  @DisplayName("Return authorization_code and state when callback is called with code and state")
  void returnAuthorizationCodeAndStateWhenCallbackCalledWithCodeAndState() {
    // Arrange
    String code = "abc123";
    String state = "xyz789";

    // Act
    ResponseEntity<Map<String, String>> response = controller.callback(code, state);

    // Assert
    assertEquals(200, response.getStatusCodeValue());
    Map<String, String> body = response.getBody();
    assertEquals(code, body.get("authorization_code"));
    assertEquals(state, body.get("state"));
  }

  @Test
  @DisplayName("Return 200 and JSON body when GET callback endpoint receives code and state params")
  void return200AndJsonBodyWhenGetCallbackEndpointWithCodeAndStateParams() throws Exception {
    mockMvc.perform(get("/v1/callback")
            .param("code", "abc123")
            .param("state", "xyz789")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.authorization_code").value("abc123"))
        .andExpect(jsonPath("$.state").value("xyz789"));
  }
}