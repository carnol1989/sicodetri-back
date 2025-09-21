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
 * Concrete {@link DeductionService} implementation.
 *
 * <p><b>Purpose</b>:
 * Orchestrates CRUD operations and lookups for {@link Deduction} entities,
 * delegating persistence concerns to {@link DeductionRepository}. It also
 * provides queries scoped by related {@link TaxDepreciation} records.</p>
 *
 * <p><b>Behavior &amp; transactionality</b>:</p>
 * <ul>
 *   <li>Write methods are wrapped in {@code @Transactional} to ensure atomicity.</li>
 *   <li>Read-only methods use {@code @Transactional(readOnly = true)} for optimal performance.</li>
 *   <li>Bulk delete operations call {@code flush()} to enforce immediate synchronization with the database.</li>
 * </ul>
 *
 * <p><b>Thread-safety</b>:
 * This service is stateless; Spring manages its lifecycle. Repository interactions are thread-safe
 * under the default Spring Data JPA transaction model.</p>
 *
 * <b>Class</b>: {@link DeductionServiceImpl}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <br/><u>Service Provider</u>: Consultor TI
 *     <br/><u>Developed by</u>:
 *     <ul>
 *     <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *     <li>Jun 06, 2025 (JAR) Class creation.</li>
 *     </ul>
 * @version 1.0
 * @since 1.0
 */
@Service
public class DeductionServiceImpl implements DeductionService {

    @Autowired
    private DeductionRepository repository;

    /**
     * Persists the given {@link Deduction}.
     *
     * @param obj the deduction to persist (must not be {@code null})
     * @return the managed instance returned by the repository (never {@code null})
     */
    @Override
    @Transactional
    public Deduction saveService(Deduction obj) {
        Deduction objCreated = repository.save(obj);
        return objCreated;
    }

    /**
     * Persists a list of {@link Deduction} entities in batch.
     *
     * @param listObj the list of deductions to persist (must not be {@code null})
     */
    @Override
    @Transactional
    public void saveAllService(List<Deduction> listObj) {
        repository.saveAll(listObj);
    }

    /**
     * Updates an existing {@link Deduction}.
     * <p>Semantically identical to {@link #saveService(Deduction)} for JPA; if the entity is
     * detached, it will be merged.</p>
     *
     * @param obj the deduction to update (must not be {@code null})
     */
    @Override
    @Transactional
    public void updateService(Deduction obj) {
        repository.save(obj);
    }

    /**
     * Deletes a {@link Deduction} by its identifier and flushes the persistence context.
     *
     * @param id the identifier of the deduction to delete (must not be {@code null})
     */
    @Override
    @Transactional
    public void deleteByIdService(Long id) {
        repository.deleteById(id);
        repository.flush();
    }

    /**
     * Deletes all provided {@link Deduction} entities and flushes the persistence context.
     *
     * @param listObj the deductions to delete (must not be {@code null})
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
     * @param id the identifier to search for (must not be {@code null})
     * @return the found deduction; if none exists, a new empty {@link Deduction} instance
     *         is returned (never {@code null})
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
     * @return a list of deductions (never {@code null}, may be empty)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Deduction> findAllService() {
        return repository.findAll();
    }

    /**
     * Retrieves deductions associated with any of the provided {@link TaxDepreciation} records.
     *
     * @param taxDepreciations the related tax depreciations to filter by (must not be {@code null})
     * @return a list of matching deductions (never {@code null}, may be empty)
     */
    @Override
    public List<Deduction> findByTaxDepreciationInService(List<TaxDepreciation> taxDepreciations) {
        return repository.findByTaxDepreciationIn(taxDepreciations);
    }
}
