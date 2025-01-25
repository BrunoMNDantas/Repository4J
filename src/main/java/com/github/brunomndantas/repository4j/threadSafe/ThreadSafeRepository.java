package com.github.brunomndantas.repository4j.threadSafe;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.Collection;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeRepository<K,E> implements IRepository<K,E> {

    protected ReentrantReadWriteLock lock;
    protected IRepository<K,E> sourceRepository;


    public ThreadSafeRepository(IRepository<K,E> sourceRepository, ReentrantReadWriteLock lock) {
        this.sourceRepository = sourceRepository;
        this.lock = lock;
    }

    public ThreadSafeRepository(IRepository<K,E> sourceRepository) {
        this(sourceRepository, new ReentrantReadWriteLock());
    }


    @Override
    public Collection<E> getAll() throws RepositoryException {
        this.lock.readLock().lock();

        try {
            return this.sourceRepository.getAll();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public E get(K key) throws RepositoryException {
        this.lock.readLock().lock();

        try {
            return this.sourceRepository.get(key);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void insert(E entity) throws RepositoryException {
        this.lock.writeLock().lock();

        try {
            this.sourceRepository.insert(entity);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void update(E entity) throws RepositoryException {
        this.lock.writeLock().lock();

        try {
            this.sourceRepository.update(entity);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(K key) throws RepositoryException {
        this.lock.writeLock().lock();

        try {
            this.sourceRepository.delete(key);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

}
