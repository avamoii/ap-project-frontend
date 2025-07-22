package org.example.approjectfrontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font; // این خط برای کار با فونت‌ها اضافه شده است
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //============== شروع تغییرات ==============

        // این دو خط، فونت‌ها را از پوشه منابع بارگذاری می‌کنند
        // مطمئن شوید نام فایل‌ها دقیقاً با نام فایل‌های فونت شما در پوشه 'fonts' یکی باشد
        Font.loadFont(getClass().getResource("/org/example/approjectfrontend/fonts/e1.ttf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/org/example/approjectfrontend/fonts/p1.ttf").toExternalForm(), 10);

        //============== پایان تغییرات ==============


        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("Main.fxml"));


        Scene scene = new Scene(fxmlLoader.load(), 500, 750);

        URL css = getClass().getResource("styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("Banana App");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}