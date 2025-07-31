package org.example.eiscuno.controller;

import javafx.scene.control.TextField;
import org.example.eiscuno.model.planeTextFiles.PlaneTextFileHandler;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.view.GameUnoStage;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Controller for the welcome screen of the UNO game.
 * Handles player name input, new game creation, and continuation of saved games.

 * This class interacts with {@link PlaneTextFileHandler} to persist player data,
 * and triggers transitions to the main game stage.

 * @author David Taborda Montenegro.
 * @version 3.5
 * @since version 2.0
 * @see GameUnoStage
 * @see Player
 * @see PlaneTextFileHandler
 */
public class WelcomeController {

    /** The reference to the TextField in the screen. */
    @FXML
    private TextField userTxt;

    /** The attribute to manage the plane text file behavior. */
    private PlaneTextFileHandler planeTextFileHandler;

    /** A global attribute to remember who the player is. */
    Player player;

    /** Boolean flag to know if the game is new or a continuing from a saved one. */
    private boolean onContinue = false;

    /**
     * Returns whether the current flow is continuing from a saved game.
     *
     * @return True if the user chose to continue a saved game; false otherwise.
     */
    public boolean isOnContinue() {return onContinue;}

    /**
     * Initializes the controller. This method is called automatically after
     * FXML loading. It sets up the file handler and ensures the player data file exists.
     */
    @FXML
    public void initialize() {
        planeTextFileHandler = new PlaneTextFileHandler();
        try {
            File file = new File("PlayerData.txt");

            if(!file.exists()) {
                planeTextFileHandler.write("PlayerData.txt", "default");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the "Play" button is clicked.
     * It reads the player's name, validates it, creates or updates player data,
     * and launches a new game session.
     *
     * @throws IOException if an error occurs while creating or writing player data or opening the game view.
     */
    @FXML
    private void handlePlayButton() throws IOException {

        String data[] = planeTextFileHandler.read("PlayerData.txt");
        String user = data[0];

        if(!userTxt.getText().isEmpty()) {
            try {
                if(Objects.equals(user, userTxt.getText())) {
                    player = new Player(user);
                } else {
                    player = new Player(userTxt.getText().trim());
                    planeTextFileHandler.write("PlayerData.txt", userTxt.getText().trim());
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

    /**
     * Handles the action when the "Continue" button is clicked.
     * It checks for a saved game state and resumes it if available.
     * If no saved state is found, shows a warning dialog.
     */
    @FXML
    private void handleContinueButton() {
        File savedGame = new File("GameUnoState.ser");
        String data[] = planeTextFileHandler.read("PlayerData.txt");
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

    /**
     * Displays an error dialog with the given message.
     *
     * @param message the error message to display.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
