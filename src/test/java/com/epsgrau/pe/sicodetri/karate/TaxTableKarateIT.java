package com.epsgrau.pe.sicodetri.karate;

import com.intuit.karate.junit5.Karate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = com.epsgrau.pe.sicodetri.SicodetriApplication.class
)
@ActiveProfiles("integration")
class TaxTableKarateIT {

  // inyecta el puerto que elegirá Spring al azar
  @LocalServerPort
  int port;

  @Value("${karate.config.location:classpath:karate-config.js}")
  String karateConfigLocation;

  @Karate.Test
  Karate runIntegrationTests() {
    // le pasamos la URL base a Karate via system property
    return Karate
        .run("tax-table-sunat.feature")
        .systemProperty("baseUrl", "http://localhost:" + port)
        .relativeTo(getClass());
  }

}