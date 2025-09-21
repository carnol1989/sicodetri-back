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
 * Registers OAuth2/OIDC clients and persists their definitions via JDBC (PostgreSQL).
 * <p>
 * Binds {@link AuthorizationServerProperties} to build/update {@link RegisteredClient} entries,
 * then stores them in a {@link JdbcRegisteredClientRepository}. Two client profiles are handled:
 * </p>
 * <ul>
 *   <li><b>Interactive (SPA)</b>: {@code authorization_code} + PKCE + {@code refresh_token}.</li>
 *   <li><b>Technical/Perf</b>: {@code client_credentials} (machine-to-machine).</li>
 * </ul>
 *
 * <p><b>Notes</b>:</p>
 * <ul>
 *   <li>Client authentication methods registered: {@code client_secret_basic} and
 *       {@code client_secret_post}.</li>
 *   <li>Token lifetimes and refresh-token reuse are sourced from {@link AuthorizationServerProperties#token()}.</li>
 *   <li>If a client configuration is missing (id/secret), it is skipped (no NPE).</li>
 *   <li>Persist uses an upsert-like behavior: an existing client is loaded
 *   and updated; otherwise a new one is created.</li>
 * </ul>
 *
 * <b>Class</b>: {@link ClientRegistrationConfig}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
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
 *       <li>Jul 21, 2025 (JAR) Initial version.</li>
 *       <li>Sep 10, 2025 (Docs) Clarify dual-client setup, PKCE, grant types, and upsert semantics.</li>
 *     </ul>
 * @version 1.1
 */
@Configuration
@EnableConfigurationProperties(AuthorizationServerProperties.class)
public class ClientRegistrationConfig {

    /**
     * Creates a {@link RegisteredClientRepository} backed by JDBC and performs client registration/update.
     * <p>Actions:</p>
     * <ol>
     *   <li>Instantiate {@link JdbcRegisteredClientRepository} with the provided {@link JdbcTemplate}.</li>
     *   <li>Upsert the <b>interactive</b> client (SPA): grants {@code authorization_code} + {@code refresh_token},
     *       PKCE required, consent configurable, redirect URIs from properties, scopes from properties.</li>
     *   <li>Upsert the <b>technical/perf</b> client: grant {@code client_credentials},
     *       no consent, PKCE disabled, redirect URIs cleared, scopes cleared (service permissions
     *       are typically conveyed by resource server config).</li>
     * </ol>
     *
     * <p><b>Token settings</b>: access token TTL, refresh token TTL, and reuse policy are taken from
     * {@link AuthorizationServerProperties#token()}.</p>
     *
     * @param jdbcTemplate JDBC access for the underlying repository
     * @param encoder password encoder for client secrets
     * @param props bound properties (client ids, secrets, redirect URIs, scopes, token lifetimes)
     * @return a configured {@link RegisteredClientRepository}
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

    /**
     * Inserts or updates a single {@link RegisteredClient} according to the supplied profile.
     *
     * <p><b>Common settings</b> (both profiles):</p>
     * <ul>
     *   <li>{@code clientId} and <i>encoded</i> {@code clientSecret}.</li>
     *   <li>Client authentication: {@link ClientAuthenticationMethod#CLIENT_SECRET_BASIC} and
     *       {@link ClientAuthenticationMethod#CLIENT_SECRET_POST}.</li>
     *   <li>Token settings from {@link AuthorizationServerProperties#token()}.</li>
     *   <li>Scopes set to the values from properties (interactive) or cleared (technical).</li>
     * </ul>
     *
     * <p><b>Interactive profile</b> ({@code interactive=true}):</p>
     * <ul>
     *   <li>Grants: {@link AuthorizationGrantType#AUTHORIZATION_CODE},
     *   {@link AuthorizationGrantType#REFRESH_TOKEN}.</li>
     *   <li>Client settings: {@code requireAuthorizationConsent} from properties;
     *   {@code requireProofKey=true} (PKCE).</li>
     *   <li>Redirect URIs taken from properties.</li>
     * </ul>
     *
     * <p><b>Technical profile</b> ({@code interactive=false}):</p>
     * <ul>
     *   <li>Grant: {@link AuthorizationGrantType#CLIENT_CREDENTIALS}.</li>
     *   <li>Client settings: {@code requireAuthorizationConsent=false}, {@code requireProofKey=false}.</li>
     *   <li>Redirect URIs cleared; scopes cleared.</li>
     * </ul>
     *
     * <p>If a client with the same {@code clientId} exists, it is loaded and updated; otherwise a new
     * client is created with a random UUID as internal id.</p>
     *
     * @param repo target repository
     * @param encoder password encoder for the client secret
     * @param c client section from properties (may be {@code null}); skipped if id/secret missing
     * @param props global properties (token settings)
     * @param interactive {@code true} for SPA/authorization-code client; {@code false}
     *                                for perf/client-credentials client
     */
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
                .scopes(Set::clear);
        }

        // Guarda (insert/update). Si existía, RegisteredClient.from(existing) conserva el id.
        repo.save(builder.build());
    }
}
