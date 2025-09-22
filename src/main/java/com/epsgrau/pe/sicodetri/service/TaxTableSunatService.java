package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;

import java.util.Collection;
import java.util.List;


/**
 * Service interface for managing SUNAT tax table entries.
 * Extends the generic {@link CrudService} for basic CRUD operations
 * and adds a method to look up a tax table entry by its accounting account code.
 * <b>Class</b>: {@link TaxTableSunatService}<br/>
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
public interface TaxTableSunatService extends CrudService<TaxTableSunat, Long> {

    /**
     * Returns all SUNAT tax rows.
     */
    List<TaxTableSunat> findAll();

    List<TaxTableSunat> findByAccountingAccountIn(Collection<String> accounts);

    /**
     * Retrieves the {@link TaxTableSunat} entry corresponding to the specified
     * accounting account code.
     *
     * @param accountingAccount the three-digit accounting account prefix
     * @return the matching TaxTableSunat entity, or {@code null} if none found
     */
    TaxTableSunat findByAccountigAccountService(String accountingAccount);

}
