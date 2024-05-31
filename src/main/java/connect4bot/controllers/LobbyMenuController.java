package connect4bot.controllers;

import connect4bot.Connect4Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import connect4bot.message.LobbyJoin;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyMenuController extends Controller implements Initializable {
    @FXML
    private TextField lobbyCode;
    @FXML
    private Label failLabel;

    public void createLobby() {
        new LobbyJoin().sendCreatePrivateRequest();
    }

    public void submitForm() {
        new LobbyJoin().sendRequest(Short.parseShort(lobbyCode.getText(), 16));
    }

    public void displayFailureLabel() {
        failLabel.setVisible(true);
    }

    public void hideFailureLabel() {
        failLabel.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connect4Application.currController = this;
    }
}
