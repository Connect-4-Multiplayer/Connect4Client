package connect4bot;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Player implements Closeable {
    final static int PORT = 24554;
    final static String HOST = "127.0.0.1";

    private final AsynchronousSocketChannel clientSock;
    private int gameId;

    public Player() throws IOException, ExecutionException, InterruptedException {
        clientSock = AsynchronousSocketChannel.open();
        clientSock.connect(new InetSocketAddress(HOST, PORT)).get();
        // this.gameId = receive().get();
    }

    public CompletableFuture<Integer> receive() throws IOException, InterruptedException, ExecutionException {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        CompletableFuture<Integer> received = new CompletableFuture<>();
        clientSock.read(buffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer integer, Void unused) {
                System.out.println("Received move");
                buffer.flip();
                received.complete(buffer.getInt());
            }

            @Override
            public void failed(Throwable throwable, Void unused) {
                received.completeExceptionally(throwable);
            }
        });
        return received;
    }

    public Future<Integer> sendMove(int col) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        // buffer.putInt(gameId);
        buffer.putInt(col);
        buffer.flip();
        return clientSock.write(buffer);
    }

    @Override
    public void close() throws IOException {
        clientSock.close();
    }
}
