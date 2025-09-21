package com.epsgrau.pe.sicodetri.dto.mapper;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for converting between {@link FinancialFixedAsset} entities and
 * {@link FinancialFixedAssetDto} data transfer objects.
 * <p>
 * This mapper is registered as a Spring bean ({@code componentModel = "spring"})
 * and is configured to {@link org.mapstruct.ReportingPolicy#IGNORE IGNORE} unmapped
 * target properties, meaning that any DTO fields without a matching source property
 * will be silently skipped.
 * </p>
 * <b>Notes:</b>
 * <ul>
 *   <li>Mapping is convention-based: properties with the same name and compatible types are mapped automatically.</li>
 *   <li>Nested/complex mappings or name mismatches may require additional MapStruct annotations on the
 *       entity/DTO or a decorator.</li>
 *   <li>Null inputs produce {@code null} outputs by default (MapStruct behavior).</li>
 * </ul>
 * <b>Usage:</b> Inject this interface where needed; MapStruct generates the implementation at build time.
 * <b>Class</b>: {@link FinancialFixedAssetMapper}<br/>
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
 *     <li>Sep 09, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE // ignora campos del DTO que no existen en la entidad
)
public interface FinancialFixedAssetMapper {

    /**
     * Converts a {@link FinancialFixedAsset} entity into its {@link FinancialFixedAssetDto} representation.
     *
     * @param entity the source entity to map; may be {@code null}
     * @return the mapped {@link FinancialFixedAssetDto}, or {@code null} if {@code entity} is {@code null}
     */
    FinancialFixedAssetDto financialFixedAssetToFinancialFixedAssetDto(FinancialFixedAsset entity);

    /**
     * Converts a list of {@link FinancialFixedAsset} entities into a list of {@link FinancialFixedAssetDto}.
     * <p>
     * Elements are mapped using {@link #financialFixedAssetToFinancialFixedAssetDto(FinancialFixedAsset)}.
     * </p>
     *
     * @param entities the source list to map; may be {@code null}
     * @return a list of mapped DTOs; returns {@code null} if {@code entities} is {@code null}
     */
    List<FinancialFixedAssetDto> listFinancialFixedAssetToListFinancialFixedAssetDto(
        List<FinancialFixedAsset> entities);

}
