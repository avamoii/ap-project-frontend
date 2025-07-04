package org.example.approjectfrontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AddressApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/approjectfrontend/Address-view.fxml"));
        primaryStage.setTitle("وارد کردن آدرس");
        primaryStage.setScene(new Scene(root, 400, 280));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
