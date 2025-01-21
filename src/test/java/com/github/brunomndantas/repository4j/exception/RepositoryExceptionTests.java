package com.github.brunomndantas.repository4j.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RepositoryExceptionTests {

    @Test
    public void shouldHaveNoMessageAndNoCause() {
        RepositoryException exception = new RepositoryException();

        Assertions.assertNull(exception.getMessage());
        Assertions.assertNull(exception.getCause());
    }

    @Test
    public void shouldByPassMessage() {
        String message = "Message";

        RepositoryException exception = new RepositoryException(message);

        Assertions.assertEquals(message, exception.getMessage());
    }

    @Test
    public void shouldByMessageAndCause() {
        String message = "Message";
        Throwable cause = new Exception();

        RepositoryException exception = new RepositoryException(message, cause);

        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }

}