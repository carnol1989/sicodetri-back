package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
     * Deletes all deductions belonging to tax depreciations of a given Financial Fixed Asset.
     *
     * <p>Bulk delete to avoid N+1 deletes.</p>
     *
     * @param ffaId the Financial Fixed Asset identifier
     * @return number of affected rows
     */
    @SuppressWarnings("checkstyle:Indentation")
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
      delete from Deduction d
      where d.taxDepreciation.id in (
        select td.id from TaxDepreciation td
        where td.financialFixedAsset.id = :ffaId
      )
      """)
    int deleteByFfaId(@Param("ffaId") Long ffaId);

    /**
     * Deletes all deductions linked to the given list of tax depreciation ids.
     *
     * @param taxDepreciationIds list of tax depreciation identifiers
     * @return number of affected rows
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("delete from Deduction d where d.taxDepreciation.id in :ids")
    int deleteByTaxDepreciationIds(@Param("ids") List<Long> taxDepreciationIds);

    /**
     * Retrieves all {@link Deduction} records linked to any of the specified
     * {@link TaxDepreciation} entities.
     *
     * @param taxDepreciations the list of TaxDepreciation entities to filter by
     * @return a list of matching Deduction entities; empty if none found
     */
    List<Deduction> findByTaxDepreciationIn(List<TaxDepreciation> taxDepreciations);

}
