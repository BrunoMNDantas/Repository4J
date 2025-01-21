package com.github.brunomndantas.repository4j.clone;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.Collection;
import java.util.function.Function;

public class CloneRepository<K,E> implements IRepository<K,E> {

    protected IRepository<K,E> repository;
    protected Function<E,E> cloneFunction;


    public CloneRepository(IRepository<K,E> repository, Function<E,E> cloneFunction) {
        this.repository = repository;
        this.cloneFunction = cloneFunction;
    }


    @Override
    public Collection<E> getAll() throws RepositoryException {
        Collection<E> entities = this.repository.getAll();
        return entities.stream().map(this.cloneFunction).toList();
    }

    @Override
    public E get(K key) throws RepositoryException {
        E entity = this.repository.get(key);

        if(entity == null   ) {
            return null;
        }

        return this.cloneFunction.apply(entity);
    }

    @Override
    public void insert(E entity) throws RepositoryException {
        E clone = this.cloneFunction.apply(entity);
        this.repository.insert(clone);
    }

    @Override
    public void update(E entity) throws RepositoryException {
        E clone = this.cloneFunction.apply(entity);
        this.repository.update(clone);
    }

    @Override
    public void delete(K key) throws RepositoryException {
        this.repository.delete(key);
    }

}
