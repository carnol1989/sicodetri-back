package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.repository.TaxTableSunatRepository;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaxTableSunatServiceImpl implements TaxTableSunatService {

    @Autowired
    private TaxTableSunatRepository repository;

    @Override
    public void saveService(TaxTableSunat obj) {
        repository.save(obj);
    }

    @Override
    public void updateService(TaxTableSunat obj) {
        repository.save(obj);
    }

    @Override
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    @Override
    public TaxTableSunat findByIdService(Long id) {
        Optional<TaxTableSunat> optionalTaxTableSunat = repository.findById(id);
        return optionalTaxTableSunat.orElseGet(TaxTableSunat::new);
    }

    @Override
    public List<TaxTableSunat> findAllService() {
        List<TaxTableSunat> listTaxTableSunat = new ArrayList<>();
        repository.findAll().forEach(listTaxTableSunat::add);
        return listTaxTableSunat;
    }
}
