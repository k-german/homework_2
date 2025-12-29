package org.hiber.services.exceptions;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Integer id) {
        super("User with id " + id + " not found");
    }
}
