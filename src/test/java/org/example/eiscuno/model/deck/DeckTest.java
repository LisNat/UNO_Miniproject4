package org.example.eiscuno.model.deck;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.exceptions.EmptyDeckException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    private Deck deck;

    // Toca iniciar JavaFX para evitar error de graficos no inicializados
    @BeforeAll
    public static void initJavaFx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

    @BeforeEach
    public void setUp() {
        deck = new Deck(); // Inicializamos un nuevo mazo antes de cada prueba
    }

    @Test
    public void testTakeCardReducesDeckSize() throws EmptyDeckException {
        int initialSize = deck.getDeckOfCards().size();
        Card card = deck.takeCard();
        assertNotNull(card); // Verificamos que la carta no sea null
        assertEquals(initialSize - 1, deck.getDeckOfCards().size()); // Que se redujo el tamaño
        assertFalse(deck.getDeckOfCards().contains(card)); // Y q no esta en el mazo
    }

    // Verificamos la cantidad de cartas
    @Test
    void deckHasCorrectSize() {
        assertEquals(54, deck.getDeckOfCards().size());
    }

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