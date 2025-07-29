package org.example.eiscuno.model.exceptions;

public class InvalidCardPlayException extends Exception {

    public InvalidCardPlayException() {
        super();
    }

    public InvalidCardPlayException(String message) {
        super(message);
    }

    public InvalidCardPlayException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCardPlayException(Throwable cause) {
        super(cause);
    }
}
