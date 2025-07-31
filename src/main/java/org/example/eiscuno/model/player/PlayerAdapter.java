package org.example.eiscuno.model.player;

import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;


/**
 * Adapter class that allows a Player object to be used as an IPlayer.
 *
 * This class implements the IPlayer interface by internally delegating
 * all method calls to a Player instance.
 */
public class PlayerAdapter implements IPlayer {

    private final Player player;

    /**
     * Constructs a PlayerAdapter with the specified Player.
     *
     * @param player the Player instance to be adapted
     */
    public PlayerAdapter(Player player) {
        this.player = player;
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param card the card to be added
     */
    @Override
    public void addCard(Card card) {
        player.addCard(card);
    }

    /**
     * Returns a copy of the player's hand of cards.
     *
     * @return a list containing the player's cards
     */
    @Override
    public ArrayList<Card> getCardsPlayer() {
        return player.getCardsPlayer();
    }

    /**
     * Removes the card at the specified index from the player's hand.
     *
     * @param index the index of the card to remove
     */
    @Override
    public void removeCard(int index) {
        player.removeCard(index);
    }

    /**
     * Retrieves the card at the specified index from the player's hand.
     *
     * @param index the index of the card to retrieve
     * @return the card at the specified index, or null if the index is invalid
     */
    @Override
    public Card getCard(int index) {
        return player.getCard(index);
    }

    /**
     * Returns the type of the player (e.g., "Human" or "Machine").
     *
     * @return the player type
     */
    public String getPlayerType() {
        return player.getTypePlayer();
    }

    /**
     * Checks if the player has at least one playable card
     * over the specified top card.
     *
     * @param topCard the top card on the discard pile
     * @return true if a playable card exists, false otherwise
     */
    public boolean canPlayCard(Card topCard) {
        return player.hasPlayableCard(topCard);
    }

    /**
     * Returns the number of cards in the player's hand.
     *
     * @return the number of cards the player holds
     */
    public int getHandSize() {
        return player.getCardCount();
    }
}