package connect4bot;

import java.nio.ByteBuffer;

import static connect4bot.Connect4Application.client;

public enum Request {

    FIND_OPPONENT(0),
    MOVE(1),
    LOBBY_VIEW(2),
    LOBBY_JOIN(3),
    START_ANALYSIS(4),
    ANALYZE_PREV(5),
    ANALYZE_NEXT(6);


    final byte type;

    Request(int type) {
        this.type = (byte) type;
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
        client.write(ByteBuffer.allocate(args.length + 1)
                .put(type)
                .put(args)
                .flip()
        );
    }

}
