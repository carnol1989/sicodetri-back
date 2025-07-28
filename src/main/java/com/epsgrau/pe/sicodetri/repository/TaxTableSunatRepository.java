package com.epsgrau.pe.sicodetri.repository;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on {@link TaxTableSunat} entities.
 * Extends {@link JpaRepository} to provide standard data access methods and declares
 * a custom finder to retrieve a tax table entry by its accounting account code.
 * <b>Class</b>: {@link TaxTableSunatRepository}<br/>
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
@Repository
public interface TaxTableSunatRepository extends JpaRepository<TaxTableSunat, Long> {

    /**
     * Retrieves the {@link TaxTableSunat} entry matching the specified accounting account code.
     *
     * @param accountingAccount the accounting account code to search for
     * @return the matching TaxTableSunat entity, or {@code null} if none found
     */
    TaxTableSunat findByAccountingAccount(String accountingAccount);

}
