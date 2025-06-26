package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import java.util.List;

/**
 * This interface with the operations of the FinancialDepreciation class. <br/>
 * <b>Class</b>: {@link FinancialDepreciationService}<br/>
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
public interface FinancialDepreciationService extends CrudService<FinancialDepreciation, Long> {

    List<FinancialDepreciation> findByFinancialFixedAssetIdService(Long fixedAssetId);

}
