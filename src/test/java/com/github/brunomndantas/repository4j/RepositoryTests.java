package com.github.brunomndantas.repository4j;

import com.github.brunomndantas.repository4j.exception.DuplicatedEntityException;
import com.github.brunomndantas.repository4j.exception.NonExistentEntityException;
import com.github.brunomndantas.repository4j.exception.RepositoryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

public abstract class RepositoryTests {

    @Test
    public void shouldInsertEntity() throws RepositoryException {
        Person entityA = new Person("1", "A");
        Person entityB = new Person("2", "B");
        Collection<Person> entities = List.of(entityA, entityB);
        IRepository<String, Person> repository = createRepository();

        for(Person entity: entities) {
            repository.insert(entity);
        }

        Collection<Person> returnedEntities = repository.getAll();
        Assertions.assertEquals(entities.size(), returnedEntities.size());
    }

    @Test
    public void shouldThrowDuplicatedEntityOnInsert() throws RepositoryException {
        String key = "1";
        Person entityA = new Person(key, "A");
        Person entityB = new Person(key, "B");
        IRepository<String, Person> repository = createRepository();

        repository.insert(entityA);

        Assertions.assertThrows(
                DuplicatedEntityException.class,
                () -> repository.insert(entityB)
        );
    }

    @Test
    public void shouldReturnAllEntities() throws RepositoryException {
        Person entityA = new Person("1", "A");
        Person entityB = new Person("2", "B");
        Collection<Person> entities = List.of(entityA, entityB);
        IRepository<String, Person> repository = createRepository();

        for(Person entity: entities) {
            repository.insert(entity);
        }

        Collection<Person> returnedEntities = repository.getAll();
        Collection<String> returnedNames = returnedEntities.stream().map(p -> p.name).toList();

        Assertions.assertEquals(entities.size(), returnedEntities.size());

        for(Person entity: entities) {
            Assertions.assertTrue(returnedNames.contains(entity.name));
        }
    }

    @Test
    public void shouldReturnEntity() throws RepositoryException {
        Person entityA = new Person("1", "A");
        Person entityB = new Person("2", "B");
        Collection<Person> entities = List.of(entityA, entityB);
        IRepository<String, Person> repository = createRepository();

        for(Person entity: entities) {
            repository.insert(entity);
        }

        Person returnedEntity = repository.get(entityA.id);

        Assertions.assertNotNull(returnedEntity);
        Assertions.assertEquals(entityA.id, returnedEntity.id);
        Assertions.assertEquals(entityA.name, returnedEntity.name);
    }

    @Test
    public void shouldReturnNullIfThereIsNoEntity() throws RepositoryException {
        Person entityA = new Person("1", "A");
        Person entityB = new Person("2", "B");
        Collection<Person> entities = List.of(entityA, entityB);
        IRepository<String, Person> repository = createRepository();

        for(Person entity: entities) {
            repository.insert(entity);
        }

        Person returnedEntity = repository.get("Not existent key");

        Assertions.assertNull(returnedEntity);
    }

    @Test
    public void shouldUpdateEntity() throws RepositoryException {
        Person entityBefore = new Person("1", "A");
        Person entityAfter = new Person("1", "B");
        IRepository<String, Person> repository = createRepository();

        repository.insert(entityBefore);
        repository.update(entityAfter);

        Person returnedEntity = repository.get(entityBefore.id);

        Assertions.assertNotNull(returnedEntity);
        Assertions.assertEquals(entityAfter.id, returnedEntity.id);
        Assertions.assertEquals(entityAfter.name, returnedEntity.name);
    }

    @Test
    public void shouldThrowNonExistentEntityOnUpdate() throws RepositoryException {
        Person entityA = new Person("1", "A");
        IRepository<String, Person> repository = createRepository();

        Assertions.assertThrows(
                NonExistentEntityException.class,
                () -> repository.update(entityA)
        );
    }

    @Test
    public void shouldDeleteEntity() throws RepositoryException {
        Person entity = new Person("1", "A");
        IRepository<String, Person> repository = createRepository();

        repository.insert(entity);
        repository.delete(entity.id);

        Person returnedEntity = repository.get(entity.id);

        Assertions.assertNull(returnedEntity);
    }

    @Test
    public void shouldIgnoreDeleteOfNonExistentEntity() throws RepositoryException {
        Person entity = new Person("1", "A");
        IRepository<String, Person> repository = createRepository();

        repository.insert(entity);
        repository.delete("Non existent key");
    }


    protected abstract IRepository<String, Person> createRepository();

}
