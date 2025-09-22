package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
     * Deletes all financial depreciation rows linked to a given Financial Fixed Asset.
     *
     * <p>Use this method instead of reading and deleting entities one-by-one to avoid the N+1 query pattern.</p>
     *
     * @param ffaId the Financial Fixed Asset identifier
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from FinancialDepreciation fd where fd.financialFixedAsset.id = :ffaId")
    int deleteByFfaId(@Param("ffaId") Long ffaId);

    /**
     * Retrieves all {@link FinancialDepreciation} entries associated with the specified
     * financial fixed asset.
     *
     * @param fixedAssetId the ID of the {@code FinancialFixedAsset}
     * @return a list of matching FinancialDepreciation entities; empty if none found
     */
    List<FinancialDepreciation> findByFinancialFixedAssetId(Long fixedAssetId);

    /**
     * Deletes all financial depreciation rows for a given accounting period.
     * Useful for re-running projections by period.
     *
     * @param period accounting period to purge
     * @return number of affected rows
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from FinancialDepreciation fd where fd.accountingPeriod = :period")
    int deleteByAccountingPeriod(@Param("period") LocalDate period);

}
