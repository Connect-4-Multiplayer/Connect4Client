module connect4bot.connect4bot {
    requires javafx.controls;
    requires javafx.fxml;

    opens connect4bot to javafx.fxml;
    exports connect4bot;
    exports connect4bot.message;
    opens connect4bot.message to javafx.fxml;
    exports connect4bot.controllers;
    opens connect4bot.controllers to javafx.fxml;
}