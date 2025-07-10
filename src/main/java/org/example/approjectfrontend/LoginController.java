package org.example.approjectfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import org.example.approjectfrontend.MockUser;
import org.example.approjectfrontend.UserDataStore;


public class LoginController {

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phonenumberField;
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
        String phone = phonenumberField.getText().trim();
        String password = passwordField.getText().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("پر کردن هر دو فیلد الزامی است!");
            return;
        }

        boolean found = false;
        for (MockUser user : UserDataStore.mockUserList) {
            if (user.getPhone().equals(phone)) {
                found = true;
                if (user.getPassword().equals(password)) {
                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("ورود موفقیت‌آمیز بود.");
                } else {
                    messageLabel.setStyle("-fx-text-fill: red;");
                    messageLabel.setText("رمز عبور اشتباه است.");
                }
                break;
            }
        }

        if (!found) {
            messageLabel.setStyle("-fx-text-fill: blue;");
            messageLabel.setText("شماره جدید است. اطلاعاتی برای این شماره ثبت نشده است.");
        }
    }

}