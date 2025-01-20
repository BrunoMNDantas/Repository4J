package com.github.brunomndantas.repository4j.memory;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.exception.DuplicatedEntityException;
import com.github.brunomndantas.repository4j.exception.NonExistentEntityException;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MemoryRepository<K,E> implements IRepository<K,E> {

    protected Map<K,E> entities;
    protected Function<E,K> keyExtractor;


    public MemoryRepository(Map<K,E> entities, Function<E,K> keyExtractor) {
        this.entities = entities;
        this.keyExtractor = keyExtractor;
    }

    public MemoryRepository(Function<E,K> keyExtractor) {
        this(new HashMap<>(), keyExtractor);
    }


    @Override
    public Collection<E> getAll() {
        return this.entities.values();
    }

    @Override
    public E get(K key) {
        return this.entities.get(key);
    }

    @Override
    public void insert(E entity) throws RepositoryException {
        K key = this.keyExtractor.apply(entity);

        if(this.entities.containsKey(key)) {
            throw new DuplicatedEntityException("There is already a entity with key:" + key);
        }

        this.entities.put(key, entity);
    }

    @Override
    public void update(E entity) throws RepositoryException {
        K key = this.keyExtractor.apply(entity);

        if(!this.entities.containsKey(key)) {
            throw new NonExistentEntityException("There is no entity with key:" + key);
        }

        this.entities.put(key, entity);
    }

    @Override
    public void delete(K key) {
        this.entities.remove(key);
    }

}