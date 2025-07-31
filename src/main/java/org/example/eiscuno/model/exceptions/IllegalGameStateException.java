package org.example.eiscuno.model.exceptions;
/**
 * Custom runtime exception thrown when the UNO game enters an illegal or inconsistent state.
 * <p>
 * This exception is used to indicate programming or logic errors that violate
 * the expected flow or rules of the game.
 */
public class IllegalGameStateException extends RuntimeException{
    /**
     * Constructs a new {@code IllegalGameStateException} with no detail message.
     */
    public IllegalGameStateException() {
        super();
    }
    /**
     * Constructs a new {@code IllegalGameStateException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public IllegalGameStateException(String message) {
        super(message);
    }
    /**
     * Constructs a new {@code IllegalGameStateException} with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the cause of the exception (which is saved for later retrieval)
     */
    public IllegalGameStateException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Constructs a new {@code IllegalGameStateException} with the specified cause.
     *
     * @param cause the cause of the exception (which is saved for later retrieval)
     */
    public IllegalGameStateException(Throwable cause) {
        super(cause);
    }

}
