package org.example.eiscuno.model.game;

import javafx.application.Platform;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.exceptions.InvalidCardPlayException;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GameUno} class functionality.
 * <p>
 * Verifies core game mechanics including:
 * </p>
 * <ul>
 *   <li>Game initialization and setup</li>
 *   <li>Card play validation rules</li>
 *   <li>Special card behavior</li>
 *   <li>Exception handling</li>
 * </ul>
 */
class GameUnoTest {
    private GameUno game;
    private Player human;
    private Player machine;

    /**
     * Initializes JavaFX environment before all tests.
     * Required for card image loading functionality.
     */
    @BeforeAll
    public static void initJavaFx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

    /**
     * Sets up test environment before each test method.
     * Creates fresh game instance with:
     * <ul>
     *   <li>New human and machine players</li>
     *   <li>Fresh deck and table</li>
     *   <li>New controller instance</li>
     * </ul>
     */
    @BeforeEach
    void setUp() {
        human = new Player("HUMAN");
        machine = new Player("MACHINE");
        game = new GameUno(human, machine, new Deck(), new Table());
    }

    /**
     * Tests {@link GameUno#startGame()} initial card distribution.
     * Verifies that:
     * <ul>
     *   <li>Total of 10 cards are dealt (5 to each player)</li>
     * </ul>
     */
    @Test
    void testStartGameDealsCards() {
        game.startGame();
        assertEquals(10, human.getCardsPlayer().size() + machine.getCardsPlayer().size());
    }

    /**
     * Tests {@link GameUno#canPlay(Card)} with incompatible cards.
     * Verifies that:
     * <ul>
     *   <li>Cards with mismatched value AND color are rejected</li>
     * </ul>
     */
    @Test
    void testCannotPlayUnmatchedCard() {
        Card topCard = new Card("/org/example/eiscuno/cards-uno/5_blue.png", "5", "BLUE");
        Card cardToPlay = new Card("/org/example/eiscuno/cards-uno/7_red.png", "7", "RED");

        game.getTable().addCardOnTheTable(topCard);
        assertFalse(game.canPlay(cardToPlay));
    }

    /**
     * Tests {@link GameUno#canPlay(Card)} with wild cards.
     * Verifies that:
     * <ul>
     *   <li>Wild cards can be played on any card</li>
     * </ul>
     */
    @Test
    void testCanPlayWildCard() {
        Card topCard = new Card("/org/example/eiscuno/cards-uno/5_blue.png", "5", "BLUE");
        Card cardToPlay = new Card("/org/example/eiscuno/cards-uno/wild.png", "WILD", "NULL");

        game.getTable().addCardOnTheTable(topCard);
        assertTrue(game.canPlay(cardToPlay));
    }

    /**
     * Tests {@link GameUno#playCard(Card)} with invalid plays.
     * Verifies that:
     * <ul>
     *   <li>Invalid card plays throw {@link InvalidCardPlayException}</li>
     * </ul>
     */
    @Test
    void testPlayCardThrowsInvalidPlayException() {
        Card topCard = new Card("/org/example/eiscuno/cards-uno/5_blue.png", "5", "BLUE");
        Card card = new Card("/org/example/eiscuno/cards-uno/7_red.png", "7", "RED");
        game.getTable().addCardOnTheTable(topCard);
        human.addCard(card);

        assertThrows(InvalidCardPlayException.class, () -> game.playCard(card));
    }

    /**
     * Tests {@link GameUno#startGame()} table initialization.
     * Verifies that:
     * <ul>
     *   <li>Starting game places valid card on table</li>
     * </ul>
     */
    @Test
    void testStartGameInitializesTopCard() {
        game.startGame();
        assertNotNull(game.getTable().getCurrentCardOnTheTable());
    }


}