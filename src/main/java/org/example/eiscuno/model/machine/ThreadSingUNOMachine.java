package org.example.eiscuno.model.machine;

import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;

public class ThreadSingUNOMachine implements Runnable{
    private ArrayList<Card> cardsPlayer;
    private volatile boolean running = true;

    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer){
        this.cardsPlayer = cardsPlayer;
    }

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

    public void interrupt() {
        running = false;
    }

    private void hasOneCardTheHumanPlayer(){
        if(cardsPlayer.size() == 1){
            System.out.println("UNO");
        }
    }
}