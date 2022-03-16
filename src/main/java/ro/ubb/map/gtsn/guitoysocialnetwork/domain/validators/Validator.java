package ro.ubb.map.gtsn.guitoysocialnetwork.domain.validators;

import ro.ubb.map.gtsn.guitoysocialnetwork.domain.exceptions.ValidationException;

public interface Validator<T>{
    void validate(T entity) throws ValidationException;
}
