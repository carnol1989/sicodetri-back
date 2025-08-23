package com.epsgrau.pe.sicodetri.config.auth;

import com.epsgrau.pe.sicodetri.config.props.AuthorizationServerProperties;
import java.util.Collection;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

/**
 * Configuration class for customizing JWT tokens issued by the OAuth2 Authorization Server.
 * This class defines a bean that injects a "roles" claim into the access token,
 * containing the authenticated user's granted authorities (with any "ROLE_" prefix removed).
 * <b>Class</b>: {@link JwtTokenCustomizerConfig}<br/>
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
 *     <li>Jul 22, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Configuration
public class JwtTokenCustomizerConfig {

    /**
     * Defines a customizer that adds a "roles" claim to JWT access tokens.
     * Only applies to tokens of type {@link OAuth2TokenType#ACCESS_TOKEN}.
     * The "roles" claim will be a list of the user's authorities,
     * with the "ROLE_" prefix stripped off if present.
     *
     * @return an {@link OAuth2TokenCustomizer} for {@link JwtEncodingContext}
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(AuthorizationServerProperties props) {
        return context -> {
            if (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                return;
            }

            // Caso 1: Client Credentials (no hay usuario final)
            if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType())) {
                var perf = props.perfClient();
                List<String> roles = perf.nonEmptyRoles();
                context.getClaims().claim("roles", roles);
                return;
            }

            // Caso 2: Flujos con usuario (authorization_code / refresh_token)
            if (context.getPrincipal() != null && context.getPrincipal().getAuthorities() != null) {
                Collection<? extends GrantedAuthority> authorities = context.getPrincipal().getAuthorities();
                List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(auth -> auth.startsWith("ROLE_") ? auth.substring(5) : auth)
                    .toList();
                context.getClaims().claim("roles", roles);
            }
        };
    }
}