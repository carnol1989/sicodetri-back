package com.epsgrau.pe.sicodetri.config.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class responsible for setting up authentication-related beans.
 * Defines a {@link PasswordEncoder} for hashing user passwords and an
 * {@link AuthenticationManager} that uses a provided {@link UserDetailsService}
 * and {@link PasswordEncoder} to authenticate users.
 * <b>Class</b>: {@link AuthenticationConfig}<br/>
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
public class AuthenticationConfig {

    /**
     * Creates a {@link PasswordEncoder} bean that uses BCrypt hashing algorithm.
     * BCrypt is recommended for secure password storage as it automatically handles
     * salting and is resistant to brute-force attacks.
     *
     * @return a {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures and builds the {@link AuthenticationManager} bean.
     * Retrieves the shared {@link AuthenticationManagerBuilder} from the provided
     * {@link HttpSecurity} context, sets up the {@link UserDetailsService} and
     * {@link PasswordEncoder}, and then builds the final {@link AuthenticationManager}.
     *
     * @param http    the {@link HttpSecurity} to obtain shared objects
     * @param uds     the {@link UserDetailsService} used to load user-specific data
     * @param encoder the {@link PasswordEncoder} used to verify user passwords
     * @return a fully configured {@link AuthenticationManager}
     * @throws Exception if an error occurs while building the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       UserDetailsService uds,
                                                       PasswordEncoder encoder)
        throws Exception {
        AuthenticationManagerBuilder authBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
            .userDetailsService(uds)
            .passwordEncoder(encoder);
        return authBuilder.build();
    }
}