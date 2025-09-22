package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.repository.TaxTableSunatRepository;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing {@link TaxTableSunat} entities.
 * Provides transactional methods for creating, updating, deleting, and
 * retrieving tax table entries, as well as querying by accounting account.
 * <b>Class</b>: {@link TaxTableSunatServiceImpl}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Service
public class TaxTableSunatServiceImpl implements TaxTableSunatService {

    @Autowired
    private TaxTableSunatRepository repository;

    /**
     * Saves a single {@link TaxTableSunat} entity.
     *
     * @param obj the TaxTableSunat to save
     * @return the saved TaxTableSunat instance
     */
    @Override
    @Transactional
    public TaxTableSunat saveService(TaxTableSunat obj) {
        TaxTableSunat objCreated = repository.save(obj);
        return objCreated;
    }

    /**
     * Saves a list of {@link TaxTableSunat} entities in batch.
     *
     * @param listObj the list of TaxTableSunat records to save
     */
    @Override
    public void saveAllService(List<TaxTableSunat> listObj) {
        repository.saveAll(listObj);
    }

    /**
     * Updates an existing {@link TaxTableSunat} entity.
     *
     * @param obj the TaxTableSunat to update
     */
    @Override
    @Transactional
    public void updateService(TaxTableSunat obj) {
        repository.save(obj);
    }

    /**
     * Deletes the {@link TaxTableSunat} entity with the given identifier.
     *
     * @param id the ID of the TaxTableSunat to delete
     */
    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    /**
     * Deletes all provided {@link TaxTableSunat} entities.
     *
     * @param listObj the list of TaxTableSunat records to delete
     */
    @Override
    @Transactional
    public void deleteAllService(List<TaxTableSunat> listObj) {
        repository.deleteAll(listObj);
    }

    /**
     * Finds a {@link TaxTableSunat} by its identifier.
     *
     * @param id the ID of the TaxTableSunat to find
     * @return the found TaxTableSunat, or a new empty instance if not found
     */
    @Override
    @Transactional(readOnly = true)
    public TaxTableSunat findByIdService(Long id) {
        Optional<TaxTableSunat> optionalTaxTableSunat = repository.findById(id);
        return optionalTaxTableSunat.orElseGet(TaxTableSunat::new);
    }

    /**
     * Retrieves all {@link TaxTableSunat} entities.
     *
     * @return a list of all TaxTableSunat records
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaxTableSunat> findAllService() {
        List<TaxTableSunat> listTaxTableSunat = new ArrayList<>();
        repository.findAll().forEach(listTaxTableSunat::add);
        return listTaxTableSunat;
    }

    @Override
    public List<TaxTableSunat> findAll() {
        return repository.findAll(Sort.by("accountingAccount").ascending());
    }

    @Override
    public List<TaxTableSunat> findByAccountingAccountIn(Collection<String> accounts) {
        return repository.findByAccountingAccountIn(accounts);
    }

    /**
     * Retrieves the {@link TaxTableSunat} entry matching the given accounting account code.
     *
     * @param accountingAccount the accounting account code to search for
     * @return the matching TaxTableSunat entity, or {@code null} if none found
     */
    @Override
    @Transactional(readOnly = true)
    public TaxTableSunat findByAccountigAccountService(String accountingAccount) {
        return repository.findByAccountingAccount(accountingAccount);
    }
}
