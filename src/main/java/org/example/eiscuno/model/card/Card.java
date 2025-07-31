package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.eiscuno.model.prototype.IPrototype;

import java.io.Serializable;

/**
 * Represents a single card in the Uno game.
 * <p>
 * Each card has a value (e.g., "5", "REVERSE", "WILD"), a color (e.g., "RED", "BLUE", or "NULL" for wild cards),
 * and a visual representation through an image loaded from a given URL.
 * This class also implements the Prototype pattern to allow card cloning,
 * and ensures compatibility with Java serialization (excluding transient JavaFX elements).
 */
public class Card implements Serializable, IPrototype<Card> {
    private String url;
    private String value;
    private String color;

    // No se tendrán en cuenta para la serialización.
    private transient Image image;
    private transient ImageView cardImageView;

    /**
     * Constructs a new Card with the specified image URL, value, and color.
     *
     * @param url   the relative path to the card's image resource
     * @param value the textual value of the card (e.g., number or special action)
     * @param color the color of the card (e.g., "RED", "BLUE", or "NULL" for wild cards)
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
     * Reconstructs JavaFX-related transient fields (image and ImageView)
     * after deserialization. This method should be called manually
     * when loading a serialized Card object.
     */
    public void loadTransientFields() {
        this.image = new Image(String.valueOf(getClass().getResource(url)));
        this.cardImageView = createCardImageView();
    }
    /**
     * Creates and configures the {@code ImageView} used to visually represent the card.
     *
     * @return a configured {@code ImageView} with the card image and fixed size
     */
    private ImageView createCardImageView() {
        ImageView card = new ImageView(this.image);
        card.setY(16);
        card.setFitHeight(90);
        card.setFitWidth(70);
        return card;
    }

    /**
     * Returns the visual {@code ImageView} representation of the card.
     *
     * @return the card's {@code ImageView}
     */
    public ImageView getCard() {
        return cardImageView;
    }

    /**
     * Returns the {@code Image} object of the card.
     *
     * @return the card's JavaFX {@code Image}
     */
    public Image getImage() {
        return image;
    }

    /**
     * Gets the card's value (e.g., "7", "REVERSE", "WILD").
     *
     * @return the value of the card
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the card's color (e.g., "RED", "BLUE", or "NULL" for wild cards).
     *
     * @return the color of the card
     */
    public String getColor() {
        return color;
    }

    /**
     * Updates the card's color.
     *
     * @param color the new color to assign (used especially for wild cards)
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Gets the URL string used to load the card image.
     *
     * @return the image URL associated with the card
     */
    public String getUrl() {return url;}

    /**
     * Determines whether this card can be legally played over another card,
     * based on standard Uno rules.
     * <p>
     * A card can be played if:
     * <ul>
     *     <li>It is a wild card ("WILD" or "+4")</li>
     *     <li>It matches the top card by color</li>
     *     <li>It matches the top card by value</li>
     * </ul>
     *
     * @param topCard the current card on the discard pile
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

    /**
     * Creates and returns a deep copy of this card instance.
     * <p>
     * This supports the Prototype design pattern, enabling cards to be cloned when needed.
     *
     * @return a new {@code Card} object with the same properties as this one
     */
    @Override
    public Card clone() {
        Card clonedCard = new Card(this.url, this.value, this.color);
        return clonedCard;
    }
}
