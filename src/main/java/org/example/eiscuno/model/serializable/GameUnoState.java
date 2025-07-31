package org.example.eiscuno.model.serializable;

import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.io.Serializable;
/**
 * Represents the complete state of an UNO game, including the deck, table, players,
 * turn information, and blocked statuses. This class is serializable to allow saving
 * and loading the game state.
 */
public class GameUnoState implements Serializable {

    private Deck deck;
    private Table table;
    private Player humanPlayer;
    private Player machinePlayer;
    private boolean gameOver;
    private boolean isHumanTurn;
    private boolean isHumanBlocked;
    private boolean isMachineBlocked;
    /**
     * Constructs a new {@code GameUnoState} with the specified game components and statuses.
     *
     * @param deck the deck of cards used in the game
     * @param table the current state of the table with played cards
     * @param humanPlayer the human player in the game
     * @param machinePlayer the machine player in the game
     * @param gameOver indicates if the game has ended
     * @param isHumanTurn indicates if it is currently the human player's turn
     * @param isHumanBlocked indicates if the human player is currently blocked from playing
     * @param isMachineBlocked indicates if the machine player is currently blocked from playing
     */
    public GameUnoState(
            Deck deck,
            Table table,
            Player humanPlayer,
            Player machinePlayer,
            boolean gameOver,
            boolean isHumanTurn,
            boolean isHumanBlocked,
            boolean isMachineBlocked
    ) {
        this.deck = deck;
        this.table = table;
        this.humanPlayer = humanPlayer;
        this.machinePlayer = machinePlayer;
        this.gameOver = gameOver;
        this.isHumanTurn = isHumanTurn;
        this.isHumanBlocked = isHumanBlocked;
        this.isMachineBlocked = isMachineBlocked;
    }

    // Getters and Setters

    public Deck getDeck() { return deck; }
    public Table getTable() { return table; }
    public Player getHumanPlayer() { return humanPlayer; }
    public Player getMachinePlayer() { return machinePlayer; }
    public boolean isGameOver() { return gameOver; }
    public boolean isHumanTurn() { return isHumanTurn; }
    public boolean isHumanBlocked() { return isHumanBlocked; }
    public boolean isMachineBlocked() { return isMachineBlocked; }

    /*public void setDeck(Deck deck) { this.deck = deck; }
    public void setTable(Table table) { this.table = table; }
    public void setHumanPlayer(Player humanPlayer) { this.humanPlayer = humanPlayer; }
    public void setMachinePlayer(Player machinePlayer) { this.machinePlayer = machinePlayer; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    public void setHumanTurn(boolean humanTurn) { isHumanTurn = humanTurn; }
    public void setHumanBlocked(boolean humanBlocked) { isHumanBlocked = humanBlocked; }
    public void setMachineBlocked(boolean machineBlocked) { isMachineBlocked = machineBlocked; }*/
}
