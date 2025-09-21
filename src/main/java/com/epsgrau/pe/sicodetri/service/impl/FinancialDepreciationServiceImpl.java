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
 *
 * <p>This component encapsulates the application-facing persistence operations for
 * financial depreciation records and delegates the actual data access to
 * {@link FinancialDepreciationRepository}. Transactional boundaries are declared at
 * method level to ensure atomicity and to clearly express read-only vs. mutating
 * behavior.</p>
 *
 * <p><b>Responsibilities</b></p>
 * <ul>
 *   <li>Create and update financial depreciation entries</li>
 *   <li>Delete records individually or in batch (with explicit flush to persist side effects)</li>
 *   <li>Retrieve records by identifier, by owning fixed asset, or in bulk</li>
 * </ul>
 *
 * <p><b>Thread-safety</b>: This class is stateless and therefore thread-safe; Spring manages its
 * lifecycle as a singleton bean.</p>
 *
 * <b>Class</b>: {@link FinancialDepreciationServiceImpl}<br/>
 * <b>Company</b>: EPS Grau<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau
 *
 * @author EPS Grau
 * @since 1.0
 * @version 1.0
 */
@Service
public class FinancialDepreciationServiceImpl implements FinancialDepreciationService {

    @Autowired
    private FinancialDepreciationRepository repository;

    /**
     * Persists a single {@link FinancialDepreciation} entity.
     *
     * <p><b>Transactional</b>: write. The returned instance reflects the persistent state
     * after the save operation.</p>
     *
     * @param obj the entity to persist (must not be {@code null})
     * @return the saved entity (managed instance)
     */
    @Override
    @Transactional
    public FinancialDepreciation saveService(FinancialDepreciation obj) {
        FinancialDepreciation objCreated = repository.save(obj);
        return objCreated;
    }

    /**
     * Persists a collection of {@link FinancialDepreciation} entities in batch.
     *
     * <p><b>Transactional</b>: write.</p>
     *
     * @param listObj the entities to persist; empty lists are allowed
     */
    @Override
    @Transactional
    public void saveAllService(List<FinancialDepreciation> listObj) {
        repository.saveAll(listObj);
    }

    /**
     * Updates an existing {@link FinancialDepreciation} entity.
     *
     * <p>Semantically equivalent to {@link #saveService(FinancialDepreciation)}; the presence
     * of an identifier on {@code obj} will dictate whether an insert or update occurs.</p>
     *
     * <p><b>Transactional</b>: write.</p>
     *
     * @param obj the entity to update (must not be {@code null})
     */
    @Override
    @Transactional
    public void updateService(FinancialDepreciation obj) {
        repository.save(obj);
    }

    /**
     * Deletes a {@link FinancialDepreciation} by its identifier and explicitly flushes
     * the persistence context to enforce immediate synchronization with the database.
     *
     * <p><b>Transactional</b>: write.</p>
     *
     * @param id the identifier of the entity to delete (must not be {@code null})
     */
    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
        repository.flush();
    }

    /**
     * Deletes the provided {@link FinancialDepreciation} entities and explicitly flushes
     * the persistence context.
     *
     * <p><b>Transactional</b>: write.</p>
     *
     * @param listObj the entities to delete; empty lists are allowed
     */
    @Override
    @Transactional
    public void deleteAllService(List<FinancialDepreciation> listObj) {
        repository.deleteAll(listObj);
        repository.flush();
    }

    /**
     * Retrieves a {@link FinancialDepreciation} by its identifier.
     *
     * <p><b>Transactional</b>: read-only.</p>
     *
     * @param id the identifier to search for (must not be {@code null})
     * @return the matching entity; if not found, a new empty {@link FinancialDepreciation}
     *         instance is returned (never {@code null})
     */
    @Override
    @Transactional(readOnly = true)
    public FinancialDepreciation findByIdService(Long id) {
        Optional<FinancialDepreciation> optionalFinancialDepreciation = repository.findById(id);
        return optionalFinancialDepreciation.orElseGet(FinancialDepreciation::new);
    }

    /**
     * Retrieves all {@link FinancialDepreciation} records.
     *
     * <p><b>Transactional</b>: read-only.</p>
     *
     * @return a list of all entities (never {@code null}; may be empty)
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialDepreciation> findAllService() {
        return repository.findAll();
    }

    /**
     * Retrieves all {@link FinancialDepreciation} entries associated with a given
     * financial fixed asset.
     *
     * <p><b>Transactional</b>: read-only.</p>
     *
     * @param fixedAssetId the owning financial fixed asset ID (must not be {@code null})
     * @return a list of matching entities (never {@code null}; may be empty)
     */
    @Override
    public List<FinancialDepreciation> findByFinancialFixedAssetIdService(Long fixedAssetId) {
        return repository.findByFinancialFixedAssetId(fixedAssetId);
    }
}
