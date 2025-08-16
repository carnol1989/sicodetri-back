package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on {@link FinancialDepreciation} entities.
 * Extends {@link JpaRepository} to provide standard data access methods,
 * and declares a custom finder to retrieve all financial depreciation records
 * for a given fixed asset.
 * <b>Class</b>: {@link FinancialDepreciationRepository}<br/>
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
public interface FinancialDepreciationRepository extends
    JpaRepository<FinancialDepreciation, Long> {

    /**
     * Retrieves all {@link FinancialDepreciation} entries associated with the specified
     * financial fixed asset.
     *
     * @param fixedAssetId the ID of the {@code FinancialFixedAsset}
     * @return a list of matching FinancialDepreciation entities; empty if none found
     */
    List<FinancialDepreciation> findByFinancialFixedAssetId(Long fixedAssetId);

}
