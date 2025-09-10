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
 * Concrete {@link AdditionService} implementation backed by a Spring Data JPA repository.
 * <p>
 * This service provides transactional operations to create, update, delete and retrieve
 * {@link Addition} entities, as well as lookups related to {@link TaxDepreciation} records.
 * The class is stateless and thread-safe from a Spring bean lifecycle perspective.
 * </p>
 *
 * <p><b>Transaction semantics</b>:</p>
 * <ul>
 *   <li>Write operations are executed within a required transaction and may flush explicitly
 *       where indicated to synchronize with the database immediately.</li>
 *   <li>Read operations are marked as {@code readOnly = true} where appropriate to avoid
 *       unnecessary persistence context overhead.</li>
 * </ul>
 *
 * <b>Class</b>: {@link AdditionServiceImpl}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <br/><u>Service Provider</u>: Consultor TI
 *     <br/><u>Developed by</u>:
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *       <li>Jun 06, 2025 (JAR) Class creation.</li>
 *     </ul>
 * @version 1.0
 * @since 1.0
 */
@Service
public class AdditionServiceImpl implements AdditionService {

    @Autowired
    private AdditionRepository repository;

    /**
     * Persists a single {@link Addition}.
     *
     * <p><b>Behavior</b>: Delegates to {@link AdditionRepository#save(Object)} and returns the
     * managed instance as stored by the persistence provider.</p>
     *
     * @param obj the entity to persist (must not be {@code null})
     * @return the saved and managed {@link Addition} instance (never {@code null})
     * @throws org.springframework.dao.DataAccessException if a persistence error occurs
     */
    @Override
    @Transactional
    public Addition saveService(Addition obj) {
        Addition objCreated = repository.save(obj);
        return objCreated;
    }

    /**
     * Persists a list of {@link Addition} entities in batch.
     *
     * <p><b>Behavior</b>: No-op for an empty list. The method does not return the persisted
     * instances; if you need the managed entities, re-query or use {@link #saveService(Addition)}
     * individually.</p>
     *
     * @param listObj the list of entities to persist (never {@code null}, may be empty)
     * @throws org.springframework.dao.DataAccessException if a persistence error occurs
     */
    @Override
    @Transactional
    public void saveAllService(List<Addition> listObj) {
        repository.saveAll(listObj);
    }

    /**
     * Updates an existing {@link Addition}.
     *
     * <p><b>Behavior</b>: Uses {@link AdditionRepository#save(Object)} which performs
     * insert-or-merge semantics depending on the entity identifier state. Callers are
     * responsible for ensuring the entity represents an update when expected.</p>
     *
     * @param obj the entity to update (must not be {@code null})
     * @throws org.springframework.dao.DataAccessException if a persistence error occurs
     */
    @Override
    @Transactional
    public void updateService(Addition obj) {
        repository.save(obj);
    }

    /**
     * Deletes an {@link Addition} by its identifier and forces an immediate flush.
     *
     * <p><b>Behavior</b>: Invokes {@link AdditionRepository#deleteById(Object)} followed by
     * {@link AdditionRepository#flush()} to synchronize the delete operation with the database
     * within the current transaction.</p>
     *
     * @param id the identifier of the entity to delete (must not be {@code null})
     * @throws org.springframework.dao.DataAccessException if a persistence error occurs
     */
    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
        repository.flush();
    }

    /**
     * Deletes all provided {@link Addition} entities and forces an immediate flush.
     *
     * <p><b>Behavior</b>: No-op for an empty list. Calls {@link AdditionRepository#deleteAll(Iterable)}
     * and then {@link AdditionRepository#flush()} to ensure the operation is propagated to the database
     * within the current transaction.</p>
     *
     * @param listObj the entities to delete (never {@code null}, may be empty)
     * @throws org.springframework.dao.DataAccessException if a persistence error occurs
     */
    @Override
    @Transactional
    public void deleteAllService(List<Addition> listObj) {
        repository.deleteAll(listObj);
        repository.flush();
    }

    /**
     * Retrieves an {@link Addition} by its identifier.
     *
     * <p><b>Behavior</b>: Returns the found entity or a new empty {@link Addition} instance if
     * none exists. This method never returns {@code null}.</p>
     *
     * @param id the identifier to search for (must not be {@code null})
     * @return the matching entity, or a new empty {@link Addition} if not found (never {@code null})
     * @throws org.springframework.dao.DataAccessException if a persistence error occurs
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
     * <p><b>Behavior</b>: Returns a possibly empty list; never {@code null}.</p>
     *
     * @return all additions managed by the repository (never {@code null})
     * @throws org.springframework.dao.DataAccessException if a persistence error occurs
     */
    @Override
    @Transactional(readOnly = true)
    public List<Addition> findAllService() {
        return repository.findAll();
    }

    /**
     * Finds all {@link Addition} entities associated with any of the given
     * {@link TaxDepreciation} records.
     *
     * <p><b>Behavior</b>: Returns a possibly empty list; never {@code null}. This
     * method does not modify entity state.</p>
     *
     * @param taxDepreciations the related tax depreciation entities to match against
     *                         (never {@code null}, may be empty)
     * @return the list of matching additions (never {@code null})
     * @throws org.springframework.dao.DataAccessException if a persistence error occurs
     */
    @Override
    public List<Addition> findByTaxDepreciationInService(List<TaxDepreciation> taxDepreciations) {
        return repository.findByTaxDepreciationIn(taxDepreciations);
    }
}
