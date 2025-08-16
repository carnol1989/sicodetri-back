package com.epsgrau.pe.sicodetri.service.report;

import com.epsgrau.pe.sicodetri.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Factory for obtaining {@link ReportStrategy} implementations by their code.
 * On initialization, this class collects all available ReportStrategy beans
 * and indexes them by their unique code. Clients can then request a strategy
 * by providing its code.
 * <b>Class</b>: {@link ReportStrategyFactory}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Component
public class ReportStrategyFactory {

    private final Map<String, ReportStrategy> strategies;

    /**
     * Constructs the factory by indexing the given list of strategies
     * by their code value.
     *
     * @param strategyList the list of available {@link ReportStrategy} beans
     */
    public ReportStrategyFactory(List<ReportStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(ReportStrategy::getCode, s -> s));
    }

    /**
     * Retrieves the {@link ReportStrategy} associated with the given code.
     *
     * @param code the unique code identifying the desired report strategy
     * @return the matching {@link ReportStrategy}
     * @throws ResourceNotFoundException if no strategy is registered for the code
     */
    public ReportStrategy getStrategy(String code) {
        if (!strategies.containsKey(code)) {
            throw new ResourceNotFoundException("Tipo de reporte no soportado "
                + "para opción: " + code);
        }
        return strategies.get(code);
    }
}