package connect4bot;

import java.nio.ByteBuffer;

public class Lobby {
    public byte isPublic;
    public int turnOrder = 2;
    public int nextOrder = 1;
    public short startTime = 180;
    public byte increment = 0;
    public byte isUnlimited = 0;
    public short code;

    public Lobby(short code) {
        this.code = code;
    }

    public Lobby(ByteBuffer buffer) {
        isPublic = buffer.get();
        turnOrder = buffer.get();
        nextOrder = buffer.get();
        startTime = (short) ((buffer.get() << 8) + buffer.get());
        increment = buffer.get();
        isUnlimited = buffer.get();
    }
}
