package com.github.brunomndantas.repository4j;

import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.Collection;

public interface IRepository<K/*Key*/,E/*Entity*/> {

    Collection<E> getAll() throws RepositoryException;

    E get(K key) throws RepositoryException;

    void insert(E entity) throws RepositoryException;

    void update(E entity) throws RepositoryException;

    void delete(K key) throws RepositoryException;

}