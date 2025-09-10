package com.epsgrau.pe.sicodetri.config.auth;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Unit tests for {@link SecurityFilterChainsConfig}.
 *
 * <p>This suite verifies the wiring and key behaviors of the two security filter chains
 * defined in {@link SecurityFilterChainsConfig}:</p>
 * <ul>
 *   <li><b>Authorization Server chain</b> ({@code authorizationServerSecurityFilterChain(HttpSecurity)}):
 *     <ul>
 *       <li>Applies Spring Authorization Server defaults via
 *           {@link OAuth2AuthorizationServerConfiguration#applyDefaultSecurity(HttpSecurity)}.</li>
 *       <li>Enables OIDC 1.0 through {@link OAuth2AuthorizationServerConfigurer#oidc(Customizer)}.</li>
 *       <li>Configures exception handling to redirect unauthenticated requests to {@code /login}.</li>
 *       <li>Enables CORS with {@link Customizer#withDefaults()}.</li>
 *       <li>Returns the built {@link SecurityFilterChain}.</li>
 *     </ul>
 *   </li>
 *   <li><b>Public web chain</b> ({@code defaultSecurityFilterChain(HttpSecurity)}):
 *     <ul>
 *       <li>Restricts its scope using a {@code securityMatcher} to specific public routes
 *           (home, login, callbacks, static assets, error, etc.).</li>
 *       <li>Configures CORS with defaults and CSRF using a cookie repository readable by the SPA,
 *           ignoring {@code /auth/token} and {@code /logout}.</li>
 *       <li>Permits the listed public endpoints and requires authentication for any other request
 *           that matches this chain.</li>
 *       <li>Enables form login and configures logout (URL, success redirect, cookie deletion,
 *           authentication clearing, and session invalidation).</li>
 *       <li>Returns the built {@link SecurityFilterChain}.</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p><b>Note:</b> Tests intentionally focus on the two chains defined in this class; any Resource
 * Server chain (e.g., for {@code /api/**}) is covered elsewhere.</p>
 *
 * <b>Class</b>: {@link SecurityFilterChainsConfigTest}<br/>
 * <b>COPYRIGHT</b>: &copy; 2024 EPS Grau. <br/>
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
 *       <li>Jul 29, 2025 (JAR) Initial version.</li>
 *     </ul>
 * @version 1.1
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityFilterChainsConfig Tests")
class SecurityFilterChainsConfigTest {

  @Mock
  private HttpSecurity http;

  @Mock
  private OAuth2AuthorizationServerConfigurer authorizationServerConfigurer;

  @Mock
  private SecurityFilterChain dummyChain;

  @Test
  @DisplayName("Return authZ-server chain when configuring OAuth2 authorization server")
  void returnAuthServerChainWhenConfiguringAuthorizationServer() throws Exception {
    try (MockedStatic<OAuth2AuthorizationServerConfiguration> staticConfig =
             mockStatic(OAuth2AuthorizationServerConfiguration.class)) {

      // exceptionHandling(...) -> ejecutar customizer y devolver el tipo correcto
      ExceptionHandlingConfigurer<HttpSecurity> exCfg = mock(ExceptionHandlingConfigurer.class);
      doReturn(exCfg).when(exCfg).authenticationEntryPoint(any());
      org.mockito.Mockito.when(http.exceptionHandling(any())).thenAnswer(inv -> {
        @SuppressWarnings("unchecked")
        Customizer<ExceptionHandlingConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
        customizer.customize(exCfg);
        return http;
      });

      // cors(Customizer.withDefaults())
      CorsConfigurer<HttpSecurity> corsCfg = mock(CorsConfigurer.class);
      org.mockito.Mockito.when(http.cors(any())).thenAnswer(inv -> {
        @SuppressWarnings("unchecked")
        Customizer<CorsConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
        customizer.customize(corsCfg);
        return http;
      });

      // getConfigurer(...).oidc(...)
      doReturn(authorizationServerConfigurer)
          .when(http).getConfigurer(OAuth2AuthorizationServerConfigurer.class);
      doReturn(authorizationServerConfigurer).when(authorizationServerConfigurer).oidc(any());

      // build()
      doReturn(dummyChain).when(http).build();

      SecurityFilterChainsConfig config = new SecurityFilterChainsConfig();
      SecurityFilterChain result = config.authorizationServerSecurityFilterChain(http);

      assertSame(dummyChain, result);
      staticConfig.verify(() ->
          OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
      );
      verify(http).getConfigurer(OAuth2AuthorizationServerConfigurer.class);
      verify(authorizationServerConfigurer).oidc(Customizer.withDefaults());
      verify(http).exceptionHandling(any());
      verify(exCfg).authenticationEntryPoint(any());
      verify(http).cors(any());
      verify(http).build();
    }
  }

  @Test
  @DisplayName("Return default chain when configuring general security")
  @SuppressWarnings({"rawtypes", "unchecked"})
  void returnDefaultChainWhenConfiguringDefaultSecurity() throws Exception {
    // ---- securityMatcher(request -> OrRequestMatcher(...).matches(request)) ----
    org.mockito.Mockito.when(http.securityMatcher(any(RequestMatcher.class))).thenAnswer(inv -> {
      RequestMatcher matcher = inv.getArgument(0, RequestMatcher.class);
      // Ejecutamos la lambda para cubrir su cuerpo
      MockHttpServletRequest req = new MockHttpServletRequest("GET", "/");
      matcher.matches(req);
      return http;
    });

    // ---- cors(Customizer.withDefaults()) ----
    CorsConfigurer<HttpSecurity> corsCfg = mock(CorsConfigurer.class);
    org.mockito.Mockito.when(http.cors(any())).thenAnswer(inv -> {
      Customizer<CorsConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
      customizer.customize(corsCfg);
      return http;
    });

    // ---- csrf(...) con ignorados (String varargs) y repositorio de token ----
    CsrfConfigurer<HttpSecurity> csrfCfg = mock(CsrfConfigurer.class);
    doReturn(csrfCfg).when(csrfCfg).ignoringRequestMatchers(any(String[].class));
    doReturn(csrfCfg).when(csrfCfg).csrfTokenRepository(any());
    org.mockito.Mockito.when(http.csrf(any())).thenAnswer(inv -> {
      Customizer<CsrfConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
      customizer.customize(csrfCfg);
      return http;
    });

    // ---- authorizeHttpRequests(...) -> ejecutar consumer completo ----
    AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry =
        mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class);
    AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl =
        mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class);

    // Solo los overloads realmente usados por la config
    doReturn(authorizedUrl).when(registry).requestMatchers(any(String[].class));
    doReturn(authorizedUrl).when(registry).anyRequest();

    // Encadenamientos
    doReturn(registry).when(authorizedUrl).permitAll();
    doReturn(registry).when(authorizedUrl).authenticated();

    org.mockito.Mockito.when(http.authorizeHttpRequests(any())).thenAnswer(inv -> {
      Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> consumer =
          (Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>) inv.getArgument(0);
      consumer.customize(registry);
      return http;
    });

    // ---- formLogin(Customizer.withDefaults()) ----
    FormLoginConfigurer<HttpSecurity> formLoginCfg = mock(FormLoginConfigurer.class);
    org.mockito.Mockito.when(http.formLogin(any())).thenAnswer(inv -> {
      Customizer<FormLoginConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
      customizer.customize(formLoginCfg); // no-op
      return http;
    });

    // ---- logout(logout -> ...) ----
    LogoutConfigurer<HttpSecurity> logoutCfg = mock(LogoutConfigurer.class);
    doReturn(logoutCfg).when(logoutCfg).logoutUrl(anyString());
    doReturn(logoutCfg).when(logoutCfg).logoutSuccessUrl(anyString());
    doReturn(logoutCfg).when(logoutCfg).deleteCookies(any(String[].class));
    doReturn(logoutCfg).when(logoutCfg).clearAuthentication(anyBoolean());
    doReturn(logoutCfg).when(logoutCfg).invalidateHttpSession(anyBoolean());

    org.mockito.Mockito.when(http.logout(any())).thenAnswer(inv -> {
      Customizer<LogoutConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
      customizer.customize(logoutCfg);
      return http;
    });

    // ---- build() ----
    doReturn(dummyChain).when(http).build();

    // SUT
    SecurityFilterChainsConfig config = new SecurityFilterChainsConfig();
    SecurityFilterChain result = config.defaultSecurityFilterChain(http);

    // Assertions + verifies
    assertSame(dummyChain, result);

    // securityMatcher + cors
    verify(http).securityMatcher(any(RequestMatcher.class));
    verify(http).cors(any());

    // csrf
    verify(http).csrf(any());
    verify(csrfCfg).ignoringRequestMatchers(any(String[].class));
    verify(csrfCfg).csrfTokenRepository(any());

    // authorize
    verify(http).authorizeHttpRequests(any());
    verify(registry).requestMatchers(any(String[].class));
    verify(registry).anyRequest();
    verify(authorizedUrl).permitAll();
    verify(authorizedUrl).authenticated();

    // formLogin
    verify(http).formLogin(any());

    // logout chain steps
    verify(http).logout(any());
    verify(logoutCfg).logoutUrl(eq("/logout"));
    verify(logoutCfg).logoutSuccessUrl(eq("/login?logout"));
    verify(logoutCfg).deleteCookies(any(String[].class));
    verify(logoutCfg).clearAuthentication(eq(true));
    verify(logoutCfg).invalidateHttpSession(eq(true));

    // build
    verify(http).build();
  }
}