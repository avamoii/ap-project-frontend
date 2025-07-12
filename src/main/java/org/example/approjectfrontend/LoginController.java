package org.example.approjectfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;


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

        // فرض کنیم statusCode رو از پاسخ بک‌اند یا mock می‌گیری
        // دریافت پاسخ (موقتاً از mock، بعداً از API واقعی)
        LoginResponse response = mockLoginToBackend(phone, password);

        int statusCode = response.getStatusCode();
        String role = response.getRole();

        switch (statusCode) {
            case 200:
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("ورود موفقیت‌آمیز بود.");
                goToHomeByRole(role);
                // ارسال به صفحه بعدی
                break;
            case 400:
                messageLabel.setStyle("-fx-text-fill: orange;");
                messageLabel.setText("ورودی نامعتبر است."); // Invalid input
                break;
            case 401:
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("نام کاربری یا رمز عبور اشتباه است."); // Unauthorized
                break;
            case 403:
                messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
                messageLabel.setText("مجوز دسترسی ندارید."); // Forbidden
                break;
            case 404:
                messageLabel.setStyle("-fx-text-fill: blue;");
                messageLabel.setText("کاربری با این اطلاعات یافت نشد."); // Not found
                break;
            case 409:
                messageLabel.setStyle("-fx-text-fill: #ffa500;");
                messageLabel.setText("تعارض در عملیات. لطفاً دوباره تلاش کنید."); // Conflict
                break;
            case 415:
                messageLabel.setStyle("-fx-text-fill: #d2691e;");
                messageLabel.setText("نوع داده پشتیبانی نمی‌شود."); // Unsupported Media Type
                break;
            case 429:
                messageLabel.setStyle("-fx-text-fill: purple;");
                messageLabel.setText("درخواست بیش از حد مجاز. لطفاً کمی صبر کنید."); // Too Many Requests
                break;
            case 500:
                messageLabel.setStyle("-fx-text-fill: gray;");
                messageLabel.setText("خطای سرور! لطفاً بعداً دوباره امتحان کنید."); // Internal Server Error
                break;
            default:
                messageLabel.setStyle("-fx-text-fill: black;");
                messageLabel.setText("خطای ناشناخته رخ داده است.");
        }
    }

    private void goToHomeByRole(String role) {
        switch (role) {
            case "buyer":
                goToHome("/org/example/approjectfrontend/BuyerHome-view.fxml");
                break;
            case "seller":
                goToHome("/org/example/approjectfrontend/SellerHome-view.fxml");
                break;
            case "courier":
                goToHome("/org/example/approjectfrontend/CourierHome-view.fxml");
                break;
            default:
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("نقش کاربر نامشخص است!");
        }
    }
    private void goToHome(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage window = (Stage) phonenumberField.getScene().getWindow();
            window.setScene(new Scene(root));
            window.show();
        } catch (IOException e) {
            e.printStackTrace(); // می‌تونی بعداً لاگ جایگزین کنی
        }
    }

    /** تابع mock مخصوص تست اولیه **/
    private LoginResponse mockLoginToBackend(String phone, String password) {
        if (phone.equals("09121234567") && password.equals("1234")) return new LoginResponse(200, "buyer");
        if (phone.equals("09121112222") && password.equals("4321")) return new LoginResponse(200, "seller");
        if (phone.equals("09123334444") && password.equals("courier")) return new LoginResponse(200, "courier");
        if (phone.isEmpty() || password.isEmpty()) return new LoginResponse(400, null);
        if (!phone.startsWith("09")) return new LoginResponse(400, null);
        if (phone.equals("blocked")) return new LoginResponse(401, null);
        return new LoginResponse(404, null);
    }

}