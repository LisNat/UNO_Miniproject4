package org.example.eiscuno.model.game;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.exceptions.EmptyDeckException;
import org.example.eiscuno.model.exceptions.IllegalGameStateException;
import org.example.eiscuno.model.exceptions.InvalidCardPlayException;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.sql.SQLOutput;

/**
 * Represents a game of Uno.
 * This class manages the game logic and interactions between players, deck, and the table.
 */
public class GameUno implements IGameUno  {

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;

    private boolean skipHumanTurn = false;
    private boolean skipMachineTurn = false;

    private boolean gameOver = false;

    /**
     * Constructs a new GameUno instance.
     *
     * @param humanPlayer   The human player participating in the game.
     * @param machinePlayer The machine player participating in the game.
     * @param deck          The deck of cards used in the game.
     * @param table         The table where cards are placed during the game.
     */
    public GameUno(Player humanPlayer, Player machinePlayer, Deck deck, Table table) {
        this.humanPlayer = humanPlayer;
        this.machinePlayer = machinePlayer;
        this.deck = deck;
        this.table = table;
    }

    /**
     * Starts the Uno game by distributing cards to players.
     * The human player and the machine player each receive 10 cards from the deck.
     */
    @Override
    public void startGame() {
        try {
            for (int i = 0; i < 10; i++) {
                if (i < 5) {
                    humanPlayer.addCard(this.deck.takeCard());
                } else {
                    machinePlayer.addCard(this.deck.takeCard());
                }
            }

            // Cualquier carta como Carta inicial
            //this.table.addCardOnTheTable(this.deck.takeCard());

            // Solo cartas normales pueden ser iniciales
            Card initialCard;
            do {
                initialCard = this.deck.takeCard();
            } while (isSpecialCard(initialCard));

            this.table.addCardOnTheTable(initialCard);

        } catch (EmptyDeckException e) {
            System.out.println("No se pudo iniciar el juego: " + e.getMessage());
        }

    }

    private boolean isSpecialCard(Card card) {
        String value = card.getValue();
        return value == null || value.equals("+2") || value.equals("+4") || value.equals("SKIP") || value.equals("WILD") || value.equals("REVERSE");
    }

    public boolean isSkipHumanTurn() {
        return skipHumanTurn;
    }
    public void clearSkipHumanTurn() {
        this.skipHumanTurn = false;
    }

    public boolean isSkipMachineTurn() {
        return skipMachineTurn;
    }
    public void clearSkipMachineTurn() {
        this.skipMachineTurn = false;
    }

    public Card drawCard(Player player) throws EmptyDeckException{
        if (isGameOver()) {
            throw new IllegalGameStateException("No se pueden robar cartas: el juego ha terminado");
        }

        if (deck.isEmpty()) {
            System.out.println("Mazo vacío. No se pueden tomar más cartas.");

            // Validamos si nadie puede jugar (el juego debe terminar)
            if (!canAnyPlayerPlay()) {
                endGameByEmptyDeck();
            }
            //return null;
            throw new EmptyDeckException("El mazo está vacío");
        }

        try{
            Card card = this.deck.takeCard();
            player.addCard(card);
            // Notificamos cambios si hay un listener
            if (listener != null) {
                Platform.runLater(() -> {
                    if (player == humanPlayer) {
                        listener.onHumanCardsChanged();
                    } else {
                        listener.onMachineCardsChanged();
                    }
                });
            }
            return card;
        } catch (EmptyDeckException e){
            // Esto en teoría nunca debería ocurrir porque ya verificamos antes isEmpty()
            System.err.println("Error inesperado: " + e.getMessage());
            throw e; // Relanzamos la excepción
        }
    }

    /**
     * Allows a player to draw a specified number of cards from the deck.
     *
     * @param player        The player who will draw cards.
     * @param numberOfCards The number of cards to draw.
     */
    @Override
    public void eatCard(Player player, int numberOfCards) {
        for (int i = 0; i < numberOfCards; i++) {
            try {
                player.addCard(this.deck.takeCard());
            } catch (EmptyDeckException e) {
                System.out.println("No se pudo robar carta: " + e.getMessage());
                break; // Salimos del ciclo si ya no hay cartas
            }
        }
        // Llamar al listener para actualizar visualmente
        if (listener != null) {
            if (player == humanPlayer) {
                Platform.runLater(listener::onHumanCardsChanged);
            } else {
                Platform.runLater(listener::onMachineCardsChanged);
            }
        }
    }

    /**
     * Places a card on the table during the game.
     *
     * @param card The card to be placed on the table.
     */
    @Override
    public void playCard(Card card) throws InvalidCardPlayException {
        if (gameOver) {
            throw new IllegalGameStateException("No se puede jugar carta: el juego ha terminado.");
        }

        Card topCard = table.getCurrentCardOnTheTable();
        if (!card.canBePlayedOver(topCard)) {
            throw new InvalidCardPlayException("La carta seleccionada no puede jugarse sobre: " + topCard.getValue() + " - " + topCard.getColor());
        }

        this.table.addCardOnTheTable(card);

        Player opponent = humanPlayer.getCardsPlayer().contains(card) ?
                machinePlayer : humanPlayer;

        // Manejamos los efectos especiales
        switch(card.getValue()) {
            case "+2":
                eatCard(opponent, 2);
                if (opponent == humanPlayer) {
                    skipHumanTurn = true;
                    System.out.println("Máquina jugó +2. Humano roba 2 cartas y pierde turno.");
                } else {
                    skipMachineTurn = true;
                    System.out.println("Humano jugó +2. Máquina roba 2 cartas y pierde turno.");
                }
                break;

            case "+4":
                eatCard(opponent, 4);
                if (opponent == humanPlayer) {
                    skipHumanTurn = true;
                    System.out.println("Máquina jugó +4. Humano roba 4 cartas y pierde turno.");
                } else {
                    skipMachineTurn = true;
                    System.out.println("Humano jugó +4. Máquina roba 4 cartas y pierde turno.");
                }
                break;

            case "SKIP":
                if (opponent == humanPlayer) {
                    System.out.println("Máquina usó SKIP: humano pierde turno");
                    skipHumanTurn = true;
                } else {
                    System.out.println("Humano usó SKIP: máquina pierde turno");
                    skipMachineTurn = true;
                }
                break;

            case "REVERSE":
                // En un juego de 2 jugadores, REVERSE funciona como SKIP
                if (opponent == humanPlayer) {
                    System.out.println("Máquina usó REVERSE: humano pierde turno");
                    skipHumanTurn = true;
                } else {
                    System.out.println("Humano usó REVERSE: máquina pierde turno");
                    skipMachineTurn = true;
                }
                break;
        }
    }

    private void endGameByEmptyDeck() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fin del Juego");
            alert.setHeaderText("¡El mazo se agotó!");
            alert.setContentText("Nadie puede jugar más. El juego ha terminado.");
            alert.showAndWait();
            setGameOver(true);
        });
    }

    /**
     * Handles the scenario when a player shouts "Uno", forcing the other player to draw a card.
     *
     * @param playerWhoSang The player who shouted "Uno".
     */
    @Override
    public void haveSungOne(String playerWhoSang) {
        try {
            if (playerWhoSang.equals("HUMAN_PLAYER")) {
                machinePlayer.addCard(this.deck.takeCard());
            } else {
                humanPlayer.addCard(this.deck.takeCard());
            }
        } catch (EmptyDeckException e) {
            System.out.println("No se pudo castigar por no decir UNO: " + e.getMessage());
        }
    }

    /**
     * Retrieves the current visible cards of the human player starting from a specific position.
     *
     * @param posInitCardToShow The initial position of the cards to show.
     * @return An array of cards visible to the human player.
     */
    @Override
    public Card[] getCurrentVisibleCardsHumanPlayer(int posInitCardToShow) {
        int totalCards = this.humanPlayer.getCardsPlayer().size();
        int numVisibleCards = Math.min(4, totalCards - posInitCardToShow);
        Card[] cards = new Card[numVisibleCards];

        for (int i = 0; i < numVisibleCards; i++) {
            cards[i] = this.humanPlayer.getCard(posInitCardToShow + i);
        }

        return cards;
    }

    /**
     * Checks if the game is over.
     *
     * @return True if the deck is empty, indicating the game is over; otherwise, false.
     */
    @Override
    public Boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isDeckEmpty() {
        return deck.isEmpty();
    }

    public boolean canPlay(Card card) {
        try {
            Card topCard = this.table.getCurrentCardOnTheTable();

            // Comodines siempre se pueden jugar
            if ("WILD".equals(card.getValue()) || "+4".equals(card.getValue())) {
                return true;
            }

            // Coincide en color o en valor
            return (card.getColor() != null && card.getColor().equals(topCard.getColor())) ||
                    (card.getValue() != null && card.getValue().equals(topCard.getValue()));

        } catch (IndexOutOfBoundsException e) {
            return !isSpecialCard(card); // si no hay carta en mesa, solo normales
        }
    }

    public Player getMachinePlayer() {
        return this.machinePlayer;
    }

    private IGameEventListener listener;

    public void setGameEventListener(IGameEventListener listener) {
        this.listener = listener;
    }

    public boolean canAnyPlayerPlay() {
        Card topCard = table.getCurrentCardOnTheTable();
        return humanPlayer.hasPlayableCard(topCard) || machinePlayer.hasPlayableCard(topCard);
    }

}
