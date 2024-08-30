package com.epsgrau.pe.sicodetri.service;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;

import java.util.List;

public interface CrudService<T, ID> {

    T saveService(T obj);

    void saveAllService(List<T> listObj);

    void updateService(T obj);

    void deleteByIdService(ID id);

    T findByIdService(ID id);

    List<T> findAllService();

}
