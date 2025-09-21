package com.epsgrau.pe.sicodetri.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * Resource Server security configuration for API endpoints under {@code /api/**}.
 * <p>
 * Configures:
 * </p>
 * <ul>
 *   <li><b>Path scope:</b> applies only to requests matching {@code /api/**}.</li>
 *   <li><b>Sessions:</b> {@link SessionCreationPolicy#IF_REQUIRED}.</li>
 *   <li><b>CSRF:</b> disabled.</li>
 *   <li><b>CORS:</b> enabled with {@link Customizer#withDefaults()}.</li>
 *   <li><b>Authorization rules:</b> role-based access for specific endpoints, plus authenticated
 *       access for the remaining API routes.</li>
 *   <li><b>OAuth2 Resource Server (JWT):</b> uses a custom {@link #jwtAuthenticationConverter()}
 *       that maps a custom {@code roles} claim to Spring authorities prefixed with {@code ROLE_}.</li>
 *   <li><b>Error handling:</b> {@link BearerTokenAuthenticationEntryPoint} for unauthenticated
 *       requests and {@link BearerTokenAccessDeniedHandler} for access denials.</li>
 * </ul>
 *
 * <p><b>Note:</b> The current JWT converter maps <i>only</i> roles from the {@code roles}
 * claim. If scope-based authorization is required, extend the converter accordingly.</p>
 *
 * <b>Class</b>: {@link ResourceServerConfig}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>: <br/>
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:<br/>
 *     <ul>
 *       <li>Jul 21, 2025 (JAR) Initial version.</li>
 *     </ul>
 * @version 1.1
 */
@Configuration
public class ResourceServerConfig {

    /**
     * Creates a {@link JwtAuthenticationConverter} that extracts authorities from a custom
     * {@code roles} claim and prefixes each value with {@code ROLE_}.
     * <p>
     * <b>Important:</b> This converter, as currently configured, does <i>not</i> merge scopes;
     * it solely maps authorities derived from the {@code roles} claim. To include scopes,
     * compose an additional {@link JwtGrantedAuthoritiesConverter} for the {@code scope}/{@code scopes}
     * claims and merge the results.
     * </p>
     *
     * @return a configured {@link JwtAuthenticationConverter} that maps {@code roles} -> {@code ROLE_*}
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
     * Builds the {@link SecurityFilterChain} for the Resource Server API endpoints.
     * <p><b>Applies to:</b> requests matching {@code /api/**}.</p>
     *
     * <p><b>Configuration summary:</b></p>
     * <ul>
     *   <li>Session management: {@link SessionCreationPolicy#IF_REQUIRED}.</li>
     *   <li>CSRF: disabled.</li>
     *   <li>CORS: defaults.</li>
     *   <li>Authorization:
     *     <ul>
     *       <li>{@code /api/v1/tax-tables/**} &rarr; role {@code TaxTableViewer}</li>
     *       <li>{@code POST /api/v1/financial-fixed-assets/upload} &rarr; role {@code AssetUploader}</li>
     *       <li>{@code POST /api/v1/financial-fixed-assets} &rarr; role {@code AssetUploader}</li>
     *       <li>{@code POST /api/v1/financial-fixed-assets/projection} &rarr; role {@code DepreciationProcessor}</li>
     *       <li>{@code GET /api/v1/financial-fixed-assets/report} &rarr; role {@code ReportGenerator}</li>
     *       <li>{@code /api/v1/financial-fixed-assets/**} &rarr; authenticated</li>
     *       <li>Any other {@code /api/**} &rarr; authenticated</li>
     *     </ul>
     *   </li>
     *   <li>OAuth2 Resource Server (JWT): custom {@link #jwtAuthenticationConverter()}.</li>
     *   <li>Error handling: {@link BearerTokenAuthenticationEntryPoint} and
     *       {@link BearerTokenAccessDeniedHandler}.</li>
     * </ul>
     *
     * <p><b>Order:</b> {@code @Order(2)} — evaluated after higher-priority chains, before any generic catch-all.</p>
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if the chain cannot be built
     * @see JwtAuthenticationConverter
     * @see BearerTokenAuthenticationEntryPoint
     * @see BearerTokenAccessDeniedHandler
     * @see Customizer#withDefaults()
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