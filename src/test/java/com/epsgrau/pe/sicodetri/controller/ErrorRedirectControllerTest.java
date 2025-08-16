package com.epsgrau.pe.sicodetri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.error.ErrorController;

/**
 * Unit tests for {@link ErrorRedirectController}.
 * Validates that {@link ErrorRedirectController#handleError(jakarta.servlet.http.HttpServletRequest)}
 * builds a <em>redirect URL</em> to the SPA error page
 * ({@code http://localhost:3000/error}) including the query parameters
 * {@code status}, {@code path}, and {@code message}, and that each value is
 * <strong>URL-encoded using UTF-8</strong>.
 * The controller reads standard servlet error attributes from the request:
 * <ul>
 *   <li>{@link jakarta.servlet.RequestDispatcher#ERROR_STATUS_CODE}</li>
 *   <li>{@link jakarta.servlet.RequestDispatcher#ERROR_REQUEST_URI}</li>
 *   <li>{@link jakarta.servlet.RequestDispatcher#ERROR_MESSAGE}</li>
 * </ul>
 * When any of these attributes are {@code null}, sensible defaults are applied
 * (e.g., status {@code 500}, empty path, message {@code "Error"}).
 * <b>Class</b>: {@link ErrorRedirectControllerTest}<br/>
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
@ExtendWith(MockitoExtension.class)
class ErrorRedirectControllerTest {

  @Mock
  HttpServletRequest request;

  private final ErrorRedirectController controller = new ErrorRedirectController();

  /**
   * Ensures that the controller returns a redirect URL with
   * <strong>UTF-8 URL-encoded</strong> query parameters when the request
   * contains non-ASCII characters and spaces in the error attributes.
   *
   * <p><b>Given</b>:<br/>
   * {@code ERROR_STATUS_CODE = 404}, {@code ERROR_REQUEST_URI = "/ruta con espacio/á"},
   * {@code ERROR_MESSAGE = "Not Found: razón"}.
   * </p>
   * <p><b>When</b>: {@link ErrorRedirectController#handleError(HttpServletRequest)} is invoked.</p>
   * <p><b>Then</b>: The returned value equals
   * {@code redirect:http://localhost:3000/error?status=...&path=...&message=...}
   * with each part URL-encoded using {@link java.net.URLEncoder} and
   * {@link java.nio.charset.StandardCharsets#UTF_8}.</p>
   *
   * @throws Exception only if the test setup or execution fails
   */
  @Test
  @DisplayName("Return redirect URL when error attributes are present and must be URL-encoded")
  void returnRedirectUrlWhenErrorAttributesArePresentAndMustBeUrlEncoded() throws Exception {
    // given
    when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);
    when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/ruta con espacio/á");
    when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Not Found: razón");

    String expected =
        "redirect:http://localhost:3000/error?"
            + "status="
            + URLEncoder.encode("404", StandardCharsets.UTF_8)
            + "&path="
            + URLEncoder.encode("/ruta con espacio/á", StandardCharsets.UTF_8)
            + "&message="
            + URLEncoder.encode("Not Found: razón", StandardCharsets.UTF_8);

    // when
    String result = controller.handleError(request);

    // then
    assertThat(result).isEqualTo(expected);
  }

  /**
   * Verifies that default values are applied and properly encoded when all
   * servlet error attributes are {@code null}.
   *
   * <p><b>Given</b>:<br/>
   * {@code ERROR_STATUS_CODE = null}, {@code ERROR_REQUEST_URI = null},
   * {@code ERROR_MESSAGE = null}.
   * </p>
   * <p><b>When</b>: {@link ErrorRedirectController#handleError(HttpServletRequest)} is invoked.</p>
   * <p><b>Then</b>: The returned value equals
   * {@code redirect:http://localhost:3000/error?status=500&path=&message=Error}
   * with all parts URL-encoded using UTF-8.</p>
   *
   * @throws Exception only if the test setup or execution fails
   */
  @Test
  @DisplayName("Return redirect URL when error attributes are null and defaults are applied")
  void returnRedirectUrlWhenErrorAttributesAreNullAndDefaultsAreApplied() throws Exception {
    // given (todos null -> usa defaults: status=500, path='', message='Error')
    when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(null);
    when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn(null);
    when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn(null);

    String expected =
        "redirect:http://localhost:3000/error?"
            + "status="
            + URLEncoder.encode("500", StandardCharsets.UTF_8)
            + "&path="
            + URLEncoder.encode("", StandardCharsets.UTF_8)
            + "&message="
            + URLEncoder.encode("Error", StandardCharsets.UTF_8);

    // when
    String result = controller.handleError(request);

    // then
    assertThat(result).isEqualTo(expected);
  }
}