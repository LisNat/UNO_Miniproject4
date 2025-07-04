package org.example.eiscuno.model.game;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

/**
 * Represents a game of Uno.
 * This class manages the game logic and interactions between players, deck, and the table.
 */
public class GameUno implements IGameUno {

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;

    private boolean skipHumanTurn = false;
    private boolean skipMachineTurn = false;

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

    public Card drawCard(Player player) {
        Card card = this.deck.takeCard();
        player.addCard(card);
        return card;
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
            player.addCard(this.deck.takeCard());
        }
    }

    /**
     * Places a card on the table during the game.
     *
     * @param card The card to be placed on the table.
     */
    @Override
    public void playCard(Card card) {
        this.table.addCardOnTheTable(card);

        Player opponent = humanPlayer.getCardsPlayer().contains(card) ?
                machinePlayer : humanPlayer;

        // Manejamos los efectos especiales (solo estan funcionales los sip y reverse)
        switch(card.getValue()) {
            case "+2":
                eatCard(opponent, 2); // Falta
                break;

            case "+4":
                eatCard(opponent, 4); // Falta
                // No break, porque también es WILD

            case "WILD":
                // La lógica de cambio de color se maneja en el controlador
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

    /**
     * Handles the scenario when a player shouts "Uno", forcing the other player to draw a card.
     *
     * @param playerWhoSang The player who shouted "Uno".
     */
    @Override
    public void haveSungOne(String playerWhoSang) {
        if (playerWhoSang.equals("HUMAN_PLAYER")) {
            machinePlayer.addCard(this.deck.takeCard());
        } else {
            humanPlayer.addCard(this.deck.takeCard());
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
        return null;
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

}
