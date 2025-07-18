package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a card in the Uno game.
 */
public class Card {
    private String url;
    private String value;
    private String color;
    private Image image;
    private ImageView cardImageView;

    /**
     * Constructs a Card with the specified image URL and name.
     *
     * @param url the URL of the card image
     * @param value of the card
     */
    public Card(String url, String value, String color) {
        this.url = url;
        this.value = value;
        this.color = color;
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

    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

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
}
