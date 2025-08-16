package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import java.util.List;

/**
 * Service interface defining operations for managing {@link Deduction} entities.
 * Extends common CRUD operations from {@link CrudService} and adds
 * methods specific to handling deductions in the context of tax depreciation.
 * <b>Class</b>: {@link DeductionService}<br/>
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
public interface DeductionService extends CrudService<Deduction, Long> {

    /**
     * Finds all {@link Deduction} records linked to any of the provided
     * {@link TaxDepreciation} entries.
     *
     * @param taxDepreciations the list of tax depreciation entities to filter by
     * @return a list of matching Deduction entities
     */
    List<Deduction> findByTaxDepreciationInService(List<TaxDepreciation> taxDepreciations);

}
