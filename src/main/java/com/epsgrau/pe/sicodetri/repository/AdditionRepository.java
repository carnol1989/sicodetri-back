package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on {@link Addition} entities.
 * Extends {@link JpaRepository} to provide standard data access methods, and
 * defines a custom finder for retrieving additions associated with a list of
 * tax depreciation records.
 * <b>Class</b>: {@link AdditionRepository}<br/>
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
public interface AdditionRepository extends JpaRepository<Addition, Long> {

    /**
     * Retrieves all {@link Addition} records associated with any of the given
     * {@link TaxDepreciation} entities.
     *
     * @param taxDepreciations the list of tax depreciation entities to filter by
     * @return a list of matching Addition entities; empty if none found
     */
    List<Addition> findByTaxDepreciationIn(List<TaxDepreciation> taxDepreciations);

}
