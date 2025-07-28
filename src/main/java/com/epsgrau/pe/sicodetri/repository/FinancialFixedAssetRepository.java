package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on {@link FinancialFixedAsset} entities.
 * Extends {@link JpaRepository} to provide standard data access methods and declares
 * additional query methods specific to financial fixed asset retrieval and deletion by period.
 * <b>Class</b>: {@link FinancialFixedAssetRepository}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Repository
public interface FinancialFixedAssetRepository extends JpaRepository<FinancialFixedAsset, Long> {

    /**
     * Retrieves all financial fixed assets for the given accounting period.
     *
     * @param accountingPeriod the accounting period to filter by
     * @return list of matching FinancialFixedAsset entities; empty if none found
     */
    List<FinancialFixedAsset> findByAccountingPeriod(LocalDate accountingPeriod);

    /**
     * Retrieves financial fixed assets for the specified period whose accounting account
     * code starts with the given prefix.
     *
     * @param accountingPeriod        the accounting period to filter by
     * @param accountingAccountPrefix prefix of the accounting account code
     * @return list of matching FinancialFixedAsset entities; empty if none found
     */
    List<FinancialFixedAsset> findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(
        LocalDate accountingPeriod, String accountingAccountPrefix);

    /**
     * Retrieves financial fixed assets whose accounting account code prefix is in the given list.
     *
     * @param prefixes list of 3-character account code prefixes
     * @return list of matching FinancialFixedAsset entities; empty if none found
     */
    @Query("SELECT f FROM FinancialFixedAsset f "
        + "WHERE SUBSTRING(f.fixedAssetAccountingAccount, 1, 3) IN :prefixes")
    List<FinancialFixedAsset> findByAccountingAccountPrefixes(
        @Param("prefixes") List<String> prefixes);

    /**
     * Retrieves financial fixed assets matching the given main fixed asset code.
     *
     * @param mainFixedAssetCode the main fixed asset code to filter by
     * @return list of matching FinancialFixedAsset entities; empty if none found
     */
    List<FinancialFixedAsset> findByMainFixedAssetCode(String mainFixedAssetCode);

    /**
     * Retrieves financial fixed assets matching the given accounting account code.
     *
     * @param fixedAssetAccountingAccount the accounting account code to filter by
     * @return list of matching FinancialFixedAsset entities; empty if none found
     */
    List<FinancialFixedAsset> findByFixedAssetAccountingAccount(String fixedAssetAccountingAccount);

    /**
     * Deletes all financial fixed assets for the specified accounting period.
     * This method executes a bulk delete—use with caution as it bypasses entity
     * lifecycle callbacks.
     *
     * @param period the accounting period of records to delete
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM FinancialFixedAsset f WHERE f.accountingPeriod = :period")
    void deleteByAccountingPeriod(LocalDate period);

}
