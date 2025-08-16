package com.epsgrau.pe.sicodetri.config.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.config.props.AuthorizationServerProperties;
import com.epsgrau.pe.sicodetri.config.props.AuthorizationServerProperties.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

/**
 * Unit tests for {@link AuthorizationServerSettingsConfig}.
 *
 * <p>
 * Verifies that the {@code providerSettings(AuthorizationServerProperties)} method
 * produces an {@link AuthorizationServerSettings} instance with the issuer URL
 * extracted from the injected {@link AuthorizationServerProperties.Provider}.
 * </p>
 *
 * <b>Class</b>: {@link AuthorizationServerSettingsConfigTest}<br/>
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
@DisplayName("AuthorizationServerSettingsConfig Tests")
class AuthorizationServerSettingsConfigTest {

  private final AuthorizationServerSettingsConfig config = new AuthorizationServerSettingsConfig();

  @Test
  @DisplayName("Return AuthorizationServerSettings when issuer is provided")
  void returnAuthorizationServerSettingsWhenIssuerProvided() {
    // Mockeamos las propiedades y su provider
    AuthorizationServerProperties props = mock(AuthorizationServerProperties.class);
    Provider provider = mock(Provider.class);

    when(provider.issuer()).thenReturn("https://issuer.example");
    when(props.provider()).thenReturn(provider);

    // Ejecutamos el método bajo prueba
    AuthorizationServerSettings settings = config.providerSettings(props);

    // Verificamos
    assertEquals("https://issuer.example", settings.getIssuer());
    verify(props).provider();
    verify(provider).issuer();
    verifyNoMoreInteractions(props, provider);
  }

  @Test
  @DisplayName("Return AuthorizationServerSettings when issuer is empty")
  void returnAuthorizationServerSettingsWhenIssuerIsEmpty() {
    AuthorizationServerProperties props = mock(AuthorizationServerProperties.class);
    Provider provider = mock(Provider.class);

    when(provider.issuer()).thenReturn("");
    when(props.provider()).thenReturn(provider);

    AuthorizationServerSettings settings = config.providerSettings(props);

    assertEquals("", settings.getIssuer());
  }
}