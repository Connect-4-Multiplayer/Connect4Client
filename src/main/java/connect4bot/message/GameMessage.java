package connect4bot.message;

import connect4bot.Client;
import connect4bot.Connect4Application;
import connect4bot.controllers.Connect4Controller;
import javafx.application.Platform;

import java.nio.ByteBuffer;

public class GameMessage extends Message {

    private static final byte START = 0, RESIGN = 1;

    public GameMessage() {
        type = GAME_MESSAGE;
    }

    @Override
    public void process(Client client, ByteBuffer buffer) {
        switch (buffer.get()) {
            case START -> startGame();
            case RESIGN -> System.out.println();
        }
    }

    private void startGame() {
        Platform.runLater(() -> {
            Connect4Application.loadScene("connect4.fxml");
        });
    }
}
