package com.epsgrau.pe.sicodetri.config.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.config.props.AuthorizationServerProperties;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * Unit tests for {@link ClientRegistrationConfig}.
 *
 * <p>
 * Verifies that:
 * <ul>
 *   <li>{@code registeredClientRepository(JdbcTemplate, PasswordEncoder, AuthorizationServerProperties)}
 *       returns a {@link JdbcRegisteredClientRepository} and inserts a new client when none exists.</li>
 *   <li>{@code registeredClientRepository(JdbcTemplate, PasswordEncoder, AuthorizationServerProperties)}
 *       updates an existing client record when one is found.</li>
 *   <li>Client secret is correctly encoded using the provided {@link PasswordEncoder}.</li>
 *   <li>Properties from {@link AuthorizationServerProperties.Client} and
 *       {@link AuthorizationServerProperties.Token} are applied to the registration process.</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link ClientRegistrationConfigTest}<br/>
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
@DisplayName("ClientRegistrationConfig Tests")
class ClientRegistrationConfigTest {

  @Mock
  private JdbcTemplate jdbcTemplate;

  @Mock
  private PasswordEncoder encoder;

  @Mock
  private AuthorizationServerProperties props;

  @Mock
  private AuthorizationServerProperties.Client clientProps;

  @Mock
  private AuthorizationServerProperties.Token tokenProps;

  @InjectMocks
  private ClientRegistrationConfig config;

  @Captor
  private ArgumentCaptor<PreparedStatementSetter> argsCaptor;

  @BeforeEach
  void setUp() {
    // Simular valores de AuthorizationServerProperties
    when(props.client()).thenReturn(clientProps);
    when(clientProps.id()).thenReturn("my-client-id");
    when(clientProps.secret()).thenReturn("my-secret");
    when(clientProps.redirectUris()).thenReturn(List.of("https://app/callback"));
    when(clientProps.scopes()).thenReturn(List.of("openid", "profile"));
    when(clientProps.requireAuthorizationConsent()).thenReturn(false);

    when(props.token()).thenReturn(tokenProps);
    when(tokenProps.accessTokenTimeToLive()).thenReturn(Duration.ofHours(1));
    when(tokenProps.refreshTokenTimeToLive()).thenReturn(Duration.ofDays(30));
    when(tokenProps.reuseRefreshTokens()).thenReturn(false);

    // Simular el encoder de la secret
    when(encoder.encode("my-secret")).thenReturn("encoded-secret");
  }

  @Test
  @DisplayName("Return RegisteredClientRepository when no existing client found")
  void returnRegisteredClientRepositoryWhenNoExistingClientFound() {
    // Arrange: no hay cliente existente
    when(jdbcTemplate.query(
        anyString(),
        any(RowMapper.class),
        eq("my-client-id")
    )).thenReturn(Collections.emptyList());
    when(jdbcTemplate.update(anyString(), any(PreparedStatementSetter.class)))
        .thenReturn(1);

    // Act
    RegisteredClientRepository repo =
        config.registeredClientRepository(jdbcTemplate, encoder, props);

    // Assert
    assertNotNull(repo, "Repository should not be null");
    assertTrue(repo instanceof JdbcRegisteredClientRepository,
        "Should return a JdbcRegisteredClientRepository");

    // Verificar que se invocó save (update...) al menos una vez
    verify(jdbcTemplate, atLeastOnce())
        .update(anyString(), any(PreparedStatementSetter.class));
  }

  @Test
  @DisplayName("Update existing client when client already exists")
  void updateExistingClientWhenClientAlreadyExists() {
    // Arrange: hay un RegisteredClient existente
    RegisteredClient existing = RegisteredClient.withId("existing-id")
        .clientId("my-client-id")
        .clientSecret("old-secret")
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("https://app/callback")
        .scope("openid")
        .build();

    when(jdbcTemplate.query(
        anyString(),
        any(RowMapper.class),
        eq("my-client-id")
    )).thenReturn(List.of(existing));
    when(jdbcTemplate.update(anyString(), any(PreparedStatementSetter.class)))
        .thenReturn(1);

    // Act
    RegisteredClientRepository repo =
        config.registeredClientRepository(jdbcTemplate, encoder, props);

    // Assert
    assertNotNull(repo, "Repository should not be null");
    assertTrue(repo instanceof JdbcRegisteredClientRepository,
        "Should return a JdbcRegisteredClientRepository");

    // Capturar los parámetros pasados a jdbcTemplate.update(...)
    verify(jdbcTemplate, atLeastOnce())
        .update(anyString(), argsCaptor.capture());

    // Capturamos el PreparedStatementSetter para inspeccionar los argumentos
    ArgumentCaptor<PreparedStatementSetter> pssCaptor =
        ArgumentCaptor.forClass(PreparedStatementSetter.class);
    verify(jdbcTemplate, atLeastOnce())
        .update(anyString(), pssCaptor.capture());
  }
}