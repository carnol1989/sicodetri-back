package com.epsgrau.pe.sicodetri.service.projection.impl;

import static java.util.Objects.requireNonNull;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.model.Projection;
import com.epsgrau.pe.sicodetri.repository.AdditionRepository;
import com.epsgrau.pe.sicodetri.repository.DeductionRepository;
import com.epsgrau.pe.sicodetri.repository.FinancialDepreciationRepository;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetRepository;
import com.epsgrau.pe.sicodetri.repository.TaxDepreciationRepository;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionGeneratorResolver;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionJobService;
import com.epsgrau.pe.sicodetri.service.tax.TaxTableCatalog;
import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

/**
 * Default implementation following SOLID and clean code practices.
 *
 * <p>SRP: Orchestrates chunked processing (no business rules).
 * <br>DIP/OCP: Delegates to {@link ProjectionGenerator} resolved by {@link ProjectionGeneratorResolver}.
 */
@Service
public class DefaultProjectionJobService implements ProjectionJobService {

    private static final Logger log = LoggerFactory.getLogger(DefaultProjectionJobService.class);

    private final FinancialFixedAssetRepository assetRepo;
    private final FinancialDepreciationRepository finDepRepo;
    private final TaxDepreciationRepository taxDepRepo;
    private final AdditionRepository additionRepo;
    private final DeductionRepository deductionRepo;
    private final TaxTableSunatService taxService;
    private final ProjectionGeneratorResolver resolver;

    @PersistenceContext
    private EntityManager entityManager;

    /**
    * Constructor.
    *
    * @param assetRepo FinancialFixedAssetRepository
    * @param finDepRepo FinancialDepreciationRepository
    * @param taxDepRepo TaxDepreciationRepository
    * @param additionRepo AdditionRepository
    * @param deductionRepo DeductionRepository
    * @param taxService TaxTableSunatService
    * @param resolver ProjectionGeneratorResolver
    */
    public DefaultProjectionJobService(
        FinancialFixedAssetRepository assetRepo,
        FinancialDepreciationRepository finDepRepo,
        TaxDepreciationRepository taxDepRepo,
        AdditionRepository additionRepo,
        DeductionRepository deductionRepo,
        TaxTableSunatService taxService,
        ProjectionGeneratorResolver resolver) {
        this.assetRepo = requireNonNull(assetRepo, "assetRepo");
        this.finDepRepo = requireNonNull(finDepRepo, "finDepRepo");
        this.taxDepRepo = requireNonNull(taxDepRepo, "taxDepRepo");
        this.additionRepo = requireNonNull(additionRepo, "additionRepo");
        this.deductionRepo = requireNonNull(deductionRepo, "deductionRepo");
        this.taxService = requireNonNull(taxService, "taxService");
        this.resolver = requireNonNull(resolver, "resolver");
    }

    @Override
    public ProjectionJobSummary generate(String username, LocalDate period, ProjectionType type, int pageSize) {
        validateInputs(period, type, pageSize);
        log.info("[INICIO][generate]");

        final ProjectionGenerator generator = resolver.get(type);

        final TaxTableCatalog taxCatalog = preloadTaxParams(type);

        final StopWatch jobWatch = new StopWatch("projection-" + type);
        jobWatch.start("total");

        Pageable page = PageRequest.of(0, pageSize, Sort.by("id").ascending());
        Page<FinancialFixedAsset> slice;

        long assets = 0;
        long deletes = 0;
        long finIns = 0;
        long taxIns = 0;
        long addIns = 0;
        long dedIns = 0;
        int pageNo = 0;

        do {
            slice = assetRepo.findAll(page);
            final List<FinancialFixedAsset> chunk = slice.getContent();

            if (!chunk.isEmpty()) {
                final ChunkResult result = processChunk(username, period, type, generator, taxCatalog, chunk);
                assets += chunk.size();
                deletes += result.deletes();
                finIns += result.finDepInserted();
                taxIns += result.taxDepInserted();
                addIns += result.additionsInserted();
                dedIns += result.deductionsInserted();

                log.info("[{}] Chunk #{} -> size={}, deletes={}, finIns={}, taxIns={}, addIns={}, dedIns={}, took={}",
                    type, pageNo, result.size(), result.deletes(), result.finDepInserted(),
                    result.taxDepInserted(), result.additionsInserted(), result.deductionsInserted(),
                    result.duration());
            }
            page = page.next();
            pageNo++;
        } while (!slice.isEmpty());

        jobWatch.stop();
        final Duration took = Duration.ofMillis(jobWatch.getTotalTimeMillis());

        log.info("[{}] Done. assets={}, deletes={}, finIns={}, taxIns={}, addIns={}, dedIns={}, took={}",
            type, assets, deletes, finIns, taxIns, addIns, dedIns, took);

        return new ProjectionJobSummary(type, period, username, assets, deletes, finIns, taxIns, addIns, dedIns, took);
    }

    /**
     * Processes one chunk within its own transaction.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected ChunkResult processChunk(
        String username,
        LocalDate period,
        ProjectionType type,
        ProjectionGenerator generator,
        TaxTableCatalog taxCatalog,
        List<FinancialFixedAsset> assets
    ) {
        log.info("[INICIO][processChunk]");
        final long t0 = System.nanoTime();

        long deletes = 0;
        long finIns = 0;
        long taxIns = 0;
        long addIns = 0;
        long dedIns = 0;

        for (FinancialFixedAsset ffa : assets) {
            deletes += call(() -> deductionRepo.deleteByFfaId(ffa.getId()));
            deletes += call(() -> additionRepo.deleteByFfaId(ffa.getId()));
            deletes += call(() -> taxDepRepo.deleteByFfaId(ffa.getId()));
            deletes += call(() -> finDepRepo.deleteByFfaId(ffa.getId()));

            final Projection p = generator.generate(username, period, ffa, taxCatalog);

            if (!p.getFinancialDepreciationList().isEmpty()) {
                finDepRepo.saveAll(p.getFinancialDepreciationList());
                finIns += p.getFinancialDepreciationList().size();
            }
            if (!p.getTaxDepreciationList().isEmpty()) {
                taxDepRepo.saveAll(p.getTaxDepreciationList());
                taxIns += p.getTaxDepreciationList().size();
            }
            if (!p.getAdditionList().isEmpty()) {
                additionRepo.saveAll(p.getAdditionList());
                addIns += p.getAdditionList().size();
            }
            if (!p.getDeductionList().isEmpty()) {
                deductionRepo.saveAll(p.getDeductionList());
                dedIns += p.getDeductionList().size();
            }
        }

        entityManager.clear();

        final Duration took = Duration.ofNanos(System.nanoTime() - t0);
        return new ChunkResult(assets.size(), deletes, finIns, taxIns, addIns, dedIns, took);
    }

    private static void validateInputs(LocalDate period, ProjectionType type, int pageSize) {
        if (period == null) {
            throw new IllegalArgumentException("period must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be > 0");
        }
    }

    private TaxTableCatalog preloadTaxParams(ProjectionType type) {
        log.info("[INICIO][preloadTaxParams]");
        final Set<String> allowed = allowedAccountsFor(type);
        final List<TaxTableSunat> rows = allowed.isEmpty()
            ? List.of()
            : taxService.findByAccountingAccountIn(allowed);

        return TaxTableCatalog.of(rows);
    }

    /**
     * Retorna el conjunto de cuentas contables permitidas según el tipo de proyección.
     */
    private static Set<String> allowedAccountsFor(ProjectionType type) {
        log.info("[INICIO][allowedAccountsFor]");
        return switch (type) {
          case TEMPORARY_DEDUCTIONS, PERMANENT_ADDITIONS -> Set.of("332");
          case TEMPORARY_ADDITIONS -> Set.of("332", "333", "334", "335", "336", "337");
        };
    }

    /**
     * Some JPA providers return void on bulk operations; guard with try/catch and normalize to long.
     */
    private static long call(LongSupplier write) {
        try {
            return Math.max(0L, write.getAsLong());
        } catch (Exception ignored) {
            return 0L;
        }
    }
}