package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.FixedAssetDepreciation;
import com.epsgrau.pe.sicodetri.repository.FixedAssetDepreciationRepository;
import com.epsgrau.pe.sicodetri.service.FixedAssetDepreciationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FixedAssetDepreciationServiceImpl implements FixedAssetDepreciationService {

    @Autowired
    private FixedAssetDepreciationRepository repository;

    @Override
    public FixedAssetDepreciation saveService(FixedAssetDepreciation obj) {
        FixedAssetDepreciation objCreated = repository.save(obj);
        return objCreated;
    }

    @Override
    public void updateService(FixedAssetDepreciation obj) {
        repository.save(obj);
    }

    @Override
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    @Override
    public FixedAssetDepreciation findByIdService(Long id) {
        Optional<FixedAssetDepreciation> optionalFixedAssetDepreciation = repository.findById(id);
        return optionalFixedAssetDepreciation.orElseGet(FixedAssetDepreciation::new);
    }

    @Override
    public List<FixedAssetDepreciation> findAllService() {
        return repository.findAll();
    }

}
