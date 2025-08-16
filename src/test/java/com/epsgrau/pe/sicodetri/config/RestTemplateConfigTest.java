package com.epsgrau.pe.sicodetri.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

/**
 * Unit test class for {@link RestTemplateConfig}.
 * <p>
 * This class verifies the behavior of the {@code RestTemplateConfig} configuration,
 * ensuring that it correctly creates and returns a {@link RestTemplate} instance
 * using a provided {@link RestTemplateBuilder}.
 * </p>
 *
 * <b>Tested Scenarios:</b>
 * <ul>
 *   <li>Ensures that when a mocked {@link RestTemplateBuilder} successfully builds
 *       a {@link RestTemplate}, the same instance is returned.</li>
 *   <li>Ensures that a non-null {@link RestTemplate} is created when a real,
 *       default {@link RestTemplateBuilder} is provided.</li>
 * </ul>
 *
 * <b>Technologies & Tools Used:</b>
 * <ul>
 *   <li>JUnit 5 for test execution</li>
 *   <li>Mockito for mocking dependencies</li>
 *   <li>AssertJ and JUnit assertions for validation</li>
 * </ul>
 *
 * <b>Class</b>: {@link RestTemplateConfigTest}<br/>
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
 *     <li>Ago 11, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@DisplayName("RestTemplateConfig Tests")
class RestTemplateConfigTest {

  private final RestTemplateConfig config = new RestTemplateConfig();

  /**
   * Tests that {@link RestTemplateConfig#restTemplate(RestTemplateBuilder)} returns
   * the same {@link RestTemplate} instance when the provided {@link RestTemplateBuilder}
   * is mocked and its {@code build()} method is stubbed to return a specific instance.
   * <p>
   * This test verifies that:
   * <ul>
   *   <li>The method returns exactly the instance provided by the builder.</li>
   *   <li>The builder's {@code build()} method is called exactly once.</li>
   *   <li>No other interactions occur with the mocked builder.</li>
   * </ul>
   */
  @Test
  @DisplayName("Return RestTemplate when RestTemplateBuilder builds successfully")
  void returnRestTemplateWhenRestTemplateBuilderBuildsSuccessfully() {
    // Arrange
    RestTemplateBuilder builder = mock(RestTemplateBuilder.class);
    RestTemplate expected = mock(RestTemplate.class);
    when(builder.build()).thenReturn(expected);

    // Act
    RestTemplate actual = config.restTemplate(builder);

    // Assert
    assertSame(expected, actual);
    verify(builder).build();
    verifyNoMoreInteractions(builder);
  }

  /**
   * Tests that {@link RestTemplateConfig#restTemplate(RestTemplateBuilder)} creates
   * a non-null {@link RestTemplate} when a real, default {@link RestTemplateBuilder}
   * is provided.
   * <p>
   * This test verifies that:
   * <ul>
   *   <li>The returned instance is not {@code null}.</li>
   *   <li>No exceptions are thrown during creation.</li>
   * </ul>
   */
  @Test
  @DisplayName("Create RestTemplate when default RestTemplateBuilder is provided")
  void createRestTemplateWhenDefaultRestTemplateBuilderIsProvided() {
    // Arrange
    RestTemplateBuilder realBuilder = new RestTemplateBuilder();

    // Act
    RestTemplate actual = config.restTemplate(realBuilder);

    // Assert
    assertThat(actual).isNotNull();
  }

}