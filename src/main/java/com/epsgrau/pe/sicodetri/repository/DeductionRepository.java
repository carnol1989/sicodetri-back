package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface is to handle the data access operations of the Deduction entity. <br/>
 * <b>Class</b>: {@link DeductionRepository}<br/>
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
 *     <li>Ago 25, 2024 (JAR) Creation Interface.</li>
 *     </ul>
 * @version 1.0
 */
@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {

    List<Deduction> findByTaxDepreciationIn(List<TaxDepreciation> taxDepreciations);

}
