package org.example.eiscuno.model.player;

import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * Represents a player in the Uno game.
 */
public class Player implements IPlayer, Serializable  {
    private ArrayList<Card> cardsPlayer;
    private String typePlayer;
    private final CardChecker cardChecker;
    private String playerName;
    /**
     * Constructs a Player object with a specified player type.
     *
     * Initializes the player's hand of cards, sets the player type,
     * and creates a new instance of CardChecker for card validation.
     *
     * @param typePlayer the type of the player (e.g., "Human" or "Machine")
     */
    public Player(String typePlayer) {
        this.cardsPlayer = new ArrayList<Card>();
        this.typePlayer = typePlayer;
        this.cardChecker = new CardChecker();
        this.playerName = "";
    }
    /**
     * Utility class responsible for checking if a player has any playable cards.
     *
     * This class implements Serializable to support the serialization of the Player class
     * that contains it as a field.
     */
    private static class CardChecker implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * Checks if there is at least one card in the list that can be played
         * over the given top card.
         *
         * @param cards the list of cards in the player's hand
         * @param topCard the top card on the discard pile
         * @return true if a playable card exists, false otherwise
         */
        public boolean hasPlayableCard(ArrayList<Card> cards, Card topCard) {
            for (Card card : cards) {
                if (card.canBePlayedOver(topCard)) {
                    return true;
                }
            }
            return false;
        }
    }
    /**
     * Adds a card to the player's hand.
     *
     * @param card the card to be added
     */
    @Override
    public void addCard(Card card) {
        cardsPlayer.add(card);
    }
    /**
     * Returns a copy of the player's hand of cards.
     *
     * This method returns a new list to preserve encapsulation and prevent
     * external modifications to the original hand.
     *
     * @return a copy of the list of cards in the player's hand
     */
    @Override
    public ArrayList<Card> getCardsPlayer() {
        return new ArrayList<>(cardsPlayer); // Devuelve copia para proteger encapsulamiento
    }
    /**
     * Removes a card from the player's hand at the specified index.
     *
     * If the index is invalid, no action is taken.
     *
     * @param index the position of the card to be removed
     */
    @Override
    public void removeCard(int index) {
        if (index >= 0 && index < cardsPlayer.size()) {
            cardsPlayer.remove(index);
        }
    }
    /**
     * Retrieves the card at the specified index from the player's hand.
     *
     * If the index is out of bounds, returns null.
     *
     * @param index the position of the card to retrieve
     * @return the card at the specified index, or null if the index is invalid
     */
    @Override
    public Card getCard(int index) {
        if (index >= 0 && index < cardsPlayer.size()) {
            return cardsPlayer.get(index);
        }
        return null;
    }
    /**
     * Returns the type of the player.
     *
     * @return the player type (e.g., "Human" or "Machine")
     */
    public String getTypePlayer() {
        return typePlayer;
    }
    /**
     * Checks if the player has at least one card that can be played
     * over the given top card.
     *
     * @param topCard the top card on the discard pile
     * @return true if the player has a playable card, false otherwise
     */
    public boolean hasPlayableCard(Card topCard) {
        return cardChecker.hasPlayableCard(cardsPlayer, topCard);
    }
    /**
     * Returns the number of cards in the player's hand.
     *
     * @return the total count of cards the player currently holds
     */
    public int getCardCount() {
        return cardsPlayer.size();
    }
    /**
     * Returns the player's name.
     *
     * @return the player name.
     */
    public String getPlayerName() {
        return playerName;
    }
    /**
     * Configure the player's name.
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}

