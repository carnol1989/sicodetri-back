package com.epsgrau.pe.sicodetri.controller;

import com.epsgrau.pe.sicodetri.repository.*;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Profile("integration")
@RequestMapping("/api/v1/test")
public class TestCleanupController {

  private final FinancialDepreciationRepository finDepRepo;
  private final TaxDepreciationRepository taxDepRepo;
  private final AdditionRepository additionRepo;
  private final DeductionRepository deductionRepo;
  private final FinancialFixedAssetRepository assetRepo;

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
