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

class GameUnoTest {
    private GameUno game;
    private Player human;
    private Player machine;
    private GameUnoController controller;

    @BeforeAll
    public static void initJavaFx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

    @BeforeEach
    void setUp() {
        human = new Player("HUMAN");
        machine = new Player("MACHINE");
        game = new GameUno(human, machine, new Deck(), new Table());
        controller = new GameUnoController();
    }

    @Test
    void testStartGameDealsCards() {
        game.startGame();
        assertEquals(10, human.getCardsPlayer().size() + machine.getCardsPlayer().size());
    }

    // Test para probar el metodo CanPlay()
    @Test // No se puede jugar una carta no coincidente
    void testCannotPlayUnmatchedCard() {
        Card topCard = new Card("/org/example/eiscuno/cards-uno/5_blue.png", "5", "BLUE");
        Card cardToPlay = new Card("/org/example/eiscuno/cards-uno/7_red.png", "7", "RED");

        game.getTable().addCardOnTheTable(topCard);
        assertFalse(game.canPlay(cardToPlay));
    }

    @Test
    void testCanPlayWildCard() {
        Card topCard = new Card("/org/example/eiscuno/cards-uno/5_blue.png", "5", "BLUE");
        Card cardToPlay = new Card("/org/example/eiscuno/cards-uno/wild.png", "WILD", "NULL");

        game.getTable().addCardOnTheTable(topCard);
        assertTrue(game.canPlay(cardToPlay));
    }

    @Test
    void testPlayCardThrowsInvalidPlayException() {
        Card topCard = new Card("/org/example/eiscuno/cards-uno/5_blue.png", "5", "BLUE");
        Card card = new Card("/org/example/eiscuno/cards-uno/7_red.png", "7", "RED");
        game.getTable().addCardOnTheTable(topCard);
        human.addCard(card);

        assertThrows(InvalidCardPlayException.class, () -> game.playCard(card));
    }

    @Test
    void testStartGameInitializesTopCard() {
        game.startGame();
        assertNotNull(game.getTable().getCurrentCardOnTheTable());
    }


}