// مسیر: src/main/java/module-info.java
module org.example.approjectfrontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires java.desktop;
    requires com.google.gson;

    // --- **تغییر اصلی اینجاست** ---
    // این خط به Gson اجازه می‌دهد به کلاس‌های شما دسترسی داشته باشد
    opens org.example.approjectfrontend to com.google.gson, javafx.fxml;

    opens org.example.approjectfrontend.api to com.google.gson;

    exports org.example.approjectfrontend;
}