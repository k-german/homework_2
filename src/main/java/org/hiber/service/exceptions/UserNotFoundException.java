package org.hiber.service.exceptions;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found");
    }
}
