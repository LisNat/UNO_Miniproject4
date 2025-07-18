package org.example.eiscuno.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.game.IGameEventListener;
import org.example.eiscuno.model.game.ThreadCheckGameOver;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
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
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.deck = new Deck();
        this.table = new Table();
        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        this.posInitCardToShow = 0;
        this.posInitMachineCardToShow = 0;
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
                    gameUno.playCard(card);
                    tableImageView.setImage(card.getImage());
                    humanPlayer.removeCard(findPosCardsHumanPlayer(card));

                    // Manejamos cambio de color para cartas WILD (por ahora así oki)
                    if ("WILD".equals(card.getValue()) || "+4".equals(card.getValue())) {
                        handleWildCard();
                    }

                    checkUnoOpportunity();

                    threadPlayMachine.setHasPlayerPlayed(true);
                    printCardsHumanPlayer();

                } else {
                    System.out.println("No puedes jugar esta carta: " + card.getValue() + " - " + card.getColor());
                }
            });

            this.gridPaneCardsPlayer.add(cardImageView, i, 0);
        }
        // Verificamos si debe aparecer el botón UNO
        checkUnoOpportunity();
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
        if (gameUno == null || humanPlayer == null) {
            System.out.println("Juego no iniciado correctamente.");
            return;
        }

        Card card = gameUno.drawCard(humanPlayer);
        System.out.println("Robaste una carta: " + card.getValue() + " " + card.getColor());

        if (gameUno.canPlay(card)) {
            System.out.println("¡Puedes jugar la carta robada!");
        }

        threadPlayMachine.setHasPlayerPlayed(true); // le da turno a la máquina
        printCardsHumanPlayer(); // actualiza la mano del jugador
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
        unoPressed = false;

        int delay = 2000 + (int)(Math.random() * 2000); // 2000–4000 ms

        unoTimer = new Timeline(new KeyFrame(Duration.millis(delay), e -> {
            if (!unoPressed) {
                System.out.println("Humano no presionó UNO a tiempo. Penalizado.");
                gameUno.drawCard(humanPlayer);
                printCardsHumanPlayer(); // Actualiza la vista
            }
            buttonUno.setVisible(false); // Ocultar de todos modos
        }));
        unoTimer.setCycleCount(1);
        unoTimer.play();
    }

    private void checkUnoOpportunity() {
        if (humanPlayer.getCardsPlayer().size() == 1) {
            buttonUno.setVisible(true);
            startUnoTimer();
        } else {
            buttonUno.setVisible(false);
            if (unoTimer != null) unoTimer.stop();
        }
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
