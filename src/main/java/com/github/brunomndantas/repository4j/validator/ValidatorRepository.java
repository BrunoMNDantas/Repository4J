package com.github.brunomndantas.repository4j.validator;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.Collection;

public class ValidatorRepository<K,E> implements IRepository<K,E> {

    protected IRepository<K,E> sourceRepository;
    protected IValidator<E> validator;


    public ValidatorRepository(IRepository<K,E> sourceRepository, IValidator<E> validator) {
        this.sourceRepository = sourceRepository;
        this.validator = validator;
    }


    @Override
    public Collection<E> getAll() throws RepositoryException {
        return this.sourceRepository.getAll();
    }

    @Override
    public E get(K key) throws RepositoryException {
        return this.sourceRepository.get(key);
    }

    @Override
    public void insert(E entity) throws RepositoryException {
        this.validator.validate(entity);
        this.sourceRepository.insert(entity);
    }

    @Override
    public void update(E entity) throws RepositoryException {
        this.validator.validate(entity);
        this.sourceRepository.update(entity);
    }

    @Override
    public void delete(K key) throws RepositoryException {
        this.sourceRepository.delete(key);
    }

}