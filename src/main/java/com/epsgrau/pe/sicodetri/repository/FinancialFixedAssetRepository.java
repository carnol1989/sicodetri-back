package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialFixedAssetRepository extends JpaRepository<FinancialFixedAsset, Long> {

    List<FinancialFixedAsset> findByAccountingPeriod(LocalDate period);

    @Transactional
    @Modifying
    @Query("DELETE FROM FinancialFixedAsset f WHERE f.accountingPeriod = :period")
    void deleteByAccountingPeriod(LocalDate period);

}
