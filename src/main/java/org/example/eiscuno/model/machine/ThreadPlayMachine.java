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
/**
 * Thread responsible for managing the machine player's turn in the UNO game.
 * <p>
 * This thread continuously runs while the game is active and controls the machine's actions:
 * - Skipping turns if required
 * - Displaying the "Machine's turn" label temporarily
 * - Playing a valid card from the machine's hand or drawing a card if no valid play exists
 * - Handling special cards like WILD and +4 by randomly selecting a color
 * - Updating the UI asynchronously via the JavaFX application thread
 * <p>
 * The thread can be interrupted to stop execution gracefully.
 */
public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private GameUno gameUno;
    private Deck deck;
    private volatile boolean hasPlayerPlayed;
    private GameUnoController gameUnocontroller;
    /**
     * Constructs a new {@code ThreadPlayMachine} to control the machine player's actions.
     *
     * @param table the game table where cards are played
     * @param machinePlayer the machine player whose turn this thread manages
     * @param tableImageView the UI component displaying the current card on the table
     * @param gameUno the main game logic controller
     * @param deck the deck of cards to draw from
     * @param gameUnocontroller the UI controller for updating the game interface
     */
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
    /**
     * Runs the thread loop that manages the machine player's turn.
     * <p>
     * While the game is not over and the thread is not interrupted:
     * - Checks if the machine's turn should be skipped and clears the skip flag if so.
     * - Displays a temporary "Machine's turn" label for 1 second.
     * - Plays a valid card or draws if no playable card is available.
     * - Clears the human skip flag if set.
     * <p>
     * Handles interruptions and unexpected exceptions gracefully.
     */
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
    /**
     * Attempts to play a valid card from the machine player's hand onto the table.
     * <p>
     * Iterates through the machine player's cards, playing the first valid card found.
     * For WILD or +4 cards, a random color is selected and set on the current card.
     * Updates the UI to reflect the played card and the machine player's hand.
     * <p>
     * If no playable card is found, the machine draws a card from the deck and updates the UI.
     * <p>
     * Displays warning alerts if an invalid card is played or if the deck is empty.
     */
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
                    System.out.println(" Carta inválida: " + e.getMessage());
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
    /**
     * Sets whether the machine player has completed its turn and played a card.
     *
     * @param hasPlayerPlayed true if the machine player has played a card this turn; false otherwise
     */
    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }
}