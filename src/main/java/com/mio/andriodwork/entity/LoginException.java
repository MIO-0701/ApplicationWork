package com.mio.andriodwork.entity;

public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return super.getMessage();
    }
}
