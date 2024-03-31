package connect4bot;

import connect4bot.controllers.LobbyController;
import javafx.application.Platform;

import java.nio.ByteBuffer;

public class Lobby {
    // Turn orders
    public static final byte HOST = 0;
    static final byte GUEST = 1;
    static final byte INIT_RANDOM = 2;

    // Next orders
    private static final byte STAY = 0;
    private static final byte ALTERNATING = 1;
    private static final byte RANDOM = 2;

    public byte isPublic;
    public int clientRole = GUEST;
    public int turnOrder = INIT_RANDOM;
    public int nextOrder = ALTERNATING;
    public short startTime = 180;
    public byte increment = 0;
    public boolean isUnlimited = false;
    public short code;
    public String hostName;
    public String guestName;
    public boolean hostReady;
    public boolean guestReady;

    public Lobby(short code) {
        this.code = code;
        clientRole = HOST;
    }

    public Lobby(ByteBuffer buffer, String guestName, boolean clientIsHost) {
        isPublic = buffer.get();
        turnOrder = buffer.get();
        nextOrder = buffer.get();
        startTime = (short) ((buffer.get() << 8) + buffer.get());
        increment = buffer.get();
        if (buffer.get() == 1) isUnlimited = true;
        hostName = getNameFromBuffer(buffer);
        this.guestName = guestName;
        if (clientIsHost) clientRole = HOST;
    }

    public String startTimeString() {
        int seconds = startTime % 60;
        return (startTime / 60) + (seconds < 10 ? ":0" : ":") + seconds;
    }

    public void updateOpponentName(ByteBuffer name) {
        String opponentName = getNameFromBuffer(name);
        if (clientRole == HOST) {
            guestName = opponentName;
            Platform.runLater(() -> ((LobbyController) Connect4Application.currController).updateGuestName(opponentName));
        }
        else {
            hostName = opponentName;
            Platform.runLater(() -> ((LobbyController) Connect4Application.currController).updateHostName(opponentName));
        }
    }

    public void toggleReady(byte player) {
        LobbyController controller = ((LobbyController) Connect4Application.currController);
        if (player == HOST) {
            hostReady = !hostReady;
            System.out.println("Host Ready: " + hostReady);
            controller.hostReady.setVisible(hostReady);
        }
        else {
            guestReady = !guestReady;
            System.out.println("Guest Ready: " + guestReady);
            controller.guestReady.setVisible(guestReady);
        }
    }

    private String getNameFromBuffer(ByteBuffer buffer) {
        StringBuilder name = new StringBuilder();
        while (buffer.hasRemaining()) {
            name.append((char) ((buffer.get() << 8) + buffer.get()));
        }
        return name.toString();
    }
}
