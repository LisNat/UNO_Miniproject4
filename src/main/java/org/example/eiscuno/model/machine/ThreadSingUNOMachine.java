package org.example.eiscuno.model.machine;

import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;
/**
 * Runnable thread that periodically checks if the human player has only one card left,
 * simulating the "UNO" call in the game.
 * <p>
 * The thread sleeps for a random interval up to 5 seconds between checks.
 * It stops running when interrupted or explicitly stopped.
 */
public class ThreadSingUNOMachine implements Runnable{
    private ArrayList<Card> cardsPlayer;
    private volatile boolean running = true;

    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer){
        this.cardsPlayer = cardsPlayer;
    }
    /**
     * Runs the thread loop that periodically checks if the human player has only one card left.
     * <p>
     * The thread sleeps for a random duration up to 5 seconds between checks.
     * If interrupted, it stops execution gracefully.
     */
    @Override
    public void run(){
        while (running && !Thread.currentThread().isInterrupted()){
            try {
                Thread.sleep((long) (Math.random() * 5000));
            } catch (InterruptedException e) {
                System.out.println("ThreadSingUNOMachine interrumpido");
                Thread.currentThread().interrupt();
                break;
            }
            if (running) {
                hasOneCardTheHumanPlayer();
            }
        }
    }
    /**
     * Stops the thread's execution by setting the running flag to false.
     */
    public void interrupt() {
        running = false;
    }

    /**
     * Checks if the human player has exactly one card left,
     * and prints "UNO" to the console if true.
     */
    private void hasOneCardTheHumanPlayer(){
        if(cardsPlayer.size() == 1){
            System.out.println("UNO");
        }
    }
}