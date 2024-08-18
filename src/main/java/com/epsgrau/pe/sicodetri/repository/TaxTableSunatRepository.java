package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxTableSunatRepository extends CrudRepository<TaxTableSunat, Long> {
}
