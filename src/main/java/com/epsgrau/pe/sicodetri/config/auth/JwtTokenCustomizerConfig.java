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
 * Configuration class that customizes JWTs issued by Spring Authorization Server.
 * Registers an {@link OAuth2TokenCustomizer} to add a {@code roles} claim to
 * <strong>access tokens</strong>, containing the subject's authorities with the {@code ROLE_}
 * prefix removed (if present).
 *
 * <p><b>Scope and business rules</b>:</p>
 * <ul>
 *   <li><b>Applies only</b> to {@link OAuth2TokenType#ACCESS_TOKEN access tokens}.</li>
 *   <li><b>Client Credentials</b>: since there is no end-user, roles are resolved from the
 *       performance client configured in {@link AuthorizationServerProperties#perfClient()}
 *       via {@code nonEmptyRoles()}.</li>
 *   <li><b>User flows</b> (<em>authorization_code</em> / <em>refresh_token</em>): roles are
 *       derived from the authenticated principal's {@link GrantedAuthority} collection.</li>
 * </ul>
 *
 * <p><b>Interoperability notes</b>:</p>
 * <ul>
 *   <li>The custom {@code roles} claim is <em>in addition to</em> standard claims (e.g. {@code scope}).</li>
 *   <li>Resource Servers should map {@code roles} back to authorities (optionally re-adding the
 *       {@code ROLE_} prefix) using a custom {@code JwtAuthenticationConverter} if needed.</li>
 * </ul>
 *
 * <p><b>Thread-safety</b>: the configuration and the produced customizer are stateless.</p>
 *
 * <b>Class</b>: {@link JwtTokenCustomizerConfig}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <br/><u>Service Provider</u>: Consultor TI
 *     <br/><u>Developed by</u>:
 *     <ul>
 *         <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *         <li>Jul 22, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class JwtTokenCustomizerConfig {

    /**
     * Builds an {@link OAuth2TokenCustomizer} that injects the {@code roles} claim into
     * JWT access tokens.
     *
     * <p><b>Behavior</b>:</p>
     * <ul>
     *   <li>If the token type is not {@link OAuth2TokenType#ACCESS_TOKEN}, the customizer is a no-op.</li>
     *   <li>For {@link AuthorizationGrantType#CLIENT_CREDENTIALS}: roles are taken from
     *       {@link AuthorizationServerProperties#perfClient()} using {@code nonEmptyRoles()}.</li>
     *   <li>For user-based grants (e.g., <em>authorization_code</em>, <em>refresh_token</em>):
     *       authorities are mapped to strings and the {@code ROLE_} prefix is stripped.</li>
     * </ul>
     *
     * @param props authorization server properties used to resolve default client roles
     *              in client-credentials scenarios.
     * @return a stateless {@link OAuth2TokenCustomizer} for {@link JwtEncodingContext}.
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