package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.repository.FinancialDepreciationRepository;
import com.epsgrau.pe.sicodetri.service.FinancialDepreciationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FinancialDepreciationServiceImpl implements FinancialDepreciationService {

    @Autowired
    private FinancialDepreciationRepository repository;

    @Override
    public FinancialDepreciation saveService(FinancialDepreciation obj) {
        FinancialDepreciation objCreated = repository.save(obj);
        return objCreated;
    }

    @Override
    public void saveAllService(List<FinancialDepreciation> listObj) {
        repository.saveAll(listObj);
    }

    @Override
    public void updateService(FinancialDepreciation obj) {
        repository.save(obj);
    }

    @Override
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    @Override
    public FinancialDepreciation findByIdService(Long id) {
        Optional<FinancialDepreciation> optionalFinancialDepreciation = repository.findById(id);
        return optionalFinancialDepreciation.orElseGet(FinancialDepreciation::new);
    }

    @Override
    public List<FinancialDepreciation> findAllService() {
        return repository.findAll();
    }
}
