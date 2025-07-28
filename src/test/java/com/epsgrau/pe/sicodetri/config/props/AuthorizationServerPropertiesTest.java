package com.epsgrau.pe.sicodetri.config.props;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link AuthorizationServerProperties}.
 *
 * <p>
 * Verifies that:
 * <ul>
 *   <li>Default values are applied when constructed with all sections null, including:
 *     <ul>
 *       <li>Empty issuer in {@link AuthorizationServerProperties.Provider}</li>
 *       <li>Key size 2048 in {@link AuthorizationServerProperties.Jwk}</li>
 *       <li>Null id/secret, empty {@code redirectUris} and {@code scopes}, and consent required in {@link AuthorizationServerProperties.Client}</li>
 *       <li>Access token TTL 15 minutes, refresh token TTL 30 days, and no reuse in {@link AuthorizationServerProperties.Token}</li>
 *     </ul>
 *   </li>
 *   <li>Provided non-null values are preserved correctly for provider, jwk, client, and token sections.</li>
 *   <li>Lists {@code redirectUris} and {@code scopes} are defensively copied and remain immutable.</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link AuthorizationServerPropertiesTest}<br/>
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
 @DisplayName("AuthorizationServerProperties Tests")
class AuthorizationServerPropertiesTest {

  @Test
  @DisplayName("Use default values when constructing properties with all sections null")
  void useDefaultValuesWhenConstructingPropertiesWithAllSectionsNull() {
    // Act
    AuthorizationServerProperties props =
        new AuthorizationServerProperties(null, null, null, null);

    // Assert provider defaults
    assertEquals("", props.provider().issuer(), "Provider.issuer should default to empty");

    // Assert jwk defaults
    assertEquals(2048, props.jwk().keySize(), "Jwk.keySize should default to 2048");

    // Assert client defaults
    AuthorizationServerProperties.Client client = props.client();
    assertNull(client.id(), "Client.id should default to null");
    assertNull(client.secret(), "Client.secret should default to null");
    assertTrue(client.redirectUris().isEmpty(), "Client.redirectUris should default to empty list");
    assertTrue(client.scopes().isEmpty(), "Client.scopes should default to empty list");
    assertTrue(client.requireAuthorizationConsent(),
        "Client.requireAuthorizationConsent should default to true");

    // Assert token defaults
    AuthorizationServerProperties.Token token = props.token();
    assertEquals(Duration.ofMinutes(15),
        token.accessTokenTimeToLive(), "Token.accessTokenTimeToLive should default to 15 minutes");
    assertEquals(Duration.ofDays(30),
        token.refreshTokenTimeToLive(), "Token.refreshTokenTimeToLive should default to 30 days");
    assertFalse(token.reuseRefreshTokens(),
        "Token.reuseRefreshTokens should default to false");
  }

  @Test
  @DisplayName("Preserve provided values when constructing properties with non-null sections")
  void preserveProvidedValuesWhenConstructingPropertiesWithNonNullSections() {
    // Arrange custom values
    AuthorizationServerProperties.Provider provider =
        new AuthorizationServerProperties.Provider("https://issuer.example");
    AuthorizationServerProperties.Jwk jwk =
        new AuthorizationServerProperties.Jwk(4096);
    List<String> uris = List.of("https://app/callback");
    List<String> scopes = List.of("read", "write");
    AuthorizationServerProperties.Client client =
        new AuthorizationServerProperties.Client("clientId", "secret", uris, scopes, false);
    Duration atTtl = Duration.ofHours(1);
    Duration rtTtl = Duration.ofDays(7);
    AuthorizationServerProperties.Token token =
        new AuthorizationServerProperties.Token(atTtl, rtTtl, true);

    // Act
    AuthorizationServerProperties props =
        new AuthorizationServerProperties(provider, jwk, client, token);

    // Assert that values are preserved
    assertEquals("https://issuer.example", props.provider().issuer());
    assertEquals(4096, props.jwk().keySize());

    AuthorizationServerProperties.Client resultClient = props.client();
    assertEquals("clientId", resultClient.id());
    assertEquals("secret", resultClient.secret());
    assertEquals(uris, resultClient.redirectUris());
    assertEquals(scopes, resultClient.scopes());
    assertFalse(resultClient.requireAuthorizationConsent());

    AuthorizationServerProperties.Token resultToken = props.token();
    assertEquals(atTtl, resultToken.accessTokenTimeToLive());
    assertEquals(rtTtl, resultToken.refreshTokenTimeToLive());
    assertTrue(resultToken.reuseRefreshTokens());
  }

  @Test
  @DisplayName("Copy redirectUris and scopes when constructing client to ensure immutability")
  void copyRedirectUrisAndScopesWhenConstructingClientToEnsureImmutability() {
    // Arrange mutable lists
    List<String> originalUris = new ArrayList<>();
    originalUris.add("u1");
    List<String> originalScopes = new ArrayList<>();
    originalScopes.add("s1");

    // Act
    AuthorizationServerProperties.Client client =
        new AuthorizationServerProperties.Client("id", "sec", originalUris, originalScopes, true);

    // Mutate originals
    originalUris.add("u2");
    originalScopes.add("s2");

    // Assert that client lists are unaffected
    List<String> clientUris = client.redirectUris();
    List<String> clientScopes = client.scopes();
    assertEquals(List.of("u1"), clientUris,
        "Client.redirectUris should not reflect mutations to original list");
    assertEquals(List.of("s1"), clientScopes,
        "Client.scopes should not reflect mutations to original list");

    // Assert that returned lists are unmodifiable
    assertThrows(UnsupportedOperationException.class,
        () -> clientUris.add("x"),
        "redirectUris list should be unmodifiable");
    assertThrows(UnsupportedOperationException.class,
        () -> clientScopes.add("y"),
        "scopes list should be unmodifiable");
  }
}