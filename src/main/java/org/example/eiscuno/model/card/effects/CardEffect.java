package org.example.eiscuno.model.card.effects;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;

/**
 * Interfaz para implementar efectos de cartas especiales
 * Siguiendo el principio Open/Closed: abierto para extensión, cerrado para modificación
 */
public interface CardEffect {
    /**
     * Aplica el efecto de la carta especial
     * @param gameUno El juego actual
     * @param card La carta que se está jugando
     * @param currentPlayer El jugador que jugó la carta
     * @param opponent El jugador oponente
     */
    void applyEffect(GameUno gameUno, Card card, Player currentPlayer, Player opponent);

    /**
     * Verifica si este efecto puede ser aplicado a la carta dada
     * @param card La carta a verificar
     * @return true si el efecto es aplicable
     */
    boolean canApply(Card card);
}
