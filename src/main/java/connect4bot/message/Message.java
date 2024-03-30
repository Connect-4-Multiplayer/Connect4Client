package connect4bot.message;

import java.nio.ByteBuffer;

import static connect4bot.Connect4Application.client;

public abstract class Message {
    static final byte LOBBY_JOIN = 0;
    static final byte MOVE = 1;
    static final byte PLAYER_SELECTION = 2;
    byte type;

    public static Message of(byte type) {
        System.out.println("Type: " + type);
        return switch (type) {
            case LOBBY_JOIN -> new LobbyJoin();
            case MOVE -> new Move();
            default -> null;
        };
    }

    /**
     * Sends a {@link Message} based on the constant selected
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

    public ByteBuffer constructMessage(int size, byte...args) {
        return ByteBuffer.allocate(size).put(type).put(args);
    }

    byte[] getBytes(ByteBuffer buffer, int count) {
        byte[] bytes = new byte[count];
        for (int i = 0; i < count; i++) bytes[i] = buffer.get();
        return bytes;
    }

    public abstract void process(ByteBuffer buffer);
}
