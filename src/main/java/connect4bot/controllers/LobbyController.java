package connect4bot.controllers;

import connect4bot.Connect4Application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController extends Controller {
    @FXML
    public Label codeLabel;
    @FXML


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connect4Application.currController = this;
    }

    public void displayLobbySettings() {

    }
}
