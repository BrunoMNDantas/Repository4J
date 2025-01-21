package com.github.brunomndantas.repository4j.clone;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.Person;
import com.github.brunomndantas.repository4j.RepositoryTests;
import com.github.brunomndantas.repository4j.exception.RepositoryException;
import com.github.brunomndantas.repository4j.memory.MemoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class CloneRepositoryTests extends RepositoryTests {

    @Override
    protected CloneRepository<String, Person> createRepository() {
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
        Function<Person,Person> cloneFunction = person -> new Person(person.id, person.name);
        return new CloneRepository<>(sourceRepository, cloneFunction);
    }


    @Test
    public void shouldReturnClonedCollection() throws RepositoryException {
        Person entityA = new Person("1", "A");
        Person entityB = new Person("2", "B");

        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
        Function<Person,Person> cloneFunction = person -> new Person(person.id, person.name);
        CloneRepository<String,Person> repository = new CloneRepository<>(sourceRepository, cloneFunction);

        sourceRepository.insert(entityA);
        sourceRepository.insert(entityB);

        Collection<Person> returnedEntities = repository.getAll();
        Assertions.assertEquals(2, returnedEntities.size());
        Assertions.assertFalse(returnedEntities.contains(entityA));
        Assertions.assertFalse(returnedEntities.contains(entityB));
    }

    @Test
    public void shouldReturnClonedEntity() throws RepositoryException {
        Person sourceEntity = new Person("1", "A");
        Person cloneEntity = new Person("1", "A");

        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
        Function<Person,Person> cloneFunction = person -> cloneEntity;
        CloneRepository<String,Person> repository = new CloneRepository<>(sourceRepository, cloneFunction);

        sourceRepository.insert(sourceEntity);

        Person returnedEntity = repository.get(sourceEntity.id);
        Assertions.assertNotNull(returnedEntity);
        Assertions.assertSame(cloneEntity, returnedEntity);
    }

    @Test
    public void shouldNotCloneNullEntity() throws RepositoryException {
        AtomicBoolean cloneFunctionCalled = new AtomicBoolean(false);
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
        Function<Person,Person> cloneFunction = person -> {
            cloneFunctionCalled.set(true);
            return new Person(person.id, person.name);
        };
        CloneRepository<String,Person> repository = new CloneRepository<>(sourceRepository, cloneFunction);

        Person entity = repository.get("Non Existent Key");
        Assertions.assertNull(entity);
        Assertions.assertFalse(cloneFunctionCalled.get());
    }

    @Test
    public void shouldInsertClonedEntity() throws RepositoryException {
        Person sourceEntity = new Person("1", "A");
        Person cloneEntity = new Person("1", "A");

        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
        Function<Person,Person> cloneFunction = person -> cloneEntity;
        CloneRepository<String,Person> repository = new CloneRepository<>(sourceRepository, cloneFunction);

        repository.insert(sourceEntity);

        Person returnedEntity = sourceRepository.get(sourceEntity.id);
        Assertions.assertSame(cloneEntity, returnedEntity);
    }

    @Test
    public void shouldUpdateClonedEntity() throws RepositoryException {
        Person sourceEntityBefore = new Person("1", "A");
        Person sourceEntityAfter = new Person("1", "B");
        Person cloneEntity = new Person("1", "B");

        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
        Function<Person,Person> cloneFunction = person -> person == sourceEntityBefore ? person :  cloneEntity;
        CloneRepository<String,Person> repository = new CloneRepository<>(sourceRepository, cloneFunction);

        repository.insert(sourceEntityBefore);
        repository.update(sourceEntityAfter);

        Person returnedEntity = sourceRepository.get(sourceEntityBefore.id);
        Assertions.assertSame(cloneEntity, returnedEntity);
    }

}