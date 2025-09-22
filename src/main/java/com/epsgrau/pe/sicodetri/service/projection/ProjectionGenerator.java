package com.epsgrau.pe.sicodetri.service.projection;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.model.Projection;
import java.time.LocalDate;
import java.util.Map;

/**
 * Defines the contract for generating different types of depreciation projections.
 * Implementations must specify the projection type they handle and provide
 * the logic to generate projection entries for a list of fixed assets over
 * a given accounting period.
 * <b>Class</b>: {@link ProjectionGenerator}<br/>
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
 *     <li>Jun 10, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public interface ProjectionGenerator {

    /**
     * Computes a projection for a single asset.
     * Implementations MUST be pure (no repositories, no transactions).
     *
     * @param username  who triggered the job (can be "ANONYMOUS")
     * @param period    accounting period (first day of month recommended)
     * @param asset     asset to project
     * @param taxParams preloaded SUNAT params indexed by a stable key
     * @return populated Projection
     */
    Projection generate(String username,
                        LocalDate period,
                        FinancialFixedAsset asset,
                        Map<String, TaxTableSunat> taxParams);

}
