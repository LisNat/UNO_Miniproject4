package org.example.eiscuno.model.player;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Player} class functionality.
 * <p>
 * Verifies player behavior including:
 * </p>
 * <ul>
 *   <li>Player type management</li>
 *   <li>Card collection handling</li>
 *   <li>Card manipulation operations</li>
 * </ul>
 */
class PlayerTest {
    private Player player;

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
     * Creates a fresh player instance with:
     * <ul>
     *   <li>Player type "HUMAN"</li>
     *   <li>Empty card collection</li>
     * </ul>
     */
    @BeforeEach
    void setUp() {
        player = new Player("HUMAN");
    }

    /**
     * Tests {@link Player#getTypePlayer()} returns correct player type.
     * Verifies that:
     * <ul>
     *   <li>Constructor-stored player type is retrievable</li>
     * </ul>
     */
    @Test
    void testPlayerTypeIsStoredCorrectly() {
        assertEquals("HUMAN", player.getTypePlayer());
    }

    /**
     * Tests {@link Player#addCard(Card)} functionality.
     * Verifies that:
     * <ul>
     *   <li>Card collection size increases</li>
     *   <li>Added card exists in collection</li>
     * </ul>
     */
    @Test
    void testAddCardToPlayer() {
        Card card = new Card("/org/example/eiscuno/cards-uno/5_red.png", "5", "RED");
        player.addCard(card);
        assertEquals(1, player.getCardsPlayer().size());
        assertTrue(player.getCardsPlayer().contains(card));
    }

    /**
     * Tests {@link Player#removeCard(int)} functionality.
     * Verifies that:
     * <ul>
     *   <li>Card collection becomes empty after removal</li>
     * </ul>
     */
    @Test
    void testRemoveCardFromPlayer() {
        Card card = new Card("/org/example/eiscuno/cards-uno/5_red.png", "5", "RED");
        player.addCard(card);
        player.removeCard(0);
        assertTrue(player.getCardsPlayer().isEmpty());
    }

    /**
     * Tests {@link Player#getCard(int)} retrieval.
     * Verifies that:
     * <ul>
     *   <li>Returns the exact card at specified position</li>
     * </ul>
     */
    @Test
    void testGetCardReturnsCorrectCard() {
        Card card = new Card("/org/example/eiscuno/cards-uno/5_red.png", "5", "RED");
        player.addCard(card);
        assertEquals(card, player.getCard(0));
    }

}