package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on {@link Deduction} entities.
 * Extends {@link JpaRepository} to provide standard database operations, and
 * declares a custom method to retrieve deductions associated with a list of
 * tax depreciation records.
 * <b>Class</b>: {@link DeductionRepository}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation Interface.</li>
 *     </ul>
 * @version 1.0
 */
@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {

    /**
     * Retrieves all {@link Deduction} records linked to any of the specified
     * {@link TaxDepreciation} entities.
     *
     * @param taxDepreciations the list of TaxDepreciation entities to filter by
     * @return a list of matching Deduction entities; empty if none found
     */
    List<Deduction> findByTaxDepreciationIn(List<TaxDepreciation> taxDepreciations);

}
