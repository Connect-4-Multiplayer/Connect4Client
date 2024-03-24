module connect4bot.connect4bot {
    requires javafx.controls;
    requires javafx.fxml;

    opens connect4bot to javafx.fxml;
    exports connect4bot;
    exports message;
    opens message to javafx.fxml;
}