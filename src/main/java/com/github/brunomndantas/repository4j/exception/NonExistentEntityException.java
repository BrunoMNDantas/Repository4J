package com.github.brunomndantas.repository4j.exception;

public class NonExistentEntityException extends RepositoryException {

    public NonExistentEntityException() {
        super();
    }

    public NonExistentEntityException(String message) {
        super(message);
    }

    public NonExistentEntityException(String message, Throwable cause) {
        super(message, cause);
    }

}