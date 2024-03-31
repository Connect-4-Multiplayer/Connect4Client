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
    private static final byte OPPONENT_NOT_FOUND = 0, OPPONENT_FOUND = 1, LOBBY_CREATED = 2;
    private static final byte PUBLIC = 1;

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
        switch (buffer.get()) {
            case OPPONENT_NOT_FOUND -> handleOpponentNotFound(buffer.get());
            case OPPONENT_FOUND -> handleOpponentFound(buffer);
            case LOBBY_CREATED -> handleLobbyCreated(buffer.getShort());
        }
    }

    private void handleOpponentNotFound(byte lobbyType) {
        Platform.runLater(() -> {
            if (lobbyType == PUBLIC) {
                Connect4Application.loadScene("connect4.fxml");
                ((Connect4Controller) Connect4Application.currController).showWaitingMessage();
            }
            else ((LobbyMenuController) Connect4Application.currController).displayFailureLabel();
        });
    }

    private void handleOpponentFound(ByteBuffer buffer) {
        final int SETTINGS_BYTES = 71;
        byte lobbyType = buffer.get();
        byte[] settings = new byte[SETTINGS_BYTES];
        buffer.get(settings);
        Platform.runLater(() -> {
            if (lobbyType == PUBLIC) Connect4Application.loadScene("connect4.fxml");
            else {
                client.lobby = new Lobby(ByteBuffer.allocate(SETTINGS_BYTES).put(settings).flip(), client.name, false);
                Connect4Application.loadScene("lobby.fxml");
                ((LobbyController) Connect4Application.currController).setUpLobbyForGuest(client.lobby);
                new PlayerSelection().sendName(client.name);
            }
        });
    }

    private void handleLobbyCreated(short code) {
        client.lobby = new Lobby(code);
        Platform.runLater(() -> {
            Connect4Application.loadScene("lobby.fxml");
            ((LobbyController) Connect4Application.currController).setUpLobbyForHost();
        });
    }
}
