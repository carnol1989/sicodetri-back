package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.repository.DeductionRepository;
import com.epsgrau.pe.sicodetri.service.DeductionService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing {@link Deduction} entities.
 * Provides transactional methods for creating, updating, deleting, and
 * retrieving deductions, as well as querying by related tax depreciation records.
 * <b>Class</b>: {@link DeductionServiceImpl}<br/>
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
public class DeductionServiceImpl implements DeductionService {

    @Autowired
    private DeductionRepository repository;

    /**
     * Saves a single {@link Deduction} entity.
     *
     * @param obj the Deduction to save
     * @return the saved Deduction instance
     */
    @Override
    @Transactional
    public Deduction saveService(Deduction obj) {
        Deduction objCreated = repository.save(obj);
        return objCreated;
    }

    /**
     * Saves a list of {@link Deduction} entities in batch.
     *
     * @param listObj the list of Deductions to save
     */
    @Override
    @Transactional
    public void saveAllService(List<Deduction> listObj) {
        repository.saveAll(listObj);
    }

    /**
     * Updates an existing {@link Deduction} entity.
     *
     * @param obj the Deduction to update
     */
    @Override
    @Transactional
    public void updateService(Deduction obj) {
        repository.save(obj);
    }

    /**
     * Deletes the {@link Deduction} entity with the given identifier.
     *
     * @param id the ID of the Deduction to delete
     */
    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
        repository.flush();
    }

    /**
     * Deletes all provided {@link Deduction} entities.
     *
     * @param listObj the list of Deductions to delete
     */
    @Override
    @Transactional
    public void deleteAllService(List<Deduction> listObj) {
        repository.deleteAll(listObj);
        repository.flush();
    }

    /**
     * Finds a {@link Deduction} by its identifier.
     *
     * @param id the ID of the Deduction to find
     * @return the found Deduction, or a new empty Deduction if not found
     */
    @Override
    @Transactional(readOnly = true)
    public Deduction findByIdService(Long id) {
        Optional<Deduction> optionalDeduction = repository.findById(id);
        return optionalDeduction.orElseGet(Deduction::new);
    }

    /**
     * Retrieves all {@link Deduction} entities.
     *
     * @return a list of all Deductions
     */
    @Override
    @Transactional(readOnly = true)
    public List<Deduction> findAllService() {
        return repository.findAll();
    }

    /**
     * Finds all {@link Deduction} entities associated with the given list of
     * {@link TaxDepreciation} records.
     *
     * @param taxDepreciations the list of TaxDepreciation entities to filter by
     * @return a list of matching Deductions
     */
    @Override
    public List<Deduction> findByTaxDepreciationInService(List<TaxDepreciation> taxDepreciations) {
        return repository.findByTaxDepreciationIn(taxDepreciations);
    }
}
