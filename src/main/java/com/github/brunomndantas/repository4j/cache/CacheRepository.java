package com.github.brunomndantas.repository4j.cache;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.Collection;
import java.util.function.Function;

public class CacheRepository<K,E> implements IRepository<K,E> {

    protected IRepository<K,E> cacheRepository;
    protected IRepository<K,E> sourceRepository;
    protected Function<E,K> keyExtractor;


    public CacheRepository(IRepository<K,E> cacheRepository, IRepository<K,E> sourceRepository, Function<E,K> keyExtractor) {
        this.cacheRepository = cacheRepository;
        this.sourceRepository = sourceRepository;
        this.keyExtractor = keyExtractor;
    }


    @Override
    public Collection<E> getAll() throws RepositoryException {
        Collection<E> entities = this.sourceRepository.getAll();

        for(E entity: entities) {
            storeOnCache(entity);
        }

        return entities;
    }

    @Override
    public E get(K key) throws RepositoryException {
        E cacheEntity = getFromCache(key);

        if(cacheEntity == null) {
            cacheEntity = this.sourceRepository.get(key);

            if(cacheEntity != null) {
                storeOnCache(cacheEntity);
            }
        }

        return cacheEntity;
    }

    @Override
    public void insert(E entity) throws RepositoryException {
        this.sourceRepository.insert(entity);
        storeOnCache(entity);
    }

    @Override
    public void update(E entity) throws RepositoryException {
        this.sourceRepository.update(entity);
        storeOnCache(entity);
    }

    @Override
    public void delete(K key) throws RepositoryException {
        this.cacheRepository.delete(key);
        this.sourceRepository.delete(key);
    }

    protected E getFromCache(K key) throws RepositoryException {
        return this.cacheRepository.get(key);
    }

    protected void storeOnCache(E entity) throws RepositoryException {
        K key = this.keyExtractor.apply(entity);

        if(this.cacheRepository.get(key) == null) {
            this.cacheRepository.insert(entity);
        } else {
            this.cacheRepository.update(entity);
        }
    }

}