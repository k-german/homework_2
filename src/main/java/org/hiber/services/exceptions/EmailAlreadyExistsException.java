package org.hiber.services.exceptions;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String email) {
        super(String.format("Other user with email \"%s\" already exists in DB table.", email));
    }
}
