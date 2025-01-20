package com.github.brunomndantas.repository4j.validator;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.Person;
import com.github.brunomndantas.repository4j.RepositoryTests;
import com.github.brunomndantas.repository4j.exception.RepositoryException;
import com.github.brunomndantas.repository4j.memory.MemoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidatorRepositoryTests extends RepositoryTests {

    @Override
    protected ValidatorRepository<String, Person> createRepository() {
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
        IValidator<Person> validator = person -> {};
        return new ValidatorRepository<>(sourceRepository, validator);
    }


    @Test
    public void shouldValidateEntityOnInsert() {
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
        RepositoryException error = new RepositoryException("Invalid");
        IValidator<Person> validator = person -> { throw error; };
        ValidatorRepository<String,Person> repository = new ValidatorRepository<>(sourceRepository, validator);

        Person entity = new Person("1", "A");

        Exception exception = Assertions.assertThrows(RepositoryException.class, () -> repository.insert(entity));
        Assertions.assertSame(error, exception);
    }

    @Test
    public void shouldNotInsertIfValidationFailed() throws RepositoryException {
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
        RepositoryException error = new RepositoryException("Invalid");
        IValidator<Person> validator = person -> { throw error; };
        ValidatorRepository<String,Person> repository = new ValidatorRepository<>(sourceRepository, validator);

        Person entity = new Person("1", "A");

        Assertions.assertThrows(RepositoryException.class, () -> repository.insert(entity));
        Assertions.assertNull(repository.get(entity.id));
    }

    @Test
    public void shouldValidateEntityOnUpdate() throws RepositoryException {
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
        RepositoryException error = new RepositoryException("Invalid");
        IValidator<Person> validator = person -> { throw error; };
        ValidatorRepository<String,Person> repository = new ValidatorRepository<>(sourceRepository, validator);

        Person entityBefore = new Person("1", "A");
        Person entityAfter = new Person("1", "A");
        sourceRepository.insert(entityBefore);

        Exception exception = Assertions.assertThrows(RepositoryException.class, () -> repository.update(entityAfter));
        Assertions.assertSame(error, exception);
    }

    @Test
    public void shouldNotUpdateIfValidationFailed() throws RepositoryException {
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(person -> person.id);
        RepositoryException error = new RepositoryException("Invalid");
        IValidator<Person> validator = person -> { throw error; };
        ValidatorRepository<String,Person> repository = new ValidatorRepository<>(sourceRepository, validator);

        Person entityBefore = new Person("1", "A");
        Person entityAfter = new Person("1", "A");
        sourceRepository.insert(entityBefore);

        Assertions.assertThrows(RepositoryException.class, () -> repository.update(entityAfter));
        Assertions.assertSame(entityBefore, sourceRepository.get(entityAfter.id));
    }

}