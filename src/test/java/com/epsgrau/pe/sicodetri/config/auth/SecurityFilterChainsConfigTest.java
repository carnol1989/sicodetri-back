package com.epsgrau.pe.sicodetri.config.auth;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Unit tests for {@link SecurityFilterChainsConfig}.
 *
 * <p>
 * Verifies that:
 * <ul>
 *   <li>{@code authorizationServerSecurityFilterChain(HttpSecurity)} applies the default
 *       OAuth2 Authorization Server security via
 *       {@link OAuth2AuthorizationServerConfiguration#applyDefaultSecurity(HttpSecurity)},
 *       configures OIDC support, exception handling, and returns the built
 *       {@link org.springframework.security.web.SecurityFilterChain}.</li>
 *   <li>{@code defaultSecurityFilterChain(HttpSecurity)} configures CSRF protection,
 *       request authorization, and form login, then returns the built
 *       {@link org.springframework.security.web.SecurityFilterChain}.</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link SecurityFilterChainsConfigTest}<br/>
 * <b>Copyright</b>: &Copy; 2024 EPS Grau. <br/>
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
 *     <li>Jul 29, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
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

  /**
   * Verifies the <em>authorization server</em> chain configuration.
   *
   * <p><b>Asserts</b> that the configuration method:</p>
   * <ul>
   *   <li>Invokes {@link OAuth2AuthorizationServerConfiguration#applyDefaultSecurity(HttpSecurity)}.</li>
   *   <li>Enables OIDC via {@link OAuth2AuthorizationServerConfigurer#oidc(Customizer)}.</li>
   *   <li>Customizes exception handling to set an authentication entry point
   *       (e.g., a login URL entry point).</li>
   *   <li>Enables CORS with {@link Customizer#withDefaults()}.</li>
   *   <li>Returns the exact {@link SecurityFilterChain} produced by {@code http.build()}.</li>
   * </ul>
   *
   * @throws Exception if the filter chain cannot be built
   */
  @Test
  @DisplayName("Return authZ-server chain when configuring OAuth2 authorization server")
  void returnAuthServerChainWhenConfiguringAuthorizationServer() throws Exception {
    try (MockedStatic<OAuth2AuthorizationServerConfiguration> staticConfig =
             mockStatic(OAuth2AuthorizationServerConfiguration.class)) {

      // exceptionHandling(...) -> ejecutar customizer y devolver el tipo correcto
      ExceptionHandlingConfigurer<HttpSecurity> exCfg = mock(ExceptionHandlingConfigurer.class);
      doReturn(exCfg).when(exCfg).authenticationEntryPoint(any());
      org.mockito.Mockito.when(http.exceptionHandling(any())).thenAnswer(inv -> {
        Customizer<ExceptionHandlingConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
        customizer.customize(exCfg);
        return http;
      });

      // cors(Customizer.withDefaults())
      CorsConfigurer<HttpSecurity> corsCfg = mock(CorsConfigurer.class);
      org.mockito.Mockito.when(http.cors(any())).thenAnswer(inv -> {
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

  /**
   * Verifies the <em>default</em> (catch-all) web chain configuration.
   *
   * <p><b>Asserts</b> that the configuration method:</p>
   * <ul>
   *   <li>Enables CORS with {@link Customizer#withDefaults()}.</li>
   *   <li>Configures CSRF to ignore specific request matchers and sets a CSRF token repository.</li>
   *   <li>Configures authorization rules by permitting selected paths and requiring
   *       authentication for any other request.</li>
   *   <li>Enables form login with defaults.</li>
   *   <li>Enables OAuth2 Resource Server (JWT) with defaults.</li>
   *   <li>Configures logout (URL, success URL, cookie deletion, clear authentication,
   *       session invalidation).</li>
   *   <li>Returns the exact {@link SecurityFilterChain} produced by {@code http.build()}.</li>
   * </ul>
   *
   * @throws Exception if the filter chain cannot be built
   */
  @Test
  @DisplayName("Return default chain when configuring general security")
  void returnDefaultChainWhenConfiguringDefaultSecurity() throws Exception {
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

    // ---- oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults())) ----
    OAuth2ResourceServerConfigurer<HttpSecurity> o2 = mock(OAuth2ResourceServerConfigurer.class);
    OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer jwtCfg =
        mock(OAuth2ResourceServerConfigurer.JwtConfigurer.class);

    org.mockito.Mockito.when(http.oauth2ResourceServer(any())).thenAnswer(inv -> {
      Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
      org.mockito.Mockito.when(o2.jwt(any())).thenAnswer(inv2 -> {
        Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer> jwtCustomizer = inv2.getArgument(0);
        jwtCustomizer.customize(jwtCfg);
        return o2;
      });
      customizer.customize(o2);
      return http;
    });

    // ---- logout(logout -> ...) ----
    LogoutConfigurer<HttpSecurity> logoutCfg = mock(LogoutConfigurer.class);
    doReturn(logoutCfg).when(logoutCfg).logoutUrl(anyString());
    doReturn(logoutCfg).when(logoutCfg).logoutSuccessUrl(anyString());
    doReturn(logoutCfg).when(logoutCfg).deleteCookies(any(String[].class)); // varargs String...
    doReturn(logoutCfg).when(logoutCfg).clearAuthentication(anyBoolean());   // boolean primitive
    doReturn(logoutCfg).when(logoutCfg).invalidateHttpSession(anyBoolean()); // boolean primitive

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

    // cors
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

    // oauth2 resource server + jwt
    verify(http).oauth2ResourceServer(any());
    verify(o2).jwt(any());

    // logout chain steps
    verify(http).logout(any());
    verify(logoutCfg).logoutUrl(anyString());
    verify(logoutCfg).logoutSuccessUrl(anyString());
    verify(logoutCfg).deleteCookies(any(String[].class));
    verify(logoutCfg).clearAuthentication(anyBoolean());
    verify(logoutCfg).invalidateHttpSession(anyBoolean());

    // build
    verify(http).build();
  }
}