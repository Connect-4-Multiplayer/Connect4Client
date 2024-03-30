package connect4bot.controllers;

import connect4bot.Connect4Application;
import connect4bot.Lobby;
import connect4bot.message.PlayerSelection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController extends Controller {
    @FXML
    public Label codeLabel;
    @FXML
    public TextField hostName;
    @FXML
    public TextField guestName;
    @FXML
    public TextField startTime;
    @FXML
    public TextField increment;
    @FXML
    public RadioButton unlimited;
    @FXML
    public ChoiceBox<String> turnOrder;
    @FXML
    public ChoiceBox<String> nextOrder;
    private static final String[] turnOrders = {"You", "Opponent", "Random"};
    private static final String[] nextOrders = {"Alternate", "Keep", "Random"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connect4Application.currController = this;
        turnOrder.setItems(FXCollections.observableArrayList(turnOrders));
        turnOrder.setValue("Random");
        nextOrder.setItems(FXCollections.observableArrayList(nextOrders));
        nextOrder.setValue("Alternate");
    }

    public void displayLobbySettings(Lobby lobby) {
        codeLabel.setText("Join Code: " + lobby.code);

        hostName.setText(lobby.hostName);
        hostName.setDisable(true);
        hostName.textProperty().addListener((__, ___, name) -> new PlayerSelection().sendNameToServer(name));

        guestName.setText(lobby.guestName);

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
}
