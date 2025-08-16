package com.epsgrau.pe.sicodetri.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;

/**
 * Unit tests for {@link OpenApiConfig}.
 *
 * <p>
 * Verifies that the OpenAPI definition and grouped API beans are configured
 * with the correct metadata, group name, and path mappings.
 * </p>
 *
 * <b>Class</b>: {@link OpenApiConfigTest}<br/>
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
@DisplayName("OpenApiConfig Tests")
class OpenApiConfigTest {

    private final OpenApiConfig openApiConfig = new OpenApiConfig();

    /**
     * Tests that {@link OpenApiConfig#customOpenApi()} returns an {@link OpenAPI}
     * instance with the expected {@link Info} metadata: title, version, description,
     * terms of service, contact details, and license information.
     */
    @Test
    @DisplayName("Return customOpenAPI with correct metadata")
    void returnCustomOpenAPIWithCorrectMetadata() {
        // Act
        OpenAPI result = openApiConfig.customOpenApi();

        // Assert Info object is not null
        Info info = result.getInfo();
        assertNotNull(info, "Info object should not be null");

        // Verify title, version, description, termsOfService
        assertEquals("SICODETRI API", info.getTitle(), "Title should match");
        assertEquals("1.0.0", info.getVersion(), "Version should match");
        assertEquals(
                "Monolítico para realizar la conciliación de la depreciación Tributaria vs la Financiera.",
                info.getDescription(),
                "Description should match"
        );
        assertEquals(
                "https://www.epsgrau.pe/webpage/desktop/views/",
                info.getTermsOfService(),
                "Terms of service URL should match"
        );

        // Verify Contact sub-object
        Contact contact = info.getContact();
        assertNotNull(contact, "Contact should not be null");
        assertEquals(
                "Ing. Inf. Carlos Augusto Nole Machaca",
                contact.getName(),
                "Contact name should match"
        );
        assertEquals(
                "https://www.linkedin.com/in/carlos-augusto-nole-machaca-1ba16a49/",
                contact.getUrl(),
                "Contact URL should match"
        );
        assertEquals(
                "ing.inf.cnolem@outlook.com",
                contact.getEmail(),
                "Contact email should match"
        );

        // Verify License sub-object
        License license = info.getLicense();
        assertNotNull(license, "License should not be null");
        assertEquals("Copyright", license.getName(), "License name should match");
        assertEquals("https://www.gob.pe/indecopi", license.getUrl(), "License URL should match");
    }

    /**
     * Tests that {@link OpenApiConfig#defaultApi()} returns a {@link GroupedOpenApi}
     * instance configured with the group name "default" and a single path pattern "/**".
     */
    @Test
    @DisplayName("Return defaultApi with correct group and path settings")
    void returnDefaultApiWithCorrectGroupAndPaths() {
        // Act
        GroupedOpenApi grouped = openApiConfig.defaultApi();

        // Assert that GroupedOpenApi is not null
        assertNotNull(grouped, "GroupedOpenApi should not be null");

        // Verify group name
        assertEquals("default", grouped.getGroup(), "Group name should be 'default'");

        // Verify pathsToMatch contains "/**"
        String[] paths = grouped.getPathsToMatch().toArray(new String[0]);
        assertNotNull(paths, "PathsToMatch should not be null");
        assertEquals(1, paths.length, "There should be exactly one path pattern");
        assertEquals("/**", paths[0], "Path pattern should be '/**'");
    }

}