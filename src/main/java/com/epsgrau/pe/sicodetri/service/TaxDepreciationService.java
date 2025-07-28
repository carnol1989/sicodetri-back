package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import java.util.List;

/**
 * Service interface for managing tax depreciation entries.
 * Extends the generic {@link CrudService} for basic CRUD operations
 * and adds a method to query depreciations by fixed asset ID.
 * <b>Class</b>: {@link TaxDepreciationService}<br/>
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
public interface TaxDepreciationService extends CrudService<TaxDepreciation, Long> {

    /**
     * Retrieves all {@link TaxDepreciation} records associated with the given financial
     * fixed asset ID.
     *
     * @param fixedAssetId the ID of the financial fixed asset
     * @return a list of TaxDepreciation entries linked to the specified asset,
     *         or an empty list if none are found
     */
    List<TaxDepreciation> findByFinancialFixedAssetIdService(Long fixedAssetId);

}
