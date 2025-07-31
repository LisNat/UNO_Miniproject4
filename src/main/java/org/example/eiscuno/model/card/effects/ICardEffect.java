package org.example.eiscuno.model.card.effects;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;

/**
 * Interface for implementing special card effects.

 * Follows the Open/Closed Principle: open for extension, closed for modification.
 * This allows new effects to be added without changing existing code.

 * @author Juan Moreno.
 * @version 3.0
 * @since version 3.0
 */
public interface ICardEffect {

    /**
     * Applies the effect of the special card.
     *
     * @param gameUno the current game instance.
     * @param card the card being played.
     * @param currentPlayer the player who played the card.
     * @param opponent the opponent player affected by the card.
     */
    void applyEffect(GameUno gameUno, Card card, Player currentPlayer, Player opponent);

    /**
     * Checks whether this effect can be applied to the given card.
     *
     * @param card the card to check.
     * @return True if the effect is applicable to the card, false otherwise.
     */
    boolean canApply(Card card);
}
