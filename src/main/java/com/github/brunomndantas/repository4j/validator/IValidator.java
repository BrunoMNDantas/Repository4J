package com.github.brunomndantas.repository4j.validator;

import com.github.brunomndantas.repository4j.exception.RepositoryException;

public interface IValidator<E> {

    void validate(E entity) throws RepositoryException;

}
