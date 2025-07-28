package org.example.eiscuno.controller;

import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.view.WelcomeStage;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;

public class WelcomeController {
    @FXML
    private Button playButton;

    @FXML
    private Button continueButton;

    private boolean onContinue = false; // Para saber si continua.

    public boolean isOnContinue() {return onContinue;}

    @FXML
    private void handlePlayButton(ActionEvent event) {
        onContinue = false;

        try {
            GameUnoStage.getInstance();
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("No se pudo abrir la ventana del juego: " + ex.getMessage());
        }
    }

    @FXML
    private void handleContinueButton(ActionEvent event) {
        File savedGame = new File("GameUnoState.ser");

        if(savedGame.exists()) {
            onContinue = true;

            try {
                GameUnoStage.getInstance();
            } catch (IOException ex) {
                ex.printStackTrace();
                showError("Error al cargar la partida guardada: " + ex.getMessage());
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No existe una partida guardada.");
            alert.showAndWait();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
