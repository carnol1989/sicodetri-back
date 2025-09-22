package com.epsgrau.pe.sicodetri.service.projection;

import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;

/**
 * Resolves a {@link ProjectionGenerator} strategy by {@link ProjectionType}.
 *
 * <p>Benefits:
 * <ul>
 *   <li>Encapsulates the underlying registry (Map) behind an interface (DIP).</li>
 *   <li>Makes testing easier (mock this interface, not the Map).</li>
 *   <li>Keeps callers focused on the behavior, not the storage detail.</li>
 * </ul>
 */
public interface ProjectionGeneratorResolver {

    /**
     * Returns the generator strategy for the given type.
     *
     * @param type the projection type
     * @return a non-null {@link ProjectionGenerator}
     * @throws IllegalArgumentException if no generator is registered for the type
     */
    ProjectionGenerator get(ProjectionType type);
}
