package org.example.eiscuno.model.card.effects;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;

/**
 * Represents the effect of a "SKIP" card in the UNO game.
 * When applied, the opponent loses their turn.
 */
public class SkipEffect implements ICardEffect {

    @Override
    public void applyEffect(GameUno gameUno, Card card, Player currentPlayer, Player opponent) {
        if (opponent.equals(gameUno.getHumanPlayer())) {
            System.out.println("Máquina usó SKIP: humano pierde turno");
            gameUno.skipHumanTurn();
        } else {
            System.out.println("Humano usó SKIP: máquina pierde turno");
            gameUno.skipMachineTurn();
        }
    }

    @Override
    public boolean canApply(Card card) {
        return "SKIP".equals(card.getValue());
    }
}
