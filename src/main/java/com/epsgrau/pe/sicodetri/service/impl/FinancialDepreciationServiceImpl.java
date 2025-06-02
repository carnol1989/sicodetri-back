package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.repository.FinancialDepreciationRepository;
import com.epsgrau.pe.sicodetri.service.FinancialDepreciationService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class implements the operations of the FinancialDepreciationService. <br/>
 * <b>Class</b>: {@link FinancialDepreciationServiceImpl}<br/>
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
