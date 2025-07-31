package org.example.eiscuno.model.card.effects;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;

/**
 * Represents the effect of a "REVERSE" card in the UNO game.
 * In a two-player game, it behaves like a "SKIP" card, causing the opponent to lose their turn.
 */
public class ReverseEffect implements ICardEffect {

    @Override
    public void applyEffect(GameUno gameUno, Card card, Player currentPlayer, Player opponent) {
        // En un juego de 2 jugadores, REVERSE funciona como SKIP
        if (opponent.equals(gameUno.getHumanPlayer())) {
            System.out.println("Máquina usó REVERSE: humano pierde turno");
            gameUno.skipHumanTurn();
        } else {
            System.out.println("Humano usó REVERSE: máquina pierde turno");
            gameUno.skipMachineTurn();
        }
    }

    @Override
    public boolean canApply(Card card) {
        return "REVERSE".equals(card.getValue());
    }
}
