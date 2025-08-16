package com.epsgrau.pe.sicodetri.controller;

import static com.epsgrau.pe.sicodetri.util.Constants.AUTHORIZATION_CODE;
import static com.epsgrau.pe.sicodetri.util.Constants.CODE;
import static com.epsgrau.pe.sicodetri.util.Constants.CODE_VERIFIER;
import static com.epsgrau.pe.sicodetri.util.Constants.ERROR;
import static com.epsgrau.pe.sicodetri.util.Constants.GRANT_TYPE;
import static com.epsgrau.pe.sicodetri.util.Constants.REDIRECT_URI;
import static com.epsgrau.pe.sicodetri.util.Constants.REFRESH_TOKEN;
import static com.epsgrau.pe.sicodetri.util.Constants.SCOPE;
import static com.epsgrau.pe.sicodetri.util.Constants.UNSUPPORTED_GRANT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.config.props.AuthorizationServerProperties;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Unit test class for {@link AuthController}.
 * This test suite verifies the behavior of the authentication controller's token endpoints:
 * <ul>
 *   <li>{@code /auth/token} with {@code application/x-www-form-urlencoded} requests</li>
 *   <li>{@code /auth/token} with {@code application/json} requests</li>
 * </ul>
 * Both the {@code authorization_code} and {@code refresh_token} grant types are tested,
 * along with error handling for unsupported grant types.
 * <b>Test Coverage:</b>
 * <ul>
 *   <li>Valid token retrieval using form-encoded parameters</li>
 *   <li>Valid token retrieval using JSON payload with default and explicit grant type</li>
 *   <li>Token refresh flow</li>
 *   <li>Unsupported grant type handling</li>
 * </ul>
 * <b>Technologies & Tools:</b>
 * <ul>
 *   <li>JUnit 5 for test execution</li>
 *   <li>Mockito for mocking and argument capturing</li>
 *   <li>AssertJ for fluent assertions</li>
 * </ul>
 *
 * <b>Class</b>: {@link AuthControllerTest}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
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
 *     <li>Ago 11, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@DisplayName("AuthController Tests")
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock
  RestTemplate restTemplate;

  // Deep stubs para props.provider().issuer() y props.client().id()/secret()/scopes()
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  AuthorizationServerProperties props;

  @InjectMocks
  AuthController controller;

  @Captor
  ArgumentCaptor<String> urlCaptor;

  @Captor
  ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> requestCaptor;

  private static final String ISSUER = "http://issuer.example";
  private static final String CLIENT_ID = "client-id";
  private static final String CLIENT_SECRET = "client-secret";
  private static final List<String> SCOPES = List.of("read", "write");

  /**
   * Configures common mock behavior for {@link AuthorizationServerProperties} before each test.
   * <p>
   * This ensures that all tests use consistent values for:
   * <ul>
   *   <li>Issuer URL</li>
   *   <li>Client ID</li>
   *   <li>Client secret</li>
   *   <li>Client scopes</li>
   * </ul>
   * </p>
   */
  @BeforeEach
  void setUp() {
    when(props.provider().issuer()).thenReturn(ISSUER);
    when(props.client().id()).thenReturn(CLIENT_ID);
    when(props.client().secret()).thenReturn(CLIENT_SECRET);
    when(props.client().scopes()).thenReturn(SCOPES);
  }

  /**
   * Tests for {@link AuthController#tokenFormController(MultiValueMap)}.
   */
  @Nested
  class TokenFormControllerTests {

    /**
     * Verifies that when posting valid form-encoded data, the controller forwards
     * the request to the Authorization Server and returns the same HTTP status and body.
     * <p>
     * Also validates that:
     * <ul>
     *   <li>The request URL matches the issuer's token endpoint</li>
     *   <li>Headers include proper Basic Authentication and content type</li>
     *   <li>The form data is preserved</li>
     * </ul>
     */
    @Test
    @DisplayName("Return OK with body when posting form data")
    void returnOkWithBodyWhenPostingFormData() {
      MultiValueMap<String, String> formIn = new LinkedMultiValueMap<>();
      formIn.add(GRANT_TYPE, AUTHORIZATION_CODE);
      formIn.add(CODE, "abc123");
      formIn.add(REDIRECT_URI, "http://localhost/callback");

      Map<String, Object> body = Map.of("access_token", "tok123", "token_type", "Bearer");
      ResponseEntity<Map> backendResponse = new ResponseEntity<>(body, HttpStatus.OK);

      when(restTemplate.postForEntity(urlCaptor.capture(), requestCaptor.capture(), eq(Map.class)))
          .thenReturn(backendResponse);

      ResponseEntity<Map<String, Object>> response = controller.tokenFormController(formIn);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(body);

      assertThat(urlCaptor.getValue()).isEqualTo(ISSUER + "/oauth2/token");

      HttpEntity<MultiValueMap<String, String>> sent = requestCaptor.getValue();
      assertThat(sent.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_FORM_URLENCODED);
      assertThat(sent.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).startsWith("Basic ");
      assertThat(sent.getBody()).isEqualTo(formIn);
    }
  }

  /**
   * Tests for {@link AuthController#tokenJsonController(Map)}.
   */
  @Nested
  class TokenJsonControllerTests {

    /**
     * Verifies that when the grant type is omitted, the controller defaults to
     * {@code authorization_code} and correctly forwards the request.
     * <p>
     * Also checks that:
     * <ul>
     *   <li>All required parameters are included in the request body</li>
     *   <li>Scopes are joined into a space-delimited string</li>
     * </ul>
     */
    @Test
    @DisplayName("Return OK when exchanging authorization code and grant type defaults to authorization_code")
    void returnOkWhenExchangingAuthorizationCodeAndGrantTypeDefaultsToAuthorizationCode() {
      Map<String, String> payload =
          Map.of(
              CODE, "theCode",
              REDIRECT_URI, "http://localhost/cb",
              CODE_VERIFIER, "verifier123"
              // sin GRANT_TYPE -> default authorization_code
          );

      Map<String, Object> body = Map.of("access_token", "at1");
      ResponseEntity<Map> backendResponse = new ResponseEntity<>(body, HttpStatus.OK);
      when(restTemplate.postForEntity(urlCaptor.capture(), requestCaptor.capture(), eq(Map.class)))
          .thenReturn(backendResponse);

      ResponseEntity<Map<String, Object>> response = controller.tokenJsonController(payload);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(body);

      assertThat(urlCaptor.getValue()).isEqualTo(ISSUER + "/oauth2/token");

      HttpEntity<MultiValueMap<String, String>> sent = requestCaptor.getValue();
      assertThat(sent.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_FORM_URLENCODED);
      assertThat(sent.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).startsWith("Basic ");

      MultiValueMap<String, String> form = sent.getBody();
      assertThat(form.getFirst(GRANT_TYPE)).isEqualTo(AUTHORIZATION_CODE);
      assertThat(form.getFirst(CODE)).isEqualTo("theCode");
      assertThat(form.getFirst(REDIRECT_URI)).isEqualTo("http://localhost/cb");
      assertThat(form.getFirst(CODE_VERIFIER)).isEqualTo("verifier123");
      assertThat(form.getFirst(SCOPE)).isEqualTo(String.join(" ", SCOPES));
    }

    /**
     * Verifies that when the grant type is explicitly set to {@code authorization_code},
     * the controller correctly processes the request and sends the expected parameters.
     */
    @Test
    @DisplayName("Return OK when exchanging authorization code with explicit grant type")
    void returnOkWhenExchangingAuthorizationCodeWithExplicitGrantType() {
      Map<String, String> payload =
          Map.of(
              GRANT_TYPE, AUTHORIZATION_CODE,
              CODE, "xyz",
              REDIRECT_URI, "http://localhost/callback",
              CODE_VERIFIER, "cv-1"
          );

      Map<String, Object> body = Map.of("access_token", "at2");
      ResponseEntity<Map> backendResponse = new ResponseEntity<>(body, HttpStatus.OK);
      when(restTemplate.postForEntity(urlCaptor.capture(), requestCaptor.capture(), eq(Map.class)))
          .thenReturn(backendResponse);

      ResponseEntity<Map<String, Object>> response = controller.tokenJsonController(payload);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(body);

      HttpEntity<MultiValueMap<String, String>> sent = requestCaptor.getValue();
      MultiValueMap<String, String> form = sent.getBody();

      assertThat(form.getFirst(GRANT_TYPE)).isEqualTo(AUTHORIZATION_CODE);
      assertThat(form.getFirst(CODE)).isEqualTo("xyz");
      assertThat(form.getFirst(REDIRECT_URI)).isEqualTo("http://localhost/callback");
      assertThat(form.getFirst(CODE_VERIFIER)).isEqualTo("cv-1");
      assertThat(form.getFirst(SCOPE)).isEqualTo(String.join(" ", SCOPES));
    }

    /**
     * Verifies that when the grant type is {@code refresh_token}, the controller sends
     * only the required parameters for the refresh flow and omits the {@code scope}.
     */
    @Test
    @DisplayName("Return OK when refreshing token")
    void returnOkWhenRefreshingToken() {
      Map<String, String> payload =
          Map.of(
              GRANT_TYPE, REFRESH_TOKEN,
              REFRESH_TOKEN, "rt-123"
          );

      Map<String, Object> body = Map.of("access_token", "newAT", "refresh_token", "newRT");
      ResponseEntity<Map> backendResponse = new ResponseEntity<>(body, HttpStatus.OK);
      when(restTemplate.postForEntity(urlCaptor.capture(), requestCaptor.capture(), eq(Map.class)))
          .thenReturn(backendResponse);

      ResponseEntity<Map<String, Object>> response = controller.tokenJsonController(payload);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(body);

      HttpEntity<MultiValueMap<String, String>> sent = requestCaptor.getValue();
      MultiValueMap<String, String> form = sent.getBody();

      assertThat(form.getFirst(GRANT_TYPE)).isEqualTo(REFRESH_TOKEN);
      assertThat(form.getFirst(REFRESH_TOKEN)).isEqualTo("rt-123");
      assertThat(form.getFirst(SCOPE)).isNull(); // no scope en refresh flow
    }

    /**
     * Verifies that when the grant type is not supported, the controller:
     * <ul>
     *   <li>Returns HTTP 400 Bad Request</li>
     *   <li>Includes an {@code unsupported_grant_type} error in the response body</li>
     *   <li>Does not call the Authorization Server</li>
     * </ul>
     */
    @Test
    @DisplayName("Return bad request when grant type is unsupported")
    @MockitoSettings(strictness = Strictness.LENIENT)
    void returnBadRequestWhenGrantTypeIsUnsupported() {
      Map<String, String> payload =
          Map.of(
              GRANT_TYPE, "password", // no soportado
              "username", "u",
              "password", "p"
          );

      ResponseEntity<Map<String, Object>> response = controller.tokenJsonController(payload);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      assertThat(response.getBody()).containsEntry(ERROR, UNSUPPORTED_GRANT_TYPE);

      // No debe llamar al Authorization Server
      verify(restTemplate, never()).postForEntity(anyString(), any(), eq(Map.class));
    }
  }
}