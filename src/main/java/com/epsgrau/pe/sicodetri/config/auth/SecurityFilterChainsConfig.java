package com.epsgrau.pe.sicodetri.config.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

/**
 * Defines two {@link SecurityFilterChain} beans for the application.
 * <ul>
 *   <li>
 *     <b>Authorization Server chain</b> ({@code @Order(1)}): applies the Spring Authorization Server
 *     defaults, enables OpenID Connect 1.0, enables CORS with defaults, and redirects unauthenticated
 *     requests to {@code /login}.
 *   </li>
 *   <li>
 *     <b>Public web chain</b> ({@code @Order(3)}): limited to a set of public
 *     endpoints (e.g., {@code /}, {@code /login**},
 *     {@code /callback}, static assets, error endpoints, etc.). Configures CORS with defaults, CSRF with
 *     cookie-based tokens (readable by JavaScript), permits the listed public endpoints and requires
 *     authentication for any other request that matches this chain. This is <i>not</i> a catch-all chain.
 *   </li>
 * </ul>
 *
 * <p><b>Notes:</b></p>
 * <ul>
 *   <li>The Resource Server chain (e.g., for {@code /api/**}) is expected to be defined elsewhere
 *       (e.g., in {@code ResourceServerConfig}) with a distinct {@code @Order} (commonly {@code 2}).</li>
 *   <li>Although {@code /oauth2/**} and {@code /.well-known/**} are permitted here, those paths are
 *       typically handled by the Authorization Server chain.</li>
 * </ul>
 *
 * <b>Class</b>: {@link SecurityFilterChainsConfig}<br/>
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
 *       <li>Jul 21, 2025 (JAR) Initial version.</li>
 *     </ul>
 * @version 1.1
 */
@Configuration
public class SecurityFilterChainsConfig {

    /**
     * Builds the high-precedence {@link SecurityFilterChain} for OAuth 2.1 / OpenID Connect
     * Authorization Server endpoints.
     *
     * <p><b>Scope</b>: endpoints registered by
     * {@link OAuth2AuthorizationServerConfiguration#applyDefaultSecurity(HttpSecurity)}, e.g.:
     * {@code /oauth2/authorize}, {@code /oauth2/token}, {@code /.well-known/openid-configuration},
     * {@code /oauth2/jwks}, among others depending on the configuration.</p>
     *
     * <p><b>Configuration</b>:</p>
     * <ul>
     *   <li>Applies Authorization Server defaults via
     *       {@link OAuth2AuthorizationServerConfiguration#applyDefaultSecurity(HttpSecurity)}.</li>
     *   <li>Enables OIDC 1.0 via {@link OAuth2AuthorizationServerConfigurer#oidc(Customizer)} with defaults.</li>
     *   <li>Exception handling: redirects unauthenticated requests to {@code /login}
     *       via {@link LoginUrlAuthenticationEntryPoint}.</li>
     *   <li>CORS: enabled with {@link Customizer#withDefaults()}.</li>
     * </ul>
     *
     * <p><b>Order</b>: {@code @Order(1)} — runs before other chains (e.g.,
     * the resource server or public web chain).</p>
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the built {@link SecurityFilterChain} for the Authorization Server
     * @throws Exception if the filter chain cannot be constructed
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
        throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .oidc(Customizer.withDefaults());
        http.exceptionHandling(exceptions ->
            exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
        );
        http.cors(Customizer.withDefaults());
        return http.build();
    }

    /**
     * Builds the public-endpoints {@link SecurityFilterChain} used for non-API routes such as the home page,
     * login, callbacks, static assets, and error endpoints.
     *
     * <p><b>Scope</b>: restricted by a {@code securityMatcher} to the following routes:
     * {@code /}, {@code /login**}, {@code /callback}, {@code /v1/callback}, {@code /index.html},
     * {@code /favicon.ico}, {@code /static/**}, {@code /auth/token}, {@code /error**}.</p>
     *
     * <p><b>Configuration</b>:</p>
     * <ul>
     *   <li>CORS: enabled with {@link Customizer#withDefaults()}.</li>
     *   <li>CSRF:
     *     <ul>
     *       <li>Ignores {@code /auth/token} and {@code /logout}.</li>
     *       <li>Uses {@link CookieCsrfTokenRepository#withHttpOnlyFalse()}
     *       so the CSRF cookie is readable by the SPA.</li>
     *     </ul>
     *   </li>
     *   <li>Authorization:
     *     <ul>
     *       <li>Permits unauthenticated access to:
     *           {@code /}, {@code /login**}, {@code /callback}, {@code /v1/callback},
     *           {@code /index.html}, {@code /favicon.ico}, {@code /static/**},
     *           {@code /auth/token}, {@code /oauth2/**}, {@code /.well-known/**}, {@code /error**}.</li>
     *       <li>Requires authentication for any other request <i>within this chain's matcher</i>.</li>
     *     </ul>
     *   </li>
     *   <li>Form Login: enabled with defaults.</li>
     *   <li>Logout:
     *     <ul>
     *       <li>URL: {@code /logout}, success redirect: {@code /login?logout}.</li>
     *       <li>Deletes cookies {@code JSESSIONID} and {@code XSRF-TOKEN}.</li>
     *       <li>Clears authentication and invalidates the HTTP session.</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <p><b>Order</b>: {@code @Order(3)} — runs after the Authorization Server chain and after any
     * resource server chain (commonly {@code @Order(2)}).</p>
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the built {@link SecurityFilterChain} for public web endpoints
     * @throws Exception if the security filter chain cannot be built
     */
    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(request -> new OrRequestMatcher(
                new AntPathRequestMatcher("/"),
                new AntPathRequestMatcher("/login**"),
                new AntPathRequestMatcher("/callback"),
                new AntPathRequestMatcher("/v1/callback"),
                new AntPathRequestMatcher("/index.html"),
                new AntPathRequestMatcher("/favicon.ico"),
                new AntPathRequestMatcher("/static/**"),
                new AntPathRequestMatcher("/auth/token"),
                new AntPathRequestMatcher("/logout"),
                new AntPathRequestMatcher("/error**")
            ).matches(request))
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/auth/token", "/logout")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/login**",
                    "/callback", "/v1/callback",
                    "/index.html", "/favicon.ico", "/static/**",
                    "/auth/token",
                    "/oauth2/**", "/.well-known/**",
                    "/logout",
                    "/error**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
            );
        return http.build();
    }
}