package com.github.brunomndantas.repository4j.disk;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.Person;
import com.github.brunomndantas.repository4j.RepositoryTests;
import com.github.brunomndantas.repository4j.exception.RepositoryException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public abstract class DiskRepositoryTests extends RepositoryTests {

    protected static final String DIRECTORY = "./repository";


    @BeforeEach
    public void startup() throws IOException {
        if(new File(DIRECTORY).exists()) {
            FileUtils.forceDelete(new File(DIRECTORY));
        }
    }

    @Test
    public void shouldCreateDirectory() throws RepositoryException {
        Person entity = new Person("1", "A");
        IRepository<String, Person> repository = createRepository();

        repository.insert(entity);

        Assertions.assertTrue(new File(DIRECTORY).exists());
    }

    @Test
    public void shouldHandleNonExistentDirectoryOnGetAll() throws RepositoryException {
        IRepository<String, Person> repository = createRepository();

        Collection<Person> entities = repository.getAll();

        Assertions.assertNotNull(entities);
        Assertions.assertTrue(entities.isEmpty());
    }

    @Test
    public void shouldHandleNonExistentDirectoryOnGet() throws RepositoryException {
        IRepository<String, Person> repository = createRepository();

        Person entity = repository.get("1");

        Assertions.assertNull(entity);
    }

    @Test
    public void shouldCreateFileForEachEntity() throws RepositoryException {
        Person entityA = new Person("1", "A");
        Person entityB = new Person("2", "B");
        IRepository<String, Person> repository = createRepository();

        repository.insert(entityA);
        File[] files = new File(DIRECTORY).listFiles();
        Assertions.assertNotNull(files);
        Assertions.assertEquals(1, files.length);

        repository.insert(entityB);
        files = new File(DIRECTORY).listFiles();
        Assertions.assertNotNull(files);
        Assertions.assertEquals(2, files.length);
    }

    @Test
    public void shouldDeleteEntityFile() throws RepositoryException {
        Person entityA = new Person("1", "A");
        Person entityB = new Person("2", "B");
        IRepository<String, Person> repository = createRepository();

        repository.insert(entityA);
        repository.insert(entityB);

        repository.delete(entityA.id);
        File[] files = new File(DIRECTORY).listFiles();
        Assertions.assertNotNull(files);
        Assertions.assertEquals(1, files.length);

        repository.delete(entityB.id);
        files = new File(DIRECTORY).listFiles();
        Assertions.assertNotNull(files);
        Assertions.assertEquals(0, files.length);
    }

}