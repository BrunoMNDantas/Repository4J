package com.github.brunomndantas.repository4j.cache.valid;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.Person;
import com.github.brunomndantas.repository4j.cache.CacheRepositoryTests;
import com.github.brunomndantas.repository4j.exception.RepositoryException;
import com.github.brunomndantas.repository4j.memory.MemoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

public class TimedCacheRepositoryTests extends CacheRepositoryTests {

    @Override
    protected TimedCacheRepository<String, Person> createCacheRepository(IRepository<String,Person> cache, IRepository<String,Person> source) {
        IRepository<String, TimedCacheRepository.Entry<String>> metadataRepository = new MemoryRepository<>(TimedCacheRepository.Entry::getKey);
        long expirationTime = 1000;
        return new TimedCacheRepository<>(cache, source, person -> person.id, metadataRepository, expirationTime);
    }

    @Override
    protected TimedCacheRepository<String, Person> createRepository() {
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        return createCacheRepository(cacheRepository, sourceRepository);
    }


    @Test
    public void shouldReturnCachedEntityIfValid() throws RepositoryException {
        Person cachedEntity = new Person("1", "A");
        Person sourceEntity = new Person("1", "A");

        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        TimedCacheRepository<String,Person> repository = createCacheRepository(cacheRepository, sourceRepository);

        repository.insert(cachedEntity);
        sourceRepository.update(sourceEntity);

        Assertions.assertSame(cachedEntity, repository.get(sourceEntity.id));
    }

    @Test
    public void shouldReturnSourceEntityIfNotValid() throws RepositoryException, InterruptedException {
        Person cachedEntity = new Person("1", "A");
        Person sourceEntity = new Person("1", "A");

        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        TimedCacheRepository<String,Person> repository = createCacheRepository(cacheRepository, sourceRepository);

        repository.insert(cachedEntity);
        sourceRepository.update(sourceEntity);

        Thread.sleep(2*1000);

        Assertions.assertSame(sourceEntity, repository.get(sourceEntity.id));
    }

    @Test
    public void shouldReturnSourceEntityIfThereIsNoEntry() throws RepositoryException {
        Person cachedEntity = new Person("1", "A");
        Person sourceEntity = new Person("1", "A");

        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> cacheRepository = new MemoryRepository<>(keyExtractor);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        TimedCacheRepository<String,Person> repository = createCacheRepository(cacheRepository, sourceRepository);

        cacheRepository.insert(cachedEntity);
        sourceRepository.insert(sourceEntity);

        Assertions.assertSame(sourceEntity, repository.get(cachedEntity.id));
    }

}