package org.example.eiscuno.model.deck;

import org.example.eiscuno.model.exceptions.EmptyDeckException;
import org.example.eiscuno.model.unoenum.EISCUnoEnum;
import org.example.eiscuno.model.card.Card;

import java.util.Collections;
import java.util.Stack;

import java.io.Serializable;
/**
 * Represents a deck of Uno cards.
 */
public class Deck implements Serializable {
    private Stack<Card> deckOfCards;

    /**
     * Returns the stack of cards representing the current deck.
     *
     */
    public Deck() {
        deckOfCards = new Stack<>();
        initializeDeck();
    }

    /**
     * Initializes the UNO deck by creating cards based on the values defined in the {@code EISCUnoEnum} enum.
     * Only valid cards (colored, special, and wild) are added to the deck.
     * After all cards are added, the deck is shuffled.
     */
    private void initializeDeck() {
        for (EISCUnoEnum cardEnum : EISCUnoEnum.values()) {
            if (cardEnum.name().startsWith("GREEN_") ||
                    cardEnum.name().startsWith("YELLOW_") ||
                    cardEnum.name().startsWith("BLUE_") ||
                    cardEnum.name().startsWith("RED_") ||
                    cardEnum.name().startsWith("SKIP_") ||
                    cardEnum.name().startsWith("RESERVE_") ||
                    cardEnum.name().startsWith("TWO_WILD_DRAW_") ||
                    cardEnum.name().equals("FOUR_WILD_DRAW") ||
                    cardEnum.name().equals("WILD")) {
                Card card = new Card(cardEnum.getFilePath(), getCardValue(cardEnum.name()), getCardColor(cardEnum.name()));
                if ("UNKNOWN".equals(card.getValue())) {
                    System.out.println("Carta con valor desconocido: " + cardEnum.name());
                }
                deckOfCards.push(card);
            }
        }
        Collections.shuffle(deckOfCards);
    }
    /**
     * Extracts the value of a card based on its enum name.
     * This includes numeric values, special actions like SKIP and REVERSE, and wild cards.
     *
     * @param name the name of the enum constant representing the card
     * @return the corresponding card value as a String, or "UNKNOWN" if no match is found
     */
    private String getCardValue(String name) {
        if (name.endsWith("0")){
            return "0";
        } else if (name.endsWith("1")){
            return "1";
        } else if (name.endsWith("2")){
            return "2";
        } else if (name.endsWith("3")){
            return "3";
        } else if (name.endsWith("4")){
            return "4";
        } else if (name.endsWith("5")){
            return "5";
        } else if (name.endsWith("6")){
            return "6";
        } else if (name.endsWith("7")){
            return "7";
        } else if (name.endsWith("8")){
            return "8";
        } else if (name.endsWith("9")){
            return "9";
        } else if (name.contains("SKIP")){
            return "SKIP";
        } else if (name.contains("RESERVE")){
            return "REVERSE";
        } else if (name.contains("TWO_WILD_DRAW")){
            return "+2";
        } else if (name.equals("FOUR_WILD_DRAW")){
            return "+4";
        } else if (name.equals("WILD")){
            return "WILD";
        }
        else {
            //return null;
            return "UNKNOWN";
        }

    }
    /**
     * Determines the color of a card based on its enum name.
     * Wild cards (e.g., WILD, +4) return {@code null} as they have no fixed color.
     *
     * @param name the name of the enum constant representing the card
     * @return the color of the card as a String, or {@code null} for wild cards
     */
    private String getCardColor(String name){
        if (name.contains("GREEN")) {
            return "GREEN";
        } else if (name.contains("YELLOW")) {
            return "YELLOW";
        } else if (name.contains("BLUE")) {
            return "BLUE";
        } else if (name.contains("RED")) {
            return "RED";
        } else {
            return null; // comodines como WILD y +4 deben quedar sin color
        }
    }

    /**
     * Removes and returns the top card from the deck.
     *
     * @return the top {@code Card} from the deck
     * @throws EmptyDeckException if the deck is empty and no card can be drawn
     */
    public Card takeCard() throws EmptyDeckException {
        if (deckOfCards.isEmpty()) {
            throw new EmptyDeckException("El mazo está vacío, no se puede tomar más cartas.");
        }
        return deckOfCards.pop();
    }

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return deckOfCards.isEmpty();
    }
    /**
     * Returns the entire stack of cards representing the current deck.
     *
     * @return a {@code Stack<Card>} containing all cards in the deck
     */
    public Stack<Card> getDeckOfCards() {return deckOfCards;}
    /**
     * Sets the deck to the given stack of cards.
     *
     * @param deckOfCards the {@code Stack<Card>} to be used as the new deck
     */
    public void setDeckOfCards(Stack<Card> deckOfCards) {this.deckOfCards = deckOfCards;}
}
