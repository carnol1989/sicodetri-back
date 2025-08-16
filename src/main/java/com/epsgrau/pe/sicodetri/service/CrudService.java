package com.epsgrau.pe.sicodetri.service;

import java.util.List;

/**
 * Generic service interface defining basic CRUD operations for a given entity type.
 * Provides methods to create, read, update, and delete entities,
 * as well as bulk operations.
 * <b>Class</b>: {@link CrudService}<br/>
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
public interface CrudService<T, IdT> {

    /**
     * Saves a single entity.
     *
     * @param obj the entity to save
     * @return the saved entity
     */
    T saveService(T obj);

    /**
     * Saves a list of entities in bulk.
     *
     * @param listObj the list of entities to save
     */
    void saveAllService(List<T> listObj);

    /**
     * Updates an existing entity.
     *
     * @param obj the entity with updated data
     */
    void updateService(T obj);

    /**
     * Deletes the entity with the given identifier.
     *
     * @param id the identifier of the entity to delete
     */
    void deleteByIdService(IdT id);

    /**
     * Deletes all entities in the given list.
     *
     * @param listObj the list of entities to delete
     */
    void deleteAllService(List<T> listObj);

    /**
     * Finds an entity by its identifier.
     *
     * @param id the identifier of the entity to find
     * @return the found entity, or null/new instance if not found
     */
    T findByIdService(IdT id);

    /**
     * Retrieves all entities of this type.
     *
     * @return a list of all entities
     */
    List<T> findAllService();
}
