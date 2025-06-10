package com.epsgrau.pe.sicodetri.service.projection;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import java.time.LocalDate;
import java.util.List;

/**
 * This class abstract for generate projection. <br/>
 * <b>Class</b>: {@link ProjectionGenerator}<br/>
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
 *     <li>Jun 10, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public interface ProjectionGenerator {

    /**
     * Identify the type of projection you implement.
     *
     * @return ProjectionType
     */
    ProjectionType getType();

    /**
     * Generation logic for that type of projection.
     *
     * @param accountingPeriod Accounting period
     * @param fixedAssets Fixed asset list
     */
    void generate(LocalDate accountingPeriod, List<FinancialFixedAsset> fixedAssets);

}
