package connect4bot;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import message.LobbyJoin;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static connect4bot.Connect4Application.client;

public class LobbyMenuController implements Initializable {
    @FXML
    private TextField lobbyCode;
    @FXML
    private Label failLabel;

    public void createLobby() {
        new LobbyJoin().sendCreatePrivateRequest();
        Connect4Application.loadScene("lobby.fxml");
    }

    public void submitForm() throws IOException {
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
        client.lobbyMenuController = this;
    }
}
