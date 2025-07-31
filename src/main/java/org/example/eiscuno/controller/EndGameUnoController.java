package org.example.eiscuno.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.view.WelcomeStage;
import java.io.IOException;

/**
 * Controller responsible for handling the behavior at the end of the UNO game.
 * This includes displaying the result (win or lose), applying UI animations,
 * and handling user actions such as replaying or exiting the game.

 * @author El/la responsable.
 * @version 2.0
 * @since version 3.0
 * @see GameUnoStage
 * @see WelcomeStage
 */
public class EndGameUnoController {

    /**
     * The visual elements references.
     */
    @FXML
    private Button playAgainButton;

    @FXML
    private Label resultLabel;

    @FXML
    private Button exitButton;

    /** A boolean flag to know is the player won or lose the game. */
    private boolean isWinner;

    /**
     * Sets the result of the game and updates the label accordingly.
     *
     * @param isWinner True if the player won, false otherwise.
     */
    public void setResult(boolean isWinner) {
        this.isWinner = isWinner;
        if (resultLabel != null) {
            resultLabel.setText(isWinner ? "¡Ganaste!" : "Perdiste...");
        }
    }

    /**
     * Initializes the controller. This method is automatically called after
     * the FXML components are loaded. It applies animations to UI elements.
     */
    @FXML
    private void initialize() {
        applyFadeEffect(resultLabel);
        applyTranslateEffect(playAgainButton, 0);
        applyTranslateEffect(exitButton, 0.2);
    }

    /**
     * Handles the action when the user clicks the "Play Again" button.
     * It closes the current window, resets the game state, and returns
     * the user to the welcome screen.
     * If the welcome screen cannot be loaded, throw IOException.
     */
    @FXML
    private void handlePlayAgain() {
        try {
            Stage stage = (Stage) playAgainButton.getScene().getWindow();
            stage.close();
            GameUnoStage.deleteInstance();
            WelcomeStage.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when the user clicks the "Exit" button.
     * It simply closes the current window.
     */
    @FXML
    private void handleExit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Applies a fade-in effect to the specified label.
     *
     * @param label the label to apply the fade effect to.
     */
    private void applyFadeEffect(Label label) {
        FadeTransition fade = new FadeTransition(Duration.seconds(1), label);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    /**
     * Applies a translation (slide up) animation to the given button with a delay.
     *
     * @param button the button to animate.
     * @param delaySeconds the delay before the animation starts, in seconds.
     */
    private void applyTranslateEffect(Button button, double delaySeconds) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.7), button);
        tt.setFromY(40);
        tt.setToY(0);
        tt.setDelay(Duration.seconds(delaySeconds));
        tt.play();
    }
}
