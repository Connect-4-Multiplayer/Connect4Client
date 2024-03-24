package message;

import java.nio.ByteBuffer;

import static connect4bot.Connect4Application.client;

public class Move extends Message {
    public Move() {
        this.type = MOVE;
    }

    @Override
    public void process(ByteBuffer buffer) {
        client.controller.playMove(getBytes(buffer, 6));
    }
}
