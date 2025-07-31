package org.example.eiscuno.controller;

import javafx.scene.control.TextField;
import javafx.scene.control.*;
import org.example.eiscuno.model.planeTextFiles.PlaneTextFileHandler;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.view.WelcomeStage;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class WelcomeController {
    @FXML
    private Button playButton;

    @FXML
    private Button continueButton;

    @FXML
    private TextField userTxt;

    private PlaneTextFileHandler planeTextFileHandler;

    Player player;

    private boolean onContinue = false; // Para saber si continua.

    public boolean isOnContinue() {return onContinue;}

    @FXML
    public void initialize() {
        planeTextFileHandler = new PlaneTextFileHandler();
        try {
            File file = new File("PlayerData.csv");

            if(!file.exists()) {
                planeTextFileHandler.write("PlayerData.csv", "default" + ",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePlayButton() throws IOException {

        String data[] = planeTextFileHandler.read("PlayerData.csv");
        String user = data[0];

        if(!userTxt.getText().isEmpty()) {
            try {
                if(Objects.equals(user, userTxt.getText())) {
                    player = new Player(user);
                } else {
                    player = new Player(userTxt.getText().trim());
                    planeTextFileHandler.write("PlayerData.csv", userTxt.getText().trim() + ",");
                }
                GameUnoStage.getInstance();
                onContinue = false;
            } catch (IOException e) {
                showError("Error visual al iniciar el juego: " + e.getMessage());
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "¡Ingresa un usuario antes de continuar!");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleContinueButton() {
        File savedGame = new File("GameUnoState.ser");
        String data[] = planeTextFileHandler.read("PlayerData.csv");
        String user = data[0];

        if(savedGame.exists()) {
            try {
                player = new Player(user);
                onContinue = true;
                GameUnoStage.getInstance();
            } catch (IOException ex) {
                ex.printStackTrace();
                showError("Error al cargar la partida guardada: " + ex.getMessage());
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No existe una partida guardada. Crea una partida nueva.");
            alert.showAndWait();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
