package org.example.eiscuno.model.exceptions;
/**
 * Custom exception thrown when an attempt is made to draw a card from an empty UNO deck.
 * <p>
 * This exception helps signal game logic errors related to deck exhaustion and can carry
 * an optional message or cause for further context.
 */
public class EmptyDeckException extends Exception{
    /**
     * Constructs a new {@code EmptyDeckException} with no detail message.
     */
    public EmptyDeckException(){
        super();
    }
    /**
     * Constructs a new {@code EmptyDeckException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public EmptyDeckException(String message) {
        super(message);
    }
    /**
     * Constructs a new {@code EmptyDeckException} with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the cause of the exception (which is saved for later retrieval)
     */
    public EmptyDeckException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Constructs a new {@code EmptyDeckException} with the specified cause.
     *
     * @param cause the cause of the exception (which is saved for later retrieval)
     */
    public EmptyDeckException(Throwable cause) {
        super(cause);
    }
}
