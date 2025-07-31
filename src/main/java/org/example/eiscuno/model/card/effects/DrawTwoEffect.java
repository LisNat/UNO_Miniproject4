package org.example.eiscuno.model.card.effects;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;

/**
 * Represents the effect of a "+2" card in the UNO game.
 * When applied, the opponent draws two cards and loses their turn.
 */
public class DrawTwoEffect implements ICardEffect {

    @Override
    public void applyEffect(GameUno gameUno, Card card, Player currentPlayer, Player opponent) {
        gameUno.eatCard(opponent, 2);

        if (opponent.equals(gameUno.getHumanPlayer())) {
            gameUno.skipHumanTurn();
            System.out.println("Máquina jugó +2. Humano roba 2 cartas y pierde turno.");
        } else {
            gameUno.skipMachineTurn();
            System.out.println("Humano jugó +2. Máquina roba 2 cartas y pierde turno.");
        }
    }

    @Override
    public boolean canApply(Card card) {
        return "+2".equals(card.getValue());
    }
}
