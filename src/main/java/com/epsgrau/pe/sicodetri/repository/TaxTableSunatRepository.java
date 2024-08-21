package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This class is a repository para entity Tax Table SUNAT. <br/>
 * <b>Class</b>: {@link TaxTableSunatRepository}<br/>
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
 *     <li>Ago 15, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Repository
public interface TaxTableSunatRepository extends CrudRepository<TaxTableSunat, Long> {
}
