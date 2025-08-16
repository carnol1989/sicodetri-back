package com.epsgrau.pe.sicodetri.controller;

import com.epsgrau.pe.sicodetri.repository.AdditionRepository;
import com.epsgrau.pe.sicodetri.repository.DeductionRepository;
import com.epsgrau.pe.sicodetri.repository.FinancialDepreciationRepository;
import com.epsgrau.pe.sicodetri.repository.FinancialFixedAssetRepository;
import com.epsgrau.pe.sicodetri.repository.TaxDepreciationRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for cleaning up test data in integration tests.
 * Active only under the "integration" Spring profile. Exposes an endpoint
 * to delete all entries from the financial depreciation, tax depreciation,
 * additions, deductions, and financial fixed asset repositories, ensuring
 * a clean database state between test runs.
 * <b>Class</b>: {@link TestCleanupController}<br/>
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
 *     <li>Jul 08, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@RestController
@Profile("integration")
@RequestMapping("/api/v1/test")
public class TestCleanupController {

    private final FinancialDepreciationRepository finDepRepo;
    private final TaxDepreciationRepository taxDepRepo;
    private final AdditionRepository additionRepo;
    private final DeductionRepository deductionRepo;
    private final FinancialFixedAssetRepository assetRepo;

    /**
     * Constructs a new TestCleanupController with the given repository dependencies.
     *
     * @param finDepRepo   repository for financial depreciation entities
     * @param taxDepRepo   repository for tax depreciation entities
     * @param additionRepo repository for addition entities
     * @param deductionRepo repository for deduction entities
     * @param assetRepo    repository for financial fixed asset entities
    */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "Spring Data repositories are container-managed, "
            + "thread-safe and effectively immutable"
    )
    public TestCleanupController(
        FinancialDepreciationRepository finDepRepo,
        TaxDepreciationRepository taxDepRepo,
        AdditionRepository additionRepo,
        DeductionRepository deductionRepo,
        FinancialFixedAssetRepository assetRepo) {
        this.finDepRepo = finDepRepo;
        this.taxDepRepo = taxDepRepo;
        this.additionRepo = additionRepo;
        this.deductionRepo = deductionRepo;
        this.assetRepo = assetRepo;
    }

    /**
     * Deletes all test data from the configured repositories.
     * This endpoint should be invoked to reset the database state before or after
     * integration test execution.
     *
     * @return a {@link ResponseEntity} with HTTP status 200 (OK) upon successful cleanup
    */
    @PostMapping("/cleanup")
    public ResponseEntity<Void> cleanup() {
        finDepRepo.deleteAll();
        taxDepRepo.deleteAll();
        additionRepo.deleteAll();
        deductionRepo.deleteAll();
        assetRepo.deleteAll();
        return ResponseEntity.ok().build();
    }
}
