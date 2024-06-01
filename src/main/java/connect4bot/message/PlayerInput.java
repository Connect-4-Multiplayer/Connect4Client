package connect4bot.message;

import connect4bot.Client;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static connect4bot.Connect4Application.client;

public class PlayerInput extends Message {
    static final byte CHANGE_NAME = 0, TOGGLE_READY = 1, QUIT = 2;
    static final int MAX_NAME_LENGTH = 64;

    public PlayerInput() {
        this.type = PLAYER_INPUT;
    }

    public void sendName(String name) {
        client.write(constructMessage(MAX_NAME_LENGTH + 2, CHANGE_NAME)
                .put(name.getBytes(StandardCharsets.UTF_16BE)).flip());
    }

    public void toggleReady() {
        client.write(constructMessage(2, TOGGLE_READY).flip());
    }

    public void quit() {
        client.write(constructMessage(2, QUIT).flip());
    }

    @Override
    public void process(Client client, ByteBuffer buffer) {
        System.out.println("got player selection");
        byte selection = buffer.get();
        switch (selection) {
            case CHANGE_NAME -> client.lobby.updateOpponentName(buffer);
            case TOGGLE_READY -> client.lobby.toggleReady(buffer.get());
            case QUIT -> client.lobby.removeOpponent();
        }
    }
}
