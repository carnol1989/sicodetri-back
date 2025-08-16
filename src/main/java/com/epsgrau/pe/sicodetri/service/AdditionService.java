package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import java.util.List;

/**
 * Service interface defining operations for managing {@link Addition} entities.
 * Extends the common CRUD operations from {@link CrudService} and adds
 * domain-specific queries related to tax depreciation.
 * <b>Class</b>: {@link AdditionService}<br/>
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
public interface AdditionService extends CrudService<Addition, Long> {

    /**
     * Finds all {@link Addition} records linked to any of the provided
     * {@link TaxDepreciation} entries.
     *
     * @param taxDepreciations the list of tax depreciation entities to filter by
     * @return a list of matching Addition entities
     */
    List<Addition> findByTaxDepreciationInService(List<TaxDepreciation> taxDepreciations);

}
