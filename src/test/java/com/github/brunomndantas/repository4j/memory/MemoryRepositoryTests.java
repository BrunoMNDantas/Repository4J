package com.github.brunomndantas.repository4j.memory;

import com.github.brunomndantas.repository4j.Person;
import com.github.brunomndantas.repository4j.RepositoryTests;
import com.github.brunomndantas.repository4j.exception.RepositoryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryRepositoryTests extends RepositoryTests {

    @Override
    protected MemoryRepository<String, Person> createRepository() {
        return new MemoryRepository<>(person -> person.id);
    }


    @Test
    public void shouldInitWithEntitiesReceivedOnConstructor() {
        Person entityA = new Person("1", "A");
        Person entityB = new Person("2", "B");
        Map<String, Person> entities = new HashMap<>();
        entities.put(entityA.id, entityA);
        entities.put(entityB.id, entityB);
        MemoryRepository<String, Person> repository = new MemoryRepository<>(entities, person -> person.id);

        Collection<Person> returnedEntities = repository.getAll();
        Assertions.assertEquals(entities.size(), returnedEntities.size());
    }

    @Test
    public void shouldUseKeyExtractor() throws RepositoryException {
        String key = "One";
        Person entity = new Person("1", "A");
        MemoryRepository<String, Person> repository = new MemoryRepository<>(person -> key);

        repository.insert(entity);

        Person returnedEntity = repository.get(entity.id);
        Assertions.assertNull(returnedEntity);

        returnedEntity = repository.get(key);
        Assertions.assertNotNull(returnedEntity);
        Assertions.assertEquals(entity.name, returnedEntity.name);
    }

}