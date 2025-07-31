package org.example.eiscuno.model.card.effects;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages special card effects in the UNO game.

 * Follows the Open/Closed Principle: new effects can be added without modifying existing code.
 * This manager delegates the effect execution to registered implementations of {@link ICardEffect}.

 * @author Juan Moreno.
 * @version 2.0
 * @since version 3.0
 * @see ICardEffect
 * @see Card
 * @see GameUno
 * @see Player
 */
public class CardEffectManager {

    /** The structure that records the effects. */
    private final List<ICardEffect> effects;


    /**
     * Constructs the effect manager and registers all built-in special effects.
     */
    public CardEffectManager() {
        this.effects = new ArrayList<>();
        registerEffect(new DrawTwoEffect());
        registerEffect(new DrawFourEffect());
        registerEffect(new SkipEffect());
        registerEffect(new ReverseEffect());
    }

    /**
     * Registers a new card effect.
     * This allows the system to be extended with additional effects without modifying the manager.
     *
     * @param effect the effect to register.
     * @see ICardEffect
     */
    public void registerEffect(ICardEffect effect) {
        effects.add(effect);
    }

    /**
     * Applies the special effect of a played card, if applicable.
     * Only the first applicable effect (based on {@link ICardEffect#canApply(Card)}) is applied.
     *
     * @param gameUno the current game instance
     * @param card the card that was played
     * @param currentPlayer the player who played the card
     * @param opponent the opponent affected by the effect
     */
    public void applyCardEffect(GameUno gameUno, Card card, Player currentPlayer, Player opponent) {
        for (ICardEffect effect : effects) {
            if (effect.canApply(card)) {
                effect.applyEffect(gameUno, card, currentPlayer, opponent);
                break;
            }
        }
    }

    /**
     * Checks if the given card has any special effect registered.
     *
     * @param card the card to check
     * @return True if a special effect can be applied to the card; false otherwise
     */
    public boolean hasSpecialEffect(Card card) {
        return effects.stream().anyMatch(effect -> effect.canApply(card));
    }
}
