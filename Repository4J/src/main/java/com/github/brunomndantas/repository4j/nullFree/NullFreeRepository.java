package com.github.brunomndantas.repository4j.nullFree;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.Collection;
import java.util.LinkedList;

public class NullFreeRepository<K,E> implements IRepository<K,E> {

    protected IRepository<K,E> repository;


    public NullFreeRepository(IRepository<K,E> repository) {
        this.repository = repository;
    }


    @Override
    public Collection<E> getAll() throws RepositoryException {
        Collection<E> entities = this.repository.getAll();

        if(entities == null) {
             entities = new LinkedList<>();
        }

        return entities;
    }

    @Override
    public E get(K key) throws RepositoryException {
        return this.repository.get(key);
    }

    @Override
    public void insert(E entity) throws RepositoryException {
        if(entity == null) {
            throw new IllegalArgumentException("Entity cannot be null!");
        }

        this.repository.insert(entity);
    }

    @Override
    public void update(E entity) throws RepositoryException {
        if(entity == null) {
            throw new IllegalArgumentException("Entity cannot be null!");
        }

        this.repository.update(entity);
    }

    @Override
    public void delete(K key) throws RepositoryException {
        if(key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }

        this.repository.delete(key);
    }

}