package com.epsgrau.pe.sicodetri.config.auth;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

/**
 * Unit tests for {@link JwtTokenCustomizerConfig}.
 *
 * <p>
 * Verifies that:
 * <ul>
 *   <li>{@code tokenCustomizer()} adds a "roles" claim with the principal’s authority names
 *       when the token type is {@link org.springframework.security.oauth2.server.authorization.OAuth2TokenType#ACCESS_TOKEN}.</li>
 *   <li>No claims are added when the token type is not ACCESS_TOKEN (e.g., REFRESH_TOKEN).</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link JwtTokenCustomizerConfigTest}<br/>
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
@DisplayName("JwtTokenCustomizerConfig Tests")
class JwtTokenCustomizerConfigTest {

  private final JwtTokenCustomizerConfig config = new JwtTokenCustomizerConfig();
  private final OAuth2TokenCustomizer<JwtEncodingContext> customizer = config.tokenCustomizer();

  @Test
  @DisplayName("Add roles claim to JWT encoding context when token type is ACCESS_TOKEN")
  void addRolesClaimToJwtEncodingContextWhenTokenTypeIsAccessToken() {
    // Arrange
    JwtEncodingContext context = mock(JwtEncodingContext.class);
    JwtClaimsSet.Builder claimsBuilder = mock(JwtClaimsSet.Builder.class);
    Authentication principal = mock(Authentication.class);

    when(context.getTokenType()).thenReturn(OAuth2TokenType.ACCESS_TOKEN);
    when(context.getPrincipal()).thenReturn(principal);

    // <-- Aquí usamos doReturn() en lugar de thenReturn()
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
    JwtEncodingContext context = mock(JwtEncodingContext.class);
    JwtClaimsSet.Builder claimsBuilder = mock(JwtClaimsSet.Builder.class);

    when(context.getTokenType()).thenReturn(OAuth2TokenType.REFRESH_TOKEN);
    when(context.getClaims()).thenReturn(claimsBuilder);

    // Act
    customizer.customize(context);

    // Assert
    verifyNoInteractions(claimsBuilder);
  }
}