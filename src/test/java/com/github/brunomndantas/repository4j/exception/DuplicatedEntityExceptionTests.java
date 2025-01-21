package com.github.brunomndantas.repository4j.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DuplicatedEntityExceptionTests {

    @Test
    public void shouldHaveNoMessageAndNoCause() {
        DuplicatedEntityException exception = new DuplicatedEntityException();

        Assertions.assertNull(exception.getMessage());
        Assertions.assertNull(exception.getCause());
    }

    @Test
    public void shouldByPassMessage() {
        String message = "Message";

        DuplicatedEntityException exception = new DuplicatedEntityException(message);

        Assertions.assertEquals(message, exception.getMessage());
    }

    @Test
    public void shouldByMessageAndCause() {
        String message = "Message";
        Throwable cause = new Exception();

        DuplicatedEntityException exception = new DuplicatedEntityException(message, cause);

        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }

}