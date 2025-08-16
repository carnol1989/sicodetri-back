package com.epsgrau.pe.sicodetri.config.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Configuration class that enables method-level security in the application.
 * Activates support for security annotations on service methods, including:
 * <ul>
 *   <li>{@code @PreAuthorize} and {@code @PostAuthorize}</li>
 *   <li>{@code @Secured}</li>
 *   <li>(Optionally) {@code @RolesAllowed}</li>
 * </ul>
 * <b>Class</b>: {@link MethodSecurityConfig}<br/>
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
 *     <li>Jul 21, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Configuration
@EnableMethodSecurity(
    prePostEnabled  = true,   // para @PreAuthorize / @PostAuthorize
    securedEnabled  = true,   // para @Secured
    jsr250Enabled   = false   // opcional: para @RolesAllowed
)
public class MethodSecurityConfig {
}
