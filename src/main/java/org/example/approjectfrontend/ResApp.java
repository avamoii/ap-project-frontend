package org.example.approjectfrontend;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
public class ResApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("RegisterRestaurant-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("res");
        stage.setScene(scene);
        stage.setWidth(400);
        stage.setHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
