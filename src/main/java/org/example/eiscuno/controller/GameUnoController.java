package org.example.eiscuno.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.exceptions.EmptyDeckException;
import org.example.eiscuno.model.exceptions.IllegalGameStateException;
import org.example.eiscuno.model.exceptions.InvalidCardPlayException;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.game.IGameEventListener;
import org.example.eiscuno.model.game.ThreadCheckGameOver;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.serializable.GameUnoState;
import org.example.eiscuno.model.serializable.SerializableFileHandler;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.WelcomeStage;
import org.example.eiscuno.model.planeTextFiles.PlaneTextFileHandler;
import java.io.File;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for the Uno game.
 */
public class GameUnoController implements IGameEventListener {

    @FXML
    private GridPane gridPaneCardsMachine;

    @FXML
    private GridPane gridPaneCardsPlayer;

    @FXML
    private ImageView tableImageView;

    @FXML
    private Button buttonTakeCard;

    @FXML
    private Button buttonExit;

    @FXML
    private Button buttonUno;

    @FXML
    private Label cardCountLabel;

    @FXML
    private Label turnLabel;


    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private int posInitCardToShow;
    private int posInitMachineCardToShow;

    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;

    private boolean skipPlayerTurn = false;

    private boolean unoPressed = false;
    private Timeline unoTimer;

    private ThreadCheckGameOver threadCheckGameOver;

    @FXML private Pane colorIndicatorBox;
    private Rectangle colorIndicator;

    private PlaneTextFileHandler planeTextFileHandler;

    /**
     * Initializes the game scene and its components. It checks whether a game is being continued,
     * sets up visual elements like the table card image and color indicator, and starts necessary
     * background threads for gameplay logic such as checking for "UNO", machine plays, and game over conditions.
     *
     * This method also loads and displays the cards of both players, sets up the game state,
     * and starts a new game if not continuing a previous one.
     */
    @FXML
    public void initialize() {
        boolean isContinuingGame = false;

        try {
            isContinuingGame = WelcomeStage.getInstance().getWelcomeController().isOnContinue();
            planeTextFileHandler = new PlaneTextFileHandler();
        } catch (IOException e) {
            System.out.println("Error con apartado visual." + e.getMessage());
            isContinuingGame = false;
        }

        WelcomeStage.deleteInstance();

        initVariables(isContinuingGame);

        this.gameUno.setGameEventListener(this);if(!isContinuingGame) {
            this.gameUno.startGame();
        }

        // Mostramos la carta inicial en la mesa
        Card topCard = table.getCurrentCardOnTheTable();
        if(topCard != null) {
            //se inicia el rectangulo indicador
            tableImageView.setImage(topCard.getImage());
            colorIndicator = new Rectangle(50, 50);
            colorIndicator.setArcWidth(10);
            colorIndicator.setArcHeight(10);
            colorIndicator.setFill(Color.GRAY);
            colorIndicator.setStroke(Color.BLACK);
            colorIndicator.setStrokeWidth(1.5);
            colorIndicatorBox.getChildren().add(colorIndicator);
            updateColorIndicator(topCard.getColor());
            updateCardCounter();
            turnLabel.setVisible(false);

        }

        printCardsHumanPlayer();
        printCardsMachinePlayer();


        // Iniciamos Hilos
        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer());
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this.gameUno, this.deck, this);
        threadPlayMachine.start();

        threadCheckGameOver = new ThreadCheckGameOver(humanPlayer, machinePlayer, this, gameUno);
        threadCheckGameOver.start();
    }

    /**
     * Initializes the core game variables depending on whether the user is continuing a previous game
     * or starting a new one. If continuing, it loads the saved game state using serialization.
     * Otherwise, it creates new instances of players, deck, table, and game logic.
     *
     * It also resets the initial positions for displaying player and machine cards.
     *
     * @param continueGame true if the game should resume from a saved state; false to start a new game.
     */
    private void initVariables(boolean continueGame) {
        // Para aplicar serialización:

        if(continueGame) {
            loadGameState();
        } else {
            this.humanPlayer = new Player("HUMAN_PLAYER");
            this.machinePlayer = new Player("MACHINE_PLAYER");
            this.deck = new Deck();
            this.table = new Table();
            this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
            humanPlayer.setPlayerName(Arrays.toString(planeTextFileHandler.read("PlayerData.txt")));
        }

        this.posInitCardToShow = 0;
        this.posInitMachineCardToShow = 0;
    }

    /**
     * Displays the current visible cards of the human player in the grid pane. Clears any existing cards,
     * updates the card counter, and sets mouse click events for playing a card.
     *
     * If the human player is skipped due to a SKIP or REVERSE card, their turn is skipped,
     * the skip flag is cleared, and the machine's turn is triggered.
     *
     * When a card is clicked, the method checks if the play is valid. If so, it updates the game state,
     * shows the played card on the table, handles special cards (like WILD), and saves the game.
     *
     * If the game is not over, it continues checking for a possible UNO declaration.
     */
    private void printCardsHumanPlayer() {
        this.gridPaneCardsPlayer.getChildren().clear();
        Card[] currentVisibleCardsHumanPlayer = this.gameUno.getCurrentVisibleCardsHumanPlayer(this.posInitCardToShow);
        updateCardCounter();
        // Verificamos si se pierde turno antes de mostrar las cartas
        if (gameUno.isSkipHumanTurn()) {
            System.out.println("Pierdes el turno por SKIP o REVERSE");
            gameUno.clearSkipHumanTurn();
            threadPlayMachine.setHasPlayerPlayed(true);
            return;
        }

        for (int i = 0; i < currentVisibleCardsHumanPlayer.length; i++) {
            Card card = currentVisibleCardsHumanPlayer[i];
            ImageView cardImageView = card.getCard();

            cardImageView.setOnMouseClicked((MouseEvent event) -> {
                if (gameUno.isSkipHumanTurn()) {
                    System.out.println("Pierdes el turno, no puedes jugar.");
                    return;
                }

                if (gameUno.canPlay(card)) {
                    try {
                        gameUno.playCard(card);
                        tableImageView.setImage(card.getImage());
                        updateColorIndicator(card.getColor());
                        updateCardCounter();
                        humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                        saveGameState();

                        if ("WILD".equals(card.getValue()) || "+4".equals(card.getValue())) {
                            handleWildCard();
                        }

                        if (!gameUno.isGameOver()) {
                            checkUnoOpportunity();
                        }

                        threadPlayMachine.setHasPlayerPlayed(true);
                        printCardsHumanPlayer();

                    } catch (IllegalGameStateException e) {
                        System.out.println("Error: " + e.getMessage());
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Estado inválido del juego");
                            alert.setHeaderText(null);
                            alert.setContentText("No se puede jugar: " + e.getMessage());
                            alert.showAndWait();
                        });
                    }
                    catch (InvalidCardPlayException e) {
                        System.out.println("Jugada inválida: " + e.getMessage());
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Jugada no permitida");
                            alert.setHeaderText(null);
                            alert.setContentText("No puedes jugar esa carta: " + e.getMessage());
                            alert.showAndWait();
                        });
                    }
                } else {
                    System.out.println("No puedes jugar esta carta: " + card.getValue() + " - " + card.getColor());
                }
            });

            this.gridPaneCardsPlayer.add(cardImageView, i, 0);
        }
        // Creo q no es totalmente necesaria
        if (!gameUno.isGameOver()) {
            checkUnoOpportunity();
        }
    }

    /**
     * Displays a limited number of the machine player's cards in the grid pane.
     * Clears the previous cards, updates the card counter, and shows up to 4 card backs
     * starting from the current position.
     *
     * This method does not reveal the actual cards of the machine player to simulate
     * the hidden nature of opponent hands in a typical UNO game.
     */
    public void printCardsMachinePlayer() {
        gridPaneCardsMachine.getChildren().clear();
        updateCardCounter();
        List<Card> machineCards = gameUno.getMachinePlayer().getCardsPlayer();
        int totalCards = machineCards.size();

        // Mostramos máximo 4 cartas desde la posición inicial, la idea es visualizar cuando
        // a la máquina le quede una, más no ver el total de cartas de ella, así es más parecido
        // a el juego del UNO normal
        int cardsToShow = Math.min(4, totalCards - posInitMachineCardToShow);

        for (int i = 0; i < cardsToShow; i++) {
            ImageView cardBack = new ImageView(new Image(getClass().getResource("/org/example/eiscuno/cards-uno/card_uno.png").toExternalForm()));
            cardBack.setFitWidth(70);
            cardBack.setPreserveRatio(true);

            gridPaneCardsMachine.add(cardBack, i, 0);
        }
    }

    @Override
    public void onHumanCardsChanged() {
        printCardsHumanPlayer();
    }

    @Override
    public void onMachineCardsChanged() {
        printCardsMachinePlayer();
    }


    /**
     * Finds the position (index) of a given card in the human player's hand.
     *
     * @param card The card to search for.
     * @return The index of the card if found, or -1 if the card is not in the hand.
     */
    private Integer findPosCardsHumanPlayer(Card card) {
        for (int i = 0; i < this.humanPlayer.getCardsPlayer().size(); i++) {
            if (this.humanPlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Handles the "Back" button action to show the previous set of cards.
     *
     * @param event the action event
     */
    @FXML
    void onHandleBack(ActionEvent event) {
        if (this.posInitCardToShow > 0) {
            this.posInitCardToShow--;
            printCardsHumanPlayer();
            updateCardCounter();

        }
    }

    /**
     * Handles the "Next" button action to show the next set of cards.
     *
     * @param event the action event
     */
    @FXML
    void onHandleNext(ActionEvent event) {
        if (this.posInitCardToShow < this.humanPlayer.getCardsPlayer().size() - 4) {
            this.posInitCardToShow++;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the action of drawing a card from the deck when the player clicks the "Take Card" button.
     *
     * Validates that the game and player are properly initialized. Checks if the deck is empty and shows
     * a warning if no cards are available. Otherwise, draws a card, saves the game state, and updates the UI.
     * If the drawn card is playable, informs the player.
     *
     * @param event The ActionEvent triggered by clicking the take card button.
     */
    @FXML
    void onHandleTakeCard(ActionEvent event) {
        try {
            if (gameUno == null || humanPlayer == null) {
                System.out.println("Juego no iniciado correctamente.");
                return;
            }

            // Verificamos si el mazo está vacío antes de intentar robar
            if (gameUno.isDeckEmpty()) {
                buttonTakeCard.setDisable(true);
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Mazo Vacío");
                    alert.setHeaderText(null);
                    alert.setContentText("No hay más cartas disponibles en el mazo");
                    alert.showAndWait();
                });
                return;
            }

            Card card = gameUno.drawCard(humanPlayer);
            saveGameState();
            System.out.println("Robaste: " + card.getValue() + " " + card.getColor());

            if (gameUno.canPlay(card)) {
                System.out.println("¡Puedes jugar la carta robada!");
            }

            threadPlayMachine.setHasPlayerPlayed(true);
            printCardsHumanPlayer();

        } catch (EmptyDeckException e) {
            // Manejo específico cuando el mazo está vacío - Desactivamos botón
            System.out.println("🚨 " + e.getMessage());
            buttonTakeCard.setDisable(true);

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Mazo Vacío");
                alert.setHeaderText(null);
                alert.setContentText("No hay más cartas disponibles en el mazo");
                alert.showAndWait();
            });

        } catch (IllegalGameStateException e) {
            System.out.println(e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Estado inválido del juego");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });
        } catch (Exception e) {
            System.err.println("Error inesperado al robar carta: " + e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error inesperado: " + e.getMessage());
                alert.showAndWait();
            });
        }
    }


    /**
     * Handles the action when the human player clicks the "UNO" button to declare UNO.
     *
     * Sets the UNO declaration flag to true, stops any active UNO timer, hides the UNO button,
     * logs the declaration, notifies the game logic, and saves the game state.
     *
     * @param event The ActionEvent triggered by clicking the UNO button.
     */
    @FXML
    void onHandleUno(ActionEvent event) {
        unoPressed = true;

        if (unoTimer != null) {
            unoTimer.stop();
        }

        buttonUno.setVisible(false);
        System.out.println("Humano declaró UNO a tiempo");
        gameUno.haveSungOne("HUMAN_PLAYER");
        saveGameState();
    }
    /**
     * Handles the exit operation of the game.
     *
     * Saves the current game state, closes the main application window,
     * and safely stops or interrupts any active background threads related
     * to checking game over conditions, machine player actions, and UNO declarations.
     */
    @FXML
    private void handleExit() {
        saveGameState();
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        stage.close();
        if (threadCheckGameOver != null) {
            threadCheckGameOver.stopRunning();
        }
        if (threadPlayMachine != null) {
            threadPlayMachine.interrupt();
        }
        if(threadSingUNOMachine != null) {
            threadSingUNOMachine.interrupt();
        }

    }
    /**
     * Handles the logic for playing a Wild card.
     *
     * Displays a dialog allowing the human player to choose a new color.
     * Once a color is selected, it updates the current card on the table with the chosen color,
     * refreshes the card image, and updates the color indicator in the UI.
     */
    private void handleWildCard() {
        // Mostrar diálogo para seleccionar color
        ChoiceDialog<String> dialog = new ChoiceDialog<>("RED",
                Arrays.asList("RED", "GREEN", "BLUE", "YELLOW"));
        dialog.setTitle("Cambio de color");
        dialog.setHeaderText("Selecciona un nuevo color");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(color -> {
            try {
                // Actualizamos el color de la carta en la mesa
                Card currentCard = table.getCurrentCardOnTheTable();
                currentCard.setColor(color);
                // Actualizamos la imagen si es necesario
                tableImageView.setImage(currentCard.getImage());
                updateColorIndicator(color);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * Starts the UNO timer for the human player.
     *
     * Initiates a countdown (randomized between 2-4 seconds) during which the player must press the "UNO" button.
     * If the player fails to press it in time and the game is not over, a penalty is applied.
     * The button is hidden after the timer ends, and the deck status is checked.
     * The current game state is saved after starting the timer.
     */
    private void startUnoTimer() {
        try {
            unoPressed = false;

            if (unoTimer != null) {
                unoTimer.stop();
            }

            int delay = 2000 + (int) (Math.random() * 2000); // 2-4s

            unoTimer = new Timeline(new KeyFrame(Duration.millis(delay), e -> {
                try {
                    if (!unoPressed && !gameUno.isGameOver()) {
                        handleUnoPenalty();
                    }
                } catch (Exception ex) {
                    System.err.println("Error inesperado en timer UNO: " + ex.getMessage());
                } finally {
                    buttonUno.setVisible(false);
                    checkDeckEmptyStatus();
                }
            }));

            unoTimer.setCycleCount(1);
            unoTimer.play();
            saveGameState();
        } catch (Exception e) {
            System.err.println("Error al iniciar timer UNO: " + e.getMessage());
        }
    }

    /**
     * Applies the penalty to the human player for not declaring "UNO" in time.
     *
     * If the human player fails to press the "UNO" button within the allowed time frame,
     * this method draws a card from the deck for the player and updates the user interface,
     * including the player's hand and the color indicator on the table.
     *
     * If the deck is empty, the penalty cannot be applied, and a warning alert is displayed instead.
     * Finally, the current game state is saved.
     */
    private void handleUnoPenalty() {
        try {
            System.out.println("¡No dijiste UNO a tiempo! Penalización aplicada.");
            gameUno.drawCard(humanPlayer); // Intenta robar carta
            printCardsHumanPlayer();
            Card topCard = table.getCurrentCardOnTheTable();
            updateColorIndicator(topCard.getColor());
            saveGameState();

        } catch (EmptyDeckException ex) {
            System.out.println("No se pudo aplicar penalización: " + ex.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Mazo Vacío");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo aplicar la penalización porque el mazo está vacío");
                alert.showAndWait();
            });
        }
    }

    /**
     * Checks whether the deck is empty and updates the "Take Card" button accordingly.
     *
     * This method runs on the JavaFX application thread and disables the "Take Card" button
     * if there are no more cards in the deck. If an error occurs while checking the deck state,
     * the error message is logged to the console.
     */
    private void checkDeckEmptyStatus() {
        Platform.runLater(() -> {
            try {
                buttonTakeCard.setDisable(gameUno.isDeckEmpty());
            } catch (Exception ex) {
                System.err.println("Error verificando estado del mazo: " + ex.getMessage());
            }
        });
    }
    /**
     * Checks if the human player is eligible to declare "UNO" and updates the visibility of the UNO button.
     *
     * This method runs on the JavaFX application thread and displays the UNO button if the human player
     * has exactly one card left, the game is not over, and the player's turn is not skipped.
     * If the conditions are not met and the button is currently visible, it hides the button and stops
     * the UNO timer if it was running.
     * Any exceptions are caught and logged to the console.
     */
    public void checkUnoOpportunity() {
        Platform.runLater(() -> {
            try {
                // Verificación más robusta
                boolean shouldShowUnoButton = humanPlayer != null &&
                        humanPlayer.getCardsPlayer() != null &&
                        humanPlayer.getCardsPlayer().size() == 1 &&
                        !gameUno.isGameOver() &&
                        !gameUno.isSkipHumanTurn();

                if (shouldShowUnoButton) {
                    System.out.println("Mostrando botón UNO - Cartas restantes: 1");
                    buttonUno.setVisible(true);
                    startUnoTimer();
                } else {
                    if (buttonUno.isVisible()) {
                        buttonUno.setVisible(false);
                        if (unoTimer != null) {
                            unoTimer.stop();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error en checkUnoOpportunity: " + e.getMessage());
            }
        });
    }
    /**
     * Displays the end game screen and stops all relevant threads.
     *
     * This method loads the EndGameUnoView FXML file and displays the result of the game based on
     * whether the human player won or lost. It also stops the background threads responsible for checking
     * game state and machine actions, and closes the current game window.
     *
     * @param playerWon true if the human player won the game; false otherwise.
     */
    public void showGameOver(boolean playerWon) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/eiscuno/EndGameUnoView.fxml"));
            Parent root = loader.load();

            EndGameUnoController controller = loader.getController();
            controller.setResult(playerWon);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Game Over");
            stage.show();

            if (threadCheckGameOver != null) {
                threadCheckGameOver.stopRunning();
            }
            if (threadPlayMachine != null) {
                threadPlayMachine.interrupt();
            }
            if(threadSingUNOMachine != null) {
                threadSingUNOMachine.interrupt();
            }

            Stage currentStage = (Stage) tableImageView.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se pudo cargar la pantalla de fin del juego.");
        }
    }

    /**
     * Saves the current state of the UNO game to a file.
     *
     * This method creates a {@link GameUnoState} object representing the current state of the game,
     * including the deck, table, human and machine players, and game flags. The state is then serialized
     * and saved to a file named "GameUnoState.ser" using {@link SerializableFileHandler}.
     *
     * If an error occurs during the saving process, it is caught and logged.
     */
    public void saveGameState() {
        try {
            GameUnoState state = new GameUnoState(
                    gameUno.getDeck(),
                    gameUno.getTable(),
                    gameUno.getHumanPlayer(),
                    gameUno.getMachinePlayer(),
                    gameUno.isGameOver(),
                    gameUno.isHumanTurn(),
                    gameUno.isSkipHumanTurn(),
                    gameUno.isSkipMachineTurn()
            );

            SerializableFileHandler handler = new SerializableFileHandler();
            handler.serialize("GameUnoState.ser", state);
            System.out.println("Estado del juego guardado exitosamente.");

        } catch (Exception e) {
            System.err.println("Error al guardar el estado del juego: " + e.getMessage());
        }
    }
    /**
     * Loads the previously saved state of the UNO game from a file.
     *
     * This method deserializes a {@link GameUnoState} object from "GameUnoState.ser" using
     * {@link SerializableFileHandler}. If a valid state is found, it restores all relevant game
     * components including the deck, table, human and machine players, game flags, and visual state.
     *
     * It also reinitializes the {@link GameUno} object and restores UI components like card images.
     * If the file does not contain a valid state, a message is logged. Any exceptions during
     * deserialization or restoration are caught and logged.
     */
    public void loadGameState() {
        try {
            SerializableFileHandler handler = new SerializableFileHandler();
            GameUnoState state = (GameUnoState) handler.deserialize("GameUnoState.ser");

            if (state != null) {
                System.out.println("Estado del juego cargado exitosamente.");

                // Restaurar lógica
                //gameUno.setDeck(state.getDeck());
                //gameUno.setTable(state.getTable());
                //gameUno.setHumanPlayer(state.getHumanPlayer());
                //gameUno.setMachinePlayer(state.getMachinePlayer());
                //gameUno.setGameOver(state.isGameOver());
                //gameUno.setHumanTurn(state.isHumanTurn());

                // Restaurar lógica del juego
                this.humanPlayer = state.getHumanPlayer();
                this.machinePlayer = state.getMachinePlayer();
                this.deck = state.getDeck();
                this.table = state.getTable();

                this.gameUno = new GameUno(humanPlayer, machinePlayer, deck, table);
                gameUno.setGameOver(state.isGameOver());
                gameUno.setHumanTurn(state.isHumanTurn());

                if (state.isHumanBlocked()) {
                    gameUno.skipHumanTurn();
                } else {
                    gameUno.clearSkipHumanTurn();
                }

                if (state.isMachineBlocked()) {
                    gameUno.skipMachineTurn();
                } else {
                    gameUno.clearSkipMachineTurn();
                }

                // Restaurar ImageViews en las cartas
                restoreImageViews();

                // Restaurar parte visual
                restoreVisualState();

                // Verificar oportunidad de cantar UNO
                checkUnoOpportunity();

            } else {
                System.out.println("No se encontró estado válido para cargar.");
            }

        } catch (Exception e) {
            System.err.println("Error al cargar el estado del juego: " + e.getMessage());
        }
    }

    private void restoreImageViews() {
        for(Card card : humanPlayer.getCardsPlayer()) {
            card.loadTransientFields();
        }

        for(Card card : machinePlayer.getCardsPlayer()) {
            card.loadTransientFields();
        }

        Card topCard = table.getCurrentCardOnTheTable();
        if(topCard != null) {
            topCard.loadTransientFields();
        }

        for(Card card : deck.getDeckOfCards()) {
            card.loadTransientFields();
        }
    }
    /**
     * Restores the transient fields (such as images) for all cards in the game.
     *
     * This method ensures that image-related data for the human player's hand, the machine player's hand,
     * the top card on the table, and the remaining cards in the deck are properly reloaded by calling
     * {@code loadTransientFields()} on each {@link Card} object.
     *
     * This is necessary after deserialization, as transient fields are not saved and must be restored manually.
     */
    private void restoreVisualState() {
        try {
            // Restaurar carta en la mesa
            Card currentCard = gameUno.getTable().getCurrentCardOnTheTable();
            if (currentCard != null) {
                tableImageView.setImage(currentCard.getImage());

            }

            // Restaurar mano del jugador humano
            printCardsHumanPlayer();

            // Restaurar mano de la máquina (oculta)
            printCardsMachinePlayer();

            // Restaurar botón de robar carta
            buttonTakeCard.setDisable(gameUno.isDeckEmpty());

            // Restaurar visibilidad del botón UNO si aplica
            checkUnoOpportunity();

        } catch (Exception e) {
            System.err.println("Error al restaurar parte visual: " + e.getMessage());
        }
    }
    /**
     * Updates the visual color indicator based on the current color of the top card.
     *
     * This method changes the fill color of the {@code colorIndicator} (usually a Circle or similar UI element)
     * to visually represent the current active color in the game. It maps string color names to JavaFX colors.
     *
     * @param color the current color of the top card (e.g., "red", "green", "blue", "yellow").
     *              If null or unrecognized, the indicator defaults to gray.
     */
    public void updateColorIndicator(String color) {
        if (color == null) {
            return; // No actualizamos si el color no está definido
        }

        Color fxColor;
        switch (color.toLowerCase()) {
            case "red":
                fxColor = Color.RED;
                break;
            case "green":
                fxColor = Color.LIMEGREEN;
                break;
            case "blue":
                fxColor = Color.DODGERBLUE;
                break;
            case "yellow":
                fxColor = Color.GOLD;
                break;
            default:
                fxColor = Color.GRAY;
                break;
        }

        colorIndicator.setFill(fxColor);
    }
    /**
     * Updates the label that displays the number of cards held by each player.
     *
     * This method retrieves the current number of cards for both the human player and the machine,
     * and updates the {@code cardCountLabel} to reflect the current game state.
     * Useful for providing the player with real-time visual feedback.
     */
    public void updateCardCounter() {
        int humanCardCount = humanPlayer.getCardCount();
        int machineCardCount = machinePlayer.getCardCount();
        cardCountLabel.setText("Máquina: " + machineCardCount + "\n" + humanPlayer.getPlayerName() + ": " + humanCardCount);
    }
    /**
     * Displays the machine's turn label for a short duration (1 second)
     * and then hides it automatically.
     * Useful for visually indicating when it is the machine's turn.
     */
    public void showMachineTurnTemporarily() {
        if (turnLabel != null) {
            turnLabel.setVisible(true); // Show the label

            // Create a timeline to hide it after 1 second
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(1),
                    event -> turnLabel.setVisible(false)
            ));

            timeline.setCycleCount(1); // Run only once
            timeline.play();
        }
    }


}
