package com.github.brunomndantas.repository4j.validator;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.Collection;

public class ValidatorRepository<K,E> implements IRepository<K,E> {

    protected IRepository<K,E> repository;
    protected IValidator<E> validator;


    public ValidatorRepository(IRepository<K,E> repository, IValidator<E> validator) {
        this.repository = repository;
        this.validator = validator;
    }


    @Override
    public Collection<E> getAll() throws RepositoryException {
        return this.repository.getAll();
    }

    @Override
    public E get(K key) throws RepositoryException {
        return this.repository.get(key);
    }

    @Override
    public void insert(E entity) throws RepositoryException {
        this.validator.validate(entity);
        this.repository.insert(entity);
    }

    @Override
    public void update(E entity) throws RepositoryException {
        this.validator.validate(entity);
        this.repository.update(entity);
    }

    @Override
    public void delete(K key) throws RepositoryException {
        this.repository.delete(key);
    }

}