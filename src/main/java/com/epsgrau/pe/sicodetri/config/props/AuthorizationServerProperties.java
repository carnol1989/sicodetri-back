package com.epsgrau.pe.sicodetri.config.props;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the OAuth2 Authorization Server.
 * <br/>
 * Binds external configuration (e.g., {@code application.yml}) using the prefix
 * {@code authorization-server}. This record provides a centralized and immutable view of:
 * <ul>
 *   <li>Provider metadata ({@link Provider}).</li>
 *   <li>JWK settings for token signing ({@link Jwk}).</li>
 *   <li>Registered client defaults for interactive usage ({@link Client}).</li>
 *   <li>A dedicated performance-test client ({@link #perfClient()}) to simplify load testing.</li>
 *   <li>Access/refresh token lifetimes and reuse policy ({@link Token}).</li>
 * </ul>
 *
 * <p><b>Business rules &amp; defaults</b>:</p>
 * <ul>
 *   <li>All sections are optional; the canonical constructor applies safe defaults when a section is {@code null}.</li>
 *   <li>{@code perfClient} is intended for automated/performance scenarios and defaults to
 *       {@code id=perf-client}, {@code secret=perf-secret}, empty lists
 *       and {@code requireAuthorizationConsent=false}.</li>
 *   <li>This type is immutable and therefore thread-safe.</li>
 * </ul>
 *
 * <b>Class</b>: {@link AuthorizationServerProperties}<br/>
 * <b>COPYRIGHT</b>: &copy; 2024 EPS Grau.<br/>
 * <b>Company</b>: EPS Grau.<br/>
 *
 * @param provider     Provider configuration section.
 * @param jwk          JWK (JSON Web Key) configuration section.
 * @param client       Default OAuth2 client configuration and settings.
 * @param perfClient   Dedicated OAuth2 client configuration used for performance tests.
 * @param token        Token lifetime and reuse policy settings.
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
 */
@ConfigurationProperties(prefix = "authorization-server")
public record AuthorizationServerProperties(
    Provider provider,
    Jwk jwk,
    Client client,
    Client perfClient,
    Token token) {

    /**
     * Canonical constructor that applies defaults to any missing section to keep the aggregate consistent.
     *
     * <p><b>Defaults applied when a section is {@code null}</b>:</p>
     * <ul>
     *   <li>{@link Provider}: {@code issuer=""}</li>
     *   <li>{@link Jwk}: {@code keySize=2048}</li>
     *   <li>{@link Client} (interactive): {@code id=null}, {@code secret=null}, empty lists for
     *       {@code redirectUris}, {@code scopes}, {@code roles}, and {@code requireAuthorizationConsent=true}</li>
     *   <li>{@link #perfClient() perfClient}: {@code id="perf-client"}, {@code secret="perf-secret"},
     *       empty lists for {@code redirectUris}, {@code scopes}, {@code roles}, and
     *       {@code requireAuthorizationConsent=false}</li>
     *   <li>{@link Token}: {@code accessTokenTimeToLive=15m}, {@code refreshTokenTimeToLive=30d},
     *       {@code reuseRefreshTokens=false}</li>
     * </ul>
     *
     * @param provider   provider configuration (may be {@code null})
     * @param jwk        JWK configuration (may be {@code null})
     * @param client     default client configuration (may be {@code null})
     * @param perfClient performance client configuration (may be {@code null})
     * @param token      token configuration (may be {@code null})
     */
    public AuthorizationServerProperties {
        // Provide defaults if any section is missing
        provider = provider == null ? new Provider("") : provider;
        jwk      = jwk      == null ? new Jwk(2048) : jwk;
        client   = client   == null ? new Client(null, null, List.of(), List.of(), List.of(), true)
            : client;
        perfClient = perfClient == null ? new Client("perf-client", "perf-secret", List.of(), List.of(), List.of(),
            false) : perfClient;
        token    = token    == null ? new Token(Duration.ofMinutes(15), Duration.ofDays(30), false)
            : token;
    }

    /**
     * Provider (issuer) metadata.
     *
     * @param issuer Issuer URL (e.g., {@code http://localhost:8080} or a fully qualified HTTPS URL).
     */
    public record Provider(String issuer) {

    }

    /**
     * JSON Web Key (JWK) configuration used by the Authorization Server to sign tokens.
     *
     * @param keySize RSA key size in bits (e.g., {@code 2048}, {@code 4096}).
     */
    public record Jwk(int keySize) {

    }

    /**
     * OAuth2 Registered Client configuration.
     *
     * @param id                          Client identifier registered with the Authorization Server.
     * @param secret                      Client secret (use secure storage/encoding in production).
     * @param redirectUris                Allowed redirect URIs for authorization code flows.
     * @param scopes                      Scopes the client can request.
     * @param roles                       Logical roles associated to the client (may drive JWT custom claims).
     * @param requireAuthorizationConsent Whether user consent is required during authorization.
     */
    public record Client(
        String id,
        String secret,
        List<String> redirectUris,
        List<String> scopes,
        List<String> roles,
        boolean requireAuthorizationConsent) {

        /**
         * Canonical constructor that normalizes {@code null} list inputs to empty unmodifiable lists,
         * and defensively copies all lists to preserve immutability.
         *
         * @param id                          client id
         * @param secret                      client secret
         * @param redirectUris                redirect URIs (nullable; normalized to empty)
         * @param scopes                      scopes (nullable; normalized to empty)
         * @param roles                       roles (nullable; normalized to empty)
         * @param requireAuthorizationConsent consent requirement flag
         */
        public Client {
            // Defensive copy to ensure immutability
            redirectUris = List.copyOf(redirectUris == null ? List.of() : redirectUris);
            scopes       = List.copyOf(scopes       == null ? List.of() : scopes);
            roles       = List.copyOf(roles       == null ? List.of() : roles);
        }

        /**
         * Builds an "empty" client with {@code id=null}, {@code secret=null}, empty lists and
         * {@code requireAuthorizationConsent=false}. Useful for tests or placeholders.
         *
         * @return an empty, immutable {@link Client} instance
         */
        public static Client empty() {
            return new Client(null, null, List.of(), List.of(), List.of(), false);
        }

        /**
         * Returns the configured roles as an immutable list.
         * <br/>This method never returns {@code null}; it yields an empty list when no roles are configured.
         *
         * @return immutable list of roles (possibly empty)
         */
        public List<String> nonEmptyRoles() {
            return roles;
        }

    }

    /**
     * Access and refresh token policy settings.
     *
     * @param accessTokenTimeToLive  Duration for which an access token is valid.
     * @param refreshTokenTimeToLive Duration for which a refresh token is valid.
     * @param reuseRefreshTokens     If {@code true}, refresh tokens are reusable; otherwise they are rotated.
     */
    public record Token(
        Duration accessTokenTimeToLive,
        Duration refreshTokenTimeToLive,
        boolean reuseRefreshTokens) {

    }
}