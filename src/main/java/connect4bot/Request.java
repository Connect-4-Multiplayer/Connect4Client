package connect4bot;

import java.nio.ByteBuffer;

import static connect4bot.Connect4Application.client;

public enum Request {

    FIND_OPPONENT(0, 1),
    MOVE_REQUEST(1, 2),
    LOBBY_REQUEST(2, 1);

    final byte type;
    final byte size;

    Request(int type, int size) {
        this.type = (byte) type;
        this.size = (byte) size;
    }

    public boolean isType(int type) {
        return this.type == type;
    }

    /**
     * Sends a {@link Request} based on the constant selected
     * @param args The bytes to send. Must be smaller than the size of the request used
     * @throws IllegalArgumentException when the size is greater than the size of the request
     */
    public void sendRequest(byte... args) {
        if (args.length > size) {
            throw new IllegalArgumentException("Size is too large!");
        }
        ByteBuffer buffer = ByteBuffer.allocate(size)
                .put(type);
        for (byte b : args) buffer.put(b);
        client.write(buffer.flip());
    }

}
