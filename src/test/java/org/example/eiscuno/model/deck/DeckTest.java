package org.example.eiscuno.model.deck;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.exceptions.EmptyDeckException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for {@link Deck} implementation.
 * <p>
 * Verifies the functionality of the UNO game deck including:
 * </p>
 * <ul>
 *   <li>Proper deck initialization</li>
 *   <li>Card drawing mechanics</li>
 *   <li>Edge case handling (empty deck scenarios)</li>
 *   <li>State consistency after operations</li>
 * </ul>
 *
 * @see Deck
 */
class DeckTest {
    private Deck deck;

    /**
     * Initializes JavaFX environment before all tests.
     * <p>
     * Required because card operations depend on graphics system initialization
     * for loading card images.
     * </p>
     */
    @BeforeAll
    public static void initJavaFx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

    /**
     * Test setup executed before each test method.
     * <p>
     * Creates a fresh {@link Deck} instance to ensure test isolation
     * and consistent initial state.
     * </p>
     */
    @BeforeEach
    public void setUp() {
        deck = new Deck(); // Inicializamos un nuevo mazo antes de cada prueba
    }

    /**
     * Tests {@link Deck#takeCard()} method behavior:
     * <ol>
     *   <li>Returns a non-null card</li>
     *   <li>Reduces deck size by one</li>
     *   <li>Removes the card from the deck</li>
     * </ol>
     *
     * @throws EmptyDeckException if deck is empty (should not occur in this test)
     */
    @Test
    public void testTakeCardReducesDeckSize() throws EmptyDeckException {
        int initialSize = deck.getDeckOfCards().size();
        Card card = deck.takeCard();
        assertNotNull(card); // Verificamos que la carta no sea null
        assertEquals(initialSize - 1, deck.getDeckOfCards().size()); // Que se redujo el tamaño
        assertFalse(deck.getDeckOfCards().contains(card)); // Y q no esta en el mazo
    }

    /**
     * Verifies the deck initializes with correct card count.
     * <p>
     * Standard UNO deck contains 108 cards, but this test validates
     * the current implementation using 54 cards.
     * </p>
     */
    @Test
    void deckHasCorrectSize() {
        assertEquals(54, deck.getDeckOfCards().size());
    }

    /**
     * Tests complete deck exhaustion scenario:
     * <ol>
     *   <li>Deck should become empty after drawing all cards</li>
     *   <li>Attempting to draw from empty deck throws {@link EmptyDeckException}</li>
     * </ol>
     *
     * @throws EmptyDeckException if unexpected error occurs while drawing cards
     */
    @Test
    // Verificamos que tomar todas las cartas si vacia el mazo
    void takeAllCardsEmptiesDeck() throws EmptyDeckException {
        int initialSize = deck.getDeckOfCards().size();

        for (int i = 0; i < initialSize; i++) {
            deck.takeCard();
        }

        assertTrue(deck.isEmpty());
        assertThrows(EmptyDeckException.class, () -> deck.takeCard());
    }

    /**
     * Verifies {@link EmptyDeckException} is thrown when:
     * <ol>
     *   <li>Deck becomes completely empty</li>
     *   <li>Attempting to draw additional card</li>
     * </ol>
     * <p>
     * Also confirms no exception is thrown while cards remain available.
     * </p>
     */
    @Test
    public void testTakeCardThrowsExceptionWhenDeckIsEmpty() {
        // Vaciamos el mazo manualmente
        while (!deck.getDeckOfCards().isEmpty()) {
            try {
                deck.takeCard();
            } catch (EmptyDeckException e) {
                fail("No debe lanzar la excepción mientras haya cartas");
            }
        }

        // Ahora el mazo está vacío y debe lanzar la excepción
        assertThrows(EmptyDeckException.class, () -> deck.takeCard());
    }


}