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

    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView,
                             GameUno gameUno, Deck deck, GameUnoController gameUnocontroller) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.gameUno = gameUno;
        this.deck = deck;
        this.hasPlayerPlayed = false;
        this.gameUnocontroller = gameUnocontroller;
    }

    @Override
    public void run() {
        while (!gameUno.isGameOver() && !Thread.currentThread().isInterrupted()) {
            if (hasPlayerPlayed) {
                try {
                    // Verificamos si se debe omitir el turno de la máquina
                    if (gameUno.isSkipMachineTurn()) {
                        gameUno.clearSkipMachineTurn();
                        hasPlayerPlayed = false;
                        continue;
                    }

                    // Mostrar la label "Turno de la máquina..." por 1 segundo
                    Platform.runLater(() -> gameUnocontroller.showMachineTurnTemporarily());

                    // Esperar 1 segundo para mostrar el mensaje
                    Thread.sleep(1000);

                    // Verificación adicional por si termina el juego durante la pausa
                    if (gameUno.isGameOver() || Thread.currentThread().isInterrupted()) {
                        return;
                    }

                    // Jugar la carta
                    putCardOnTheTable();

                    if (gameUno.isSkipHumanTurn()) {
                        gameUno.clearSkipHumanTurn();
                    } else {
                        hasPlayerPlayed = false;
                    }

                } catch (InterruptedException e) {
                    System.out.println("ThreadPlayMachine interrumpido");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error inesperado en ThreadPlayMachine: " + e.getMessage());
                }
            }
        }
    }

    private void putCardOnTheTable() {
        for (int i = 0; i < machinePlayer.getCardsPlayer().size(); i++) {
            Card card = machinePlayer.getCard(i);
            if (gameUno.canPlay(card)) {
                try {
                    gameUno.playCard(card);
                    tableImageView.setImage(card.getImage());
                    machinePlayer.removeCard(i);

                    String colorToSet = card.getColor();

                    if ("WILD".equals(card.getValue()) || "+4".equals(card.getValue())) {
                        String[] colors = {"RED", "GREEN", "BLUE", "YELLOW"};
                        String selectedColor = colors[(int)(Math.random() * colors.length)];
                        table.getCurrentCardOnTheTable().setColor(selectedColor);
                        colorToSet = selectedColor;
                        System.out.println("Máquina eligió el color: " + selectedColor);
                    }

                    final String finalColor = colorToSet;
                    Platform.runLater(() -> {
                        gameUnocontroller.printCardsMachinePlayer();
                        gameUnocontroller.updateColorIndicator(finalColor);
                    });

                } catch (InvalidCardPlayException e) {
                    System.out.println("⚠️ Carta inválida: " + e.getMessage());
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

        try {
            Card drawnCard = deck.takeCard();
            machinePlayer.addCard(drawnCard);
            Platform.runLater(() -> gameUnocontroller.printCardsMachinePlayer());
            System.out.println("Máquina robó: " + drawnCard.getValue() + " - " + drawnCard.getColor());
        } catch (EmptyDeckException e) {
            System.out.println("Mazo vacío. No se puede robar más.");
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