package com.hit.dao;

import java.util.Collection;

public interface IDao<T> {
    void save(T obj) throws Exception;
    T get(int id) throws Exception;
    Collection<T> getAll() throws Exception;
    void delete(int id) throws Exception;
    void update(T obj) throws Exception;
}
