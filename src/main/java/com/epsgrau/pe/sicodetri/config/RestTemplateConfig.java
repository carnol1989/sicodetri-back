package com.epsgrau.pe.sicodetri.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Spring configuration class that exposes a reusable
 * {@link org.springframework.web.client.RestTemplate}
 * bean for performing HTTP requests.
 * <b>Class</b>: {@link RestTemplateConfig}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <u>Service Provider</u>: Consultor TI<br/>
 *     <u>Developed by</u>:
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *       <li>Ago 10, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 * @see org.springframework.web.client.RestTemplate
 * @see org.springframework.boot.web.client.RestTemplateBuilder
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates and registers a {@link org.springframework.web.client.RestTemplate} bean using the
     * provided {@link org.springframework.boot.web.client.RestTemplateBuilder}.
     *
     * @param builder a pre-configured
     *                {@link org.springframework.boot.web.client.RestTemplateBuilder}
     *                supplied by Spring Boot
     * @return a fully built {@link org.springframework.web.client.RestTemplate} ready for injection
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}