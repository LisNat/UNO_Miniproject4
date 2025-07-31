package org.example.eiscuno.model.card.effects;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;

/**
 * Represents the effect of a "+4" card in the UNO game.
 * When applied, the opponent draws four cards and loses their turn.
 */
public class DrawFourEffect implements ICardEffect {

    @Override
    public void applyEffect(GameUno gameUno, Card card, Player currentPlayer, Player opponent) {
        gameUno.eatCard(opponent, 4);

        if (opponent.equals(gameUno.getHumanPlayer())) {
            gameUno.skipHumanTurn();
            System.out.println("Máquina jugó +4. Humano roba 4 cartas y pierde turno.");
        } else {
            gameUno.skipMachineTurn();
            System.out.println("Humano jugó +4. Máquina roba 4 cartas y pierde turno.");
        }
    }

    @Override
    public boolean canApply(Card card) {
        return "+4".equals(card.getValue());
    }
}
