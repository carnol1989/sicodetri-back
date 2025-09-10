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
 * <p><b>Objectives</b>:</p>
 * <ul>
 *   <li>Validate that the canonical constructor applies sane defaults when any section is {@code null}
 *       (provider, jwk, client, perfClient, token).</li>
 *   <li>Verify that provided values are preserved verbatim when sections are non-null.</li>
 *   <li>Assert immutability via defensive copies for {@code redirectUris}, {@code scopes} and {@code roles}
 *       (lists are unmodifiable and do not reflect external mutations).</li>
 *   <li>Check normalization of {@code null} lists to empty lists.</li>
 *   <li>Confirm the semantics of {@link AuthorizationServerProperties.Client#empty()} and
 *       {@link AuthorizationServerProperties.Client#nonEmptyRoles()}.</li>
 *   <li>Ensure default configuration for the dedicated {@code perfClient} is applied when not provided.</li>
 * </ul>
 *
 * <p><b>Notes</b>:</p>
 * <ul>
 *   <li>These tests focus on data integrity, immutability, and constructor-level business rules.</li>
 *   <li>Thrown {@link UnsupportedOperationException} is used to prove returned lists are unmodifiable.</li>
 * </ul>
 *
 * <b>Class</b>: {@link AuthorizationServerPropertiesTest}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau.<br/>
 * <b>Company</b>: EPS Grau.<br/>
 *
 * @author EPS Grau
 *     <br/><u>Service Provider</u>: Consultor TI
 *     <br/><u>Developed by</u>:
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *       <li>Jul 17, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 * @since 1.0
 * @see AuthorizationServerProperties
 * @see AuthorizationServerProperties.Client
 */
 @DisplayName("AuthorizationServerProperties Tests")
class AuthorizationServerPropertiesTest {

  @Test
  @DisplayName("Use default values when constructing properties with all sections null")
  void useDefaultValuesWhenConstructingPropertiesWithAllSectionsNull() {
    // Act
    AuthorizationServerProperties props =
        new AuthorizationServerProperties(null, null, null, null, null);

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
    assertTrue(client.roles().isEmpty(), "Client.roles should default to empty list");
    assertTrue(client.requireAuthorizationConsent(),
        "Client.requireAuthorizationConsent should default to true");

    // Assert perfClient defaults
    AuthorizationServerProperties.Client perf = props.perfClient();
    assertEquals("perf-client", perf.id(), "perfClient.id should default to 'perf-client'");
    assertEquals("perf-secret", perf.secret(), "perfClient.secret should default to 'perf-secret'");
    assertTrue(perf.redirectUris().isEmpty(), "perfClient.redirectUris should default to empty list");
    assertTrue(perf.scopes().isEmpty(), "perfClient.scopes should default to empty list");
    assertTrue(perf.roles().isEmpty(), "perfClient.roles should default to empty list");
    assertFalse(perf.requireAuthorizationConsent(),
        "perfClient.requireAuthorizationConsent should default to false");

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
    List<String> roles = List.of("AssetUploader", "DepreciationProcessor");
    AuthorizationServerProperties.Client client =
        new AuthorizationServerProperties.Client("clientId", "secret", uris, scopes, roles, false);
    Duration atTtl = Duration.ofHours(1);
    Duration rtTtl = Duration.ofDays(7);
    AuthorizationServerProperties.Token token =
        new AuthorizationServerProperties.Token(atTtl, rtTtl, true);

    // Act
    AuthorizationServerProperties props =
        new AuthorizationServerProperties(provider, jwk, client, null, token);

    // Assert that values are preserved
    assertEquals("https://issuer.example", props.provider().issuer());
    assertEquals(4096, props.jwk().keySize());

    AuthorizationServerProperties.Client resultClient = props.client();
    assertEquals("clientId", resultClient.id());
    assertEquals("secret", resultClient.secret());
    assertEquals(uris, resultClient.redirectUris());
    assertEquals(scopes, resultClient.scopes());
    assertEquals(roles, resultClient.roles());
    assertFalse(resultClient.requireAuthorizationConsent());

    AuthorizationServerProperties.Token resultToken = props.token();
    assertEquals(atTtl, resultToken.accessTokenTimeToLive());
    assertEquals(rtTtl, resultToken.refreshTokenTimeToLive());
    assertTrue(resultToken.reuseRefreshTokens());

    // Also assert perfClient defaults when provided as null in constructor
    AuthorizationServerProperties.Client perf = props.perfClient();
    assertEquals("perf-client", perf.id(), "perfClient.id should default to 'perf-client'");
    assertEquals("perf-secret", perf.secret(), "perfClient.secret should default to 'perf-secret'");
    assertTrue(perf.redirectUris().isEmpty(), "perfClient.redirectUris should default to empty list");
    assertTrue(perf.scopes().isEmpty(), "perfClient.scopes should default to empty list");
    assertTrue(perf.roles().isEmpty(), "perfClient.roles should default to empty list");
    assertFalse(perf.requireAuthorizationConsent(),
        "perfClient.requireAuthorizationConsent should default to false");
  }

  @Test
  @DisplayName("Copy redirectUris and scopes when constructing client to ensure immutability")
  void copyRedirectUrisAndScopesWhenConstructingClientToEnsureImmutability() {
    // Arrange mutable lists
    List<String> originalUris = new ArrayList<>();
    originalUris.add("u1");
    List<String> originalScopes = new ArrayList<>();
    originalScopes.add("s1");
    List<String> roles = List.of("AssetUploader", "DepreciationProcessor");

    // Act
    AuthorizationServerProperties.Client client =
        new AuthorizationServerProperties.Client("id", "sec", originalUris, originalScopes, roles, true);

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

  @Test
  @DisplayName("Copy roles when constructing client to ensure immutability")
  void copyRolesWhenConstructingClientToEnsureImmutability() {
    // Arrange mutable roles
    List<String> originalRoles = new ArrayList<>();
    originalRoles.add("R1");

    // Act
    AuthorizationServerProperties.Client client =
        new AuthorizationServerProperties.Client("id", "sec", List.of("cb"), List.of("sc"), originalRoles, false);

    // Mutate original
    originalRoles.add("R2");

    // Assert that client roles are unaffected and unmodifiable
    List<String> clientRoles = client.roles();
    assertEquals(List.of("R1"), clientRoles,
        "Client.roles should not reflect mutations to original list");
    assertThrows(UnsupportedOperationException.class,
        () -> clientRoles.add("Z"),
        "roles list should be unmodifiable");

    // Also validate nonEmptyRoles() mirrors roles()
    assertEquals(clientRoles, client.nonEmptyRoles(),
        "nonEmptyRoles should return the same (unmodifiable) roles list");
  }

  @Test
  @DisplayName("Normalize null lists in Client constructor (redirectUris, scopes, roles) to empty lists")
  void normalizeNullListsInClientConstructorToEmptyLists() {
    // Act
    AuthorizationServerProperties.Client client =
        new AuthorizationServerProperties.Client("id", "sec", null, null, null, true);

    // Assert
    assertTrue(client.redirectUris().isEmpty(), "redirectUris should default to empty when null is provided");
    assertTrue(client.scopes().isEmpty(), "scopes should default to empty when null is provided");
    assertTrue(client.roles().isEmpty(), "roles should default to empty when null is provided");
    assertTrue(client.requireAuthorizationConsent(), "requireAuthorizationConsent should be preserved");
    // nonEmptyRoles should also be empty
    assertTrue(client.nonEmptyRoles().isEmpty(), "nonEmptyRoles should be empty when roles are empty");
  }

  @Test
  @DisplayName("Client.empty() should return an empty client with expected defaults")
  void clientEmptyShouldReturnExpectedDefaults() {
    // Act
    AuthorizationServerProperties.Client empty = AuthorizationServerProperties.Client.empty();

    // Assert
    assertNull(empty.id(), "empty.id should be null");
    assertNull(empty.secret(), "empty.secret should be null");
    assertTrue(empty.redirectUris().isEmpty(), "empty.redirectUris should be empty");
    assertTrue(empty.scopes().isEmpty(), "empty.scopes should be empty");
    assertTrue(empty.roles().isEmpty(), "empty.roles should be empty");
    assertFalse(empty.requireAuthorizationConsent(),
        "empty.requireAuthorizationConsent should be false");
  }
}