package connect4bot.message;

import connect4bot.Client;

import java.nio.ByteBuffer;

import static connect4bot.Connect4Application.client;

public class SetSetting extends Message {
    public static final byte TURN_ORDER = 0;
    public static final byte NEXT_ORDER = 1;
    public static final byte START_TIME = 2;
    public static final byte INCREMENT = 3;
    public static final byte IS_UNLIMITED = 4;

    public SetSetting() {
        this.type = SET_SETTING;
    }

    public void sendSetting(byte settingId, byte settingVal) {
        client.write(constructMessage(3, settingId, settingVal).flip());
    }

    public void sendStartTime(short startTime) {
        System.out.println(startTime);
        client.write(constructMessage(4, START_TIME, (byte) (startTime >> 8), (byte) startTime).flip());
    }

    @Override
    public void process(Client client, ByteBuffer buffer) {
        System.out.println("got setting");
        byte settingId = buffer.get();
        byte settingVal0 = buffer.get();
        byte settingVal1 = buffer.get();
        switch (settingId) {
            case TURN_ORDER -> client.lobby.setTurnOrder(settingVal0);
            case NEXT_ORDER -> client.lobby.setNextOrder(settingVal0);
            case START_TIME -> client.lobby.setStartTime((short) (((settingVal0 & 255) << 8) + (settingVal1 & 255)));
            case INCREMENT -> client.lobby.setIncrement(settingVal0);
            case IS_UNLIMITED -> client.lobby.setUnlimited(settingVal0);
        }
    }
}
