package org.example.eiscuno.model.exceptions;
/**
 * Custom exception thrown when a player attempts to play an invalid card
 * that does not follow the rules of the UNO game.
 * <p>
 * This exception helps enforce valid gameplay and provides details about
 * illegal moves or card mismatches.
 */
public class InvalidCardPlayException extends Exception {
    /**
     * Constructs a new {@code InvalidCardPlayException} with no detail message.
     */
    public InvalidCardPlayException() {
        super();
    }
    /**
     * Constructs a new {@code InvalidCardPlayException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidCardPlayException(String message) {
        super(message);
    }
    /**
     * Constructs a new {@code InvalidCardPlayException} with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the cause of the exception (which is saved for later retrieval)
     */
    public InvalidCardPlayException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Constructs a new {@code InvalidCardPlayException} with the specified cause.
     *
     * @param cause the cause of the exception (which is saved for later retrieval)
     */
    public InvalidCardPlayException(Throwable cause) {
        super(cause);
    }
}
