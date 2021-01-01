module BeeChatNetwork {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires kotlin.stdlib;
    requires xbee.java.library;
    requires java.logging;
    requires bcprov.jdk16;

    opens src to javafx.graphics, javafx.fxml, javafx.base, javafx.controls;
    exports src;
}