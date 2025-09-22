package com.epsgrau.pe.sicodetri.service.projection;

import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import java.time.Duration;
import java.time.LocalDate;

/**
 * High-level batch projection service (chunk-based).
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Paginate assets into manageable chunks.</li>
 *   <li>Open 1 transaction per chunk (short, resilient).</li>
 *   <li>Preload tax parameters once and pass them to pure generators.</li>
 *   <li>Bulk-delete previous rows and batch-insert new ones.</li>
 * </ul>
 */
public interface ProjectionJobService {

    /**
     * Runs a full projection job over all Financial Fixed Assets using a paged-chunk approach.
     *
     * @param username who initiated the job (for audit/log)
     * @param period   accounting period used by generators
     * @param type     projection type (strategy)
     * @param pageSize number of assets per chunk (typical: 500–2000)
     * @return a summary of the job with global counters and timing
     * @throws IllegalArgumentException if input is invalid or strategy is missing
     */
    ProjectionJobSummary generate(String username, LocalDate period, ProjectionType type, int pageSize);

    /**
     * Immutable summary returned by {@link #generate(String, LocalDate, ProjectionType, int)}.
     */
    record ProjectionJobSummary(
        ProjectionType type,
        LocalDate period,
        String requestedBy,
        long totalAssets,
        long totalDeletes,
        long financialDepInserted,
        long taxDepInserted,
        long additionsInserted,
        long deductionsInserted,
        Duration duration
    ) {}

    /**
     * Immutable per-chunk result used internally for aggregation and logging.
     */
    record ChunkResult(
        int size,
        long deletes,
        long finDepInserted,
        long taxDepInserted,
        long additionsInserted,
        long deductionsInserted,
        Duration duration
    ) {}
}
