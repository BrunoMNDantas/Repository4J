package com.github.brunomndantas.repository4j.cache.valid;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.cache.CacheRepository;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.function.Function;

public abstract class ValidCacheRepository<K,E> extends CacheRepository<K,E> {

    public ValidCacheRepository(IRepository<K,E> cacheRepository, IRepository<K, E> sourceRepository, Function<E,K> keyExtractor) {
        super(cacheRepository, sourceRepository, keyExtractor);
    }


    @Override
    protected E getFromCache(K key) throws RepositoryException {
        E entity = super.getFromCache(key);

        if(entity != null && isValid(entity)) {
            return entity;
        }

        return null;
    }


    protected abstract boolean isValid(E entity) throws RepositoryException;

}