package org.example.eiscuno.model.game;

import javafx.application.Platform;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.player.Player;
/**
 * A background thread that continuously checks if either player has won the UNO game
 * by running out of cards. If a player has no cards left, the game is marked as over,
 * and the appropriate game-over screen is shown on the JavaFX application thread.
 * <p>
 * The check runs every 0.5 seconds until a winner is detected or the thread is stopped.
 */
public class ThreadCheckGameOver extends Thread{
    private final Player humanPlayer;
    private final Player machinePlayer;
    private final GameUnoController gameUnocontroller;
    private final GameUno gameUno;

    private boolean running = true;
    /**
     * Constructs a new {@code ThreadCheckGameOver} that monitors the game state for a winner.
     *
     * @param humanPlayer the human player to monitor
     * @param machinePlayer the machine player to monitor
     * @param gameUnocontroller the controller responsible for updating the UI
     * @param gameUno the game instance to update the game-over state
     */
    public ThreadCheckGameOver(Player humanPlayer, Player machinePlayer, GameUnoController gameUnocontroller, GameUno gameUno) {
        this.humanPlayer = humanPlayer;
        this.machinePlayer = machinePlayer;
        this.gameUnocontroller = gameUnocontroller;
        this.gameUno = gameUno;
    }
    /**
     * Continuously runs in the background, checking every 0.5 seconds
     * whether the human or machine player has no cards left.
     * <p>
     * When a player runs out of cards, it sets the game as over,
     * stops the thread, and updates the UI on the JavaFX application thread
     * to show the game-over screen indicating the winner.
     */
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(500); // Verifica cada 0.5 segundos
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (humanPlayer.getCardsPlayer().isEmpty()) {
                gameUno.setGameOver(true);
                running = false;
                Platform.runLater(() -> gameUnocontroller.showGameOver(true));
            } else if (machinePlayer.getCardsPlayer().isEmpty()) {
                gameUno.setGameOver(true);
                running = false;
                Platform.runLater(() -> gameUnocontroller.showGameOver(false));
            }
        }
    }
    /**
     * Stops the background thread from continuing its game-over checks.
     */
    public void stopRunning() {
        this.running = false;
    }
}

