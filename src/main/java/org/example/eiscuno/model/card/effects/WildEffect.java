package org.example.eiscuno.model.card.effects;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;

/**
 * Represents the effect of a "WILD" card in the UNO game.
 * This card allows the player to change the current color.
 * The color change itself is handled externally (e.g., in the controller).
 */
public class WildEffect implements ICardEffect {

    @Override
    public void applyEffect(GameUno gameUno, Card card, Player currentPlayer, Player opponent) {
        // Las cartas WILD no tienen efectos especiales más allá del cambio de color
        // que se maneja en el controlador
        if (currentPlayer.equals(gameUno.getHumanPlayer())) {
            System.out.println("Humano jugó WILD - cambio de color");
        } else {
            System.out.println("Máquina jugó WILD - cambio de color");
        }
    }

    @Override
    public boolean canApply(Card card) {
        return "WILD".equals(card.getValue());
    }
}
