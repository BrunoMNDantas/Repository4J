package com.github.brunomndantas.repository4j.disk;

import com.github.brunomndantas.repository4j.Person;
import com.github.brunomndantas.repository4j.exception.RepositoryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class JsonDiskRepositoryTests extends DiskRepositoryTests {

    @Override
    protected JsonDiskRepository<String, Person> createRepository() {
        return new JsonDiskRepository<>(DIRECTORY, person -> person.id, Person.class);
    }


    @Test
    public void shouldCreateFileWithJsonExtension() throws RepositoryException {
        Person entity = new Person("1", "A");
        JsonDiskRepository<String,Person> repository = createRepository();

        repository.insert(entity);

        File[] files = new File(DIRECTORY).listFiles();
        Assertions.assertNotNull(files);

        String fileName = files[0].getName();
        String[] fileNameParts = fileName.split("\\.");
        Assertions.assertTrue(fileNameParts.length > 1);

        String extension = fileNameParts[fileNameParts.length-1];
        Assertions.assertEquals(JsonDiskRepository.EXTENSION, extension);
    }

}