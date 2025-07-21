package com.epsgrau.pe.sicodetri.config.props;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This class entity for tax additions. <br/>
 * <b>Class</b>: {@link AuthorizationServerProperties}<br/>
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
 *     <li>Jul 17, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@ConfigurationProperties(prefix = "authorization-server")
public record AuthorizationServerProperties(
    Provider provider,
    Jwk jwk,
    Client client,
    Token token) {

    /**
    * Constructor.
    *
    * @param provider Provider
    * @param jwk Jwk
    * @param client Client
    * @param token Token
    */
    public AuthorizationServerProperties {
        // Provide defaults if any section is missing
        provider = provider == null ? new Provider("") : provider;
        jwk      = jwk      == null ? new Jwk(2048) : jwk;
        client   = client   == null ? new Client(null, null, List.of(), List.of(), true)
            : client;
        token    = token    == null ? new Token(Duration.ofMinutes(15), Duration.ofDays(30), false)
            : token;
    }

    /**
    * Provider.
    */
    public record Provider(String issuer) {

    }

    /**
    * Jwk.
    */
    public record Jwk(int keySize) {

    }

    /**
    * Client.
    */
    public record Client(
        String id,
        String secret,
        List<String> redirectUris,
        List<String> scopes,
        boolean requireAuthorizationConsent) {

        /**
        * Constructor.
        *
        * @param id Id
        * @param secret Secret
        * @param redirectUris Redirect uris
        * @param scopes Scopes
        * @param requireAuthorizationConsent Require authorization consent
        */
        public Client {
            // Defensive copy to ensure immutability
            redirectUris = List.copyOf(redirectUris == null ? List.of() : redirectUris);
            scopes       = List.copyOf(scopes       == null ? List.of() : scopes);
        }

    }

    /**
    * Token.
    */
    public record Token(
        Duration accessTokenTimeToLive,
        Duration refreshTokenTimeToLive,
        boolean reuseRefreshTokens) {

    }
}