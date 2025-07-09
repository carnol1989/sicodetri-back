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
public class AllKarateFeaturesIT {

  @LocalServerPort
  int port;

  @Value("${karate.config.location:classpath:karate-config.js}")
  String karateConfigLocation;

  @Karate.Test
  Karate testUploadEndpoint() {
    return Karate
        .run("tax-table-sunat.feature",
            "upload.feature",
            "save-financial-fixed-assets.feature",
            "projection.feature")
        .systemProperty("baseUrl", "http://localhost:" + port)
        .relativeTo(getClass());
  }
}
