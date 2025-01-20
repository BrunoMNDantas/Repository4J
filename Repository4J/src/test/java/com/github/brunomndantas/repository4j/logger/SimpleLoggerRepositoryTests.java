package com.github.brunomndantas.repository4j.logger;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.Person;
import com.github.brunomndantas.repository4j.RepositoryTests;
import com.github.brunomndantas.repository4j.exception.RepositoryException;
import com.github.brunomndantas.repository4j.memory.MemoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;

public class SimpleLoggerRepositoryTests extends RepositoryTests {

    @Override
    protected SimpleLoggerRepository<String, Person> createRepository() {
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        return new SimpleLoggerRepository<>(sourceRepository, keyExtractor, System.out::println);
    }


    @Test
    public void logEnteringOnGetAll() throws RepositoryException {
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        repository.getAll();

        Assertions.assertTrue(messages.contains("Entering Get All"));
    }

    @Test
    public void logLeavingFromGetAll() throws RepositoryException {
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        repository.getAll();

        Assertions.assertTrue(messages.contains("Leaving Get All"));
    }

    @Test
    public void logReturnOfGetAll() throws RepositoryException {
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        repository.insert(new Person("1", "A"));
        repository.insert(new Person("2", "B"));
        repository.getAll();

        Assertions.assertTrue(messages.contains("Get All returning 2 entities"));
    }

    @Test
    public void logExceptionOnGetAll() throws RepositoryException {
        String error = "::ERROR::";
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor){
            @Override
            public Collection<Person> getAll() {
                throw new IllegalArgumentException(error);
            }
        };
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        try{
            repository.getAll();
            Assertions.fail("Exception should be thrown!");
        } catch (Exception e) {
            Assertions.assertTrue(messages.contains("Exception on Get All message:" + error));
        }
    }

    @Test
    public void logEnteringOnGet() throws RepositoryException {
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        Person entity = new Person("1", "A");
        sourceRepository.insert(entity);
        repository.get(entity.id);

        Assertions.assertTrue(messages.contains("Entering Get with key:" + entity.id));
    }

    @Test
    public void logLeavingFromGet() throws RepositoryException {
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        Person entity = new Person("1", "A");
        sourceRepository.insert(entity);
        repository.get(entity.id);

        Assertions.assertTrue(messages.contains("Leaving Get with key:" + entity.id));
    }

    @Test
    public void logReturnOfGet() throws RepositoryException {
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        Person entity = new Person("1", "A");
        sourceRepository.insert(entity);
        repository.get(entity.id);

        Assertions.assertTrue(messages.contains("Get returning entity for key:" + entity.id));

        String nonExistentId = "Non Existent Id";
        repository.get(nonExistentId);

        Assertions.assertTrue(messages.contains("Get returning null for key:" + nonExistentId));
    }

    @Test
    public void logExceptionOnGet() {
        String error = "::ERROR::";
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor){
            @Override
            public Person get(String key) {
                throw new IllegalArgumentException(error);
            }
        };
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        try{
            repository.get("");
            Assertions.fail("Exception should be thrown!");
        } catch (Exception e) {
            Assertions.assertTrue(messages.contains("Exception on Get with key: message:" + error));
        }
    }

    @Test
    public void logEnteringOnInsert() throws RepositoryException {
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        Person entity = new Person("1", "A");
        repository.insert(entity);

        Assertions.assertTrue(messages.contains("Entering Insert with key:" + entity.id));
    }

    @Test
    public void logLeavingFromInsert() throws RepositoryException {
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        Person entity = new Person("1", "A");
        repository.insert(entity);

        Assertions.assertTrue(messages.contains("Leaving Insert with key:" + entity.id));
    }

    @Test
    public void logExceptionOnInsert() {
        String error = "::ERROR::";
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor) {
            @Override
            public void insert(Person entity) {
                throw new IllegalArgumentException(error);
            }
        };
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        Person entity = new Person("1", "A");
        try{
            repository.insert(entity);
            Assertions.fail("Exception should be thrown!");
        } catch (Exception e) {
            Assertions.assertTrue(messages.contains("Exception on Insert with key:" + entity.id + " message:" + error));
        }
    }

    @Test
    public void logEnteringOnUpdate() throws RepositoryException {
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        Person entity = new Person("1", "A");
        sourceRepository.insert(entity);
        repository.update(entity);

        Assertions.assertTrue(messages.contains("Entering Update with key:" + entity.id));
    }

    @Test
    public void logLeavingFromUpdate() throws RepositoryException {
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        Person entity = new Person("1", "A");
        sourceRepository.insert(entity);
        repository.update(entity);

        Assertions.assertTrue(messages.contains("Leaving Update with key:" + entity.id));
    }

    @Test
    public void logExceptionOnUpdate() throws RepositoryException {
        String error = "::ERROR::";
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor) {
            @Override
            public void update(Person entity) {
                throw new IllegalArgumentException(error);
            }
        };
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        Person entity = new Person("1", "A");
        sourceRepository.insert(entity);

        try{
            repository.update(entity);
            Assertions.fail("Exception should be thrown!");
        } catch (Exception e) {
            Assertions.assertTrue(messages.contains("Exception on Update with key:" + entity.id + " message:" + error));
        }
    }

    @Test
    public void logEnteringOnDelete() throws RepositoryException {
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        Person entity = new Person("1", "A");
        sourceRepository.insert(entity);
        repository.delete(entity.id);

        Assertions.assertTrue(messages.contains("Entering Delete with key:" + entity.id));
    }

    @Test
    public void logLeavingFromDelete() throws RepositoryException {
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor);
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        Person entity = new Person("1", "A");
        sourceRepository.insert(entity);
        repository.delete(entity.id);

        Assertions.assertTrue(messages.contains("Leaving Delete with key:" + entity.id));
    }

    @Test
    public void logExceptionOnDelete() throws RepositoryException {
        String error = "::ERROR::";
        Collection<String> messages = new LinkedList<>();
        Function<Person,String> keyExtractor = person -> person.id;
        IRepository<String,Person> sourceRepository = new MemoryRepository<>(keyExtractor){
            @Override
            public void delete(String key) {
                throw new IllegalArgumentException(error);
            }
        };
        SimpleLoggerRepository<String,Person> repository = new SimpleLoggerRepository<>(sourceRepository, keyExtractor, messages::add);

        Person entity = new Person("1", "A");
        sourceRepository.insert(entity);
        try {
            repository.delete(entity.id);
            Assertions.fail("Exception should be thrown!");
        } catch (Exception e) {
            Assertions.assertTrue(messages.contains("Exception on Delete with key:" + entity.id + " message:" + error));
        }
    }

}