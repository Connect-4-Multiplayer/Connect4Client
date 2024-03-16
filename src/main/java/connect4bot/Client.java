package connect4bot;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Client implements Closeable {
    static final int PORT = 24454;
    static final String HOST = "127.0.0.1";//"71.244.148.113";
    static final int INPUT_BYTES = 7;
    static final int OUTPUT_BYTES = 6;

    private final AsynchronousSocketChannel clientSock;
    Connect4Controller controller;

    public Client() throws IOException, ExecutionException, InterruptedException {
        clientSock = AsynchronousSocketChannel.open();
        clientSock.connect(new InetSocketAddress(HOST, PORT)).get();
        receive();
    }

    public void receive() throws IOException, InterruptedException, ExecutionException {
        ByteBuffer buffer = ByteBuffer.allocate(INPUT_BYTES);
        clientSock.read(buffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer integer, Void unused) {
                try {
                    System.out.println("recieved");
                    processServerMessage(buffer.flip());
                    receive();
                }
                catch (IOException | InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void failed(Throwable throwable, Void unused) {
                // received.completeExceptionally(throwable);
            }
        });
    }

    private void processServerMessage(ByteBuffer buffer) {
        System.out.println(buffer.remaining());
        byte type = buffer.get();
        if (Request.MOVE_REQUEST.isType(type)) handleMove(buffer);
    }

    private void handleMove(ByteBuffer buffer) {
        byte col = buffer.get();
        byte colHeight = buffer.get();
        byte color = buffer.get();
        byte gameState = buffer.get();
        byte winningSpot = buffer.get();
        byte winInc = buffer.get();
        controller.playMove(col, colHeight, color, gameState, winningSpot, winInc);
    }

    public Future<Integer> write(ByteBuffer buffer) {
        return clientSock.write(buffer);
    }

    @Override
    public void close() throws IOException {
        clientSock.close();
    }
}
