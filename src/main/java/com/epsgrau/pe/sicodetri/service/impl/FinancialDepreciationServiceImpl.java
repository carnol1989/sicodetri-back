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
 * Service implementation for managing {@link FinancialDepreciation} entities.
 * Provides transactional methods to create, update, delete, and retrieve financial
 * depreciation records, as well as to query by associated fixed asset ID.
 * <b>Class</b>: {@link FinancialDepreciationServiceImpl}<br/>
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
public class FinancialDepreciationServiceImpl implements FinancialDepreciationService {

    @Autowired
    private FinancialDepreciationRepository repository;

    /**
     * Saves a single {@link FinancialDepreciation} entity.
     *
     * @param obj the FinancialDepreciation to save
     * @return the saved FinancialDepreciation instance
     */
    @Override
    @Transactional
    public FinancialDepreciation saveService(FinancialDepreciation obj) {
        FinancialDepreciation objCreated = repository.save(obj);
        return objCreated;
    }

    /**
     * Saves a list of {@link FinancialDepreciation} entities in batch.
     *
     * @param listObj the list of FinancialDepreciation records to save
     */
    @Override
    @Transactional
    public void saveAllService(List<FinancialDepreciation> listObj) {
        repository.saveAll(listObj);
    }

    /**
     * Updates an existing {@link FinancialDepreciation} entity.
     *
     * @param obj the FinancialDepreciation to update
     */
    @Override
    @Transactional
    public void updateService(FinancialDepreciation obj) {
        repository.save(obj);
    }

    /**
     * Deletes the {@link FinancialDepreciation} entity with the given identifier.
     *
     * @param id the ID of the FinancialDepreciation to delete
     */
    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
        repository.flush();
    }

    /**
     * Deletes all provided {@link FinancialDepreciation} entities.
     *
     * @param listObj the list of FinancialDepreciation records to delete
     */
    @Override
    @Transactional
    public void deleteAllService(List<FinancialDepreciation> listObj) {
        repository.deleteAll(listObj);
        repository.flush();
    }

    /**
     * Finds a {@link FinancialDepreciation} by its identifier.
     *
     * @param id the ID of the FinancialDepreciation to find
     * @return the found FinancialDepreciation, or a new empty instance if not found
     */
    @Override
    @Transactional(readOnly = true)
    public FinancialDepreciation findByIdService(Long id) {
        Optional<FinancialDepreciation> optionalFinancialDepreciation = repository.findById(id);
        return optionalFinancialDepreciation.orElseGet(FinancialDepreciation::new);
    }

    /**
     * Retrieves all {@link FinancialDepreciation} entities.
     *
     * @return a list of all FinancialDepreciation records
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialDepreciation> findAllService() {
        return repository.findAll();
    }

    /**
     * Retrieves all {@link FinancialDepreciation} entries associated with the specified
     * financial fixed asset ID.
     *
     * @param fixedAssetId the ID of the financial fixed asset
     * @return a list of matching FinancialDepreciation entities; empty if none found
     */
    @Override
    public List<FinancialDepreciation> findByFinancialFixedAssetIdService(Long fixedAssetId) {
        return repository.findByFinancialFixedAssetId(fixedAssetId);
    }
}
