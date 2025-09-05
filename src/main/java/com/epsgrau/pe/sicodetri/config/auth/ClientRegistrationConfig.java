package com.epsgrau.pe.sicodetri.config.auth;

import com.epsgrau.pe.sicodetri.config.props.AuthorizationServerProperties;
import java.util.Set;
import java.util.UUID;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

/**
 * Configuration class responsible for registering OAuth2 clients and persisting
 * their definitions in a PostgreSQL database via JDBC.
 * This class binds the {@link AuthorizationServerProperties} to read the
 * client registration details from application configuration, and then
 * creates or updates a {@link RegisteredClient} in the {@link RegisteredClientRepository}.
 * <b>Class</b>: {@link ClientRegistrationConfig}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
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
 *     <li>Jul 21, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(AuthorizationServerProperties.class)
public class ClientRegistrationConfig {

    /**
     * Creates a {@link RegisteredClientRepository} bean that persists OAuth2 clients
     * to the database. It will load an existing client by ID if present, or create
     * a new one otherwise. Client properties (ID, secret, redirect URIs, scopes,
     * grant types, and token lifetimes) are sourced from the bound
     * {@link AuthorizationServerProperties}.
     * Steps performed:
     * <ol>
     *   <li>Instantiate a {@link JdbcRegisteredClientRepository} using the provided
     *   {@link JdbcTemplate}.</li>
     *   <li>Attempt to load an existing {@link RegisteredClient} by client ID; if not found,
     *       initialize a new builder with a random UUID.</li>
     *   <li>Configure the builder with properties: client ID, encoded client secret,
     *       authentication methods, authorization grant types, redirect URIs, scopes,
     *       {@link ClientSettings}, and {@link TokenSettings}.</li>
     *   <li>Build the final {@link RegisteredClient} and save (insert or update) it
     *   in the repository.</li>
     * </ol>
     *
     * @param jdbcTemplate the {@link JdbcTemplate} used for JDBC operations
     * @param encoder      the {@link PasswordEncoder} used to encode client secrets
     * @param props        the bound {@link AuthorizationServerProperties} containing
     *                     the client and token configuration sections
     * @return a {@link RegisteredClientRepository} backed by JDBC
    */
    @Bean
    public RegisteredClientRepository registeredClientRepository(
        JdbcTemplate jdbcTemplate,
        PasswordEncoder encoder,
        AuthorizationServerProperties props) {

        JdbcRegisteredClientRepository repo = new JdbcRegisteredClientRepository(jdbcTemplate);

        // 1) Cliente INTERACTIVO (SPA): authorization_code + PKCE + refresh_token
        upsertClient(repo, encoder, props.client(), props, true);

        // 2) Cliente TÉCNICO (performance): client_credentials
        upsertClient(repo, encoder, props.perfClient(), props, false);

        return repo;
    }

    private void upsertClient(JdbcRegisteredClientRepository repo,
                              PasswordEncoder encoder,
                              AuthorizationServerProperties.Client c,
                              AuthorizationServerProperties props,
                              boolean interactive) {
        if (c == null || c.id() == null || c.secret() == null) {
            // Evita NullPointer si no hay configuración; puedes cambiar a log.warn si prefieres
            return;
        }

        RegisteredClient existing = repo.findByClientId(c.id());
        RegisteredClient.Builder builder = (existing != null)
            ? RegisteredClient.from(existing)
            : RegisteredClient.withId(UUID.randomUUID().toString());

        // Configuración base común
        builder
            .clientId(c.id())
            .clientSecret(encoder.encode(c.secret()))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .scopes(s -> {
                s.clear();
                s.addAll(c.scopes());
            })
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(props.token().accessTokenTimeToLive())
                .refreshTokenTimeToLive(props.token().refreshTokenTimeToLive())
                .reuseRefreshTokens(props.token().reuseRefreshTokens())
                .build());

        if (interactive) {
            // Cliente para la SPA: Authorization Code + Refresh + PKCE
            builder
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .clientSettings(ClientSettings.builder()
                    .requireAuthorizationConsent(c.requireAuthorizationConsent())
                    .requireProofKey(true) // PKCE
                    .build())
                .redirectUris(r -> {
                    r.clear();
                    r.addAll(c.redirectUris());
                });
        } else {
            // Cliente TÉCNICO de performance: Client Credentials
            builder
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientSettings(ClientSettings.builder()
                    .requireAuthorizationConsent(false)
                    .requireProofKey(false)
                    .build())
                .redirectUris(Set::clear)
                .scopes(s -> {
                    s.clear();
                    s.addAll(c.scopes());
                });
        }

        // Guarda (insert/update). Si existía, RegisteredClient.from(existing) conserva el id.
        repo.save(builder.build());
    }
}
