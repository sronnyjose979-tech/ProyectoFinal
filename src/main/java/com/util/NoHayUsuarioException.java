package com.util;

public class NoHayUsuarioException extends Exception {

    public NoHayUsuarioException() {
    }

    public NoHayUsuarioException(String message) {
        super(message);
    }

    public NoHayUsuarioException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoHayUsuarioException(Throwable cause) {
        super(cause);
    }

    public NoHayUsuarioException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
