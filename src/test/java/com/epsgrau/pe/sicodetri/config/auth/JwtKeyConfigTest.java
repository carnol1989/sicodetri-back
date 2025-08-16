package com.epsgrau.pe.sicodetri.config.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.lang.reflect.Field;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link JwtKeyConfig}.
 *
 * <p>
 * Verifies that:
 * <ul>
 *   <li>{@code jwkSource()} returns a {@link com.nimbusds.jose.jwk.source.JWKSource}
 *       containing exactly one {@link com.nimbusds.jose.jwk.RSAKey} with non-null
 *       public and private keys and a non-empty key ID.</li>
 *   <li>{@code jwkSource()} throws {@link IllegalStateException} when RSA key generation fails,
 *       propagating a {@link NoSuchAlgorithmException} as its cause.</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link JwtKeyConfigTest}<br/>
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
@DisplayName("JwtKeyConfig Test")
class JwtKeyConfigTest {

  @Test
  @DisplayName("Return JWKSource when keySize is positive")
  void returnJwkSourceWhenKeySizeIsPositive() throws Exception {
    // Arrange: inyectamos keySize = 2048
    JwtKeyConfig config = new JwtKeyConfig();
    Field keySizeField = JwtKeyConfig.class.getDeclaredField("keySize");
    keySizeField.setAccessible(true);
    keySizeField.setInt(config, 2048);

    // Act: obtenemos el JWKSource y seleccionamos la clave
    JWKSource<SecurityContext> source = config.jwkSource();
    JWKSelector selector = new JWKSelector(new JWKMatcher.Builder().build());
    List<JWK> keys = source.get(selector, null);

    // Assert: debe haber exactamente una RSAKey con clave pública y privada no nulas
    assertEquals(1, keys.size(), "Debe haber una sola clave en el JWKSet");
    JWK jwk = keys.get(0);
    assertTrue(jwk instanceof RSAKey, "El JWK debe ser una RSAKey");
    RSAKey rsaKey = (RSAKey) jwk;

    // EXTRAEMOS las claves Java válidas
    RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
    assertNotNull(publicKey, "La clave pública no debe ser null");

    RSAPrivateKey privateKey = rsaKey.toRSAPrivateKey();
    assertNotNull(privateKey, "La clave privada no debe ser null");

    // Y el keyID
    String keyId = rsaKey.getKeyID();
    assertNotNull(keyId, "El keyID no debe ser null");
    assertFalse(keyId.isEmpty(), "El keyID no debe estar vacío");
  }

  @Test
  @DisplayName("Throw IllegalStateException when RSA key generation fails")
  void throwIllegalStateExceptionWhenRsaKeyGenerationFails() throws Exception {
    // Arrange: mismo keySize
    JwtKeyConfig config = new JwtKeyConfig();
    Field keySizeField = JwtKeyConfig.class.getDeclaredField("keySize");
    keySizeField.setAccessible(true);
    keySizeField.setInt(config, 2048);

    // Mockeamos KeyPairGenerator.getInstance para que falle
    try (var kpgMock = mockStatic(KeyPairGenerator.class)) {
      kpgMock.when(() -> KeyPairGenerator.getInstance("RSA"))
          .thenThrow(new NoSuchAlgorithmException("algorithm not found"));

      // Act & Assert: jwkSource() debe lanzar IllegalStateException
      IllegalStateException ex = assertThrows(
          IllegalStateException.class,
          config::jwkSource,
          "Se esperaba IllegalStateException al fallar la generación RSA"
      );

      assertTrue(
          ex.getMessage().contains("No se pudo generar la clave RSA"),
          "El mensaje debe indicar el fallo en generación RSA"
      );
      assertNotNull(ex.getCause(), "La causa no debe ser null");
      assertTrue(
          ex.getCause() instanceof NoSuchAlgorithmException,
          "La causa debe ser NoSuchAlgorithmException"
      );
    }
  }
}