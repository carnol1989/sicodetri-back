package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;

import java.util.List;

public interface TaxTableSunatService {

    void saveService(TaxTableSunat obj);

    void updateService(TaxTableSunat obj);

    void deleteByIdService(Long id);

    TaxTableSunat findByIdService(Long id);

    List<TaxTableSunat> findAllService();

}
