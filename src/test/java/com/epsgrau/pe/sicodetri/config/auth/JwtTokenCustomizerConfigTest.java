package com.epsgrau.pe.sicodetri.config.auth;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.config.props.AuthorizationServerProperties;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

/**
 * Unit tests for {@link JwtTokenCustomizerConfig}.
 *
 * <p><b>Objectives</b>:</p>
 * <ul>
 *   <li>Verify that the {@code roles} claim is added only for
 *       {@link OAuth2TokenType#ACCESS_TOKEN}.</li>
 *   <li>Ensure {@code roles} are derived from the authenticated principal and the
 *       {@code ROLE_} prefix is removed when present.</li>
 *   <li>Validate the {@code client_credentials} path where roles come from
 *       {@code props.perfClient().nonEmptyRoles()}.</li>
 *   <li>Assert defensive behavior when {@code principal.getAuthorities()} is {@code null}
 *       (no claims are added).</li>
 * </ul>
 *
 * <p><b>Notes</b>:</p>
 * <ul>
 *   <li>Deep stubs are used to mock nested property access on {@link AuthorizationServerProperties}.</li>
 *   <li>{@code doReturn(...).when(...)} is preferred for collections to avoid
 *       Mockito's {@code PotentialStubbingProblem}.</li>
 * </ul>
 *
 * <b>Class</b>: {@link JwtTokenCustomizerConfigTest}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <br/><u>Service Provider</u>: Consultor TI
 *     <br/><u>Developed by</u>:
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *       <li>Jul 29, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 * @since 1.0
 * @see JwtTokenCustomizerConfig
 */
@DisplayName("JwtTokenCustomizerConfig Tests")
class JwtTokenCustomizerConfigTest {

  private OAuth2TokenCustomizer<JwtEncodingContext> newCustomizerWithProps(AuthorizationServerProperties props) {
    return new JwtTokenCustomizerConfig().tokenCustomizer(props);
  }

  @Test
  @DisplayName("Add roles claim to JWT encoding context when token type is ACCESS_TOKEN")
  void addRolesClaimToJwtEncodingContextWhenTokenTypeIsAccessToken() {
    // Arrange
    AuthorizationServerProperties props = mock(AuthorizationServerProperties.class, RETURNS_DEEP_STUBS);
    OAuth2TokenCustomizer<JwtEncodingContext> customizer = newCustomizerWithProps(props);

    JwtEncodingContext context = mock(JwtEncodingContext.class);
    JwtClaimsSet.Builder claimsBuilder = mock(JwtClaimsSet.Builder.class);
    Authentication principal = mock(Authentication.class);

    when(context.getTokenType()).thenReturn(OAuth2TokenType.ACCESS_TOKEN);
    when(context.getPrincipal()).thenReturn(principal);

    // <-- doReturn() evita PotentialStubbingProblem con colecciones
    doReturn(List.of(
        new SimpleGrantedAuthority("ROLE_ADMIN"),
        new SimpleGrantedAuthority("USER")
    )).when(principal).getAuthorities();

    when(context.getClaims()).thenReturn(claimsBuilder);

    // Act
    customizer.customize(context);

    // Assert
    verify(claimsBuilder).claim("roles", List.of("ADMIN", "USER"));
    verifyNoMoreInteractions(claimsBuilder);
  }

  @Test
  @DisplayName("Do not add roles claim to JWT encoding context when token type is not ACCESS_TOKEN")
  void doNotAddRolesClaimToJwtEncodingContextWhenTokenTypeIsNotAccessToken() {
    // Arrange
    AuthorizationServerProperties props = mock(AuthorizationServerProperties.class, RETURNS_DEEP_STUBS);
    OAuth2TokenCustomizer<JwtEncodingContext> customizer = newCustomizerWithProps(props);

    JwtEncodingContext context = mock(JwtEncodingContext.class);
    JwtClaimsSet.Builder claimsBuilder = mock(JwtClaimsSet.Builder.class);

    when(context.getTokenType()).thenReturn(OAuth2TokenType.REFRESH_TOKEN);
    when(context.getClaims()).thenReturn(claimsBuilder);

    // Act
    customizer.customize(context);

    // Assert
    verifyNoInteractions(claimsBuilder);
  }

  @Test
  @DisplayName("Add roles claim using perf roles when grant type is CLIENT_CREDENTIALS")
  void addRolesClaimUsingPerfRolesWhenGrantTypeIsClientCredentials() {
    // Arrange
    AuthorizationServerProperties props = mock(AuthorizationServerProperties.class, RETURNS_DEEP_STUBS);
    OAuth2TokenCustomizer<JwtEncodingContext> customizer = newCustomizerWithProps(props);

    JwtEncodingContext context = mock(JwtEncodingContext.class);
    JwtClaimsSet.Builder claimsBuilder = mock(JwtClaimsSet.Builder.class);

    when(context.getTokenType()).thenReturn(OAuth2TokenType.ACCESS_TOKEN);
    when(context.getAuthorizationGrantType()).thenReturn(AuthorizationGrantType.CLIENT_CREDENTIALS);

    // roles del perf client (provienen de props.perfClient().nonEmptyRoles())
    when(props.perfClient().nonEmptyRoles()).thenReturn(List.of("perf.read", "perf.write"));
    when(context.getClaims()).thenReturn(claimsBuilder);

    // Act
    customizer.customize(context);

    // Assert
    verify(claimsBuilder).claim("roles", List.of("perf.read", "perf.write"));
    verifyNoMoreInteractions(claimsBuilder);
  }

  @Test
  @DisplayName("Do not add roles when principal exists but authorities is null")
  void doNotAddRolesWhenPrincipalAuthoritiesIsNull() {
    // Arrange
    AuthorizationServerProperties props = mock(AuthorizationServerProperties.class, RETURNS_DEEP_STUBS);
    OAuth2TokenCustomizer<JwtEncodingContext> customizer = newCustomizerWithProps(props);

    JwtEncodingContext context = mock(JwtEncodingContext.class);
    Authentication principal = mock(Authentication.class);

    when(context.getTokenType()).thenReturn(OAuth2TokenType.ACCESS_TOKEN);
    when(context.getAuthorizationGrantType()).thenReturn(AuthorizationGrantType.AUTHORIZATION_CODE);
    when(context.getPrincipal()).thenReturn(principal);
    // authorities = null
    when(principal.getAuthorities()).thenReturn(null);

    // Act
    customizer.customize(context);

    // Assert: nunca intenta agregar claims si authorities es null
    verify(context, org.mockito.Mockito.never()).getClaims();
  }
}