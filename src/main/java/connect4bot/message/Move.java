package connect4bot.message;

import connect4bot.Connect4Application;
import connect4bot.controllers.Connect4Controller;

import java.nio.ByteBuffer;

public class Move extends Message {
    public Move() {
        this.type = MOVE;
    }

    @Override
    public void process(ByteBuffer buffer) {
        ((Connect4Controller) Connect4Application.currController).playMove(getBytes(buffer, 6));
    }
}
