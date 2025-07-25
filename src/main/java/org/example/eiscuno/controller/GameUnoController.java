package org.example.eiscuno.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        initVariables();

        this.gameUno.setGameEventListener(this);

        this.gameUno.startGame();

        // Mostramos la carta inicial en la mesa
        Card topCard = table.getCurrentCardOnTheTable();
        tableImageView.setImage(topCard.getImage());

        printCardsHumanPlayer();


        // Iniciamos Hilos
        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer());
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this.gameUno, this.deck, this);
        threadPlayMachine.start();

        threadCheckGameOver = new ThreadCheckGameOver(humanPlayer, machinePlayer, this, gameUno);
        threadCheckGameOver.start();

        printCardsMachinePlayer();
    }

    /**
     * Initializes the variables for the game.
     */
    private void initVariables() {
        // Para aplicar serialización:
        GameUnoState loadedState = tryLoadGameUnoState();

        if(loadedState != null) {
            this.humanPlayer = loadedState.getHumanPlayer();
            this.machinePlayer = loadedState.getMachinePlayer();
            this.deck = loadedState.getDeck();
            this.table = loadedState.getTable();
            this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        } else {
            this.humanPlayer = new Player("HUMAN_PLAYER");
            this.machinePlayer = new Player("MACHINE_PLAYER");
            this.deck = new Deck();
            this.table = new Table();
            this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        }

        this.posInitCardToShow = 0;
        this.posInitMachineCardToShow = 0;
    }

    private GameUnoState tryLoadGameUnoState() {
        SerializableFileHandler handler = new SerializableFileHandler();
        return (GameUnoState) handler.deserialize("GameUnoState.ser");
    }

    /**
     * Prints the human player's cards on the grid pane.
     */
    private void printCardsHumanPlayer() {
        this.gridPaneCardsPlayer.getChildren().clear();
        Card[] currentVisibleCardsHumanPlayer = this.gameUno.getCurrentVisibleCardsHumanPlayer(this.posInitCardToShow);

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
                    return; // Evita cualquier acción si el humano debe saltar turno
                }

                if (gameUno.canPlay(card)) {
                    try {
                        gameUno.playCard(card);
                        tableImageView.setImage(card.getImage());
                        humanPlayer.removeCard(findPosCardsHumanPlayer(card));

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

    public void printCardsMachinePlayer() {
        gridPaneCardsMachine.getChildren().clear();

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
     * Finds the position of a specific card in the human player's hand.
     *
     * @param card the card to find
     * @return the position of the card, or -1 if not found
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
     * Handles the action of taking a card.
     *
     * @param event the action event
     */
    @FXML
    void onHandleTakeCard(ActionEvent event) {
        try {
            if (gameUno == null || humanPlayer == null) {
                System.out.println("Juego no iniciado correctamente.");
                return;
            }

            Card card = gameUno.drawCard(humanPlayer);
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
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });

        } catch (IllegalGameStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al robar carta: " + e.getMessage());
        }
    }


    /**
     * Handles the action of saying "Uno".
     *
     * @param event the action event
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
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        stage.close();
    }

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

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
        } catch (Exception e) {
            System.err.println("Error al iniciar timer UNO: " + e.getMessage());
        }
    }

    // Metodo para manejar la penalización
    private void handleUnoPenalty() {
        try {
            System.out.println("¡No dijiste UNO a tiempo! Penalización aplicada.");
            gameUno.drawCard(humanPlayer); // Intenta robar carta
            printCardsHumanPlayer();

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

    // Metodo para verificar estado del mazo
    private void checkDeckEmptyStatus() {
        Platform.runLater(() -> {
            try {
                buttonTakeCard.setDisable(gameUno.isDeckEmpty());
            } catch (Exception ex) {
                System.err.println("Error verificando estado del mazo: " + ex.getMessage());
            }
        });
    }

    private void checkUnoOpportunity() {
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

    public void showGameOver(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin del Juego");
        alert.setHeaderText(message);
        alert.setContentText("Presiona aceptar.");
        alert.showAndWait();

        // Cierra la ventana del juego
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        stage.close();
    }

}
