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
 * This class provide the endpoints for Fixed Asset. <br/>
 * <b>Class</b>: {@link TestCleanupController}<br/>
 * <b>Copyright</b>: &Copy; 2024 EPS Grau. <br/>
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
    * Constructor for TestCleanupController.
    *
    * @param finDepRepo Layer for financial depreciation repository
    * @param taxDepRepo Layer for tax depreciation repository
    * @param additionRepo Layer for addition repository
    * @param deductionRepo Layer for deduction repository
    * @param assetRepo Layer for financial fixed asset repository
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
    * This method is used to clean up all test data in the database.
    *
    * @return ResponseEntity with status OK
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
