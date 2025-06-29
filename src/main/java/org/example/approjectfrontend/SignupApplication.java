package org.example.approjectfrontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SignupApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        DatabaseHelper.initialize();
        FXMLLoader fxmlLoader = new FXMLLoader(SignupApplication.class.getResource("Signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sign Up :>");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
