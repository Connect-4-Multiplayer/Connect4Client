package message;

import connect4bot.Connect4Application;
import connect4bot.Lobby;
import javafx.application.Platform;

import java.nio.ByteBuffer;

import static connect4bot.Connect4Application.client;

public class LobbyJoin extends Message {
    static final short PUBLIC_CODE = Short.MAX_VALUE;
    static final short CREATE_PRIVATE = Short.MIN_VALUE;
    private final byte NOT_FOUND = 0, FOUND = 1, CREATED = 2;
    private final byte PRIVATE = 0;
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
        byte messageCode = buffer.get();
        switch (messageCode) {
            case NOT_FOUND -> {
                if (buffer.get() == PRIVATE) client.lobbyMenuController.displayFailureLabel();
                else {
                    Connect4Application.loadScene("connect4.fxml");
                    Platform.runLater(() -> client.controller.showWaitingMessage());
                }
            }
            case FOUND -> {
                if (buffer.get() == PUBLIC) Connect4Application.loadScene("connect4.fxml");
                else {
                    client.lobby = new Lobby(buffer);
                    Connect4Application.loadScene("lobby.fxml");
                }
            }
            case CREATED -> {
                client.lobby = new Lobby(buffer.getShort());
            }
        }
    }
}
