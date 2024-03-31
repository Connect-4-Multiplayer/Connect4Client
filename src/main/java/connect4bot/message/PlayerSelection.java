package connect4bot.message;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static connect4bot.Connect4Application.client;

public class PlayerSelection extends Message {
    static final byte NAME = 0, TOGGLE_READY = 1;
    static final int MAX_NAME_LENGTH = 64;

    public PlayerSelection() {
        this.type = PLAYER_SELECTION;
    }

    public void sendName(String name) {
        client.write(constructMessage(MAX_NAME_LENGTH + 2, NAME)
                .put(name.getBytes(StandardCharsets.UTF_16BE)).flip());
    }

    public void toggleReady() {
        client.write(constructMessage(3, TOGGLE_READY).flip());
    }

    @Override
    public void process(ByteBuffer buffer) {
        System.out.println("got player selection");
        byte selection = buffer.get();
        switch (selection) {
            case NAME -> client.lobby.updateOpponentName(buffer);
            case TOGGLE_READY -> client.lobby.toggleReady(buffer.get());
        }
    }
}
