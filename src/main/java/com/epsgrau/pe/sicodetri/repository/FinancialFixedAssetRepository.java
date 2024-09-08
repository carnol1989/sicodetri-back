package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * This class is a repository para entity Financial Fixed Asset. <br/>
 * <b>Class</b>: {@link FinancialFixedAssetRepository}<br/>
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
 *     <li>Ago 18, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Repository
public interface FinancialFixedAssetRepository extends JpaRepository<FinancialFixedAsset, Long> {

    List<FinancialFixedAsset> findByAccountingPeriod(LocalDate accountingPeriod);

    List<FinancialFixedAsset> findByAccountingPeriodAndFixedAssetAccountingAccountStartingWith(LocalDate accountingPeriod, String accountingAccountPrefix);

    List<FinancialFixedAsset> findByMainFixedAssetCode(String mainFixedAssetCode);

    List<FinancialFixedAsset> findByFixedAssetAccountingAccount(String fixedAssetAccountingAccount);

    @Transactional
    @Modifying
    @Query("DELETE FROM FinancialFixedAsset f WHERE f.accountingPeriod = :period")
    void deleteByAccountingPeriod(LocalDate period);

}
