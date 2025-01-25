package com.github.brunomndantas.repository4j.clone;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.Collection;
import java.util.function.Function;

public class CloneRepository<K,E> implements IRepository<K,E> {

    protected IRepository<K,E> sourceRepository;
    protected Function<E,E> cloneFunction;


    public CloneRepository(IRepository<K,E> sourceRepository, Function<E,E> cloneFunction) {
        this.sourceRepository = sourceRepository;
        this.cloneFunction = cloneFunction;
    }


    @Override
    public Collection<E> getAll() throws RepositoryException {
        Collection<E> entities = this.sourceRepository.getAll();
        return entities.stream().map(this.cloneFunction).toList();
    }

    @Override
    public E get(K key) throws RepositoryException {
        E entity = this.sourceRepository.get(key);

        if(entity == null   ) {
            return null;
        }

        return this.cloneFunction.apply(entity);
    }

    @Override
    public void insert(E entity) throws RepositoryException {
        E clone = this.cloneFunction.apply(entity);
        this.sourceRepository.insert(clone);
    }

    @Override
    public void update(E entity) throws RepositoryException {
        E clone = this.cloneFunction.apply(entity);
        this.sourceRepository.update(clone);
    }

    @Override
    public void delete(K key) throws RepositoryException {
        this.sourceRepository.delete(key);
    }

}
