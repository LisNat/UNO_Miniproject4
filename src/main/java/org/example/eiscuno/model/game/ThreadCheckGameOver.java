package org.example.eiscuno.model.game;

import javafx.application.Platform;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.player.Player;

public class ThreadCheckGameOver extends Thread{
    private final Player humanPlayer;
    private final Player machinePlayer;
    private final GameUnoController gameUnocontroller;
    private final GameUno gameUno;

    private boolean running = true;

    public ThreadCheckGameOver(Player humanPlayer, Player machinePlayer, GameUnoController gameUnocontroller, GameUno gameUno) {
        this.humanPlayer = humanPlayer;
        this.machinePlayer = machinePlayer;
        this.gameUnocontroller = gameUnocontroller;
        this.gameUno = gameUno;
    }
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
                Platform.runLater(() -> gameUnocontroller.showGameOver("¡Ganaste!"));
            } else if (machinePlayer.getCardsPlayer().isEmpty()) {
                gameUno.setGameOver(true);
                running = false;
                Platform.runLater(() -> gameUnocontroller.showGameOver("Perdiste"));
            }
        }
    }

    public void stopRunning() {
        this.running = false;
    }
}

