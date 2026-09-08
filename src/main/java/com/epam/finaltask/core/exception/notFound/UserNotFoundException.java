package com.epam.finaltask.core.exception.notFound;


public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}