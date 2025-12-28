package org.hiber.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super(String.format("Other user with email \"%s\" already exists in DB table.", email));
    }
}
