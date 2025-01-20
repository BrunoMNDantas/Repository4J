package com.github.brunomndantas.repository4j.logger;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class SimpleLoggerRepository<K,E> extends LoggerRepository<K,E> {

    protected Function<E,K> keyExtractor;
    protected Consumer<String> logger;


    public SimpleLoggerRepository(IRepository<K,E> repository, Function<E,K> keyExtractor, Consumer<String> logger) {
        super(repository);
        this.keyExtractor = keyExtractor;
        this.logger = logger;
    }


    @Override
    protected void logEnteringOnGetAll() throws RepositoryException {
        this.logger.accept("Entering Get All");
    }

    @Override
    protected void logLeavingFromGetAll() throws RepositoryException {
        this.logger.accept("Leaving Get All");
    }

    @Override
    protected void logReturnOfGetAll(Collection<E> entities) throws RepositoryException {
        this.logger.accept("Get All returning " + entities.size() + " entities");
    }

    @Override
    protected void logExceptionOnGetAll(Exception e) throws RepositoryException {
        this.logger.accept("Exception on Get All message:" + e.getMessage());
    }

    @Override
    protected void logEnteringOnGet(K key) throws RepositoryException {
        this.logger.accept("Entering Get with key:" + key);
    }

    @Override
    protected void logLeavingFromGet(K key) throws RepositoryException {
        this.logger.accept("Leaving Get with key:" + key);
    }

    @Override
    protected void logReturnOfGet(K key, E entity) throws RepositoryException {
        if(entity != null) {
            this.logger.accept("Get returning entity for key:" + key);
        } else {
            this.logger.accept("Get returning null for key:" + key);
        }
    }

    @Override
    protected void logExceptionOnGet(K key, Exception e) throws RepositoryException {
        this.logger.accept("Exception on Get with key:" + key + " message:" + e.getMessage());
    }

    @Override
    protected void logEnteringOnInsert(E entity) throws RepositoryException {
        K key = this.keyExtractor.apply(entity);
        this.logger.accept("Entering Insert with key:" + key);
    }

    @Override
    protected void logLeavingFromInsert(E entity) throws RepositoryException {
        K key = this.keyExtractor.apply(entity);
        this.logger.accept("Leaving Insert with key:" + key);
    }

    @Override
    protected void logExceptionOnInsert(E entity, Exception e) throws RepositoryException {
        K key = this.keyExtractor.apply(entity);
        this.logger.accept("Exception on Insert with key:" + key + " message:" + e.getMessage());
    }

    @Override
    protected void logEnteringOnUpdate(E entity) throws RepositoryException {
        K key = this.keyExtractor.apply(entity);
        this.logger.accept("Entering Update with key:" + key);
    }

    @Override
    protected void logLeavingFromUpdate(E entity) throws RepositoryException {
        K key = this.keyExtractor.apply(entity);
        this.logger.accept("Leaving Update with key:" + key);
    }

    @Override
    protected void logExceptionOnUpdate(E entity, Exception e) throws RepositoryException {
        K key = this.keyExtractor.apply(entity);
        this.logger.accept("Exception on Update with key:" + key + " message:" + e.getMessage());
    }

    @Override
    protected void logEnteringOnDelete(K key) throws RepositoryException {
        this.logger.accept("Entering Delete with key:" + key);
    }

    @Override
    protected void logLeavingFromDelete(K key) throws RepositoryException {
        this.logger.accept("Leaving Delete with key:" + key);
    }

    @Override
    protected void logExceptionOnDelete(K key, Exception e) throws RepositoryException {
        this.logger.accept("Exception on Delete with key:" + key + " message:" + e.getMessage());
    }

}
