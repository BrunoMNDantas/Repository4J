package com.github.brunomndantas.repository4j.disk;

import com.github.brunomndantas.repository4j.IRepository;
import com.github.brunomndantas.repository4j.exception.DuplicatedEntityException;
import com.github.brunomndantas.repository4j.exception.NonExistentEntityException;
import com.github.brunomndantas.repository4j.exception.RepositoryException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;

public abstract class DiskRepository<K,E> implements IRepository<K,E> {

    protected String directory;
    protected String fileExtension;
    protected Function<E,K> keyExtractor;


    public DiskRepository(String directory, String fileExtension, Function<E,K> keyExtractor) {
        this.directory = directory;
        this.fileExtension = fileExtension;
        this.keyExtractor = keyExtractor;
    }


    @Override
    public Collection<E> getAll() throws RepositoryException {
        Collection<E> entities = new LinkedList<>();
        File[] files = new File(this.directory).listFiles();

        if(files != null) {
            String entityAsString;
            for(File file: files) {
                try {
                    entityAsString = FileUtils.readFileToString(file);
                    entities.add(deserialize(entityAsString));
                } catch (IOException e) {
                    throw new RepositoryException("Error reading file:" + file.getPath(), e);
                }
            }
        }

        return entities;
    }

    @Override
    public E get(K key) throws RepositoryException {
        String filePath = buildFilePath(key);
        File file = new File(filePath);

        try {
            if(file.exists()) {
                String entityAsString = FileUtils.readFileToString(file);
                return deserialize(entityAsString);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RepositoryException("Error reading file:" + file.getPath(), e);
        }
    }

    @Override
    public void insert(E entity) throws RepositoryException {
        K key = this.keyExtractor.apply(entity);
        String entityAsString = serialize(entity);
        String filePath = buildFilePath(key);
        File file = new File(filePath);

        if(file.exists()) {
            throw new DuplicatedEntityException("There is already a entity with key:" + key);
        }

        try {
            FileUtils.write(file, entityAsString, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RepositoryException("Error writing file for entity with key:" + key, e);
        }
    }

    @Override
    public void update(E entity) throws RepositoryException {
        K key = this.keyExtractor.apply(entity);
        String entityAsString = serialize(entity);
        String filePath = buildFilePath(key);
        File file = new File(filePath);

        if(!file.exists()) {
            throw new NonExistentEntityException("There is no entity with key:" + key);
        }

        try {
            FileUtils.write(file, entityAsString, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RepositoryException("Error writing file for entity with key:" + key, e);
        }
    }

    @Override
    public void delete(K key) throws RepositoryException {
        String filePath = buildFilePath(key);
        File file = new File(filePath);

        try {
            if(file.exists()) {
                FileUtils.forceDelete(file);
            }
        } catch (IOException e) {
            throw new RepositoryException("Error deleting file:" + file.getPath(), e);
        }
    }

    protected String buildFilePath(K key) throws RepositoryException {
        String fileDirectory = buildFileDirectory(key);
        String fileName = buildFileName(key);
        String filePath = fileDirectory + File.separator + fileName + "." + this.fileExtension;
        return filePath;
    }

    protected String buildFileDirectory(K key) throws RepositoryException {
        new File(this.directory).mkdirs();
        return this.directory;
    }

    protected String buildFileName(K key) throws RepositoryException {
        return key.toString();
    }


    protected abstract String serialize(E entity) throws RepositoryException;
    protected abstract E deserialize(String entityAsString) throws RepositoryException;

}