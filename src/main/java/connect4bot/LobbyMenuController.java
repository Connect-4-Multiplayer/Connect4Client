package connect4bot;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static connect4bot.Client.MAX_LOBBIES;

public class LobbyMenuController implements Initializable {
    @FXML
    private VBox lobbies;

    public void updateLobbies(ArrayList<String> openLobbies) {
        int i = 0;
        for (String lobbyName : openLobbies) {
            Button lobbyButton = (Button) lobbies.getChildren().get(i++);
            lobbyButton.setText(lobbyName);
            lobbyButton.setVisible(true);
        }
        while (i < MAX_LOBBIES) {
            lobbies.getChildren().get(i++).setVisible(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Node lobby : lobbies.getChildren()) {
            lobby.setOnMouseClicked(e -> {
                Request.LOBBY_JOIN.sendRequest(((Button) lobby).getText().getBytes(StandardCharsets.UTF_16BE));
            });
        }
    }
}
