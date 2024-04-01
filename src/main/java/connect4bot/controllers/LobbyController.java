package connect4bot.controllers;

import connect4bot.Connect4Application;
import connect4bot.Lobby;
import connect4bot.message.PlayerInput;
import connect4bot.message.SetSetting;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

import static connect4bot.Connect4Application.client;
import static connect4bot.Lobby.GUEST;
import static connect4bot.Lobby.HOST;

public class LobbyController extends Controller {
    @FXML
    private Label codeLabel;
    @FXML
    public TextField hostName;
    @FXML
    private Label hostReady;
    @FXML
    public TextField guestName;
    @FXML
    private Label guestReady;

    @FXML
    public TextField startTime;
    @FXML
    public TextField increment;
    @FXML
    public RadioButton unlimited;

    @FXML
    public ChoiceBox<String> turnOrder;
    public static final String[] turnOrders = {"You", "Opponent", "Random"};

    @FXML
    public ChoiceBox<String> nextOrder;

    @FXML
    private Button readyButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Connect4Application.currController = this;
        nextOrder.setItems(FXCollections.observableArrayList("Alternate", "Random", "Keep"));
        nextOrder.setValue("Alternate");
        unlimited.setContentDisplay(ContentDisplay.RIGHT);
    }

    public void setUpLobbyForHost() {
        codeLabel.setText("Code: " + client.lobby.code);
        hostName.textProperty().addListener((unused0, unused1, name) -> {
            new PlayerInput().sendName(name);
        });
        guestName.setEditable(false);

        startTime.textProperty().addListener((unused0, unused1, unused2) -> this.changeStartTime());
        increment.textProperty().addListener((unused0, unused1, unused2) -> this.changeIncrement());

        turnOrder.setItems(FXCollections.observableArrayList("You", "Opponent", "Random"));
        turnOrder.setValue("Random");
        turnOrder.getSelectionModel().selectedIndexProperty().addListener(
                (unused0, unused1, order) -> new SetSetting().sendSetting(SetSetting.TURN_ORDER, order.byteValue())
        );
        nextOrder.getSelectionModel().selectedIndexProperty().addListener(
                (unused0, unused1, order) -> new SetSetting().sendSetting(SetSetting.NEXT_ORDER, order.byteValue())
        );
    }

    public void setUpLobbyForGuest(Lobby lobby) {
        codeLabel.setVisible(false);

        hostName.setText(lobby.hostName);
        hostName.setEditable(false);
        guestName.setText(lobby.guestName);
        guestName.textProperty().addListener((unused0, unused1, name) -> {
            new PlayerInput().sendName(name);
        });

        startTime.setText(lobby.startTimeString());
        startTime.setEditable(false);
        increment.setText(lobby.increment + "");
        increment.setEditable(false);
        unlimited.setSelected(lobby.isUnlimited);
        unlimited.setDisable(true);

        turnOrder.getSelectionModel().select(lobby.turnOrder);
        turnOrder.setDisable(true);
        turnOrder.setItems(FXCollections.observableArrayList("Opponent", "You", "Random"));
        turnOrder.setValue("Random");
        nextOrder.getSelectionModel().select(lobby.nextOrder);
        nextOrder.setDisable(true);
    }

    public void changeStartTime() {
        try {
            String[] time = startTime.getText().split(":");
            new SetSetting().sendStartTime((short)
                    (Short.parseShort(time[0].strip()) * 60
                    + Short.parseShort(time[1].strip()))
            );
            System.out.println("Legal start time");
        }
        catch (Exception e) {
            System.out.println("Invalid start time");
        }
    }

    public void changeIncrement() {
        try {
            new SetSetting().sendRequest(SetSetting.INCREMENT, Byte.parseByte(increment.getText().strip()));
        }
        catch (Exception e) {
            System.out.println("Invalid increment time");
        }
    }

    public void toggleUnlimited() {
        new SetSetting().sendRequest(SetSetting.IS_UNLIMITED, (byte) (unlimited.isSelected() ? 1 : 0));
    }

    public void toggleReady() {
        new PlayerInput().toggleReady();
    }

    public void setHostReady(boolean ready) {
        hostReady.setVisible(ready);
        if (client.lobby.clientRole == HOST) readyButton.setText(ready ? "Unready" : "Ready");
    }

    public void setGuestReady(boolean ready) {
        guestReady.setVisible(ready);
        if (client.lobby.clientRole == GUEST) readyButton.setText(ready ? "Unready" : "Ready");
    }
}
