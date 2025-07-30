package org.example.eiscuno.model.card.effects;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de efectos de cartas especiales
 * Implementa el principio Open/Closed permitiendo agregar nuevos efectos sin modificar código existente
 */
public class CardEffectManager {
    private final List<CardEffect> effects;

    public CardEffectManager() {
        this.effects = new ArrayList<>();
        // Registramos los efectos disponibles
        registerEffect(new DrawTwoEffect());
        registerEffect(new DrawFourEffect());
        registerEffect(new SkipEffect());
        registerEffect(new ReverseEffect());
    }

    /**
     * Registra un nuevo efecto (abierto para extensión)
     * @param effect El efecto a registrar
     */
    public void registerEffect(CardEffect effect) {
        effects.add(effect);
    }

    /**
     * Aplica el efecto correspondiente a la carta jugada
     * @param gameUno El juego actual
     * @param card La carta jugada
     * @param currentPlayer El jugador que jugó la carta
     * @param opponent El jugador oponente
     */
    public void applyCardEffect(GameUno gameUno, Card card, Player currentPlayer, Player opponent) {
        for (CardEffect effect : effects) {
            if (effect.canApply(card)) {
                effect.applyEffect(gameUno, card, currentPlayer, opponent);
                break; // Solo aplicamos el primer efecto que coincida
            }
        }
    }

    /**
     * Verifica si una carta tiene algún efecto especial
     * @param card La carta a verificar
     * @return true si la carta tiene un efecto especial
     */
    public boolean hasSpecialEffect(Card card) {
        return effects.stream().anyMatch(effect -> effect.canApply(card));
    }
}
