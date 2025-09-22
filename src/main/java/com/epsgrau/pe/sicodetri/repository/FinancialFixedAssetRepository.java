package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link FinancialFixedAsset}.
 *
 * <p><b>Purpose</b>:
 * Provides CRUD operations via {@link JpaRepository} and declares
 * domain-specific queries optimized for reporting and batch maintenance
 * of financial fixed assets.</p>
 *
 * <p><b>Notes &amp; conventions</b>:</p>
 * <ul>
 *   <li>All finder methods return a <i>non-null</i> {@link List}; when no rows match,
 *       an empty list is returned.</li>
 *   <li>Parameters are expected to be non-null unless explicitly documented.</li>
 *   <li>The bulk delete method is annotated with {@link Modifying} and
 *       {@link Transactional}; it bypasses entity lifecycle callbacks and may
 *       not synchronize the persistence context automatically.</li>
 * </ul>
 *
 * <b>Class</b>: {@link FinancialFixedAssetRepository}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <br/><u>Service Provider</u>: Consultor TI
 *     <br/><u>Developed by</u>:
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *       <li>Jun 06, 2025 (JAR) Class creation.</li>
 *     </ul>
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface FinancialFixedAssetRepository extends JpaRepository<FinancialFixedAsset, Long> {

    /**
     * Paginates Financial Fixed Assets for chunk-based projection generation.
     *
     * @param pageable Spring Data pageable
     * @return a page of Financial Fixed Assets
     */
    Page<FinancialFixedAsset> findAll(Pageable pageable);

    /**
     * Retrieves all financial fixed assets created by {@code username} for the given
     * {@code accountingPeriod}.
     *
     * @param accountingPeriod the period to filter by (inclusive exact match)
     * @param username the user who added the records
     * @return a non-null list of matching entities; empty if none found
     */
    List<FinancialFixedAsset> findByAccountingPeriodAndAddedBy(LocalDate accountingPeriod,
                                                               String username);

    /**
     * Retrieves financial fixed assets created by {@code username} for the given
     * {@code accountingPeriod} whose accounting account code <em>starts with</em>
     * {@code accountingAccountPrefix}.
     *
     * @param accountingPeriod the period to filter by (exact match)
     * @param username the user who added the records
     * @param accountingAccountPrefix the leading substring to match (case-sensitive)
     * @return a non-null list of matching entities; empty if none found
     */
    List<FinancialFixedAsset> findByAccountingPeriodAndAddedByAndFixedAssetAccountingAccountStartingWith(
        LocalDate accountingPeriod, String username, String accountingAccountPrefix);

    /**
     * Retrieves financial fixed assets created by {@code username} whose accounting
     * account code has a 3-character prefix contained in {@code prefixes}.
     *
     * <p><b>Implementation detail</b>:
     * Uses {@code SUBSTRING(f.fixedAssetAccountingAccount, 1, 3)} against the provided
     * prefix list. Ensure an index exists on the accounting account column for optimal
     * performance.</p>
     *
     * @param username the user who added the records
     * @param prefixes the list of 3-character prefixes to match (must not be null)
     * @return a non-null list of matching entities; empty if none found
     */
    @Query("SELECT f FROM FinancialFixedAsset f "
        + "WHERE f.addedBy = :addedBy AND SUBSTRING(f.fixedAssetAccountingAccount, 1, 3) "
        + "IN :prefixes")
    List<FinancialFixedAsset> findByAddedByAndAccountingAccountPrefixes(
        @Param("addedBy") String username,
        @Param("prefixes") List<String> prefixes);

    /**
     * Retrieves financial fixed assets created by {@code username} that match the
     * provided main fixed asset code.
     *
     * @param mainFixedAssetCode the main fixed asset code to filter by
     * @param username the user who added the records
     * @return a non-null list of matching entities; empty if none found
     */
    List<FinancialFixedAsset> findByMainFixedAssetCodeAndAddedBy(String mainFixedAssetCode,
                                                                 String username);

    /**
     * Retrieves financial fixed assets created by {@code username} that match the
     * provided accounting account code.
     *
     * @param fixedAssetAccountingAccount the full accounting account code to filter by
     * @param username the user who added the records
     * @return a non-null list of matching entities; empty if none found
     */
    List<FinancialFixedAsset> findByFixedAssetAccountingAccountAndAddedBy(
        String fixedAssetAccountingAccount, String username);

    /**
     * Deletes all financial fixed assets for the specified {@code period} and {@code addedBy}.
     *
     * <p><b>Warning</b>:
     * This is a bulk operation. It bypasses JPA entity lifecycle callbacks and may leave
     * the persistence context out of sync. If the calling layer holds managed instances
     * for the affected rows, consider clearing/evicting the context or issuing an explicit
     * flush after the operation.</p>
     *
     * @param period the accounting period of records to delete
     * @param addedBy the user who added the records
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM FinancialFixedAsset f WHERE f.accountingPeriod = :period and "
        + "f.addedBy = :addedBy")
    void deleteByAccountingPeriodAndAddedBy(LocalDate period, String addedBy);

}
