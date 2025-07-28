package com.epsgrau.pe.sicodetri.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (Swagger) documentation of the SICODETRI API.
 * Provides beans to customize the OpenAPI metadata (title, version, description,
 * terms of service, contact, license) and to define a default API group.
 * <b>Class</b>: {@link OpenApiConfig}<br/>
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
 *     <li>Jun 06, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates a customized {@link OpenAPI} bean containing metadata for the API documentation.
     * Sets the API title, version, description, terms of service URL, contact information,
     * and license details, which will appear in the generated Swagger UI.
     *
     * @return a configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SICODETRI API")
                        .version("1.0.0")
                        .description("Monolítico para realizar la conciliación de la depreciación "
                            + "Tributaria vs la Financiera.")
                        .termsOfService("https://www.epsgrau.pe/webpage/desktop/views/")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Ing. Inf. Carlos Augusto Nole Machaca")
                                .url("https://www.linkedin.com/in/carlos-augusto-nole-machaca-1ba16a49/")
                                .email("ing.inf.cnolem@outlook.com"))
                        .license(new License()
                                .name("Copyright")
                                .url("https://www.gob.pe/indecopi"))
                );
    }

    /**
     * Defines the default API grouping for all endpoints.
     * Registers a {@link GroupedOpenApi} bean named "default" that matches all paths.
     * Additional groupings can be configured if needed.
     *
     * @return a {@link GroupedOpenApi} instance for the default API group
     */
    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch("/**")
                .build();
    }

}
