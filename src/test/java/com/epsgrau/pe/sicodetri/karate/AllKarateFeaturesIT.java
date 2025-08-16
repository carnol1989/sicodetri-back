package com.epsgrau.pe.sicodetri.karate;

import com.intuit.karate.junit5.Karate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test suite that executes all Karate feature files against
 * the running application in the "integration" profile.
 *
 * <p>
 * This test class uses a random port Spring Boot context configured with
 * {@link com.epsgrau.pe.sicodetri.config.IntegrationTestConfig} and
 * {@link com.epsgrau.pe.sicodetri.config.TestRegisteredClientConfig} to
 * stub external dependencies. It runs the following Karate scenarios:
 * <ul>
 *   <li>login.feature</li>
 *   <li>tax-table-sunat.feature</li>
 *   <li>upload.feature</li>
 *   <li>save-financial-fixed-assets.feature</li>
 *   <li>projection.feature</li>
 *   <li>get-financial-fixed-assets.feature</li>
 *   <li>report.feature</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link AllKarateFeaturesIT}<br/>
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
 *     <li>Jul 07, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
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

    /**
     * Executes the full end-to-end integration test flow using Karate.
     * <p>
     * Sets the {@code baseUrl} to the local server port and runs the
     * login, tax table, upload, save, projection, retrieval, and report
     * feature files in sequence.
     * </p>
     *
     * @return a configured {@link Karate} runner for the integration features
     */
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
