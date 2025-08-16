package com.epsgrau.pe.sicodetri.config.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Unit tests for {@link MethodSecurityConfig}.
 *
 * <p>
 * Verifies that:
 * <ul>
 *   <li>The {@link MethodSecurityConfig} bean is registered in the Spring application context.</li>
 *   <li>The {@link EnableMethodSecurity} annotation on {@link MethodSecurityConfig}
 *       has the expected attribute values:
 *       <ul>
 *         <li>{@code prePostEnabled} = true</li>
 *         <li>{@code securedEnabled} = true</li>
 *         <li>{@code jsr250Enabled} = false</li>
 *       </ul>
 *   </li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link MethodSecurityConfigTest}<br/>
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
@DisplayName("MethodSecurityConfig Tests")
class MethodSecurityConfigTest {

  @Test
  @DisplayName("Load MethodSecurityConfig bean when context is loaded")
  void loadMethodSecurityConfigBeanWhenContextIsLoaded() {
    try (AnnotationConfigApplicationContext context =
             new AnnotationConfigApplicationContext(MethodSecurityConfig.class)) {
      MethodSecurityConfig bean = context.getBean(MethodSecurityConfig.class);
      assertNotNull(bean, "MethodSecurityConfig bean should be registered in context");
    }
  }

  @Test
  @DisplayName("Assert EnableMethodSecurity annotation attributes on MethodSecurityConfig")
  void assertEnableMethodSecurityAttributesOnMethodSecurityConfigWhenInspected() {
    EnableMethodSecurity annotation =
        MethodSecurityConfig.class.getAnnotation(EnableMethodSecurity.class);
    assertNotNull(annotation, "EnableMethodSecurity annotation must be present");
    assertTrue(annotation.prePostEnabled(), "prePostEnabled should be true");
    assertTrue(annotation.securedEnabled(), "securedEnabled should be true");
    assertFalse(annotation.jsr250Enabled(), "jsr250Enabled should be false");
  }
}