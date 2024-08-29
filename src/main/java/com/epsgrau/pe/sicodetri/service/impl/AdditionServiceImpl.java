package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.repository.AdditionRepository;
import com.epsgrau.pe.sicodetri.service.AdditionService;
import org.apache.commons.math3.analysis.function.Add;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdditionServiceImpl implements AdditionService {

    @Autowired
    private AdditionRepository repository;

    @Override
    public Addition saveService(Addition obj) {
        Addition objCreated = repository.save(obj);
        return objCreated;
    }

    @Override
    public void updateService(Addition obj) {
        repository.save(obj);
    }

    @Override
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Addition findByIdService(Long id) {
        Optional<Addition> optionalAddition = repository.findById(id);
        return optionalAddition.orElseGet(Addition::new);
    }

    @Override
    public List<Addition> findAllService() {
        return repository.findAll();
    }
}
