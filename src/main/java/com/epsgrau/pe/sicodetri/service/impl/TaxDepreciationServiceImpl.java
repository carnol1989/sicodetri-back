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
 * Service implementation of {@link TaxDepreciationService} for managing
 * {@link TaxDepreciation} entities.
 *
 * <p>This component provides transactional create, update, delete, and read
 * operations and delegates the data-access workload to
 * {@link TaxDepreciationRepository}. It also exposes convenience queries to
 * retrieve tax depreciations by their owning fixed asset identifier.</p>
 *
 * <p><b>Transactional semantics</b>:
 * write methods are executed within a read-write transaction; pure read methods
 * are marked {@code readOnly=true} to optimize interaction with the persistence
 * provider.</p>
 *
 * <p><b>Thread-safety</b>: this service is stateless and designed to be used as
 * a Spring singleton.</p>
 *
 * <b>Class</b>: {@link TaxDepreciationServiceImpl}<br/>
 * <b>Company</b>: EPS Grau<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau
 *
 * @author EPS Grau
 * @version 1.0
 * @since 1.0
 */
@Service
public class TaxDepreciationServiceImpl implements TaxDepreciationService {

    @Autowired
    private TaxDepreciationRepository repository;

    /**
     * Persists a single {@link TaxDepreciation}.
     *
     * <p>Delegates to {@link TaxDepreciationRepository#save(Object)} and returns
     * the managed instance as persisted by the JPA provider.</p>
     *
     * @param obj the entity to persist (must not be {@code null})
     * @return the saved instance as returned by the repository (never {@code null})
     * @apiNote May propagate any {@code DataAccessException} thrown by the repository.
     */
    @Override
    @Transactional
    public TaxDepreciation saveService(TaxDepreciation obj) {
        TaxDepreciation objCreated = repository.save(obj);
        return objCreated;
    }

    /**
     * Persists a batch of {@link TaxDepreciation} entities.
     *
     * <p>Delegates to {@link TaxDepreciationRepository#saveAll(Iterable)}.
     * The method completes silently; callers can verify persistence by querying later.</p>
     *
     * @param listObj the list of entities to persist (must not be {@code null})
     */
    @Override
    @Transactional
    public void saveAllService(List<TaxDepreciation> listObj) {
        repository.saveAll(listObj);
    }

    /**
     * Updates an existing {@link TaxDepreciation}.
     *
     * <p>Functionally equivalent to {@link #saveService(TaxDepreciation)} as per
     * Spring Data JPA's upsert semantics.</p>
     *
     * @param obj the entity to update (must not be {@code null})
     */
    @Override
    @Transactional
    public void updateService(TaxDepreciation obj) {
        repository.save(obj);
    }

    /**
     * Deletes the {@link TaxDepreciation} identified by {@code id} and forces a flush.
     *
     * <p>Invokes {@link TaxDepreciationRepository#deleteById(Object)} followed by
     * {@link TaxDepreciationRepository#flush()} to synchronize the persistence context
     * with the database.</p>
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
     * Deletes all provided {@link TaxDepreciation} entities and forces a flush.
     *
     * <p>Invokes {@link TaxDepreciationRepository#deleteAll(Iterable)} followed by
     * {@link TaxDepreciationRepository#flush()} to ensure changes are persisted immediately.</p>
     *
     * @param listObj the collection of entities to delete (must not be {@code null})
     */
    @Override
    @Transactional
    public void deleteAllService(List<TaxDepreciation> listObj) {
        repository.deleteAll(listObj);
        repository.flush();
    }

    /**
     * Retrieves a {@link TaxDepreciation} by its identifier.
     *
     * <p>Returns a new, empty {@link TaxDepreciation} instance if no record exists
     * for the provided {@code id}. This avoids returning {@code null} to callers.</p>
     *
     * @param id the identifier of the entity to retrieve (must not be {@code null})
     * @return the found entity, or a new empty instance if none exists (never {@code null})
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
     * @return a list containing all entities; may be empty but never {@code null}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaxDepreciation> findAllService() {
        return repository.findAll();
    }

    /**
     * Retrieves all {@link TaxDepreciation} entries associated with the given
     * financial fixed asset identifier.
     *
     * <p>Delegates to {@link TaxDepreciationRepository#findByFinancialFixedAssetId(Long)}.</p>
     *
     * @param fixedAssetId the owning fixed asset ID (must not be {@code null})
     * @return a list of matching entities; may be empty but never {@code null}
     */
    @Override
    public List<TaxDepreciation> findByFinancialFixedAssetIdService(Long fixedAssetId) {
        return repository.findByFinancialFixedAssetId(fixedAssetId);
    }
}
