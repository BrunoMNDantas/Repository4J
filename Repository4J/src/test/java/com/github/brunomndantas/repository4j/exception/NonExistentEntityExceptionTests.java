package com.github.brunomndantas.repository4j.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NonExistentEntityExceptionTests {

    @Test
    public void shouldHaveNoMessageAndNoCause() {
        NonExistentEntityException exception = new NonExistentEntityException();

        Assertions.assertNull(exception.getMessage());
        Assertions.assertNull(exception.getCause());
    }

    @Test
    public void shouldByPassMessage() {
        String message = "Message";

        NonExistentEntityException exception = new NonExistentEntityException(message);

        Assertions.assertEquals(message, exception.getMessage());
    }

    @Test
    public void shouldByMessageAndCause() {
        String message = "Message";
        Throwable cause = new Exception();

        NonExistentEntityException exception = new NonExistentEntityException(message, cause);

        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }

}