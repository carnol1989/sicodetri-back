package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.repository.AdditionRepository;
import com.epsgrau.pe.sicodetri.service.AdditionService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing {@link Addition} entities.
 * Provides transactional methods for creating, updating, deleting, and
 * retrieving additions, as well as querying by related tax depreciation records.
 * <b>Class</b>: {@link AdditionServiceImpl}<br/>
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
public class AdditionServiceImpl implements AdditionService {

    @Autowired
    private AdditionRepository repository;

    /**
     * Saves a single {@link Addition} entity.
     *
     * @param obj the Addition to save
     * @return the saved Addition instance
     */
    @Override
    @Transactional
    public Addition saveService(Addition obj) {
        Addition objCreated = repository.save(obj);
        return objCreated;
    }

    /**
     * Saves a list of {@link Addition} entities in batch.
     *
     * @param listObj the list of Additions to save
     */
    @Override
    @Transactional
    public void saveAllService(List<Addition> listObj) {
        repository.saveAll(listObj);
    }

    /**
     * Updates an existing {@link Addition} entity.
     *
     * @param obj the Addition to update
     */
    @Override
    @Transactional
    public void updateService(Addition obj) {
        repository.save(obj);
    }

    /**
     * Deletes the {@link Addition} entity with the given identifier.
     *
     * @param id the ID of the Addition to delete
     */
    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
    }

    /**
     * Deletes all provided {@link Addition} entities.
     *
     * @param listObj the list of Additions to delete
     */
    @Override
    @Transactional
    public void deleteAllService(List<Addition> listObj) {
        repository.deleteAll(listObj);
    }

    /**
     * Finds an {@link Addition} by its identifier.
     *
     * @param id the ID of the Addition to find
     * @return the found Addition, or a new empty Addition if not found
     */
    @Override
    @Transactional(readOnly = true)
    public Addition findByIdService(Long id) {
        Optional<Addition> optionalAddition = repository.findById(id);
        return optionalAddition.orElseGet(Addition::new);
    }

    /**
     * Retrieves all {@link Addition} entities.
     *
     * @return a list of all Additions
     */
    @Override
    @Transactional(readOnly = true)
    public List<Addition> findAllService() {
        return repository.findAll();
    }

    /**
     * Finds all {@link Addition} entities associated with the given list of
     * {@link TaxDepreciation} records.
     *
     * @param taxDepreciations the list of TaxDepreciation entities to filter by
     * @return a list of matching Additions
     */
    @Override
    public List<Addition> findByTaxDepreciationInService(List<TaxDepreciation> taxDepreciations) {
        return repository.findByTaxDepreciationIn(taxDepreciations);
    }
}
