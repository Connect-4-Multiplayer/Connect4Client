package connect4bot;

import connect4bot.controllers.LobbyController;
import javafx.application.Platform;

import java.nio.ByteBuffer;

public class Lobby {
    // Turn orders
    public static final byte HOST = 0;
    public static final byte GUEST = 1;
    static final byte INIT_RANDOM = 2;

    // Next orders
    private static final byte ALTERNATING = 0;
//    private static final byte RANDOM = 1;

    public short code;
    public byte isPublic;

    public byte clientRole = GUEST;
    public byte hostTurn;

    public int turnOrder = INIT_RANDOM;
    public int nextOrder = ALTERNATING;

    public short startTime = 180;
    public byte increment = 0;
    public boolean isUnlimited = false;

    public String hostName = "";
    public String guestName = "";
    public boolean hostReady;
    public boolean guestReady;

    public int hostScore = 0;
    public int guestScore = 0;

    public LobbyController controller;

    public Lobby(short code) {
        this.code = code;
        clientRole = HOST;
    }

    public Lobby(ByteBuffer buffer, String guestName) {
        code = buffer.getShort();
        isPublic = buffer.get();
        turnOrder = buffer.get();
        nextOrder = buffer.get();
        increment = buffer.get();
        if (buffer.get() != 0) isUnlimited = true;
        startTime = (short) (((buffer.get() & 255) << 8) + (buffer.get() & 255));
        hostName = getNameFromBuffer(buffer);
        this.guestName = guestName;
    }

    public String startTimeString() {
        int seconds = startTime % 60;
        return (startTime / 60) + (seconds < 10 ? ":0" : ":") + seconds;
    }

    public void updateOpponentName(ByteBuffer name) {
        String opponentName = getNameFromBuffer(name);
        if (clientRole == HOST) {
            guestName = opponentName;
            Platform.runLater(() -> controller.guestName.setText(opponentName));
        }
        else {
            hostName = opponentName;
            Platform.runLater(() -> controller.hostName.setText(opponentName));
        }
    }

    public void toggleReady(byte player) {
        if (player == HOST) {
            hostReady = !hostReady;
            Platform.runLater(() -> controller.setHostReady(hostReady));
        }
        else {
            guestReady = !guestReady;
            Platform.runLater(() -> controller.setGuestReady(guestReady));
        }
    }

    public void removeOpponent() {
        if (clientRole == GUEST) hostName = guestName;
        clientRole = HOST;
        guestName = "";
        guestReady = false;
        hostScore = guestScore = 0;
        Platform.runLater(() -> {
            Connect4Application.loadScene("lobby.fxml");
            controller = (LobbyController) Connect4Application.currController;
            controller.setGuestReady(false);
            controller.hostName.setText(hostName);
            controller.guestName.setText("");
            controller.setUpLobbyForHost(this);
        });
    }

    private String getNameFromBuffer(ByteBuffer buffer) {
        StringBuilder name = new StringBuilder();
        while (buffer.hasRemaining()) {
            name.append((char) ((buffer.get() << 8) + buffer.get()));
        }
        return name.toString().replace("\0", "");
    }

    public void setTurnOrder(byte turnOrder) {
        this.turnOrder = turnOrder;
        Platform.runLater(() -> controller.turnOrder.getSelectionModel().select(turnOrder));

    }

    public void setNextOrder(byte nextOrder) {
        this.nextOrder = nextOrder;
        Platform.runLater(() -> controller.nextOrder.getSelectionModel().select(nextOrder));
    }

    public void setStartTime(short startTime) {
        this.startTime = startTime;
        if (clientRole == GUEST) Platform.runLater(() -> controller.startTime.setText(startTimeString()));
    }

    public void setIncrement(byte increment) {
        this.increment = increment;
        if (clientRole == GUEST) Platform.runLater(() -> controller.increment.setText(increment + ""));
    }

    public void setUnlimited(byte isUnlimited) {
        this.isUnlimited = isUnlimited != 0;
        Platform.runLater(() -> controller.unlimited.setSelected(this.isUnlimited));
    }
}
