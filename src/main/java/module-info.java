module org.example.approjectfrontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.approjectfrontend to javafx.fxml;
    exports org.example.approjectfrontend;
}