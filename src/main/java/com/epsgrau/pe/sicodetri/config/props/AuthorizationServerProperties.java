package com.epsgrau.pe.sicodetri.config.props;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the OAuth2 Authorization Server.
 * These properties are mapped from the application configuration file
 * (e.g., application.yml or application.properties) using the prefix {@code authorization-server}.
 * <b>Class</b>: {@link AuthorizationServerProperties}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @param provider Provider configuration section.
 * @param jwk JWK (JSON Web Key) configuration section.
 * @param client OAuth2 client credentials and settings.
 * @param token Token lifespan and refresh policy settings.
 *
 * @author EPS Grau <br/>
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>: <br/>
 *     <ul>
 *     <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:<br/>
 *     <ul>
 *     <li>Jul 17, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@ConfigurationProperties(prefix = "authorization-server")
public record AuthorizationServerProperties(
    Provider provider,
    Jwk jwk,
    Client client,
    Client perfClient,
    Token token) {

    /**
     * Constructs the properties with default values when any section is missing.
     *
     * @param provider Provider configuration; defaults to empty issuer if null.
     * @param jwk JWK configuration; defaults to key size 2048 if null.
     * @param client Client configuration; defaults to empty fields if null.
     * @param token Token configuration; defaults to 15-minute access token,
     *              30-day refresh token if null.
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
     * Configuration for the Authorization Server provider metadata.
     *
     * @param issuer Issuer URL (e.g., {@code http://localhost:8080}).
     */
    public record Provider(String issuer) {

    }

    /**
     * Configuration for the JSON Web Key (JWK) used to sign tokens.
     *
     * @param keySize Size of the RSA key pair in bits (e.g., 2048, 4096).
     */
    public record Jwk(int keySize) {

    }

    /**
     * Configuration for the OAuth2 registered client.
     *
     * @param id Client ID registered with the Authorization Server.
     * @param secret Client secret (should be securely stored).
     * @param redirectUris List of allowed redirect URIs for authorization code flow.
     * @param scopes List of scopes the client can request.
     * @param requireAuthorizationConsent Whether to require user consent before authorization.
     */
    public record Client(
        String id,
        String secret,
        List<String> redirectUris,
        List<String> scopes,
        List<String> roles,
        boolean requireAuthorizationConsent) {

        /**
         * Constructs a client configuration with immutable lists.
         *
         * @param id Client ID.
         * @param secret Client secret.
         * @param redirectUris List of allowed redirect URIs.
         * @param scopes List of allowed scopes.
         * @param requireAuthorizationConsent Whether consent is required.
         */
        public Client {
            // Defensive copy to ensure immutability
            redirectUris = List.copyOf(redirectUris == null ? List.of() : redirectUris);
            scopes       = List.copyOf(scopes       == null ? List.of() : scopes);
            roles       = List.copyOf(roles       == null ? List.of() : roles);
        }

        /**
         * empty.
         *
         * @return Client
         */
        public static Client empty() {
            return new Client(null, null, List.of(), List.of(), List.of(), false);
        }

        /**
         * nonEmptyRoles.
         *
         * @return Roles
         */
        public List<String> nonEmptyRoles() {
            return roles;
        }

    }

    /**
     * Configuration for access and refresh token settings.
     *
     * @param accessTokenTimeToLive Duration that the access token is valid.
     * @param refreshTokenTimeToLive Duration that the refresh token is valid.
     * @param reuseRefreshTokens Whether refresh tokens can be reused or should be rotated.
     */
    public record Token(
        Duration accessTokenTimeToLive,
        Duration refreshTokenTimeToLive,
        boolean reuseRefreshTokens) {

    }
}