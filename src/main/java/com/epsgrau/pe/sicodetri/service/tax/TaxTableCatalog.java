package com.epsgrau.pe.sicodetri.service.tax;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Catalog for SUNAT tax table entries, optimized for efficient lookup by accounting account prefix
 * and acquisition date.
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Pre-groups entries by the first three digits of the accounting account.</li>
 *   <li>Within each group, sorts entries by specificity (ranges first) and effectiveFrom date (most recent first).</li>
 *   <li>Provides a resolve method to find the applicable tax entry for a given account prefix and acquisition date.</li>
 * </ul>
 *
 * <b>Class</b>: {@link TaxTableCatalog}<br/>
 * <b>Company</b>: EPS Grau<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau
 *
 * @see TaxTableSunat
 */
public final class TaxTableCatalog {

    private static final LocalDate CUT_2010 = LocalDate.of(2010, 1, 1);

    private final Map<String, List<TaxTableSunat>> byAccount;

    /**
     * Private constructor to enforce use of the factory method.
     *
     * @param byAccount Map of account prefixes to their corresponding tax entries.
     */
    private TaxTableCatalog(Map<String, List<TaxTableSunat>> byAccount) {
        this.byAccount = byAccount;
    }

    /**
     * Factory method to create a TaxTableCatalog from a list of TaxTableSunat entries.
     * Validates input and organizes entries for efficient resolution.
     *
     * @param rows List of TaxTableSunat entries to include in the catalog.
     * @return A TaxTableCatalog instance containing the provided entries.
     * @throws NullPointerException if rows is null.
     */
    public static TaxTableCatalog of(List<TaxTableSunat> rows) {
        Objects.requireNonNull(rows, "rows");

        Map<String, List<TaxTableSunat>> grouped = rows.stream()
            .filter(r -> r.getAccountingAccount() != null && !r.getAccountingAccount().isBlank())
            .filter(r -> r.getStatus() != null && r.getStatus().equalsIgnoreCase("S"))
            .collect(Collectors.groupingBy(
                r -> r.getAccountingAccount().trim(),
                Collectors.collectingAndThen(Collectors.toList(), list -> {
                    // Ordena: más específico primero (con rangos), luego por effectiveFrom más reciente
                    list.sort(Comparator
                        .comparingInt((TaxTableSunat t) -> specificity(t)).reversed()
                        .thenComparing(t -> Optional.ofNullable(t.getEffectiveFrom()).orElse(LocalDate.MIN),
                            Comparator.reverseOrder())
                    );
                    return List.copyOf(list);
                })
            ));

        return new TaxTableCatalog(Collections.unmodifiableMap(grouped));
    }

    /**
     * Calcula la especificidad de una entrada de TaxTableSunat.
     * Se considera más específico si tiene effectiveFrom y/o effectiveTo definidos.
     *
     * @param t La entrada de TaxTableSunat a evaluar.
     * @return Un entero representando la especificidad (0-2).
     */
    private static int specificity(TaxTableSunat t) {
        int s = 0;
        if (t.getEffectiveFrom() != null) {
            s++;
        }
        if (t.getEffectiveTo() != null) {
            s++;
        }
        return s;
    }

    /**
     * Resuelve la entrada de TaxTableSunat aplicable para un prefijo de cuenta contable y fecha de adquisición dados.
     *
     * <p>Política de resolución:</p>
     * <ul>
     *   <li>Busca entradas por el prefijo (3 dígitos) de la cuenta contable.</li>
     *   <li>Si acquisitionDate es null, retorna la entrada más específica/reciente.</li>
     *   <li>Si acquisitionDate no es null, busca la primera entrada cuyo rango de vigencia incluya la fecha.</li>
     *   <li>Si no se encuentra una entrada válida y el prefijo es "332" con acquisitionDate antes de 2010, lanza una excepción específica.</li>
     *   <li>En caso contrario, retorna la primera entrada encontrada para el prefijo.</li>
     * </ul>
     *
     * @param prefix3        Prefijo de cuenta contable (3 dígitos).
     * @param acquisitionDate Fecha de adquisición del activo (puede ser null).
     * @return La entrada de TaxTableSunat aplicable según las reglas descritas.
     * @throws IllegalArgumentException si no se encuentra ninguna entrada para el prefijo dado.
     * @throws IllegalStateException si falta configuración crítica para el caso específico de cuenta 332 y fecha antes de 2010.
     */
    public TaxTableSunat resolve(String prefix3, LocalDate acquisitionDate) {
        String key = normalizePrefix(prefix3);

        List<TaxTableSunat> candidates = byAccount.get(key);
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("Missing TaxTableSunat for account prefix: " + key);
        }

        if (acquisitionDate == null) {
            // política: si falta fecha, usa el más específico/reciente
            return candidates.get(0);
        }

        Optional<TaxTableSunat> match = candidates.stream()
            .filter(t -> t.isEffectiveOn(acquisitionDate))
            .findFirst();

        if (match.isPresent()) {
            return match.get();
        }

        // Fail-fast específico para tu regla crítica
        if ("332".equals(key) && acquisitionDate.isBefore(CUT_2010)) {
            throw new IllegalStateException(
              "Missing SUNAT tax config for account 332 and acquisitionDate < 2010. "
                  + "Insert a row with annual_rate=3 and effective_to=2009-12-31."
            );
        }

        return candidates.get(0);
    }

    /**
     * Normaliza el prefijo de cuenta contable, asegurando que no sea nulo o vacío
     * y limitándolo a 3 caracteres.
     *
     * @param prefix El prefijo de cuenta contable a normalizar.
     * @return El prefijo normalizado (trimmed y limitado a 3 caracteres).
     * @throws IllegalArgumentException si el prefijo es nulo o está en blanco.
     */
    private static String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            throw new IllegalArgumentException("prefix must not be blank");
        }
        String p = prefix.trim();
        return p.length() >= 3 ? p.substring(0, 3) : p;
    }
}