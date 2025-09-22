package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_01;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_03;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.model.Projection;
import com.epsgrau.pe.sicodetri.service.AdditionService;
import com.epsgrau.pe.sicodetri.service.DeductionService;
import com.epsgrau.pe.sicodetri.service.FinancialDepreciationService;
import com.epsgrau.pe.sicodetri.service.TaxDepreciationService;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionGenerator;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract base for depreciation projection engines.
 *
 * <p><b>Purpose</b>:
 * Centralizes common month-by-month projection mechanics used by concrete
 * generators (e.g., temporary additions/deductions). It orchestrates the
 * calculation of financial and tax depreciation entries and decides whether
 * to register an {@link Addition} or a {@link Deduction} per period.</p>
 *
 * <p><b>How it works</b>:</p>
 * <ol>
 *   <li>Iterates over 12 monthly periods starting from the {@code Projection}'s
 *       current {@code accountigPeriodNext}.</li>
 *   <li>For each month, computes a {@link FinancialDepreciation} and a
 *       {@link TaxDepreciation} using the provided asset and SUNAT tax table.</li>
 *   <li>Compares monthly tax vs. financial depreciation and creates either an
 *       {@code Addition} or a {@code Deduction}, according to business rules
 *       and projection type (see notes below).</li>
 *   <li>Advances {@code accountigPeriodNext} month by month and updates the
 *       running {@code TaxDepreciation} state in the {@code Projection}.</li>
 * </ol>
 *
 * <p><b>Decision rules (summary)</b>:</p>
 * <ul>
 *   <li>If tax &gt; financial and current tax net value &gt; 0, or if prior tax
 *       net value &gt; 0 and the current net value falls to 0, a {@link Deduction}
 *       is recorded for the delta (tax − financial).</li>
 *   <li>Otherwise, an {@link Addition} is recorded. For types {@link com.epsgrau.pe.sicodetri.util.Constants#S_VAL_01}
 *       or {@link com.epsgrau.pe.sicodetri.util.Constants#S_VAL_03}, the addition uses the financial depreciation; for
 *       other types, the addition uses the tax depreciation up to the asset's
 *       useful life in years; beyond that, the amount is zero.</li>
 * </ul>
 *
 * <p><b>Scope &amp; lifecycle</b>:
 * This abstract class is not a Spring bean by itself; it relies on
 * autowired services provided by concrete subclasses. The method herein
 * mutates the passed {@link Projection} (lists and cursors). Callers are
 * expected to persist any produced entities via services at a higher layer.</p>
 *
 * <p><b>Thread-safety</b>:
 * Instances rely on Spring-managed, typically thread-safe services. The
 * projection method is not synchronized and modifies the provided
 * {@link Projection}; callers must ensure appropriate concurrency control.</p>
 *
 * <b>Class</b>: {@link AbstractProjectionGenerator}<br/>
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
 *     <li>Jun 10, 2025 (JAR) Class creation.</li>
 *     </ul>
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractProjectionGenerator implements ProjectionGenerator {

    /** High precision for financial intermediate ops; round at write boundaries. */
    protected static final MathContext MC = new MathContext(34, RoundingMode.HALF_UP);

    /** Typical scales; adjust if your domain requires different decimals. */
    protected static final int SCALE_MONEY = 2;
    protected static final int SCALE_RATE = 10;

    protected static final BigDecimal ZERO = BigDecimal.ZERO;
    protected static final BigDecimal ONE = BigDecimal.ONE;
    protected static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    protected static final BigDecimal TWELVE = new BigDecimal("12");

    /**
     * Final template method that validates/normalizes inputs and delegates to {@link #doGenerate}.
     *
     * <p>Subclasses must implement the pure calculation in {@code doGenerate}, returning a {@link Projection}
     * with pre-populated lists. No repository access inside this method or {@code doGenerate}.</p>
     */
    @Override
    public final Projection generate(
        String username,
        LocalDate period,
        FinancialFixedAsset asset,
        Map<String, TaxTableSunat> taxParams) {

        Objects.requireNonNull(asset, "asset");
        Objects.requireNonNull(taxParams, "taxParams");
        Objects.requireNonNull(period, "period");

        final LocalDate normalizedPeriod = firstDayOfMonth(period);
        final String safeUser = safeUsername(username);

        return doGenerate(safeUser, normalizedPeriod, asset, taxParams);
    }

    /**
     * Concrete pure computation. Implement in subclasses.
     *
     * @param username   who started the job (never null/blank; normalized to "ANONYMOUS" if absent)
     * @param period     normalized to first day of its month
     * @param asset      asset to project (non-null)
     * @param taxParams  preloaded map of SUNAT params (index by your stable key)
     * @return projection with populated lists (financial/tax/additions/deductions)
     */
    protected abstract Projection doGenerate(
        String username,
        LocalDate period,
        FinancialFixedAsset asset,
        Map<String, TaxTableSunat> taxParams);

    /** Ensures a non-null username for audit/log; avoids null downstream. */
    protected String safeUsername(String username) {
        return (username == null || username.isBlank()) ? "ANONYMOUS" : username;
    }

    /** Returns the first day of the month for the given date; never null. */
    protected LocalDate firstDayOfMonth(LocalDate date) {
        return YearMonth.from(date).atDay(1);
    }

    /** Returns the last day of the month for the given date. */
    protected LocalDate endOfMonth(LocalDate anyDay) {
        return YearMonth.from(anyDay).atEndOfMonth();
    }

    /** Looks up a tax row by key; throws if not found to fail fast and keep data consistent. */
    protected TaxTableSunat requireTax(String key, Map<String, TaxTableSunat> map) {
        Objects.requireNonNull(key, "key");
        final TaxTableSunat t = map.get(key);
        if (t == null) {
            throw new IllegalArgumentException("Missing TaxTableSunat for key: " + key);
        }
        return t;
    }

    /** Inclusive month count between two dates, normalized to YearMonth. */
    protected int monthsInclusive(LocalDate start, LocalDate end) {
        final YearMonth s = YearMonth.from(start);
        final YearMonth e = YearMonth.from(end);
        final long diff = ChronoUnit.MONTHS.between(s, e);
        return (int) diff + 1;
    }

    /** Next month's first day. */
    protected LocalDate nextMonth(LocalDate date) {
        return YearMonth.from(date).plusMonths(1).atDay(1);
    }

    /** Previous month's first day. */
    protected LocalDate prevMonth(LocalDate date) {
        return YearMonth.from(date).minusMonths(1).atDay(1);
    }

    /**
     * Guards against drift by forcing a month boundary loop:
     * <pre>
     * for (LocalDate m = start; !m.isAfter(end); m = nextMonth(m)) { ... }
     * </pre>
     */
    protected void requireMonthLoopInvariant(LocalDate start, LocalDate end) {
        if (!start.equals(firstDayOfMonth(start))) {
            throw new IllegalStateException("Loop start must be first day of month: " + start);
        }
        if (!YearMonth.from(end).equals(YearMonth.from(endOfMonth(end)))) {
            throw new IllegalStateException("Loop end must be within a valid month: " + end);
        }
    }

    /** Converts a percentage (e.g., 20) to decimal (0.20). Accepts null as 0. */
    protected BigDecimal toDecimalRate(BigDecimal percent) {
        if (percent == null || percent.signum() == 0) return ZERO;
        return percent.divide(ONE_HUNDRED, SCALE_RATE, RoundingMode.HALF_UP);
    }

    /** Converts annual rate (decimal) to monthly (simple division by 12; adapt if compound is needed). */
    protected BigDecimal annualToMonthly(BigDecimal annualRateDecimal) {
        if (annualRateDecimal == null || annualRateDecimal.signum() == 0) return ZERO;
        return annualRateDecimal.divide(TWELVE, SCALE_RATE, RoundingMode.HALF_UP);
    }

    /** Rounds a monetary value to {@link #SCALE_MONEY}. Null becomes 0.00. */
    protected BigDecimal roundMoney(BigDecimal value) {
        if (value == null) return ZERO.setScale(SCALE_MONEY, RoundingMode.HALF_UP);
        return value.setScale(SCALE_MONEY, RoundingMode.HALF_UP);
    }

    /** Returns value if positive, otherwise 0 (helps avoid tiny negatives after rounding). */
    protected BigDecimal nonNegative(BigDecimal value) {
        if (value == null) return ZERO;
        return value.signum() < 0 ? ZERO : value;
    }

    /** True if value is null or ≤ 0. */
    protected boolean isZeroOrNegative(BigDecimal value) {
        return value == null || value.signum() <= 0;
    }

    /** Suggests stopping when remaining net value is effectively zero/non-positive. */
    protected boolean shouldStop(BigDecimal remainingNetValue) {
        return isZeroOrNegative(remainingNetValue);
    }

    /** Null-safe multiply using {@link #MC}. */
    protected BigDecimal mul(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) return ZERO;
        if (a.signum() == 0 || b.signum() == 0) return ZERO;
        return a.multiply(b, MC);
    }

    /** Null/zero-safe divide using {@link #MC}. Returns 0 if divisor is zero or any is null. */
    protected BigDecimal div(BigDecimal numerator, BigDecimal denominator) {
        if (numerator == null || denominator == null || denominator.signum() == 0) return ZERO;
        return numerator.divide(denominator, MC);
    }

    /** Safe min for BigDecimal (null-safe). */
    protected BigDecimal min(BigDecimal a, BigDecimal b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.min(b);
    }

    /** Safe max for BigDecimal (null-safe). */
    protected BigDecimal max(BigDecimal a, BigDecimal b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.max(b);
    }

    /**
     * Resolves a stable key to read tax parameters from {@code taxParams}.
     * <p>Default: uses the asset's accounting account; override if your domain needs a different key
     * (e.g., asset class, category, or a composite key).</p>
     */
    protected String resolveTaxKey(FinancialFixedAsset asset) {
        return asset.getFixedAssetAccountingAccount();
    }

    /**
     * Computes the end date for the projection window given the start period.
     * <p>Default: end = period's month end. Override to extend across useful life if needed.</p>
     */
    protected LocalDate computeProjectionEnd(LocalDate period, FinancialFixedAsset asset) {
        return endOfMonth(period);
    }

}
