package com.epsgrau.pe.sicodetri.karate;

import com.intuit.karate.junit5.Karate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test suite that executes all Karate feature files against
 * a running Spring Boot application using the {@code integration} profile.
 *
 * <p><b>Spring context</b>:</p>
 * <ul>
 *   <li>{@link org.springframework.boot.test.context.SpringBootTest} with {@code RANDOM_PORT}.</li>
 *   <li>Configuration classes:
 *     {@code SicodetriApplication},
 *     {@code IntegrationTestConfig},
 *     {@code TestRegisteredClientConfig} (stubs/external dependencies).</li>
 *   <li>{@link org.springframework.test.context.ActiveProfiles @ActiveProfiles}({@code "integration"}).</li>
 * </ul>
 *
 * <p><b>Karate</b>:</p>
 * <ul>
 *   <li>Features executed (relative to this classpath location):
 *     {@code login.feature},
 *     {@code tax-table-sunat.feature},
 *     {@code upload.feature},
 *     {@code save-financial-fixed-assets.feature},
 *     {@code projection.feature},
 *     {@code get-financial-fixed-assets.feature},
 *     {@code report.feature}.</li>
 *   <li>Sets {@code baseUrl} system property to the random server port.</li>
 *   <li>External config file resolved via {@code karate.config.location}
 *       (default: {@code classpath:karate-config.js}).</li>
 * </ul>
 *
 * <b>Class</b>: {@link AllKarateFeaturesIT}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>:
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *       <li>Jul 07, 2025 (JAR) Initial version.</li>
 *     </ul>
 * @version 1.1
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {
        com.epsgrau.pe.sicodetri.SicodetriApplication.class,
        com.epsgrau.pe.sicodetri.config.IntegrationTestConfig.class,
        com.epsgrau.pe.sicodetri.config.TestRegisteredClientConfig.class
    }
)
@ActiveProfiles("integration")
public class AllKarateFeaturesIT {

    @LocalServerPort
    int port;

    @Value("${karate.config.location:classpath:karate-config.js}")
    String karateConfigLocation;

    @Karate.Test
    Karate testFlowIntegrationEndpoint() {
        return Karate
            .run("login.feature",
                "tax-table-sunat.feature",
                "upload.feature",
                "save-financial-fixed-assets.feature",
                "projection.feature",
                "get-financial-fixed-assets.feature",
                "report.feature")
            .systemProperty("baseUrl", "http://localhost:" + port)
            .relativeTo(getClass());
    }
}
