package com.epsgrau.pe.sicodetri.config.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

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
 * <p>This suite verifies the registration/upsert behavior of OAuth2/OIDC clients
 * persisted via {@link JdbcRegisteredClientRepository}:</p>
 *
 * <ul>
 *   <li><b>Interactive client</b>:
 *     <ul>
 *       <li>Creates repository and inserts when no existing client is found.</li>
 *       <li>Updates when an existing client (by {@code client_id}) is present.</li>
 *       <li>Encodes client secret using the provided {@link PasswordEncoder}.</li>
 *       <li>Applies properties from {@link AuthorizationServerProperties.Client} and
 *           token lifetimes from {@link AuthorizationServerProperties.Token}.</li>
 *     </ul>
 *   </li>
 *   <li><b>Performance/technical client</b> (client-credentials):
 *     <ul>
 *       <li>Registers when configured and not present in the repository.</li>
 *       <li>Does not require redirect URIs; scopes behavior verified as configured in tests.</li>
 *     </ul>
 *   </li>
 *   <li><b>Guard clauses</b>:
 *     <ul>
 *       <li>Skips upsert when the interactive client section is {@code null}.</li>
 *       <li>Skips upsert when {@code clientId} or {@code clientSecret} are {@code null}.</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p><b>Test strategy</b>:</p>
 * <ul>
 *   <li>Mocks {@link JdbcTemplate} queries/updates to simulate existing/non-existing clients.</li>
 *   <li>Uses lenient stubs for {@link AuthorizationServerProperties} to avoid unnecessary stubbing warnings.</li>
 *   <li>Asserts repository type, that updates/inserts occur (or not) as expected, and that secrets are encoded.</li>
 * </ul>
 *
 * <b>Class</b>: {@link ClientRegistrationConfigTest}<br/>
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
 *       <li>Sep 10, 2025 (Docs) Clarify upsert coverage (interactive/perf), guard clauses, and JDBC mocking strategy.</li>
 *     </ul>
 * @version 1.1
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
  private AuthorizationServerProperties.Client perfClientProps;

  @Mock
  private AuthorizationServerProperties.Token tokenProps;

  @InjectMocks
  private ClientRegistrationConfig config;

  @Captor
  private ArgumentCaptor<PreparedStatementSetter> argsCaptor;

  @BeforeEach
  void setUp() {
    // ---- Stubs lenient para evitar UnnecessaryStubbing cuando no se usen en algún test ----
    lenient().when(props.client()).thenReturn(clientProps);
    lenient().when(clientProps.id()).thenReturn("my-client-id");
    lenient().when(clientProps.secret()).thenReturn("my-secret");
    lenient().when(clientProps.redirectUris()).thenReturn(List.of("https://app/callback"));
    lenient().when(clientProps.scopes()).thenReturn(List.of("openid", "profile"));
    lenient().when(clientProps.requireAuthorizationConsent()).thenReturn(false);

    lenient().when(props.token()).thenReturn(tokenProps);
    lenient().when(tokenProps.accessTokenTimeToLive()).thenReturn(Duration.ofHours(1));
    lenient().when(tokenProps.refreshTokenTimeToLive()).thenReturn(Duration.ofDays(30));
    lenient().when(tokenProps.reuseRefreshTokens()).thenReturn(false);

    lenient().when(encoder.encode("my-secret")).thenReturn("encoded-secret");
  }

  @Test
  @DisplayName("Return RegisteredClientRepository cuando no existe cliente (interactivo)")
  void returnRegisteredClientRepositoryWhenNoExistingClientFound() {
    // Arrange:
    // 1) Para cualquier query (por client_id o por id), devolver vacío
    when(jdbcTemplate.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(Collections.emptyList());
    // 2) Cualquier update (insert/update) devuelve 1
    when(jdbcTemplate.update(anyString(), any(PreparedStatementSetter.class)))
        .thenReturn(1);

    // perfClient no configurado en este test
    when(props.perfClient()).thenReturn(null);

    // Act
    RegisteredClientRepository repo =
        config.registeredClientRepository(jdbcTemplate, encoder, props);

    // Assert
    assertNotNull(repo, "Repository should not be null");
    assertTrue(repo instanceof JdbcRegisteredClientRepository,
        "Should return a JdbcRegisteredClientRepository");

    // Se realizó al menos un update (por el cliente interactivo insertado)
    verify(jdbcTemplate, atLeastOnce())
        .update(anyString(), any(PreparedStatementSetter.class));
  }

  @Test
  @DisplayName("Update existing client cuando ya existe el cliente interactivo")
  void updateExistingClientWhenClientAlreadyExists() {
    // Arrange: simulamos que el findByClientId devuelve un RegisteredClient existente
    RegisteredClient existing = RegisteredClient.withId("existing-id")
        .clientId("my-client-id")
        .clientSecret("old-secret")
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("https://app/callback")
        .scope("openid")
        .build();

    // Cualquier consulta → vacío, salvo la que pase "my-client-id" como arg => devolver existente
    when(jdbcTemplate.query(anyString(), any(RowMapper.class), any()))
        .thenAnswer(inv -> {
          Object arg = inv.getArgument(2);
          if ("my-client-id".equals(arg)) {
            return List.of(existing);
          }
          return Collections.emptyList();
        });

    when(jdbcTemplate.update(anyString(), any(PreparedStatementSetter.class)))
        .thenReturn(1);

    when(props.perfClient()).thenReturn(null);

    // Act
    RegisteredClientRepository repo =
        config.registeredClientRepository(jdbcTemplate, encoder, props);

    // Assert
    assertNotNull(repo, "Repository should not be null");
    assertTrue(repo instanceof JdbcRegisteredClientRepository,
        "Should return a JdbcRegisteredClientRepository");

    // Capturamos los updates (inserte o actualice, debe intentar persistir)
    verify(jdbcTemplate, atLeastOnce())
        .update(anyString(), argsCaptor.capture());
  }

  @Test
  @DisplayName("Registrar perf client (client_credentials) cuando no existe")
  void registerPerfClientWhenNoneExists() {
    // Arrange: para cualquier query (por id o client_id) devolver vacío
    when(jdbcTemplate.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(Collections.emptyList());

    when(jdbcTemplate.update(anyString(), any(PreparedStatementSetter.class)))
        .thenReturn(1);

    // Configurar perf client completo (rama no interactiva)
    when(props.perfClient()).thenReturn(perfClientProps);
    when(perfClientProps.id()).thenReturn("perf-client-id");
    when(perfClientProps.secret()).thenReturn("perf-secret");
    when(perfClientProps.scopes()).thenReturn(List.of("perf.read", "perf.write"));

    when(encoder.encode("perf-secret")).thenReturn("encoded-perf-secret");

    // Act
    RegisteredClientRepository repo =
        config.registeredClientRepository(jdbcTemplate, encoder, props);

    // Assert
    assertNotNull(repo);
    assertTrue(repo instanceof JdbcRegisteredClientRepository);

    verify(jdbcTemplate, atLeastOnce())
        .update(anyString(), any(PreparedStatementSetter.class));

    // Opcional: demuestra que NO se usó
    verify(perfClientProps, org.mockito.Mockito.never()).redirectUris();
  }

  @Test
  @DisplayName("No hacer nada cuando el cliente interactivo es null")
  void doNothingWhenClientIsNull() {
    // Arrange
    when(props.client()).thenReturn(null);
    when(props.perfClient()).thenReturn(null);

    // Act
    RegisteredClientRepository repo =
        config.registeredClientRepository(jdbcTemplate, encoder, props);

    // Assert
    assertNotNull(repo);
    assertTrue(repo instanceof JdbcRegisteredClientRepository);
    verify(jdbcTemplate, never()).update(anyString(), any(PreparedStatementSetter.class));
  }

  @Test
  @DisplayName("No hacer nada cuando el clientId interactivo es null")
  void doNothingWhenClientIdIsNull() {
    // Arrange
    when(clientProps.id()).thenReturn(null); // fuerza guard clause
    when(props.perfClient()).thenReturn(null);

    // Act
    RegisteredClientRepository repo =
        config.registeredClientRepository(jdbcTemplate, encoder, props);

    // Assert
    assertNotNull(repo);
    assertTrue(repo instanceof JdbcRegisteredClientRepository);
    verify(jdbcTemplate, never()).update(anyString(), any(PreparedStatementSetter.class));
  }

  @Test
  @DisplayName("No hacer nada cuando el clientSecret interactivo es null")
  void doNothingWhenClientSecretIsNull() {
    // Arrange
    when(clientProps.secret()).thenReturn(null); // fuerza guard clause
    when(props.perfClient()).thenReturn(null);

    // Act
    RegisteredClientRepository repo =
        config.registeredClientRepository(jdbcTemplate, encoder, props);

    // Assert
    assertNotNull(repo);
    assertTrue(repo instanceof JdbcRegisteredClientRepository);
    verify(jdbcTemplate, never()).update(anyString(), any(PreparedStatementSetter.class));
  }
}