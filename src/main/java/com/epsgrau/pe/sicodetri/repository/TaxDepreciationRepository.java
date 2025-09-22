package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for performing CRUD operations on {@link TaxDepreciation} entities.
 * Extends {@link JpaRepository} to provide standard data access methods and declares
 * a custom finder to retrieve all tax depreciation records for a given fixed asset.
 * <b>Class</b>: {@link TaxDepreciationRepository}<br/>
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
public interface TaxDepreciationRepository extends JpaRepository<TaxDepreciation, Long> {

    /**
     * Deletes all tax depreciation rows linked to a given Financial Fixed Asset.
     * This is a bulk operation to avoid loading entities into memory.
     *
     * @param ffaId the Financial Fixed Asset identifier
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from TaxDepreciation td where td.financialFixedAsset.id = :ffaId")
    int deleteByFfaId(@Param("ffaId") Long ffaId);

    /**
     * Retrieves all {@link TaxDepreciation} entries associated with the specified
     * financial fixed asset ID.
     *
     * @param fixedAssetId the ID of the financial fixed asset
     * @return a list of matching TaxDepreciation entities; empty if none found
     */
    List<TaxDepreciation> findByFinancialFixedAssetId(Long fixedAssetId);

    /**
     * Deletes all tax depreciation rows for a given accounting period.
     *
     * @param period accounting period to purge
     * @return number of affected rows
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from TaxDepreciation td where td.accountingPeriod = :period")
    int deleteByAccountingPeriod(@Param("period") LocalDate period);

}
