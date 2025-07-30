package org.example.eiscuno.model.card.effects;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;
/**
 * Efecto para cartas WILD
 * Las cartas WILD permiten al jugador cambiar el color actual del juego.
 */
public class WildEffect implements CardEffect {

    @Override
    public void applyEffect(GameUno gameUno, Card card, Player currentPlayer, Player opponent) {
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
