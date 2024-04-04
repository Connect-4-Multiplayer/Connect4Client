package connect4bot;

import connect4bot.controllers.Connect4Controller;
import connect4bot.controllers.LobbyMenuController;
import connect4bot.message.Message;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client implements Closeable {
    static final int PORT = 24454;
    static final String HOST = "127.0.0.1";//"71.244.148.113";
    static final int INPUT_BYTES = 513;
    static final int MAX_NAME_LENGTH = 32;
    static final int MAX_LOBBIES = 8;

    private final AsynchronousSocketChannel clientSock;
    public Lobby lobby;
    public String name = "";

    public Client() throws IOException, ExecutionException, InterruptedException {
        clientSock = AsynchronousSocketChannel.open();
        clientSock.connect(new InetSocketAddress(HOST, PORT)).get();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                clientSock.close();
                System.out.println("Closed");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        receive();
    }

    public void receive() throws IOException, InterruptedException, ExecutionException {
        Client client = this;
        ByteBuffer buffer = ByteBuffer.allocate(INPUT_BYTES);
        clientSock.read(buffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer bytes, Void unused) {
                try {
                    if (bytes == -1) clientSock.close();
                    System.out.println("RECEIVED");
                    buffer.flip();
                    System.out.println(buffer.remaining());
                    while (buffer.hasRemaining()) {
                        Message.of(buffer.get()).process(client, buffer);
                    }
                    receive();
                }
                catch (IOException | InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void failed(Throwable throwable, Void unused) {}
        });
    }

    private void getLobbies(ByteBuffer buffer) {
        ArrayList<String> lobbies = new ArrayList<>();
        for (int i = 0; i < MAX_LOBBIES; i++) {
            StringBuilder name = new StringBuilder();
            for (int j = 0; j < MAX_NAME_LENGTH; j++) {
                name.append((char) buffer.getShort());
            }
            lobbies.add(name.toString());
        }
//        lobbyMenuController.updateLobbies(lobbies);
    }

//    private void getNextMove(ByteBuffer buffer) {
//        controller.showNextMove(getBytes(buffer, 6), decodeMinimax(buffer.get(), buffer.get()));
//    }

//    private void getPrevMove(ByteBuffer buffer) {
//        controller.showPrevMove(buffer.get(), buffer.get(), decodeMinimax(buffer.get(), buffer.get()));
//    }

    private String decodeMinimax(byte moves, byte minimax) {
        final int bound = 22;
        if (minimax == 0) return "This leads to a draw";
        return "You " + (minimax > 0 ? "win" : "lose") + " in " + (bound - moves) + " moves";
    }

    public Future<Integer> write(ByteBuffer buffer) {
        return clientSock.write(buffer);
    }

    @Override
    public void close() throws IOException {
        clientSock.close();
    }
}
