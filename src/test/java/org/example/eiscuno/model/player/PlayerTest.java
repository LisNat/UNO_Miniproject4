package org.example.eiscuno.model.player;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    @BeforeAll
    public static void initJavaFx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

    @BeforeEach
    void setUp() {
        player = new Player("HUMAN");
    }

    @Test
    void testPlayerTypeIsStoredCorrectly() {
        assertEquals("HUMAN", player.getTypePlayer());
    }

    @Test
    void testAddCardToPlayer() {
        Card card = new Card("/org/example/eiscuno/cards-uno/5_red.png", "5", "RED");
        player.addCard(card);
        assertEquals(1, player.getCardsPlayer().size());
        assertTrue(player.getCardsPlayer().contains(card));
    }

    @Test
    void testRemoveCardFromPlayer() {
        Card card = new Card("/org/example/eiscuno/cards-uno/5_red.png", "5", "RED");
        player.addCard(card);
        player.removeCard(0);
        assertTrue(player.getCardsPlayer().isEmpty());
    }

    @Test
    void testGetCardReturnsCorrectCard() {
        Card card = new Card("/org/example/eiscuno/cards-uno/5_red.png", "5", "RED");
        player.addCard(card);
        assertEquals(card, player.getCard(0));
    }

}