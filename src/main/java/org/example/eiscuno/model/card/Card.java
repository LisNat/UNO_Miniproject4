package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.eiscuno.model.unoenum.EISCUnoEnum;
import org.example.eiscuno.model.prototype.Prototype;

import java.io.Serializable;

/**
 * Represents a card in the Uno game.
 */
public class Card implements Serializable, Prototype<Card> {
    private String url;
    private String value;
    private String color;

    // No se tendrán en cuenta para la serialización.
    private transient Image image;
    private transient ImageView cardImageView;

    /**
     * Constructs a Card with the specified image URL and name.
     *
     * @param url the URL of the card image
     * @param value of the card
     * @param color of the card
     */
    public Card(String url, String value, String color) {
        this.url = url;
        this.value = value;
        this.color = color;
        //this.image = new Image(String.valueOf(getClass().getResource(url)));
        //this.cardImageView = createCardImageView();
        loadTransientFields();
    }

    /**
     * Reconstructs transient fields after deserialization.
     */
    public void loadTransientFields() {
        this.image = new Image(String.valueOf(getClass().getResource(url)));
        this.cardImageView = createCardImageView();
    }
    /**
     * Creates and configures the ImageView for the card.
     *
     * @return the configured ImageView of the card
     */
    private ImageView createCardImageView() {
        ImageView card = new ImageView(this.image);
        card.setY(16);
        card.setFitHeight(90);
        card.setFitWidth(70);
        return card;
    }

    /**
     * Gets the ImageView representation of the card.
     *
     * @return the ImageView of the card
     */
    public ImageView getCard() {
        return cardImageView;
    }

    /**
     * Gets the image of the card.
     *
     * @return the Image of the card
     */
    public Image getImage() {
        return image;
    }
    /**
     * Returns the value of the card (e.g., number or special action).
     *
     * @return the card's value as a String
     */
    public String getValue() {
        return value;
    }
    /**
     * Returns the color of the card (e.g., red, blue, green, yellow, or none for wild cards).
     *
     * @return the card's color as a String
     */
    public String getColor() {
        return color;
    }
    /**
     * Sets the color of the card.
     *
     * @param color the new color to assign to the card
     */
    public void setColor(String color) {
        this.color = color;
    }
    /**
     * Returns the URL associated with the card, typically used for displaying its image.
     *
     * @return the card's image URL as a String
     */
    public String getUrl() {return url;}
    /**
     * Determines if this card can be legally played over the given top card
     * according to UNO rules.
     * <p>
     * Wild and +4 cards can always be played. Otherwise, the card can be played
     * if it shares the same color or value with the top card.
     *
     * @param topCard the card currently on top of the discard pile
     * @return true if this card can be played, false otherwise
     */
    public boolean canBePlayedOver(Card topCard) {
        // WILD y +4 se pueden jugar siempre
        if ("WILD".equals(this.value) || "+4".equals(this.value)) {
            return true;
        }

        // Si alguna de las propiedades es null, no se puede jugar
        if (this.color == null || topCard.getColor() == null ||
                this.value == null || topCard.getValue() == null) {
            return false;
        }

        // Regla normal: color o valor coincidente
        return this.color.equals(topCard.getColor()) ||
                this.value.equals(topCard.getValue());
    }

    @Override
    public Card clone() {
        Card clonedCard = new Card(this.url, this.value, this.color);
        return clonedCard;
    }
}
