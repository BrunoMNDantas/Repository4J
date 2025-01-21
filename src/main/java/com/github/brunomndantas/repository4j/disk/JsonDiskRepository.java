package com.github.brunomndantas.repository4j.disk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.brunomndantas.repository4j.exception.RepositoryException;

import java.util.function.Function;

public class JsonDiskRepository<K,E> extends DiskRepository<K,E> {

    public static final String EXTENSION = "json";
    private static final ObjectMapper MAPPER = new ObjectMapper();


    protected Class<E> entityClass;


    public JsonDiskRepository(String directory, Function<E, K> keyExtractor, Class<E> entityClass) {
        super(directory, EXTENSION, keyExtractor);
        this.entityClass = entityClass;
    }


    @Override
    protected String serialize(E entity) throws RepositoryException {
        try {
            return MAPPER
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new RepositoryException("Error serializing entity!", e);
        }
    }

    @Override
    protected E deserialize(String entityAsString) throws RepositoryException {
        try {
            return MAPPER.readValue(entityAsString, this.entityClass);
        } catch (JsonProcessingException e) {
            throw new RepositoryException("Error deserializing entity!", e);
        }
    }

}