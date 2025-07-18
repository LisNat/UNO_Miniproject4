package org.example.eiscuno.model.exceptions;

public class EmptyDeckException extends Exception{

    public EmptyDeckException(){
        super();
    }

    public EmptyDeckException(String message) {
        super(message);
    }

    public EmptyDeckException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyDeckException(Throwable cause) {
        super(cause);
    }
}
