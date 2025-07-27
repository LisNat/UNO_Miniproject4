package org.example.eiscuno.model.serializable;

import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.io.Serializable;

public class GameUnoState implements Serializable {

    private Deck deck;
    private Table table;
    private Player humanPlayer;
    private Player machinePlayer;
    private boolean gameOver;
    private boolean isHumanTurn;
    private boolean isHumanBlocked;
    private boolean isMachineBlocked;

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

    // Getters y Setters

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
