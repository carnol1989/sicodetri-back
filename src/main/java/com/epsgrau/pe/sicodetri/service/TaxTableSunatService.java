package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;


/**
 * This class is the interface of the service for Tax Table Sunat. <br/>
 * <b>Class</b>: {@link TaxTableSunatService}<br/>
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
 *     <li>Ago 15, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public interface TaxTableSunatService extends CrudService<TaxTableSunat, Long> {

    TaxTableSunat findByAccountigAccountService(String accountingAccount);

}
