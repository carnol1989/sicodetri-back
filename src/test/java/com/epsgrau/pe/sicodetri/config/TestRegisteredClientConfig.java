package com.epsgrau.pe.sicodetri.config;

import java.util.List;
import java.util.UUID;

import com.epsgrau.pe.sicodetri.config.props.AuthorizationServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

/**
 * Test configuration for OAuth2 RegisteredClient setup in integration tests.
 *
 * <p>
 * Provides an in-memory {@link RegisteredClientRepository} bean that registers a
 * test client with predefined client ID, secret, grant types, redirect URI,
 * and scopes. This client is used to simulate OAuth2 authorization_code and
 * client_credentials flows during integration testing.
 * </p>
 *
 * <b>Class</b>: {@link TestRegisteredClientConfig}<br/>
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
 *     <li>Jul 24, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@TestConfiguration
@Profile("integration")
@EnableConfigurationProperties(AuthorizationServerProperties.class)
@Slf4j
public class TestRegisteredClientConfig {

  @Bean
  @Primary
  public RegisteredClientRepository registeredClientRepository(WebServerApplicationContext webContext,
                                                               PasswordEncoder encoder,
                                                               AuthorizationServerProperties props) {
    int port = webContext.getWebServer().getPort();
    log.info("port: {}", port);

    var clientProps = props.client();
    String clientId     = clientProps.id();
    String clientSecret = clientProps.secret();
    List<String> redirectUris = clientProps.redirectUris();
    List<String> scopes       = clientProps.scopes();
    boolean requireConsent    = clientProps.requireAuthorizationConsent();

    RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId(clientId)
        .clientSecret(encoder.encode(clientSecret))
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .clientSettings(ClientSettings.builder()
            .requireAuthorizationConsent(requireConsent)
            .build());

    // Ajusta las redirect URIs dinámicamente con el puerto
    redirectUris.stream()
        .map(uri -> uri.replace("{port}", String.valueOf(port)))
        .forEach(builder::redirectUri);

    // Agrega los scopes definidos en YAML
    scopes.forEach(builder::scope);

    RegisteredClient client = builder.build();
    return new InMemoryRegisteredClientRepository(client);
  }

}