package connect4bot.message;

import connect4bot.Connect4Application;
import connect4bot.Lobby;
import connect4bot.controllers.Connect4Controller;
import connect4bot.controllers.LobbyController;
import connect4bot.controllers.LobbyMenuController;
import javafx.application.Platform;

import java.nio.ByteBuffer;

import static connect4bot.Connect4Application.client;

public class LobbyJoin extends Message {
    static final short PUBLIC_CODE = Short.MAX_VALUE;
    static final short CREATE_PRIVATE = Short.MIN_VALUE;
    private final byte NO_OPPONENT = 0, OPPONENT_FOUND = 1, LOBBY_CREATED = 2;
    private final byte PUBLIC = 1;

    public LobbyJoin() {
        this.type = LOBBY_JOIN;
    }

    public void sendJoinPublicRequest() {
        sendRequest(PUBLIC_CODE);
    }

    public void sendCreatePrivateRequest() {
        sendRequest(CREATE_PRIVATE);
    }

    public void sendRequest(short code) {
        sendRequest((byte) (code >> 8), (byte) code);
    }

    @Override
    public void process(ByteBuffer buffer) {
        Platform.runLater(() -> {
            byte messageCode = buffer.get();
            switch (messageCode) {
                case NO_OPPONENT -> {
                    if (buffer.get() == PUBLIC) {
                        Connect4Application.loadScene("connect4.fxml");
                        ((Connect4Controller) Connect4Application.currController).showWaitingMessage();
                    }
                    else ((LobbyMenuController) Connect4Application.currController).displayFailureLabel();
                }
                case OPPONENT_FOUND -> {
                    if (buffer.get() == PUBLIC) Connect4Application.loadScene("connect4.fxml");
                    else {
                        client.lobby = new Lobby(buffer, client.name);
                        Connect4Application.loadScene("lobby.fxml");
                        ((LobbyController) Connect4Application.currController)
                    }
                }
                case LOBBY_CREATED -> {
                    client.lobby = new Lobby(buffer.getShort());
                    ((LobbyController) Connect4Application.currController).codeLabel.setText("Code: " + client.lobby.code);
                }
            }
        });
    }
}
