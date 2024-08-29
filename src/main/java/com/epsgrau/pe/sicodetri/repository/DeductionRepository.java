package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {
}
