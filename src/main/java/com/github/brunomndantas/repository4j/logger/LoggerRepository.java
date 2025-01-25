package com.github.brunomndantas.repository4j.logger;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.Collection;

public abstract class LoggerRepository<K,E> implements IRepository<K,E> {

    protected IRepository<K,E> sourceRepository;


    public LoggerRepository(IRepository<K,E> sourceRepository) {
        this.sourceRepository = sourceRepository;
    }


    @Override
    public Collection<E> getAll() throws RepositoryException {
        try {
            logEnteringOnGetAll();

            Collection<E> entities = this.sourceRepository.getAll();
            logReturnOfGetAll(entities);

            return entities;
        } catch (RepositoryException | RuntimeException e) {
            logExceptionOnGetAll(e);
            throw e;
        } finally {
            logLeavingFromGetAll();
        }
    }

    @Override
    public E get(K key) throws RepositoryException {
        try {
            logEnteringOnGet(key);

            E entity = this.sourceRepository.get(key);
            logReturnOfGet(key, entity);

            return entity;
        } catch (RepositoryException | RuntimeException e) {
            logExceptionOnGet(key, e);
            throw e;
        } finally {
            logLeavingFromGet(key);
        }
    }

    @Override
    public void insert(E entity) throws RepositoryException {
        try {
            logEnteringOnInsert(entity);
            this.sourceRepository.insert(entity);
        } catch (RepositoryException | RuntimeException e) {
            logExceptionOnInsert(entity, e);
            throw e;
        } finally {
            logLeavingFromInsert(entity);
        }
    }

    @Override
    public void update(E entity) throws RepositoryException {
        try {
            logEnteringOnUpdate(entity);
            this.sourceRepository.update(entity);
        } catch (RepositoryException | RuntimeException e) {
            logExceptionOnUpdate(entity, e);
            throw e;
        } finally {
            logLeavingFromUpdate(entity);
        }
    }

    @Override
    public void delete(K key) throws RepositoryException {
        try {
            logEnteringOnDelete(key);
            this.sourceRepository.delete(key);
        } catch (RepositoryException | RuntimeException e) {
            logExceptionOnDelete(key, e);
            throw e;
        } finally {
            logLeavingFromDelete(key);
        }
    }


    protected abstract void logEnteringOnGetAll() throws RepositoryException;

    protected abstract void logLeavingFromGetAll() throws RepositoryException;

    protected abstract void logReturnOfGetAll(Collection<E> entities) throws RepositoryException;

    protected abstract void logExceptionOnGetAll(Exception e) throws RepositoryException;

    protected abstract void logEnteringOnGet(K key) throws RepositoryException;

    protected abstract void logLeavingFromGet(K key) throws RepositoryException;

    protected abstract void logReturnOfGet(K key, E entity) throws RepositoryException;

    protected abstract void logExceptionOnGet(K key, Exception e) throws RepositoryException;

    protected abstract void logEnteringOnInsert(E entity) throws RepositoryException;

    protected abstract void logLeavingFromInsert(E entity) throws RepositoryException;

    protected abstract void logExceptionOnInsert(E entity, Exception e) throws RepositoryException;

    protected abstract void logEnteringOnUpdate(E entity) throws RepositoryException;

    protected abstract void logLeavingFromUpdate(E entity) throws RepositoryException;

    protected abstract void logExceptionOnUpdate(E entity, Exception e) throws RepositoryException;

    protected abstract void logEnteringOnDelete(K key) throws RepositoryException;

    protected abstract void logLeavingFromDelete(K key) throws RepositoryException;

    protected abstract void logExceptionOnDelete(K key, Exception e) throws RepositoryException;

}
