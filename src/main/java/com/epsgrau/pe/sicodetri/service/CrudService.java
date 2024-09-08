package com.epsgrau.pe.sicodetri.service;

import java.util.List;

/**
 * This interface generics with the operations CRUD of a class. <br/>
 * <b>Class</b>: {@link CrudService}<br/>
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
 *     <li>Ago 25, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public interface CrudService<T, ID> {

    T saveService(T obj);

    void saveAllService(List<T> listObj);

    void updateService(T obj);

    void deleteByIdService(ID id);

    T findByIdService(ID id);

    List<T> findAllService();

}
