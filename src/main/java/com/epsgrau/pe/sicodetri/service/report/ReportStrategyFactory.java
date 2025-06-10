package com.epsgrau.pe.sicodetri.service.report;

import com.epsgrau.pe.sicodetri.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * This class is Report Strategy Factory. <br/>
 * <b>Class</b>: {@link ReportStrategyFactory}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Component
public class ReportStrategyFactory {

    private final Map<String, ReportStrategy> strategies;

    /**
    * Constructor.
    *
    * @param strategyList Strategies List
    */
    public ReportStrategyFactory(List<ReportStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(ReportStrategy::getCode, s -> s));
    }

    /**
    * Get ReportStrategy.
    *
    * @param code Code of report
    * @return Report Strategy
    */
    public ReportStrategy getStrategy(String code) {
        if (!strategies.containsKey(code)) {
            throw new ResourceNotFoundException("Tipo de reporte no soportado "
                + "para opción: " + code);
        }
        return strategies.get(code);
    }
}