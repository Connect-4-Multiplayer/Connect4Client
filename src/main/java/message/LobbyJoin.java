package message;

import connect4bot.Connect4Application;

import java.io.IOException;
import java.nio.ByteBuffer;

import static connect4bot.Connect4Application.client;

public class LobbyJoin extends Message {
    static final short PUBLIC_CODE = Short.MAX_VALUE;
    private final byte FAIL = 0;
    private final byte PRIVATE = 0;
    private final byte PUBLIC = 1;
    public LobbyJoin() {
        this.type = LOBBY_JOIN;
    }

    public void sendJoinPublicRequest() {
        sendJoinRequest(PUBLIC_CODE);
    }

    public void sendJoinRequest(short code) {
        sendRequest((byte) (code >> 8), (byte) code);
    }

    @Override
    public void process(ByteBuffer buffer) {
        byte lobbyType = buffer.get();
        byte status = buffer.get();
        if (lobbyType == PRIVATE) {
            if (status == FAIL) client.lobbyMenuController.displayFailureLabel();
            else {
                try {
                    Connect4Application.loadScene("connect4.fxml");
                } catch (IOException ignored) {
                }
            }
        }
        else {
            if (status == FAIL) {
            }

        }
    }
}
