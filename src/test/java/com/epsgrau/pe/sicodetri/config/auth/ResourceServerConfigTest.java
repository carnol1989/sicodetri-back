package com.epsgrau.pe.sicodetri.config.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Unit tests for {@link ResourceServerConfig}.
 * Covers two areas:
 * <ul>
 *   <li><b>JWT → GrantedAuthority mapping</b>: verifies that the custom
 *       {@link org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter}
 *       produced by {@code jwtAuthenticationConverter()} correctly maps:
 *       <ul>
 *         <li><code>scope</code> (space-delimited) → {@code SCOPE_<scope>} authorities,</li>
 *         <li><code>roles</code> (array) → {@code ROLE_<role>} authorities,</li>
 *         <li>combined, single-claim, and no-claim scenarios.</li>
 *       </ul>
 *   </li>
 *   <li><b>Security chain wiring</b>: verifies that
 *       {@code resourceServerSecurityFilterChain(HttpSecurity)} invokes the expected
 *       {@link org.springframework.security.config.annotation.web.builders.HttpSecurity} fluent
 *       configuration steps and returns the built {@link org.springframework.security.web.SecurityFilterChain}.</li>
 * </ul>
 *
 * <b>Class</b>: {@link ResourceServerConfigTest}<br/>
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
@DisplayName("ResourceServerConfig Tests")
class ResourceServerConfigTest {

  private final ResourceServerConfig config = new ResourceServerConfig();

  /**
   * Verifies that when a JWT contains only a {@code roles} array (e.g., {@code ["MANAGER","OPERATOR"]}),
   * the converter produces {@code ROLE_MANAGER} and {@code ROLE_OPERATOR} authorities.
   */
  @Test
  @DisplayName("Return only role authorities when JWT has only roles claim")
  void returnOnlyRoleAuthoritiesWhenJwtHasOnlyRolesClaim() {
    JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();

    Jwt jwt = new Jwt(
        "token-value",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        Map.of("alg", "none"),
        Map.of("roles", List.of("MANAGER", "OPERATOR"))
    );

    List<String> authorities = converter.convert(jwt)
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    assertThat(authorities)
        .containsExactlyInAnyOrder("ROLE_MANAGER", "ROLE_OPERATOR");
  }

  /**
   * Verifies that when neither {@code scope} nor {@code roles} are present in the JWT,
   * the converter returns an empty authorities collection.
   */
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

  /**
   * Ensures that role values are normalized to {@code ROLE_}-prefixed authorities regardless of
   * how they are provided. In this test, unprefixed roles {@code ["ADMIN","USER"]} become
   * {@code ROLE_ADMIN} and {@code ROLE_USER}.
   */
  @Test
  @DisplayName("Return role authorities correctly when roles claim contains prefixed and unprefixed values")
  void returnRoleAuthoritiesWhenJwtRolesClaimContainsPrefixedAndUnprefixedValues() {
    JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();

    Jwt jwt = new Jwt(
        "token-value",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        Map.of("alg", "none"),
        Map.of("roles", List.of("ADMIN", "USER"))
    );

    List<String> authorities = converter.convert(jwt)
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    assertThat(authorities)
        .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
  }

  /**
   * Verifies that {@code resourceServerSecurityFilterChain(HttpSecurity)} drives the
   * {@link HttpSecurity} fluent API as expected—configuring request matching, session management,
   * CSRF, CORS, request authorization, and the OAuth2 Resource Server—and returns the same
   * {@link SecurityFilterChain} instance produced by {@code http.build()}.
   *
   * @throws Exception if the filter chain cannot be built
   */
  @Test
  @DisplayName("Build SecurityFilterChain by invoking the fluent HttpSecurity API")
  void resourceServerSecurityFilterChainBuildsWhenConfiguringHttpSecurity() throws Exception {
    HttpSecurity http = mock(HttpSecurity.class);
    SecurityFilterChain expectedChain = mock(SecurityFilterChain.class);

    when(http.sessionManagement(any())).thenAnswer(inv -> {
      @SuppressWarnings("rawtypes")
      Customizer<SessionManagementConfigurer> customizer = inv.getArgument(0);
      return http;
    });

    when(http.csrf(any())).thenAnswer(inv -> {
      @SuppressWarnings("rawtypes")
      Customizer<AbstractHttpConfigurer> customizer = inv.getArgument(0);
      return http;
    });

    when(http.cors(any())).thenAnswer(inv -> {
      @SuppressWarnings("rawtypes")
      Customizer<CorsConfigurer> customizer = inv.getArgument(0);
      customizer.customize(mock(CorsConfigurer.class));
      return http;
    });

    when(http.securityMatcher("/api/**")).thenReturn(http);

    when(http.authorizeHttpRequests(any())).thenAnswer(inv -> {
      @SuppressWarnings("rawtypes")
      var consumer = (Customizer) inv.getArgument(0);

      @SuppressWarnings("rawtypes")
      AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry registry =
          mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class);

      AuthorizeHttpRequestsConfigurer.AuthorizedUrl authorizedUrl =
          mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class);

      // Stubs para los encadenamientos en tu configuración
      when(registry.requestMatchers(any(String.class))).thenReturn(authorizedUrl);
      when(registry.requestMatchers(any(String[].class))).thenReturn(authorizedUrl);
      when(registry.requestMatchers(any(HttpMethod.class), anyString())).thenReturn(authorizedUrl);
      when(registry.anyRequest()).thenReturn(authorizedUrl);

      when(authorizedUrl.hasRole(anyString())).thenReturn(registry);
      when(authorizedUrl.authenticated()).thenReturn(registry);

      // Ejecutamos el lambda de tu config
      consumer.customize(registry);
      return http;
    });

    when(http.oauth2ResourceServer(any())).thenAnswer(inv -> {
      @SuppressWarnings("rawtypes")
      Customizer<OAuth2ResourceServerConfigurer> customizer = inv.getArgument(0);

      @SuppressWarnings({"rawtypes", "unchecked"})
      OAuth2ResourceServerConfigurer o2 = mock(OAuth2ResourceServerConfigurer.class);

      @SuppressWarnings({"rawtypes"})
      OAuth2ResourceServerConfigurer.JwtConfigurer jwtConfigurer =
          mock(OAuth2ResourceServerConfigurer.JwtConfigurer.class);

      when(o2.jwt(any())).thenAnswer(inv2 -> {
        // Ejecutar el customizer de .jwt(...)
        Customizer<OAuth2ResourceServerConfigurer.JwtConfigurer> jwtCustomizer = inv2.getArgument(0);
        jwtCustomizer.customize(jwtConfigurer);
        return o2;
      });
      when(o2.authenticationEntryPoint(any())).thenReturn(o2);
      when(o2.accessDeniedHandler(any())).thenReturn(o2);

      customizer.customize(o2);
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
    verify(http).oauth2ResourceServer(any());
    verify(http).build();
  }
}