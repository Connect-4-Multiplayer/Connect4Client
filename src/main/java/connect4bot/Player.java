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

    private final AsynchronousSocketChannel clientSock;

    public Player(InetAddress ip) throws IOException {
        clientSock = AsynchronousSocketChannel.open();

        final int PORT = 24553;
        InetSocketAddress addr = new InetSocketAddress(ip, PORT);

        clientSock.bind(addr);
    }

    public Future<Void> connect(SocketAddress server) {
        return this.clientSock.connect(server);
    }

    public Future<Long> receive() throws IOException, InterruptedException, ExecutionException {
        // TODO specify what should be the max size later (Bitboard should not be huge, but maybe we need extra data)
        // 8 bytes for now, enough to fit the bitboard
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

        CompletableFuture<Long> received = new CompletableFuture<>();
        clientSock.read(buffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer integer, Void unused) {
                buffer.flip();
                received.complete(buffer.getLong());
            }

            @Override
            public void failed(Throwable throwable, Void unused) {
                received.completeExceptionally(throwable);
            }

        });

        return received;
    }

    public Future<Integer> sendMove(int col, int gameId) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2);
        buffer.putInt(gameId);
        buffer.putInt(col);
        buffer.flip();
        return clientSock.write(buffer);
    }

    @Override
    public void close() throws IOException {
        clientSock.close();
    }
}
