package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.repository.DeductionRepository;
import com.epsgrau.pe.sicodetri.service.DeductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeductionServiceImpl implements DeductionService {

    @Autowired
    private DeductionRepository repository;

    @Override
    public Deduction saveService(Deduction obj) {
        Deduction objCreated = repository.save(obj);
        return objCreated;
    }

    @Override
    public void saveAllService(List<Deduction> listObj) {
        repository.saveAll(listObj);
    }

    @Override
    public void updateService(Deduction obj) {
        repository.save(obj);
    }

    @Override
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Deduction findByIdService(Long id) {
        Optional<Deduction> optionalDeduction = repository.findById(id);
        return optionalDeduction.orElseGet(Deduction::new);
    }

    @Override
    public List<Deduction> findAllService() {
        return repository.findAll();
    }
}
