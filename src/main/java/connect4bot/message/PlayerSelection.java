package connect4bot.message;

import connect4bot.Lobby;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static connect4bot.Connect4Application.client;

public class PlayerSelection extends Message {
    static final byte NAME = 0, SET_READY = 1;
    static final int MAX_NAME_LENGTH = 64;

    public PlayerSelection() {
        this.type = PLAYER_SELECTION;
    }

    public void sendNameToServer(String name) {
        client.write(constructMessage(MAX_NAME_LENGTH + 2, NAME)
                .put(name.getBytes(StandardCharsets.UTF_16BE)).flip());
    }

    @Override
    public void process(ByteBuffer buffer) {
        byte selection = buffer.get();
        switch (selection) {
            case NAME -> client.lobby.updateOpponentName(buffer);
            case SET_READY -> System.out.println();
        }
    }
}
