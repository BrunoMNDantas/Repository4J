package com.github.brunomndantas.repository4j.nullFree;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.Person;
import com.github.brunomndantas.repository4j.RepositoryTests;
import com.github.brunomndantas.repository4j.exception.RepositoryException;
import com.github.brunomndantas.repository4j.memory.MemoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class NullFreeRepositoryTests extends RepositoryTests {

    @Override
    protected NullFreeRepository<String, Person> createRepository() {
        return new NullFreeRepository<>(new MemoryRepository<>(person -> person.id));
    }


    @Test
    public void shouldReturnEmptyListInsteadOfNull() throws RepositoryException  {
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id) {
            @Override
            public Collection<Person> getAll() {
                return null;
            }
        };
        IRepository<String,Person> repository = new NullFreeRepository<>(sourceRepository);

        Collection<Person> entities = repository.getAll();

        Assertions.assertNotNull(entities);
        Assertions.assertTrue(entities.isEmpty());
    }

    @Test
    public void shouldNotAllowNullEntityOnInsert() {
        NullFreeRepository<String,Person> repository = createRepository();

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> repository.insert(null));
        Assertions.assertEquals("Entity cannot be null!", exception.getMessage());
    }

    @Test
    public void shouldNotAllowNullEntityOnUpdate() {
        NullFreeRepository<String,Person> repository = createRepository();

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> repository.update(null));
        Assertions.assertEquals("Entity cannot be null!", exception.getMessage());
    }

    @Test
    public void shouldNotAllowNullKeyOnDelete() {
        NullFreeRepository<String,Person> repository = createRepository();

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> repository.delete(null));
        Assertions.assertEquals("Key cannot be null!", exception.getMessage());
    }

}