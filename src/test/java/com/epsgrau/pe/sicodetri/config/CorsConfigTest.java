package com.epsgrau.pe.sicodetri.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Unit tests for {@link CorsConfig}.
 *
 * <p>
 * Verifies that CORS mappings are registered properly with the expected
 * allowed origins, HTTP methods, credential flag, and exposed headers.
 * </p>
 *
 * <b>Class</b>: {@link CorsConfigTest}<br/>
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
@DisplayName("CorsConfig Tests")
class CorsConfigTest {

    private CorsConfig corsConfig;
    private CorsRegistry registry;

    /**
     * Initializes a fresh {@link CorsConfig} instance and a new {@link CorsRegistry}
     * before each test, ensuring a clean registry state.
     */
    @BeforeEach
    void setUp() {
        corsConfig = new CorsConfig();
        registry   = new CorsRegistry();
    }

    /**
     * Tests that {@code CorsConfig#addCorsMappings(CorsRegistry)}:
     * <ul>
     *   <li>Registers exactly one {@link CorsRegistration} for all paths ("**").</li>
     *   <li>Sets the allowed origin to "http://localhost:3000".</li>
     *   <li>Enables GET, POST, PUT, and DELETE methods.</li>
     *   <li>Allows credentials.</li>
     *   <li>Exposes the Content-Disposition header.</li>
     * </ul>
     *
     * @throws NoSuchFieldException   if the private field cannot be located by reflection
     * @throws IllegalAccessException if the private field cannot be made accessible
     */
    @Test
    @DisplayName("Configure CORS mapping with allowed origin, methods, credentials, and exposed headers")
    void configureCorsMappingWithAllowedOriginsMethodsCredentialsAndExposedHeaders() throws IllegalAccessException, NoSuchFieldException {
        corsConfig.addCorsMappings(registry);

        Field registrationsField = CorsRegistry.class.getDeclaredField("registrations");
        registrationsField.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<CorsRegistration> registrations =
                (List<CorsRegistration>) registrationsField.get(registry);

        assertNotNull(registrations, "Registrations must not be null");
        assertEquals(1, registrations.size(), "There must be exactly one CorsRegistration");

        CorsRegistration firstRegistration = registrations.get(0);
        assertNotNull(firstRegistration, "First CorsRegistration must not be null");

        Field patternField = CorsRegistration.class.getDeclaredField("pathPattern");
        patternField.setAccessible(true);
        String pathPattern = (String) patternField.get(firstRegistration);
        assertEquals("/**", pathPattern, "Path pattern must be /**");

        Field configField = CorsRegistration.class.getDeclaredField("config");
        configField.setAccessible(true);
        CorsConfiguration config = (CorsConfiguration) configField.get(firstRegistration);
        assertNotNull(config, "CorsConfiguration must not be null");

        List<String> allowedOrigins = config.getAllowedOrigins();
        assertNotNull(allowedOrigins, "Allowed origins must not be null");
        assertEquals(1, allowedOrigins.size(), "Exactly one allowed origin is expected");
        assertEquals("http://localhost:3000", allowedOrigins.get(0));

        List<String> allowedMethods = config.getAllowedMethods();
        assertNotNull(allowedMethods, "Allowed methods must not be null");
        assertTrue(allowedMethods.contains("GET"),    "Must allow GET");
        assertTrue(allowedMethods.contains("POST"),   "Must allow POST");
        assertTrue(allowedMethods.contains("PUT"),    "Must allow PUT");
        assertTrue(allowedMethods.contains("DELETE"), "Must allow DELETE");
        assertTrue(allowedMethods.contains("OPTIONS"), "Must allow OPTIONS");
        assertEquals(5, allowedMethods.size(), "Exactly five HTTP methods should be allowed");

        List<String> allowedHeaders = config.getAllowedHeaders();
        assertNotNull(allowedHeaders, "Allowed headers must not be null");
        assertTrue(allowedHeaders.contains("Authorization"));
        assertTrue(allowedHeaders.contains("Content-Type"));
        assertTrue(allowedHeaders.contains("X-Requested-With"));
        assertTrue(allowedHeaders.contains("X-XSRF-TOKEN"));
        assertEquals(4, allowedHeaders.size(), "Exactly four allowed headers are expected");

        Boolean allowCredentials = config.getAllowCredentials();
        assertNotNull(allowCredentials, "El flag allowCredentials no debe ser null");
        assertTrue(allowCredentials, "allowCredentials debe ser true");

        List<String> exposedHeaders = config.getExposedHeaders();
        assertNotNull(exposedHeaders, "Exposed headers must not be null");
        assertTrue(exposedHeaders.contains(HttpHeaders.CONTENT_DISPOSITION));
        assertTrue(exposedHeaders.contains(HttpHeaders.AUTHORIZATION));
        assertEquals(2, exposedHeaders.size(), "Exactly two exposed headers are expected");
    }
}