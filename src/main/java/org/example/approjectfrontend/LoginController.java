package org.example.approjectfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
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
         if (!DatabaseHelper.checkLogin(username, password)) {
            messageLabel.setText("نام کاربری یا رمز عبور غلط است.");
            return;
        }
        String role = DatabaseHelper.getUserRole(username);
        if (role == null) {
            messageLabel.setText("خطا در دریافت نقش کاربر!");
            return;
        }

        // نقش بر اساس دیتابیس تعیین می‌شود؛ باید ریدایرکت صورت بگیرد
        if (role.equals("buyer") || role.equals("seller")) {
            gotoAddressPage(event, username);
        }


    }
    private void gotoAddressPage(ActionEvent event, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/approjectfrontend/Address-view.fxml"));
            Parent root = loader.load();
            // اختیاری: ارسال username به کنترلر مقصد
            // org.example.approjectfrontend.AddressController controller = loader.getController();
            // controller.setUsername(username);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
