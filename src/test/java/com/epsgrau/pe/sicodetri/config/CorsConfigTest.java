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

@DisplayName("CorsConfig Tests")
class CorsConfigTest {

    private CorsConfig corsConfig;
    private CorsRegistry registry;

    @BeforeEach
    void setUp() {
        corsConfig = new CorsConfig();
        registry   = new CorsRegistry();
    }

    @Test
    @DisplayName("Configure CORS mapping with allowed origin, methods, credentials, and exposed headers")
    void configureCorsMappingWithAllowedOriginsMethodsCredentialsAndExposedHeaders() throws IllegalAccessException, NoSuchFieldException {
        // Act: invocamos addCorsMappings, que internamente rellenará `registrations`
        corsConfig.addCorsMappings(registry);

        // 1. Accedemos por reflexión a la lista privada `registrations` en CorsRegistry
        Field registrationsField = CorsRegistry.class.getDeclaredField("registrations");
        registrationsField.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<CorsRegistration> registrations =
                (List<CorsRegistration>) registrationsField.get(registry);

        // Debe haber exactamente una CorsRegistration, resultado de addMapping("/**")
        assertNotNull(registrations, "La lista de registrations no debe ser null");
        assertEquals(1, registrations.size(), "Debe existir una única CorsRegistration para '/**'");

        CorsRegistration firstRegistration = registrations.get(0);
        assertNotNull(firstRegistration, "No se obtuvo el primer CorsRegistration");

        // 2. Ahora, para extraer el CorsConfiguration
        Field configField = CorsRegistration.class.getDeclaredField("config");
        configField.setAccessible(true);

        CorsConfiguration config = (CorsConfiguration) configField.get(firstRegistration);
        assertNotNull(config, "El CorsConfiguration asociado no debe ser null");

        // 3. Verificamos el origen permitido
        List<String> allowedOrigins = config.getAllowedOrigins();
        assertNotNull(allowedOrigins, "La lista de allowedOrigins no debe ser null");
        assertEquals(1, allowedOrigins.size(), "Debe haber exactamente un origen permitido");
        assertEquals("http://localhost:3000", allowedOrigins.get(0),
                "El origen permitido debe ser http://localhost:3000");

        // 4. Verificamos los métodos permitidos
        List<String> allowedMethods = config.getAllowedMethods();
        assertNotNull(allowedMethods, "La lista de allowedMethods no debe ser null");
        assertTrue(allowedMethods.contains("GET"),    "Debe permitir GET");
        assertTrue(allowedMethods.contains("POST"),   "Debe permitir POST");
        assertTrue(allowedMethods.contains("PUT"),    "Debe permitir PUT");
        assertTrue(allowedMethods.contains("DELETE"), "Debe permitir DELETE");
        assertEquals(4, allowedMethods.size(), "Deben permitirse exactamente 4 métodos HTTP");

        // 5. Verificamos allowCredentials
        Boolean allowCredentials = config.getAllowCredentials();
        assertNotNull(allowCredentials, "El flag allowCredentials no debe ser null");
        assertTrue(allowCredentials, "allowCredentials debe ser true");

        // 6. Verificamos los encabezados expuestos
        List<String> exposedHeaders = config.getExposedHeaders();
        assertNotNull(exposedHeaders, "La lista de exposedHeaders no debe ser null");
        assertEquals(1, exposedHeaders.size(), "Debe haber exactamente un encabezado expuesto");
        assertEquals(HttpHeaders.CONTENT_DISPOSITION, exposedHeaders.get(0),
                "Debe exponerse Content-Disposition");
    }
}