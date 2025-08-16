package com.epsgrau.pe.sicodetri.config.auth;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that generates and exposes an RSA JSON Web Key (JWK) source
 * for signing JSON Web Tokens (JWTs).
 * On initialization, this class generates a new RSA key pair, wraps the public
 * and private keys in an {@link RSAKey}, and publishes them as a single-entry
 * {@link JWKSet}. The resulting {@link JWKSource} bean can then be used by the
 * authorization server to select keys for JWT signing and verification.
 * <b>Class</b>: {@link JwtKeyConfig}<br/>
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
public class JwtKeyConfig {

    @Value("${authorization-server.jwk.key-size}")
    private int keySize;

    /**
     * Creates a {@link JWKSource} containing an RSA key pair for JWT signing.
     * Generates a new RSA key pair, builds an {@link RSAKey} with a random key ID,
     * wraps it in a {@link JWKSet}, and returns a source that selects from this set.
     *
     * @return a {@link JWKSource} providing the RSA key pair for token signing
    */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, ctx) -> jwkSelector.select(jwkSet);
    }

    /**
     * Generates an {@link RSAKey} containing a newly created RSA key pair.
     * Delegates to {@link #generateRsaKey()} to create a {@link KeyPair},
     * extracts the public and private keys, and assigns a random UUID as the key ID.
     *
     * @return an {@link RSAKey} with public and private RSA keys
     */
    private RSAKey generateRsa() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build();
    }

    /**
     * Generates a new RSA {@link KeyPair} using a 2048-bit key size.
     *
     * @return a freshly generated RSA {@link KeyPair}
     * @throws IllegalStateException if the RSA algorithm is not available
     */
    private KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar la clave RSA", ex);
        }
    }
}