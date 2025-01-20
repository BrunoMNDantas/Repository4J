package com.github.brunomndantas.repository4j.exception;

public class DuplicatedEntityException extends RepositoryException {

    public DuplicatedEntityException() {
        super();
    }

    public DuplicatedEntityException(String message) {
        super(message);
    }

    public DuplicatedEntityException(String message, Throwable cause) {
        super(message, cause);
    }

}