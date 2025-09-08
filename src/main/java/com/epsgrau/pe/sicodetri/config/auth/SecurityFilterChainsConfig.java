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
 * Configuration class defining security filter chains for the application.
 * Registers two {@link SecurityFilterChain} beans:
 * <ul>
 *   <li>
 *     A high-precedence chain for OAuth2 Authorization Server endpoints
 *     (<code>/oauth2/**</code>), enabling OpenID Connect support and a
 *     login URL entry point.
 *   </li>
 *   <li>
 *     A default, lowest-precedence chain for all other requests, configuring
 *     CSRF via cookies, permitting public access to <code>/</code>,
 *     <code>/login</code>, and <code>/error</code>, and enforcing form-based
 *     authentication on all other endpoints.
 *   </li>
 * </ul>
 * <b>Class</b>: {@link SecurityFilterChainsConfig}<br/>
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
public class SecurityFilterChainsConfig {

    /**
     * Builds the {@link org.springframework.security.web.SecurityFilterChain} dedicated to the
     * OAuth 2.1 / OpenID Connect Authorization Server endpoints.
     * <b>Scope</b>: this chain applies to the endpoints registered by
     * {@link org.springframework.security.oauth2.server.authorization.config.annotation.web
     * .configuration.OAuth2AuthorizationServerConfiguration},
     * e.g. {@code /oauth2/authorize}, {@code /oauth2/token}, {@code
     * /.well-known/openid-configuration},
     * {@code /oauth2/jwks}, and related endpoints depending on your configuration.
     * <b>Configuration</b>:
     * <ul>
     *   <li>Applies the framework’s defaults for the Authorization Server via
     *       {@link org.springframework.security.oauth2.server.authorization.config.annotation.web
     *       .configuration.OAuth2AuthorizationServerConfiguration#applyDefaultSecurity(org
     *       .springframework.security.config.annotation.web.builders.HttpSecurity)}.</li>
     *   <li>Enables OpenID Connect 1.0 support with
     *       {@link org.springframework.security.oauth2.server.authorization.config.annotation.web
     *       .configurers
     *       .OAuth2AuthorizationServerConfigurer#oidc(org.springframework.security.config
     *       .Customizer)}
     *       using {@link org.springframework.security.config.Customizer#withDefaults()}.</li>
     *   <li>Configures exception handling so unauthenticated requests are redirected to
     *       {@code /login} using {@link org.springframework.security.web.authentication
     *       .LoginUrlAuthenticationEntryPoint}.</li>
     *   <li>Enables CORS with default settings ({@link org.springframework.security.config
     *   .Customizer
     *   #withDefaults()}).</li>
     * </ul>
     * <b>Order</b>: annotated with {@link org.springframework.core.annotation.Order @Order}
     * at {@code Ordered.HIGHEST_PRECEDENCE}, ensuring this chain runs before other security chains
     * (e.g., resource-server or general web chains).
     *
     * @param http the {@link org.springframework.security.config.annotation.web.builders
     *             .HttpSecurity} to configure
     * @return the built {@link org.springframework.security.web.SecurityFilterChain} for the
     *          Authorization Server
     * @throws Exception if the filter chain cannot be constructed
     * @see org.springframework.security.oauth2.server.authorization.config
     *      .annotation.configuration.OAuth2AuthorizationServerConfiguration
     * @see org.springframework.security.oauth2.server.authorization.config.annotation.web
     *      .configurers.OAuth2AuthorizationServerConfigurer
     * @see org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
     * @see org.springframework.security.config.Customizer#withDefaults()
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
     * Builds the default (catch-all) {@link org.springframework.security.web.SecurityFilterChain}
     * for non-API endpoints.
     *
     * <p><b>Order</b>: annotated with {@link org.springframework.core.annotation.Order @Order}
     * at {@code Ordered.LOWEST_PRECEDENCE}, ensuring it runs after higher-priority chains
     * such as the authorization server and the resource server.</p>
     *
     * <p><b>Configuration</b>:</p>
     * <ul>
     *   <li><b>CORS</b>: enabled with {@link org.springframework.security.config.Customizer
     *   #withDefaults()}.</li>
     *   <li><b>CSRF</b>:
     *     <ul>
     *       <li>Ignores CSRF protection for {@code /auth/token} and {@code /logout}.</li>
     *       <li>Uses {@link org.springframework.security.web.csrf.CookieCsrfTokenRepository
     *       #withHttpOnlyFalse()}
     *           so the CSRF cookie is readable by JavaScript (e.g., SPA frameworks).</li>
     *     </ul>
     *   </li>
     *   <li><b>Authorization</b>:
     *     <ul>
     *       <li>Permits unauthenticated access to:
     *         <code>/</code>, <code>/login**</code>, <code>/callback</code>,
     *         <code>/v1/callback</code>,
     *         <code>/index.html</code>, <code>/favicon.ico</code>, <code>/static/**</code>,
     *         <code>/auth/token</code>, <code>/oauth2/**</code>, <code>/.well-known/**</code>,
     *         <code>/error**</code>.
     *       </li>
     *       <li>Requires authentication for any other request.</li>
     *     </ul>
     *   </li>
     *   <li><b>Form Login</b>: enabled with defaults.</li>
     *   <li><b>OAuth2 Resource Server (JWT)</b>: enabled with defaults.</li>
     *   <li><b>Logout</b>:
     *     <ul>
     *       <li>Logout URL: <code>/logout</code>, success redirect to <code>/login?logout</code>
     *       .</li>
     *       <li>Deletes cookies <code>JSESSIONID</code> and <code>XSRF-TOKEN</code>.</li>
     *       <li>Clears authentication and invalidates the HTTP session.</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @param http the {@link org.springframework.security.config.annotation.web.builders
     *             .HttpSecurity} to configure
     * @return the built {@link org.springframework.security.web.SecurityFilterChain} for general
     *         web endpoints
     * @throws Exception if the security filter chain cannot be built
     * @see org.springframework.security.config.Customizer#withDefaults()
     * @see org.springframework.security.web.csrf.CookieCsrfTokenRepository
     * @see org.springframework.security.oauth2.server.resource.authentication
     *      .JwtAuthenticationToken
     * @see org.springframework.security.web.authentication.logout.LogoutHandler
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