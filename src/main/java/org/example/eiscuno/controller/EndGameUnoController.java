package org.example.eiscuno.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.eiscuno.view.WelcomeStage;

import java.io.IOException;

public class EndGameUnoController {

    @FXML
    private Button playAgainButton;

    @FXML
    private Label resultLabel;

    @FXML
    private Button exitButton;

    private boolean isWinner; // Lo puedes setear antes de cargar la vista

    public void setResult(boolean isWinner) {
        this.isWinner = isWinner;
        if (resultLabel != null) {
            resultLabel.setText(isWinner ? "¡You Won!" : "You Lost...");
        }
    }

    @FXML
    private void initialize() {
        applyFadeEffect(resultLabel);
        applyTranslateEffect(playAgainButton, 0);
        applyTranslateEffect(exitButton, 0.2);
    }

    @FXML
    private void handlePlayAgain() {
        try {
            Stage stage = (Stage) playAgainButton.getScene().getWindow();
            stage.close();

            WelcomeStage.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void applyFadeEffect(Label label) {
        FadeTransition fade = new FadeTransition(Duration.seconds(1), label);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void applyTranslateEffect(Button button, double delaySeconds) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.7), button);
        tt.setFromY(40);
        tt.setToY(0);
        tt.setDelay(Duration.seconds(delaySeconds));
        tt.play();
    }


}
