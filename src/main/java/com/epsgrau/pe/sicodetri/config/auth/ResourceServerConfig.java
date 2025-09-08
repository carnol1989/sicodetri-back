package com.epsgrau.pe.sicodetri.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Resource Server.
 * Defines how incoming requests to protected API endpoints under <code>/api/**</code>
 * are authenticated and authorized using JWTs. Configures stateless session management,
 * CSRF disabling, role- and scope-based access rules, and custom JWT authority mapping.
 * <b>Class</b>: {@link ResourceServerConfig}<br/>
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
public class ResourceServerConfig {

    /**
     * Creates a {@link JwtAuthenticationConverter} that extracts authorities from both
     * the standard <code>scope</code> claim and a custom <code>roles</code> claim.
     * The converter applies a prefix of <code>ROLE_</code> to each role found in the
     * <code>roles</code> claim, combines these with any scopes, and returns the full
     * authority list for use in authorization decisions.
     *
     * @return a configured {@link JwtAuthenticationConverter}
    */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthorityPrefix("ROLE_");
        rolesConverter.setAuthoritiesClaimName("roles");

        // combinamos ambos (scopes + roles)
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // 1) Creamos siempre una lista nueva
            List<GrantedAuthority> authorities = new ArrayList<>();

            Collection<GrantedAuthority> roleAuths = rolesConverter.convert(jwt);
            if (roleAuths != null) {
                authorities.addAll(roleAuths);
            }

            return authorities;
        });
        return converter;
    }

    /**
     * Builds the resource-server {@link org.springframework.security.web.SecurityFilterChain}
     * for API endpoints.
     *
     * <p><b>Scope</b>: applies only to requests that match {@code "/api/**"}.</p>
     *
     * <p><b>Configuration</b>:</p>
     * <ul>
     *   <li>Session management: {@link org.springframework.security.config.http
     *   .SessionCreationPolicy#STATELESS}.</li>
     *   <li>CSRF: disabled via {@link org.springframework.security.config.annotation.web
     *   .configurers.AbstractHttpConfigurer#disable}.</li>
     *   <li>CORS: enabled with {@link org.springframework.security.config.Customizer
     *   #withDefaults()}.</li>
     *   <li>Authorization rules:
     *     <ul>
     *       <li><code>/api/v1/tax-tables/**</code> → requires role <code>TaxTableViewer</code>
     *       .</li>
     *       <li><code>POST /api/v1/financial-fixed-assets/upload</code> → requires role
     *       <code>AssetUploader</code>.</li>
     *       <li><code>POST /api/v1/financial-fixed-assets</code> → requires role
     *       <code>AssetUploader</code>.</li>
     *       <li><code>POST /api/v1/financial-fixed-assets/projection</code> → requires role
     *       <code>DepreciationProcessor</code>.</li>
     *       <li><code>GET /api/v1/financial-fixed-assets/report</code> → requires role
     *       <code>ReportGenerator</code>.</li>
     *       <li><code>/api/v1/financial-fixed-assets/**</code> → any method
     *       requires authentication.</li>
     *       <li>Any other request (under <code>/api/**</code>) → requires authentication.</li>
     *     </ul>
     *   </li>
     *   <li>OAuth2 Resource Server (JWT):
     *     <ul>
     *       <li>Uses a custom {@link #jwtAuthenticationConverter()} to map JWT claims
     *       to authorities.</li>
     *       <li>Handles unauthenticated access with {@link org.springframework.security
     *       .oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint}.</li>
     *       <li>Handles access denials with {@link org.springframework.security.oauth2.server
     *       .resource.web.access.BearerTokenAccessDeniedHandler}.</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <p><b>Order</b>: annotated with {@link org.springframework.core.annotation.Order @Order}
     * at {@code Ordered.LOWEST_PRECEDENCE - 1} so it runs after higher-priority chains
     * (e.g., the authorization-server chain) but before a generic catch-all chain.</p>
     *
     * @param http the {@link org.springframework.security.config.annotation.web.builders
     *             .HttpSecurity} to configure
     * @return the built {@link org.springframework.security.web.SecurityFilterChain} for
     *         resource-server endpoints
     * @throws Exception if the security filter chain cannot be built
     * @see org.springframework.security.oauth2.server.resource.authentication
     *      .JwtAuthenticationConverter
     * @see org.springframework.security.oauth2.server.resource.web
     *      .BearerTokenAuthenticationEntryPoint
     * @see org.springframework.security.oauth2.server.resource.web.access
     *      .BearerTokenAccessDeniedHandler
     * @see org.springframework.security.config.Customizer#withDefaults()
     */
    @Bean
    @Order(2)
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http)
        throws Exception {
        http
            .securityMatcher("/api/**")
            .sessionManagement(sm -> sm
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/tax-tables/**").hasRole("TaxTableViewer")
                .requestMatchers(HttpMethod.POST, "/api/v1/financial-fixed-assets/upload")
                    .hasRole("AssetUploader")
                .requestMatchers(HttpMethod.POST, "/api/v1/financial-fixed-assets")
                    .hasRole("AssetUploader")
                .requestMatchers(HttpMethod.POST, "/api/v1/financial-fixed-assets/projection")
                    .hasRole("DepreciationProcessor")
                .requestMatchers(HttpMethod.GET, "/api/v1/financial-fixed-assets/report")
                    .hasRole("ReportGenerator")
                .requestMatchers("/api/v1/financial-fixed-assets/**")
                    .authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        return http.build();
    }

}