package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
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
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    throw new RuntimeException(e);
                }

                if (gameUno.isGameOver()) {
                    return;
                }

                // Verificamos si la máquina pierde turno
                if (gameUno.isSkipMachineTurn()) {
                    gameUno.clearSkipMachineTurn();
                    hasPlayerPlayed = false; // Pasa turno al humano
                    continue;
                }

                // Máquina juega con normalidad
                putCardOnTheTable();

                // Verificamos si el humano fue saltado (SKIP o REVERSE)
                if (gameUno.isSkipHumanTurn()) {
                    gameUno.clearSkipHumanTurn();
                    continue;
                }else {
                    hasPlayerPlayed = false; // Pasar turno al humano
                }
            }
        }
    }

    private void putCardOnTheTable() {
        // Buscamos carta jugable
        for (int i = 0; i < machinePlayer.getCardsPlayer().size(); i++) {
            Card card = machinePlayer.getCard(i);
            if (gameUno.canPlay(card)) {
                gameUno.playCard(card);
                tableImageView.setImage(card.getImage());
                machinePlayer.removeCard(i);
                //gameUnocontroller.printCardsMachinePlayer(); // Al ser hilos genera error
                Platform.runLater(() -> gameUnocontroller.printCardsMachinePlayer());

                // Manejamos los colores de las cartas especiales
                if ("WILD".equals(card.getValue()) || "+4".equals(card.getValue())) {
                    // La máquina elige color de forma aleatoria
                    String[] colors = {"RED", "GREEN", "BLUE", "YELLOW"};
                    String selectedColor = colors[(int)(Math.random() * colors.length)];
                    try {
                        table.getCurrentCardOnTheTable().setColor(selectedColor);
                        System.out.println("Máquina eligió el color: " + selectedColor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        } catch (IllegalStateException e) {
            System.out.println("Mazo vacío. No se puede robar más.");
        }
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
}