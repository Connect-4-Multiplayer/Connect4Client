package connect4bot.controllers;

import connect4bot.Connect4Application;
import connect4bot.Lobby;
import connect4bot.message.PlayerSelection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

import static connect4bot.Connect4Application.client;

public class LobbyController extends Controller {
    @FXML
    private Label codeLabel;
    @FXML
    private TextField hostName;
    @FXML
    public Label hostReady;
    @FXML
    private TextField guestName;
    @FXML
    public Label guestReady;
    @FXML
    private TextField startTime;
    @FXML
    private TextField increment;
    @FXML
    private RadioButton unlimited;

    @FXML
    private ChoiceBox<String> turnOrder;
    @FXML
    private ChoiceBox<String> nextOrder;
    private static final String[] turnOrders = {"You", "Opponent", "Random"};
    private static final String[] nextOrders = {"Alternate", "Keep", "Random"};

    @FXML
    private Button readyButton;
    private static final String[] readyOptions = {"Ready", "Unready"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connect4Application.currController = this;
        turnOrder.setItems(FXCollections.observableArrayList(turnOrders));
        turnOrder.setValue("Random");
        nextOrder.setItems(FXCollections.observableArrayList(nextOrders));
        nextOrder.setValue("Alternate");
    }

    public void setUpLobbyForHost() {
        codeLabel.setText("Code: " + client.lobby.code);
        hostName.textProperty().addListener((__, ___, name) -> {
            new PlayerSelection().sendName(name);
        });
        guestName.setEditable(false);
    }

    public void setUpLobbyForGuest(Lobby lobby) {
        codeLabel.setVisible(false);

        hostName.setText(lobby.hostName);
        hostName.setEditable(false);
        guestName.setText(lobby.guestName);
        guestName.textProperty().addListener((__, ___, name) -> {
            new PlayerSelection().sendName(name);
        });

        startTime.setText(lobby.startTimeString());
        increment.setText(lobby.increment + "");
        unlimited.setSelected(lobby.isUnlimited);
        turnOrder.setValue(turnOrders[lobby.turnOrder]);
        nextOrder.setValue(nextOrders[lobby.nextOrder]);
    }

    public void updateHostName(String name) {
        hostName.setText(name);
    }

    public void updateGuestName(String name) {
        guestName.setText(name);
    }

    public void toggleReady() {
        new PlayerSelection().toggleReady();
    }
}
