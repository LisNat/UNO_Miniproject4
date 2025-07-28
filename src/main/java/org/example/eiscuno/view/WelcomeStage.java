package org.example.eiscuno.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.eiscuno.controller.WelcomeController;

import java.io.IOException;

/**
 * Represents the main stage of the Uno game application.
 * This stage displays the game interface to the user.
 */
public class WelcomeStage extends Stage {
    WelcomeController welController;  // Para conocer el controlador.
    /**
     * Constructs a new instance of GameUnoStage.
     *
     * @throws IOException if an error occurs while loading the FXML file for the game interface.
     */
    public WelcomeStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eiscuno/visual-try-view.fxml"));
        Parent root;
        //welController = loader.getController();
        try {
            root = loader.load();
            welController = loader.getController();
        } catch (IOException e) {
            // Re-throwing the caught IOException
            throw new IOException("Error while loading FXML file", e);
        }
        Scene scene = new Scene(root);
        // Configuring the stage
        setTitle("EISC Uno"); // Sets the title of the stage
        setScene(scene); // Sets the scene for the stage
        setResizable(false); // Disallows resizing of the stage

        this.getIcons().add(new Image(getClass().getResource("/org/example/eiscuno/favicon.png").toExternalForm()));

        show(); // Displays the stage
    }

    /**
     * Closes the instance of GameUnoStage.
     * This method is used to clean up resources when the game stage is no longer needed.
     */
    public static void deleteInstance() {
        WelcomeStageHolder.INSTANCE.close();
        WelcomeStageHolder.INSTANCE = null;
    }

    /**
     * Retrieves the singleton instance of GameUnoStage.
     *
     * @return the singleton instance of GameUnoStage.
     * @throws IOException if an error occurs while creating the instance.
     */
    public static WelcomeStage getInstance() throws IOException {
        return WelcomeStageHolder.INSTANCE != null ?
                WelcomeStageHolder.INSTANCE :
                (WelcomeStageHolder.INSTANCE = new WelcomeStage());
    }

    /**
     * Holder class for the singleton instance of GameUnoStage.
     * This class ensures lazy initialization of the singleton instance.
     */
    private static class WelcomeStageHolder {
        private static WelcomeStage INSTANCE;
    }

    public WelcomeController getWelcomeController() {return welController;}
}
