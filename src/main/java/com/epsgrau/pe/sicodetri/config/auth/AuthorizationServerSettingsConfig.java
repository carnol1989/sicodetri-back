package com.epsgrau.pe.sicodetri.config.auth;

import com.epsgrau.pe.sicodetri.config.props.AuthorizationServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

/**
 * Configuration class that exposes the {@link AuthorizationServerSettings} bean
 * for the OAuth2 Authorization Server, based on application properties.
 * This class is annotated with {@link EnableConfigurationProperties} to bind
 * the {@link AuthorizationServerProperties} and uses its {@code provider.issuer}
 * value to set the issuer URL in the server settings.
 * <b>Class</b>: {@link AuthorizationServerSettingsConfig}<br/>
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
public class AuthorizationServerSettingsConfig {

    /**
     * Creates and configures the {@link AuthorizationServerSettings} bean.
     * The issuer URL is taken from the {@link AuthorizationServerProperties}
     * (prefix {@code authorization-server.provider.issuer}) to ensure that
     * all issued tokens carry the correct authorization server identifier.
     *
     * @param props the bound {@link AuthorizationServerProperties} instance
     * @return a configured {@link AuthorizationServerSettings} object
     */
    @Bean
    public AuthorizationServerSettings providerSettings(AuthorizationServerProperties props) {
        return AuthorizationServerSettings.builder()
            .issuer(props.provider().issuer())
            .build();
    }
}
