package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.repository.TaxDepreciationRepository;
import com.epsgrau.pe.sicodetri.service.TaxDepreciationService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing {@link TaxDepreciation} entities.
 * Provides transactional operations for creating, updating, deleting, and
 * retrieving tax depreciation records, as well as querying by related fixed asset ID.
 * <b>Class</b>: {@link TaxDepreciationServiceImpl}<br/>
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
public class TaxDepreciationServiceImpl implements TaxDepreciationService {

    @Autowired
    private TaxDepreciationRepository repository;

    /**
     * Saves a single {@link TaxDepreciation} entity.
     *
     * @param obj the TaxDepreciation to save
     * @return the saved TaxDepreciation instance
     */
    @Override
    @Transactional
    public TaxDepreciation saveService(TaxDepreciation obj) {
        TaxDepreciation objCreated = repository.save(obj);
        return objCreated;
    }

    /**
     * Saves a list of {@link TaxDepreciation} entities in batch.
     *
     * @param listObj the list of TaxDepreciation records to save
     */
    @Override
    @Transactional
    public void saveAllService(List<TaxDepreciation> listObj) {
        repository.saveAll(listObj);
    }

    /**
     * Updates an existing {@link TaxDepreciation} entity.
     *
     * @param obj the TaxDepreciation to update
     */
    @Override
    @Transactional
    public void updateService(TaxDepreciation obj) {
        repository.save(obj);
    }

    /**
     * Deletes the {@link TaxDepreciation} entity with the given identifier.
     *
     * @param id the ID of the TaxDepreciation to delete
     */
    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
        repository.flush();
    }

    /**
     * Deletes all provided {@link TaxDepreciation} entities.
     *
     * @param listObj the list of TaxDepreciation records to delete
     */
    @Override
    @Transactional
    public void deleteAllService(List<TaxDepreciation> listObj) {
        repository.deleteAll(listObj);
        repository.flush();
    }

    /**
     * Finds a {@link TaxDepreciation} by its identifier.
     *
     * @param id the ID of the TaxDepreciation to find
     * @return the found TaxDepreciation, or a new empty instance if not found
     */
    @Override
    @Transactional(readOnly = true)
    public TaxDepreciation findByIdService(Long id) {
        Optional<TaxDepreciation> optionalFixedAssetDepreciation = repository.findById(id);
        return optionalFixedAssetDepreciation.orElseGet(TaxDepreciation::new);
    }

    /**
     * Retrieves all {@link TaxDepreciation} entities.
     *
     * @return a list of all TaxDepreciation records
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaxDepreciation> findAllService() {
        return repository.findAll();
    }

    /**
     * Retrieves all {@link TaxDepreciation} entries associated with the specified
     * financial fixed asset ID.
     *
     * @param fixedAssetId the ID of the financial fixed asset
     * @return a list of matching TaxDepreciation entities; empty if none found
     */
    @Override
    public List<TaxDepreciation> findByFinancialFixedAssetIdService(Long fixedAssetId) {
        return repository.findByFinancialFixedAssetId(fixedAssetId);
    }
}
