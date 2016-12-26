package com.github.yeriomin.playstoreapi;

public class AuthException extends GooglePlayException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, int code) {
        super(message);
        setCode(code);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
