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

    // خط زیر را اضافه کنید
    requires com.google.gson;

    opens org.example.approjectfrontend to javafx.fxml;

    // این خط را اضافه کنید تا Gson بتواند به کلاس‌های شما در پکیج api دسترسی داشته باشد
    opens org.example.approjectfrontend.api to com.google.gson;

    exports org.example.approjectfrontend;
}