package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.repository.TaxDepreciationRepository;
import com.epsgrau.pe.sicodetri.service.TaxDepreciationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * This class implements the operations of the TaxDepreciationService. <br/>
 * <b>Class</b>: {@link TaxDepreciationServiceImpl}<br/>
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
public class TaxDepreciationServiceImpl implements TaxDepreciationService {

    @Autowired
    private TaxDepreciationRepository repository;

    @Override
    public TaxDepreciation saveService(TaxDepreciation obj) {
        TaxDepreciation objCreated = repository.save(obj);
        return objCreated;
    }

    @Override
    public void saveAllService(List<TaxDepreciation> listObj) {
        repository.saveAll(listObj);
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
