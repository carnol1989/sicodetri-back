package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import java.util.List;

/**
 * This interface with the operations of the Addition class. <br/>
 * <b>Class</b>: {@link AdditionService}<br/>
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
 *     <li>Ago 25, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public interface AdditionService extends CrudService<Addition, Long> {

    List<Addition> findByTaxDepreciationInService(List<TaxDepreciation> taxDepreciations);

}
