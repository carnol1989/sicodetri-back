package com.epsgrau.pe.sicodetri.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller that handles application errors and redirects to an external
 * Single Page Application (SPA) error page.
 * Implements Spring Boot's {@link ErrorController} to intercept requests to the
 * {@code /error} path.
 * Instead of rendering a server-side error view, it constructs a redirect URL pointing to
 * the frontend's error page (running on port 3000) and appends error details as query parameters.
 * <b>Key Features:</b>
 * <ul>
 *   <li>Captures error details such as HTTP status code, request URI, and error message.</li>
 *   <li>Encodes all parameters to ensure safe inclusion in a URL.</li>
 *   <li>Redirects to the SPA's error page so that the frontend can render a user-friendly
 *   error view.</li>
 * </ul>
 *
 * <b>Usage Notes:</b>
 * <ul>
 *   <li>This controller is typically invoked automatically by Spring Boot when an unhandled
 *   error occurs.</li>
 *   <li>The SPA should handle the {@code /error} route and display the provided error details.</li>
 * </ul>
 *
 * <b>Class</b>: {@link ErrorRedirectController}<br/>
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
 *     <li>Jul 21, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Controller
public class ErrorRedirectController implements ErrorController {

    /**
     * Handles requests to {@code /error} and redirects the user to the SPA's error page
     * with error details as query string parameters.
     * This method retrieves the following attributes from the request:
     * <ul>
     *   <li>{@link RequestDispatcher#ERROR_STATUS_CODE} - the HTTP status code (defaults to
     *   {@code 500} if absent)</li>
     *   <li>{@link RequestDispatcher#ERROR_REQUEST_URI} - the original request path that caused
     *   the error</li>
     *   <li>{@link RequestDispatcher#ERROR_MESSAGE} - the error message (defaults to
     *   {@code "Error"} if absent)</li>
     * </ul>
     * All values are URL-encoded using {@link StandardCharsets#UTF_8} to ensure safe transmission.
     *
     * <b>Example redirect:</b>
     * <pre>
     * redirect:<a href="http://localhost:3000/error?status=404&path=/api/data&message=Not%20Found">...</a>
     * </pre>
     *
     * @param request the {@link HttpServletRequest} containing error attributes
     * @return a redirect string pointing to the SPA error page with encoded query parameters
     * @throws UnsupportedEncodingException if UTF-8 encoding is not supported
     *                                      (unlikely in modern JVMs)
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) throws UnsupportedEncodingException {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Object msg = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        String statusStr = status != null ? status.toString() : "500";
        String path = uri != null ? uri.toString() : "";
        String message = msg != null ? msg.toString() : "Error";

        String qs = "status=" + URLEncoder.encode(statusStr, StandardCharsets.UTF_8)
            + "&path=" + URLEncoder.encode(path, StandardCharsets.UTF_8)
            + "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

        // Redirige a la SPA (puerto 3000) para que ella pinte la vista de error
        return "redirect:http://localhost:3000/error?" + qs;
    }
}