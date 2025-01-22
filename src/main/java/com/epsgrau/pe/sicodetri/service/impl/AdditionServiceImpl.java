package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.repository.AdditionRepository;
import com.epsgrau.pe.sicodetri.service.AdditionService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class implements the operations of the AdditionService. <br/>
 * <b>Class</b>: {@link AdditionServiceImpl}<br/>
 * <b>Copyright</b>: &Copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau <br/>
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>: <br/>
 *     <ul>
 *     <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:<br/>
 *     <ul>
 *     <li>Ago 25, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
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
    public void saveAllService(List<Addition> listObj) {
        repository.saveAll(listObj);
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
