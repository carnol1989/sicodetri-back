package com.epsgrau.pe.sicodetri.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS (Cross-Origin Resource Sharing) configuration for the application.
 * This configuration allows cross-origin requests from specified origins and methods,
 * enables credentials sharing, and exposes relevant headers to clients.
 * <b>Class</b>: {@link CorsConfig}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configures global Cross-Origin Resource Sharing (CORS) mappings.
     * Registers a mapping for all paths ({@code "/**"}) that:
     * <ul>
     *   <li><b>Allows origins:</b> {@code http://localhost:3000}</li>
     *   <li><b>Allows methods:</b> {@code GET}, {@code POST}, {@code PUT}, {@code DELETE},
     *   {@code OPTIONS}</li>
     *   <li><b>Allows headers:</b> {@code Authorization}, {@code Content-Type}, {@code
     *   X-Requested-With}, {@code X-XSRF-TOKEN}</li>
     *   <li><b>Allows credentials:</b> {@code true} (sends {@code
     *   Access-Control-Allow-Credentials: true})</li>
     *   <li><b>Exposes headers:</b> {@link org.springframework.http.HttpHeaders
     *   #CONTENT_DISPOSITION}, {@link org.springframework.http.HttpHeaders#AUTHORIZATION}</li>
     * </ul>
     * <b>Note:</b> When credentials are allowed, the {@code Access-Control-Allow-Origin} header
     * cannot be {@code *}; Spring will echo the allowed origin instead.
     *
     * @param registry the {@link org.springframework.web.servlet.config.annotation.CorsRegistry}
     *                 to register mappings with
     * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addCorsMappings
     *      (org.springframework.web.servlet.config.annotation.CorsRegistry)
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With", "X-XSRF-TOKEN")
                .allowCredentials(true)
                .exposedHeaders(HttpHeaders.CONTENT_DISPOSITION, HttpHeaders.AUTHORIZATION);
    }

}
