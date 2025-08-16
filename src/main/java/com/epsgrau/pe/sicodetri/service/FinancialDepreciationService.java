package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import java.util.List;

/**
 * Service interface defining operations for managing {@link FinancialDepreciation} entities.
 * Extends common CRUD operations from {@link CrudService} and adds
 * a method to retrieve financial depreciation records by the associated fixed asset identifier.
 * <b>Class</b>: {@link FinancialDepreciationService}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public interface FinancialDepreciationService extends CrudService<FinancialDepreciation, Long> {

    /**
     * Retrieves all {@link FinancialDepreciation} records associated with the given
     * financial fixed asset identifier.
     *
     * @param fixedAssetId the identifier of the financial fixed asset
     * @return a list of matching FinancialDepreciation entities
     */
    List<FinancialDepreciation> findByFinancialFixedAssetIdService(Long fixedAssetId);

}
