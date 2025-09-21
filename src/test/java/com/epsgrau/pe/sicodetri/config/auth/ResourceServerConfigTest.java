package com.epsgrau.pe.sicodetri.config.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Unit tests for {@link ResourceServerConfig}.
 * <p>
 * This suite covers two areas:
 * </p>
 * <ul>
 *   <li><b>JWT → GrantedAuthority mapping</b>:
 *     verifies that the custom
 *     {@link org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter}
 *     produced by {@code jwtAuthenticationConverter()} maps <i>only</i> the {@code roles}
 *     claim to Spring authorities (prefixed with {@code ROLE_}). It also validates the
 *     behavior when the JWT has no role-related claims.</li>
 *   <li><b>Security chain wiring</b>:
 *     verifies that {@code resourceServerSecurityFilterChain(HttpSecurity)}
 *     invokes the expected {@link org.springframework.security.config.annotation.web.builders.HttpSecurity}
 *     fluent configuration steps and returns the built {@link org.springframework.security.web.SecurityFilterChain}.</li>
 * </ul>
 *
 * <b>Notes:</b>
 * <ul>
 *   <li>Scope-based authorities are <b>not</b> mapped by the current converter; tests intentionally
 *       focus on role mapping from the {@code roles} claim.</li>
 * </ul>
 *
 * <b>Class</b>: {@link ResourceServerConfigTest}<br/>
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
 *       <li>Jul 29, 2025 (JAR) Initial version.</li>
 *     </ul>
 * @version 1.1
 */
@DisplayName("ResourceServerConfig Tests")
class ResourceServerConfigTest {

  private final ResourceServerConfig config = new ResourceServerConfig();

  @Test
  @DisplayName("Return only role authorities when JWT has only roles claim")
  void returnOnlyRoleAuthoritiesWhenJwtHasOnlyRolesClaim() {
    JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();

    Jwt jwt = new Jwt(
        "token-value",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        Map.of("alg", "none"),
        Map.of("roles", List.of("AssetUploader", "TaxTableViewer", "DepreciationProcessor", "ReportGenerator"))
    );

    List<String> authorities = converter.convert(jwt)
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    assertThat(authorities)
        .containsExactlyInAnyOrder("ROLE_AssetUploader", "ROLE_TaxTableViewer", "ROLE_DepreciationProcessor",
            "ROLE_ReportGenerator");
  }

  @Test
  @DisplayName("Return empty authorities when JWT has no roles or scope claims")
  void returnEmptyAuthoritiesWhenJwtHasNoRolesOrScopeClaims() {
    JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();

    Jwt jwt = new Jwt(
        "token-value",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        Map.of("alg", "none"),
        Map.of("sub", "test-user")
    );

    Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

    assertThat(authorities).isEmpty();
  }

  @Test
  @DisplayName("Return role authorities correctly when roles claim contains prefixed and unprefixed values")
  void returnRoleAuthoritiesWhenJwtRolesClaimContainsPrefixedAndUnprefixedValues() {
    JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();

    Jwt jwt = new Jwt(
        "token-value",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        Map.of("alg", "none"),
        Map.of("roles", List.of("AssetUploader", "TaxTableViewer"))
    );

    List<String> authorities = converter.convert(jwt)
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    assertThat(authorities)
        .containsExactlyInAnyOrder("ROLE_AssetUploader", "ROLE_TaxTableViewer");
  }

  @Test
  @DisplayName("Build SecurityFilterChain by invoking the fluent HttpSecurity API")
  void resourceServerSecurityFilterChainBuildsWhenConfiguringHttpSecurity() throws Exception {
    HttpSecurity http = mock(HttpSecurity.class);
    SecurityFilterChain expectedChain = mock(SecurityFilterChain.class);

    // sessionManagement(sm -> sm.sessionCreationPolicy(IF_REQUIRED))
    when(http.sessionManagement(any())).thenAnswer(inv -> {
      Customizer<SessionManagementConfigurer> customizer = inv.getArgument(0);
      SessionManagementConfigurer sm = mock(SessionManagementConfigurer.class);
      when(sm.sessionCreationPolicy(any())).thenReturn(sm);
      customizer.customize(sm);
      verify(sm).sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
      return http;
    });

    // csrf(AbstractHttpConfigurer::disable)  ==> Customizer<CsrfConfigurer<HttpSecurity>>
    when(http.csrf(any())).thenAnswer(inv -> {
      Customizer<CsrfConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
      CsrfConfigurer<HttpSecurity> csrf = mock(CsrfConfigurer.class);
      when(csrf.disable()).thenReturn(null); // retorno ignorado por el Customizer
      customizer.customize(csrf);            // aquí se invoca disable()
      return http;
    });

    // cors(Customizer.withDefaults())
    when(http.cors(any())).thenAnswer(inv -> {
      Customizer<CorsConfigurer> customizer = inv.getArgument(0);
      customizer.customize(mock(CorsConfigurer.class));
      return http;
    });

    when(http.securityMatcher("/api/**")).thenReturn(http);

    // authorizeHttpRequests(...)
    when(http.authorizeHttpRequests(any())).thenAnswer(inv -> {
      var consumer = (Customizer) inv.getArgument(0);

      AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry registry =
          mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class);

      AuthorizeHttpRequestsConfigurer.AuthorizedUrl authorizedUrl =
          mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class);

      // Stubs de encadenamiento
      when(registry.requestMatchers(any(String.class))).thenReturn(authorizedUrl);
      when(registry.requestMatchers(any(String[].class))).thenReturn(authorizedUrl);
      when(registry.requestMatchers(any(HttpMethod.class), anyString())).thenReturn(authorizedUrl);
      when(registry.anyRequest()).thenReturn(authorizedUrl);

      when(authorizedUrl.hasRole(anyString())).thenReturn(registry);
      when(authorizedUrl.authenticated()).thenReturn(registry);

      // Ejecutamos el lambda de la configuración
      consumer.customize(registry);

      // Verificaciones finas de reglas (rutas, métodos y roles)
      verify(registry).requestMatchers("/api/v1/tax-tables/**");
      verify(authorizedUrl).hasRole("TaxTableViewer");

      verify(registry).requestMatchers(eq(HttpMethod.POST), eq("/api/v1/financial-fixed-assets/upload"));
      verify(registry).requestMatchers(eq(HttpMethod.POST), eq("/api/v1/financial-fixed-assets"));
      verify(authorizedUrl, times(2)).hasRole("AssetUploader");

      verify(registry).requestMatchers(eq(HttpMethod.POST), eq("/api/v1/financial-fixed-assets/projection"));
      verify(authorizedUrl).hasRole("DepreciationProcessor");

      verify(registry).requestMatchers(eq(HttpMethod.GET), eq("/api/v1/financial-fixed-assets/report"));
      verify(authorizedUrl).hasRole("ReportGenerator");

      verify(registry).requestMatchers("/api/v1/financial-fixed-assets/**");

      // authenticated() se llama para la ruta específica y para anyRequest()
      verify(registry).anyRequest();
      verify(authorizedUrl, times(2)).authenticated();

      return http;
    });

    // exceptionHandling(ex -> ex.authenticationEntryPoint(...).accessDeniedHandler(...))
    when(http.exceptionHandling(any())).thenAnswer(inv -> {
      Customizer<ExceptionHandlingConfigurer> customizer = inv.getArgument(0);
      ExceptionHandlingConfigurer exc = mock(ExceptionHandlingConfigurer.class);
      when(exc.authenticationEntryPoint(any())).thenReturn(exc);
      when(exc.accessDeniedHandler(any())).thenReturn(exc);
      customizer.customize(exc);
      verify(exc).authenticationEntryPoint(any());
      verify(exc).accessDeniedHandler(any());
      return http;
    });

    // oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(...)))
    when(http.oauth2ResourceServer(any())).thenAnswer(inv -> {
      Customizer<OAuth2ResourceServerConfigurer> customizer = inv.getArgument(0);

      OAuth2ResourceServerConfigurer o2 = mock(OAuth2ResourceServerConfigurer.class);
      OAuth2ResourceServerConfigurer.JwtConfigurer jwtConfigurer =
          mock(OAuth2ResourceServerConfigurer.JwtConfigurer.class);

      when(jwtConfigurer.jwtAuthenticationConverter(any())).thenReturn(jwtConfigurer);

      when(o2.jwt(any())).thenAnswer(inv2 -> {
        Customizer<OAuth2ResourceServerConfigurer.JwtConfigurer> jwtCustomizer = inv2.getArgument(0);
        jwtCustomizer.customize(jwtConfigurer);
        return o2;
      });

      customizer.customize(o2);

      // Asegura que se estableció el converter de JWT
      verify(jwtConfigurer).jwtAuthenticationConverter(any());
      return http;
    });

    doReturn(expectedChain).when(http).build();

    SecurityFilterChain actual = config.resourceServerSecurityFilterChain(http);

    assertThat(actual).isSameAs(expectedChain);

    verify(http).securityMatcher("/api/**");
    verify(http).sessionManagement(any());
    verify(http).csrf(any());
    verify(http).cors(any());
    verify(http).authorizeHttpRequests(any());
    verify(http).exceptionHandling(any());
    verify(http).oauth2ResourceServer(any());
    verify(http).build();
  }
}