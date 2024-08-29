package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.FixedAssetDepreciation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixedAssetDepreciationRepository extends JpaRepository<FixedAssetDepreciation, Long> {
}
