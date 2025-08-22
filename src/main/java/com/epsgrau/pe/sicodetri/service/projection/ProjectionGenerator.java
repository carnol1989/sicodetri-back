package com.epsgrau.pe.sicodetri.service.projection;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import java.time.LocalDate;
import java.util.List;

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
     * Returns the {@link ProjectionType} that this generator supports.
     *
     * @return the projection type handled by this implementation
     */
    ProjectionType getType();

    /**
     * Executes the projection computation for the specified accounting period
     * and list of fixed assets.
     * Implementations should iterate over the assets, apply the appropriate
     * depreciation and addition/deduction logic, and persist or return the
     * results as needed.
     *
     * @param accountingPeriod the starting period for projection (e.g., month/year)
     * @param fixedAssets      the list of {@link FinancialFixedAsset} entities to process
     */
    void generate(String username, LocalDate accountingPeriod, List<FinancialFixedAsset> fixedAssets);

}
