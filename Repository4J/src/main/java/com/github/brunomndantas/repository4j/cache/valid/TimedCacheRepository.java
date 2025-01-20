package com.github.brunomndantas.repository4j.cache.valid;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.function.Function;

public class TimedCacheRepository<K,E> extends ValidCacheRepository<K,E> {

    public static class Entry<K> {

        protected K key;
        public K getKey() { return this.key; }

        protected long date;
        public long getDate() { return this.date; }


        public Entry(K key, long date) {
            this.key = key;
            this.date = date;
        }

    }

    protected IRepository<K,Entry<K>> metadataRepository;
    protected long expirationTime;


    public TimedCacheRepository(IRepository<K, E> cacheRepository, IRepository<K, E> sourceRepository, Function<E, K> keyExtractor,
                                IRepository<K,Entry<K>> metadataRepository, long expirationTime) {
        super(cacheRepository, sourceRepository, keyExtractor);
        this.metadataRepository = metadataRepository;
        this.expirationTime = expirationTime;
    }


    @Override
    protected boolean isValid(E entity) throws RepositoryException {
        K key = super.keyExtractor.apply(entity);
        Entry<K> entry = this.metadataRepository.get(key);

        if(entry != null) {
            long cachedTime = System.currentTimeMillis() - entry.getDate();
            return cachedTime < expirationTime;
        }

        return false;
    }

    @Override
    protected void storeOnCache(E entity) throws RepositoryException {
        super.storeOnCache(entity);

        K key = super.keyExtractor.apply(entity);
        Entry<K> entry = new Entry<>(key, System.currentTimeMillis());
        if(metadataRepository.get(key) == null) {
            metadataRepository.insert(entry);
        } else {
            metadataRepository.update(entry);
        }
    }

    @Override
    public void delete(K key) throws RepositoryException {
        super.delete(key);
        this.metadataRepository.delete(key);
    }

}