package connect4bot.controllers;

import connect4bot.Connect4Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import connect4bot.message.LobbyJoin;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Responsible for controlling the title screen
 */
public class TitleController extends Controller implements Initializable {
    /**
     * The background of the title screen
     */
    @FXML
    private AnchorPane backGround;

    /**
     * Sets up the title screen
     *
     * @param url            N/A
     * @param resourceBundle N/A
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connect4Application.currController = this;
        final int OFFSET_X = 177, OFFSET_Y = 408, CELL_WIDTH = 66, CELL_HEIGHT = 56, ROWS = 6, COLS = 7;
        final int[] HEIGHTS = {0, 5, 1, 6, 4, 2, 1}, PARITIES = {0, 1, 0, 0, 1, 0, 0};
        for (int c = 0; c < COLS; c++) {
            for (int r = 0; r < ROWS; r++) {
                Color color = Color.YELLOW;
                if (r >= HEIGHTS[c]) color = Color.WHITE;
                else if ((r & 1) == PARITIES[c]) color = Color.RED;
                backGround.getChildren().add(new Circle(OFFSET_X + c * CELL_WIDTH, OFFSET_Y - r * CELL_HEIGHT, 25, color));
            }
        }
    }

    public void findOpponent() {
        new LobbyJoin().sendJoinPublicRequest();
        Platform.runLater(() -> Connect4Application.loadScene("connect4.fxml"));
    }

    public void openLobbyMenu() {
        Platform.runLater(() -> Connect4Application.loadScene("lobbyMenu.fxml"));
    }

    public static void main(String[] args) {
    }
}
