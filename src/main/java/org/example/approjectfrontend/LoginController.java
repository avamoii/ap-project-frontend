package org.example.approjectfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class LoginController {

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;
    @FXML
    private Label messageLabel;

    @FXML
    void goToSignUp(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/org/example/approjectfrontend/Signup-view.fxml"));
            javafx.scene.Parent signupRoot = loader.load();
            javafx.scene.Scene signupScene = new javafx.scene.Scene(signupRoot);

            javafx.stage.Stage currentStage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(signupScene);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {

            messageLabel.setText("پر کردن هر دو فیلد الزامی است!");
            return;
        }
        else{
            messageLabel.setText("ورود موفقیت امیز بود.");
        }

    }

}