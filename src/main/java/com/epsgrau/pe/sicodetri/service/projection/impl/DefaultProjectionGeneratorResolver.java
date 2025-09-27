package com.epsgrau.pe.sicodetri.service.projection.impl;

import static java.util.Objects.requireNonNull;

import com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionGeneratorResolver;
import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default resolver backed by a type-safe registry.
 */
@Slf4j
@Component
public class DefaultProjectionGeneratorResolver implements ProjectionGeneratorResolver {

    private final Map<ProjectionType, ProjectionGenerator> registry;

    public DefaultProjectionGeneratorResolver(Map<ProjectionType, ProjectionGenerator> registry) {
        this.registry = requireNonNull(registry, "registry");
    }

    @Override
    public ProjectionGenerator get(ProjectionType type) {
        log.info("[INICIO][get]");
        requireNonNull(type, "type");
        final ProjectionGenerator generator = registry.get(type);
        if (generator == null) {
            throw new IllegalArgumentException("No ProjectionGenerator registered for type: " + type);
        }
        return generator;
    }
}
