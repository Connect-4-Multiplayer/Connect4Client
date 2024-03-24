package connect4bot;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import message.Move;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static connect4bot.Connect4Application.client;

/**
 * Controllers the gui elements used when playing a game
 */
public class Connect4Controller implements Initializable {
    /**
     * Number of rows and columns in the board
     */
    private static final int ROWS = 6, COLUMNS = 7, SPOTS = 42;
    /**
     * Dimensions of the board
     */
    private static final int BOARD_WIDTH = 462, BOARD_HEIGHT = 336, BOARD_X = 144, BOARD_Y = 70;
    /**
     * Dimensions of each cell on the board
     */
    private static final int CELL_WIDTH = BOARD_WIDTH / 7, CELL_HEIGHT = BOARD_HEIGHT / 6;
    /**
     * Radius of each game piece
     */
    private static final int PIECE_RADIUS = 25;
    /**
     * Starting y location of a piece when it is dropped into the board
     */
    private static final int DROP_START_Y = 42;
    /**
     * Opacity of a faded piece
     */
    private static final double FADED_OPACITY = .375;
    /**
     * Stores all spots filled during the game
     */
    private static final byte WIN = 0, LOSS = 1, DRAW = 2, NOT_OVER = 3;
    private final Circle[] board = new Circle[SPOTS];
    /**
     * Highlights the spot where the user's piece will fall
     */
    @FXML
    private Circle moveMarker;
    /**
     * Background of the scene
     */
    @FXML
    private AnchorPane backGround;
    /**
     * Container for the columns
     */
    @FXML
    private GridPane grid;
    /**
     * Options that allow the user to quit or restart
     */
    @FXML
    private HBox endOptions;
    /**
     * Label that displays different messages to the user
     */
    @FXML
    private Label message;
    @FXML
    private HBox engineNavigator;

    /**
     * Initializes the gui elements for starting the game
     * @param url            N/A
     * @param resourceBundle N/A
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client.controller = this;
        moveMarker.setFill(Color.RED);
        Shape board = new Rectangle(BOARD_X, BOARD_Y, BOARD_WIDTH, BOARD_HEIGHT);
        for (int c = 0; c < COLUMNS; c++) {
            for (int r = 0; r < ROWS; r++) {
                board = Shape.subtract(board, getPiece(Color.WHITE, BOARD_X + CELL_WIDTH * (c + 0.5), BOARD_Y + CELL_HEIGHT * (r + 0.5)));
            }
            Pane colPane = new Pane();
            try {
                configureMouseEvents(colPane, c);
            } catch (IOException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            grid.add(colPane, c, 0);
        }
        board.setFill(Color.BLUE);
        board.setMouseTransparent(true);
        backGround.getChildren().add(board);
    }

    /**
     * Sets up events that are dispatched when the user clicks a column
     * @param colPane The Pane containing the column
     * @param col     The index of the column
     */
    private void configureMouseEvents(Pane colPane, int col) throws IOException, ExecutionException, InterruptedException {
        colPane.setOnMouseEntered(e0 -> {
            moveMarker.setCenterX(BOARD_X + CELL_WIDTH * (0.5 + col));
            moveMarker.setCenterY(BOARD_Y + BOARD_HEIGHT - CELL_HEIGHT * (0.5 + 0));
            moveMarker.setVisible(true);
        });
        colPane.setOnMouseExited(e -> moveMarker.setVisible(false));
        colPane.setOnMouseClicked(e0 -> new Move().sendRequest((byte) col));
    }

    /**
     * Creates the animation for dropping a piece into a column
     */
    public void playMove(byte[] args) {
        final byte col = args[0], colHeight = args[1], color = args[2],
                gameState = args[3], winningSpot = args[4], winInc = args[5];
        Platform.runLater(() -> {
            Circle piece = getPiece(color == 1 ? Color.RED : Color.YELLOW, BOARD_X + CELL_WIDTH / 2d + CELL_WIDTH * col, DROP_START_Y);
            board[col * 6 + colHeight] = piece;
            backGround.getChildren().add(piece);
            piece.toBack();

            TranslateTransition drop = new TranslateTransition(Duration.seconds(0.2), piece);
            drop.setByY(BOARD_HEIGHT - CELL_HEIGHT * colHeight);
            drop.setOnFinished(e -> {
                if (gameState != NOT_OVER) {
                    message.setVisible(true);
                    displayEndOptions();
                }
                if (gameState == DRAW) {
                    message.setText("DRAW!");
                } else if (gameState != NOT_OVER) {
                    highlightWin(winningSpot, winInc);
                    if (gameState == WIN) {
                        message.setText("You Win!");
                    }
                    else message.setText("You Lose!");
                }
            });
            drop.play();
        });
    }

    public void highlightWin(byte winningSpot, byte winInc) {
        for (Circle piece : board) {
            if (piece != null) piece.setOpacity(FADED_OPACITY);
        }
        int end = winningSpot + (winInc << 2);
        for (int spot = winningSpot; spot < end; spot += winInc) {
            board[spot].setOpacity(1);
        }
    }

    /**
     * Creates a new piece
     * @param color The color of the piece
     * @param x     The x-position of the piece
     * @param y     The y-position of the piece
     * @return THe piece
     */
    public Circle getPiece(Color color, double x, double y) {
        Circle piece = new Circle();
        piece.setRadius(PIECE_RADIUS);
        piece.setCenterX(x);
        piece.setCenterY(y);
        piece.setFill(color);
        return piece;
    }

    public void startAnalysis() {
        endOptions.setVisible(false);
        engineNavigator.setVisible(true);
        backGround.getChildren().removeAll(Arrays.stream(board).filter(Objects::nonNull).toList());
    }

    public void showNextMove(byte[] args, String minimax) {
        message.setText(minimax);
        playMove(args);
    }

    public void showPrevMove(byte col, byte height, String minimax) {
        message.setText(minimax);
        backGround.getChildren().remove(board[col * 6 + height]);
    }

    public void showWaitingMessage() {
        Platform.runLater(() -> {
            message.setText("Waiting...");
            message.setVisible(true);
        });
    }

    /**
     * Displays the options for ending the game
     */
    private void displayEndOptions() {
        endOptions.setVisible(true);
    }

    /**
     * Restarts the game
     */
    public void playAgain() throws IOException {
        Connect4Application.loadScene("connect4.fxml");
    }

    /**
     * Returns to the title screen
     */
    public void quit() throws IOException {
        Connect4Application.loadScene("title.fxml");
    }
}
