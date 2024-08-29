package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.repository.TaxDepreciationRepository;
import com.epsgrau.pe.sicodetri.service.TaxDepreciationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaxDepreciationServiceImpl implements TaxDepreciationService {

    @Autowired
    private TaxDepreciationRepository repository;

    @Override
    public TaxDepreciation saveService(TaxDepreciation obj) {
        TaxDepreciation objCreated = repository.save(obj);
        return objCreated;
    }

    @Override
    public void updateService(TaxDepreciation obj) {
        repository.save(obj);
    }

    @Override
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    @Override
    public TaxDepreciation findByIdService(Long id) {
        Optional<TaxDepreciation> optionalFixedAssetDepreciation = repository.findById(id);
        return optionalFixedAssetDepreciation.orElseGet(TaxDepreciation::new);
    }

    @Override
    public List<TaxDepreciation> findAllService() {
        return repository.findAll();
    }
}
