package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.exceptions.EmptyDeckException;
import org.example.eiscuno.model.exceptions.InvalidCardPlayException;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private GameUno gameUno;
    private Deck deck;
    private volatile boolean hasPlayerPlayed;
    private GameUnoController gameUnocontroller;

    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, GameUno gameUno, Deck deck, GameUnoController gameUnocontroller) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.gameUno = gameUno;
        this.deck = deck;
        this.hasPlayerPlayed = false;
        this.gameUnocontroller = gameUnocontroller;
    }

    public void run() {
        while (!gameUno.isGameOver()) {
            if (hasPlayerPlayed) {
                try {
                    Thread.sleep(2000);

                    if (gameUno.isGameOver()) {
                        return;
                    }

                    // Verificamos si la máquina debe perder turno
                    if (gameUno.isSkipMachineTurn()) {
                        gameUno.clearSkipMachineTurn();
                        hasPlayerPlayed = false;
                        continue; // Vuelve al inicio del ciclo (turno humano)
                    }

                    // Máquina juega con normalidad
                    putCardOnTheTable();

                    // Manejamos efectos especiales que hacen repetir turno
                    if (gameUno.isSkipHumanTurn()) {
                        gameUno.clearSkipHumanTurn();
                        // No cambiamos hasPlayerPlayed para que repita turno
                    } else {
                        hasPlayerPlayed = false; // Pasamos turno al humano normalmente
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Hilo interrumpido");
                }
            }
        }
    }

    private void putCardOnTheTable() {
        // Buscamos carta jugable
        for (int i = 0; i < machinePlayer.getCardsPlayer().size(); i++) {
            Card card = machinePlayer.getCard(i);
            if (gameUno.canPlay(card)) {
                try {
                    gameUno.playCard(card);
                    tableImageView.setImage(card.getImage());
                    machinePlayer.removeCard(i);

                    Platform.runLater(() -> gameUnocontroller.printCardsMachinePlayer());

                    // Manejamos los colores de las cartas especiales
                    if ("WILD".equals(card.getValue()) || "+4".equals(card.getValue())) {
                        String[] colors = {"RED", "GREEN", "BLUE", "YELLOW"};
                        String selectedColor = colors[(int)(Math.random() * colors.length)];
                        try {
                            table.getCurrentCardOnTheTable().setColor(selectedColor);
                            System.out.println("Máquina eligió el color: " + selectedColor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InvalidCardPlayException e) {
                    System.out.println("⚠️ La máquina intentó jugar una carta inválida: " + e.getMessage());
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error de juego");
                        alert.setHeaderText(null);
                        alert.setContentText("La máquina intentó jugar una carta inválida: " + e.getMessage());
                        alert.showAndWait();
                    });
                }
                return;
            }
        }

        // Toma una carta si no puede jugar
        try {
            Card drawnCard = deck.takeCard();
            machinePlayer.addCard(drawnCard);
            Platform.runLater(() -> gameUnocontroller.printCardsMachinePlayer());
            System.out.println("Máquina no puede jugar y roba: " + drawnCard.getValue() + " - " + drawnCard.getColor());
        } catch (EmptyDeckException e) {
            System.out.println("Mazo vacío. No se puede robar más.");
            // Por ahora mostraré una alerta (se puede cambiar)
            // Deberia colocarse en los demás
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Mazo vacío");
                alert.setHeaderText(null);
                alert.setContentText("El mazo está vacío. No se puede robar más cartas.");
                alert.showAndWait();
            });
        }
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
}