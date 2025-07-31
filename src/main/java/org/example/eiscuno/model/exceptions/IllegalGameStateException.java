package org.example.eiscuno.model.exceptions;

public class IllegalGameStateException extends RuntimeException{

    public IllegalGameStateException() {
        super();
    }

    public IllegalGameStateException(String message) {
        super(message);
    }

    public IllegalGameStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalGameStateException(Throwable cause) {
        super(cause);
    }

}
