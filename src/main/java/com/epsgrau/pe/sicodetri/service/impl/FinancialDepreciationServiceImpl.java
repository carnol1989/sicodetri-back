package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.repository.FinancialDepreciationRepository;
import com.epsgrau.pe.sicodetri.service.FinancialDepreciationService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public FinancialDepreciation saveService(FinancialDepreciation obj) {
        FinancialDepreciation objCreated = repository.save(obj);
        return objCreated;
    }

    @Override
    @Transactional
    public void saveAllService(List<FinancialDepreciation> listObj) {
        repository.saveAll(listObj);
    }

    @Override
    @Transactional
    public void updateService(FinancialDepreciation obj) {
        repository.save(obj);
    }

    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllService(List<FinancialDepreciation> listObj) {
        repository.deleteAll(listObj);
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialDepreciation findByIdService(Long id) {
        Optional<FinancialDepreciation> optionalFinancialDepreciation = repository.findById(id);
        return optionalFinancialDepreciation.orElseGet(FinancialDepreciation::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialDepreciation> findAllService() {
        return repository.findAll();
    }

    @Override
    public List<FinancialDepreciation> findByFinancialFixedAssetIdService(Long fixedAssetId) {
        return repository.findByFinancialFixedAssetId(fixedAssetId);
    }
}
