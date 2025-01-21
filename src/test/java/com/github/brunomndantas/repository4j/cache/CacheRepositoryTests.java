package com.github.brunomndantas.repository4j.cache;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.Person;
import com.github.brunomndantas.repository4j.RepositoryTests;
import com.github.brunomndantas.repository4j.exception.DuplicatedEntityException;
import com.github.brunomndantas.repository4j.exception.NonExistentEntityException;
import com.github.brunomndantas.repository4j.exception.RepositoryException;
import com.github.brunomndantas.repository4j.memory.MemoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

public class CacheRepositoryTests extends RepositoryTests {

    @Override
    protected IRepository<String, Person> createRepository() {
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        return new CacheRepository<>(cacheRepository, sourceRepository, keyExtractor);
    }


    @Test
    public void shouldStoreAllEntitiesOnGetAll() throws RepositoryException {
        Person entityA = new Person("1", "A");
        Person entityB = new Person("2", "B");

        Function<Person, String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        CacheRepository<String,Person> repository = createCacheRepository(cacheRepository, sourceRepository);

        sourceRepository.insert(entityA);
        sourceRepository.insert(entityB);

        repository.getAll();

        Assertions.assertNotNull(cacheRepository.get(entityA.id));
        Assertions.assertNotNull(cacheRepository.get(entityB.id));
    }

    @Test
    public void shouldFetchAllEntitiesOnGetAll() throws RepositoryException {
        Person entityA = new Person("1", "A");
        Person entityB = new Person("2", "B");
        Person entityC = new Person("3", "C");

        Function<Person, String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        CacheRepository<String,Person> repository = new CacheRepository<>(cacheRepository, sourceRepository, keyExtractor);

        cacheRepository.insert(entityA);
        sourceRepository.insert(entityA);
        sourceRepository.insert(entityB);
        sourceRepository.insert(entityC);

        repository.getAll();

        Assertions.assertNotNull(cacheRepository.get(entityA.id));
        Assertions.assertNotNull(cacheRepository.get(entityB.id));
        Assertions.assertNotNull(cacheRepository.get(entityC.id));
    }

    @Test
    public void shouldStoreEntityOnGet() throws RepositoryException {
        Person entity = new Person("1", "A");

        Function<Person, String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        CacheRepository<String,Person> repository = new CacheRepository<>(cacheRepository, sourceRepository, keyExtractor);

        sourceRepository.insert(entity);

        repository.get(entity.id);

        Assertions.assertNotNull(cacheRepository.get(entity.id));
    }

    @Test
    public void shouldStoreEntityOnInsert() throws RepositoryException {
        Person entity = new Person("1", "A");

        Function<Person, String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        CacheRepository<String,Person> repository = new CacheRepository<>(cacheRepository, sourceRepository, keyExtractor);

        repository.insert(entity);

        Assertions.assertNotNull(cacheRepository.get(entity.id));
        Assertions.assertNotNull(sourceRepository.get(entity.id));
    }

    @Test
    public void shouldNotStoreEntityOnInsertIfInsertFailsOnSourceRepository() throws RepositoryException {
        Person entity = new Person("1", "A");

        Function<Person, String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        CacheRepository<String,Person> repository = new CacheRepository<>(cacheRepository, sourceRepository, keyExtractor);

        sourceRepository.insert(entity);

        try {
            repository.insert(entity);
            Assertions.fail("DuplicatedEntityException should be thrown!");
        } catch(DuplicatedEntityException e) {
            Assertions.assertNull(cacheRepository.get(entity.id));
        }
    }

    @Test
    public void shouldInsertEntityOnCacheOnUpdate() throws RepositoryException {
        String id  = "1";
        Person entityBefore = new Person(id, "A");
        Person entityAfter = new Person(id, "AA");

        Function<Person, String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        CacheRepository<String,Person> repository = new CacheRepository<>(cacheRepository, sourceRepository, keyExtractor);

        sourceRepository.insert(entityBefore);
        repository.update(entityAfter);

        Assertions.assertSame(entityAfter, cacheRepository.get(id));
        Assertions.assertSame(entityAfter, sourceRepository.get(id));
    }

    @Test
    public void shouldUpdateEntityOnCacheOnUpdate() throws RepositoryException {
        String id  = "1";
        Person entityBefore = new Person(id, "A");
        Person entityAfter = new Person(id, "AA");

        Function<Person, String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        CacheRepository<String,Person> repository = new CacheRepository<>(cacheRepository, sourceRepository, keyExtractor);

        cacheRepository.insert(entityBefore);
        sourceRepository.insert(entityBefore);
        repository.update(entityAfter);

        Assertions.assertSame(entityAfter, cacheRepository.get(id));
        Assertions.assertSame(entityAfter, sourceRepository.get(id));
    }

    @Test
    public void shouldNotUpdateEntityOnCacheIfUpdateOnSourceFails() throws RepositoryException {
        String id  = "1";
        Person entityBefore = new Person(id, "A");
        Person entityAfter = new Person(id, "AA");

        Function<Person, String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        CacheRepository<String,Person> repository = new CacheRepository<>(cacheRepository, sourceRepository, keyExtractor);

        cacheRepository.insert(entityBefore);

        try {
            repository.update(entityAfter);
            Assertions.fail("NonExistentEntityException should be thrown!");
        } catch (NonExistentEntityException e) {
            Assertions.assertSame(entityBefore, cacheRepository.get(id));
        }
    }

    @Test
    public void shouldDeleteEntityFromCacheOnDelete() throws RepositoryException {
        Person entity = new Person("1", "A");

        Function<Person, String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        CacheRepository<String,Person> repository = new CacheRepository<>(cacheRepository, sourceRepository, keyExtractor);

        cacheRepository.insert(entity);
        sourceRepository.insert(entity);
        repository.delete(entity.id);

        Assertions.assertNull(cacheRepository.get(entity.id));
        Assertions.assertNull(sourceRepository.get(entity.id));
    }

    @Test
    public void shouldDeleteEntityFromSourceWhenItsNotPresentOnCache() throws RepositoryException {
        Person entity = new Person("1", "A");

        Function<Person, String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        CacheRepository<String,Person> repository = new CacheRepository<>(cacheRepository, sourceRepository, keyExtractor);

        sourceRepository.insert(entity);
        repository.delete(entity.id);

        Assertions.assertNull(cacheRepository.get(entity.id));
        Assertions.assertNull(sourceRepository.get(entity.id));
    }

    protected CacheRepository<String, Person> createCacheRepository(IRepository<String,Person> cache, IRepository<String,Person> source) {
        return new CacheRepository<>(cache, source, person -> person.id);
    }

}