package com.epsgrau.pe.sicodetri.config.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit tests for {@link AuthenticationConfig}.
 *
 * <p>
 * Verifies that the {@code passwordEncoder()} method returns a {@link BCryptPasswordEncoder},
 * and that {@code authenticationManager(HttpSecurity, UserDetailsService, PasswordEncoder)}
 * constructs a {@link ProviderManager} using the provided {@link UserDetailsService}
 * and {@link PasswordEncoder}.
 * </p>
 *
 * <b>Class</b>: {@link AuthenticationConfigTest}<br/>
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
@DisplayName("AuthenticationConfig Tests (no mocks)")
class AuthenticationConfigTest {

  private final AuthenticationConfig config = new AuthenticationConfig();

  @Test
  @DisplayName("passwordEncoder returns BCryptPasswordEncoder")
  void passwordEncoderReturnsBCrypt() {
    PasswordEncoder encoder = config.passwordEncoder();
    assertTrue(encoder instanceof BCryptPasswordEncoder,
        "passwordEncoder() debe devolver BCryptPasswordEncoder");
  }

  @Test
  @DisplayName("authenticationManager builds a ProviderManager with the provided UDS and encoder")
  void authenticationManagerBuildsProviderManager() throws Exception {
    // 1) ObjectPostProcessor trivial
    ObjectPostProcessor<Object> opp = new ObjectPostProcessor<>() {
      @Override
      public <O> O postProcess(O object) { return object; }
    };

    // 2) Creamos el builder real
    AuthenticationManagerBuilder amb = new AuthenticationManagerBuilder(opp);

    // 3) Construimos HttpSecurity con los 3 parámetros:
    //    - opp
    //    - amb
    //    - un map que contenga al builder para getSharedObject()
    HttpSecurity http = new HttpSecurity(
        opp,
        amb,
        Map.of(AuthenticationManagerBuilder.class, amb)
    );

    // 4) UserDetailsService de prueba: no lanza excepciones aunque no haya usuarios
    UserDetailsService uds = username -> null;

    // 5) PasswordEncoder real
    PasswordEncoder encoder = new BCryptPasswordEncoder();

    // 6) Invocamos tu método
    AuthenticationManager manager =
        config.authenticationManager(http, uds, encoder);

    // 7) Comprobamos que
    assertNotNull(manager, "AuthenticationManager no debe ser null");
    assertTrue(manager instanceof ProviderManager,
        "Debería ser un ProviderManager");
  }
}