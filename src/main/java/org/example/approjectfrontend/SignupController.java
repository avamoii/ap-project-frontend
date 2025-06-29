package org.example.approjectfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;


public class SignupController {

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField usernameField;
    @FXML
    private Label messageLabel;

    @FXML
    void gotoLogin(ActionEvent event) {
      try {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/approjectfrontend/login-view.fxml"));

        Parent loginRoot = loader.load();
        Scene loginScene = new Scene(loginRoot);

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(loginScene);}
      catch (IOException e) {
          e.printStackTrace();
      }

    }

    @FXML
    void handleSignup(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {

            messageLabel.setText("پر کردن هر دو فیلد الزامی است!");
            return;
        }
        if (!email.matches(".+@.+\\..+")) {
            messageLabel.setText("ایمیل معتبر وارد کنید.");
            return;
        }
        boolean success = DatabaseHelper.registerUser(username, password, email);
        if (success) {
            messageLabel.setText("ثبت نام با موفقیت انجام شد!");

        } else {
            messageLabel.setText("خطا: نام کاربری یا ایمیل قبلاً استفاده شده است.");
        }

    }

}
