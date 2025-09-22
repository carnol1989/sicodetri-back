package com.epsgrau.pe.sicodetri.config;

import com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator;
import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires a type-safe registry of projection generators keyed by {@link ProjectionType}.
 *
 * <p>Principles:
 * <ul>
 *   <li><b>DIP</b>: depend on abstractions ({@link ProjectionGenerator}) not concretions.</li>
 *   <li><b>OCP</b>: adding a new projection type is a new bean + one line here.</li>
 *   <li><b>Clean Code</b>: small, explicit, immutable map exposed via Spring bean.</li>
 * </ul>
 */
@Configuration
public class ProjectionGeneratorsConfig {

    /**
     * Registers the strategy map for projection generators.
     *
     * @param temporaryDeductionProjectionGenerator strategy for TEMPORARY_DEDUCTIONS
     * @param temporaryAdditionProjectionGenerator  strategy for TEMPORARY_ADDITIONS
     * @param permanentAdditionProjectionGenerator  strategy for PERMANENT_ADDITIONS
     * @return immutable map of {@link ProjectionType} to {@link ProjectionGenerator}
     */
    @Bean
    public Map<ProjectionType, ProjectionGenerator> projectionGenerators(
        ProjectionGenerator temporaryDeductionProjectionGenerator,
        ProjectionGenerator temporaryAdditionProjectionGenerator,
        ProjectionGenerator permanentAdditionProjectionGenerator
    ) {
        Objects.requireNonNull(temporaryDeductionProjectionGenerator, "temporaryDeductionProjectionGenerator");
        Objects.requireNonNull(temporaryAdditionProjectionGenerator,  "temporaryAdditionProjectionGenerator");
        Objects.requireNonNull(permanentAdditionProjectionGenerator,  "permanentAdditionProjectionGenerator");

        EnumMap<ProjectionType, ProjectionGenerator> map = new EnumMap<>(ProjectionType.class);
        map.put(ProjectionType.TEMPORARY_DEDUCTIONS, temporaryDeductionProjectionGenerator);
        map.put(ProjectionType.TEMPORARY_ADDITIONS,  temporaryAdditionProjectionGenerator);
        map.put(ProjectionType.PERMANENT_ADDITIONS,  permanentAdditionProjectionGenerator);

        return Map.copyOf(map); // return as unmodifiable to avoid accidental mutation
    }
}
