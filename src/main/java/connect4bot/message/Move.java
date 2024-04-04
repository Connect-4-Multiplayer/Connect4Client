package connect4bot.message;

import connect4bot.Client;
import connect4bot.Connect4Application;
import connect4bot.controllers.Connect4Controller;
import javafx.application.Platform;

import java.nio.ByteBuffer;

public class Move extends Message {

    private static final byte MOVE = 0, PRE_MOVE = 1, DELETE_PRE_MOVE = 2;

    public Move() {
        this.type = MOVE_MESSAGE;
    }

    public void sendMove(byte col) {
        sendRequest(MOVE, col);
    }

    public void sendDeletePreMove() {
        sendRequest(DELETE_PRE_MOVE);
    }

    @Override
    public void process(Client client, ByteBuffer buffer) {
        Connect4Controller controller = (Connect4Controller) Connect4Application.currController;
        switch (buffer.get()) {
            case MOVE -> {
                byte[] args = getBytes(buffer, 6);
                Platform.runLater(() -> controller.playMove(args));
            }
            case PRE_MOVE -> {
                byte[] args = getBytes(buffer, 7);
                Platform.runLater(() -> controller.displayPreMoves(args));
            }
        }

    }
}
