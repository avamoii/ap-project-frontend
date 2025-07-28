package org.example.approjectfrontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // مسیر FXML را نسبت به سورس‌ست یا روت پروژه تعیین کن
        Parent root = FXMLLoader.load(getClass().getResource("SellerHome-view.fxml"));
        primaryStage.setTitle("پنل فروشنده");
        // سایز دلخواهت مثلا
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
