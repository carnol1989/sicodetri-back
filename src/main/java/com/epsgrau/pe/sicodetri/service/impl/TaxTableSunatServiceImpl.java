package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.repository.TaxTableSunatRepository;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the implementation of the service for Tax Table SUNAT. <br/>
 * <b>Class</b>: {@link TaxTableSunatServiceImpl}<br/>
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
 *     <li>Ago 15, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Service
public class TaxTableSunatServiceImpl implements TaxTableSunatService {

    @Autowired
    private TaxTableSunatRepository repository;

    @Override
    @Transactional
    public TaxTableSunat saveService(TaxTableSunat obj) {
        TaxTableSunat objCreated = repository.save(obj);
        return objCreated;
    }

    @Override
    public void saveAllService(List<TaxTableSunat> listObj) {
        repository.saveAll(listObj);
    }

    @Override
    @Transactional
    public void updateService(TaxTableSunat obj) {
        repository.save(obj);
    }

    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllService(List<TaxTableSunat> listObj) {
        repository.deleteAll(listObj);
    }

    @Override
    @Transactional(readOnly = true)
    public TaxTableSunat findByIdService(Long id) {
        Optional<TaxTableSunat> optionalTaxTableSunat = repository.findById(id);
        return optionalTaxTableSunat.orElseGet(TaxTableSunat::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaxTableSunat> findAllService() {
        List<TaxTableSunat> listTaxTableSunat = new ArrayList<>();
        repository.findAll().forEach(listTaxTableSunat::add);
        return listTaxTableSunat;
    }

    @Override
    @Transactional(readOnly = true)
    public TaxTableSunat findByAccountigAccountService(String accountingAccount) {
        return repository.findByAccountingAccount(accountingAccount);
    }
}
