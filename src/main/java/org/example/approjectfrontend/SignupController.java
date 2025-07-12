package org.example.approjectfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
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
    private TextField phoneField;
    @FXML
    private Label messageLabel;
    @FXML
    private RadioButton buyerRadio;
    @FXML
    private RadioButton sellerRadio;
    @FXML
    private RadioButton courierRadio;


    @FXML
    void gotoLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/approjectfrontend/Login-view.fxml"));
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSignup(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = null;

        if (buyerRadio.isSelected()) {
            role = "buyer";
        } else if (sellerRadio.isSelected()) {
            role = "seller";
        } else if (courierRadio.isSelected()) {
            role = "courier";
        }

        SignupResponse response = mockSignupToBackend(username, email, password, phone, role);
        int statusCode = response.getStatusCode();; // بعداً اینو با API واقعی جایگزین می‌کنی

        switch (statusCode) {
            case 200:
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("ثبت‌نام با موفقیت انجام شد! 🎉");
                goToProfileByRole(response.getRole());
                break;
            case 400:
                messageLabel.setStyle("-fx-text-fill: orange;");
                messageLabel.setText("ورودی نامعتبر است.");
                break;
            case 401:
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("دسترسی غیرمجاز.");
                break;
            case 403:
                messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
                messageLabel.setText("شما اجازه ثبت‌نام ندارید.");
                break;
            case 404:
                messageLabel.setStyle("-fx-text-fill: blue;");
                messageLabel.setText("آدرس سرویس ثبت‌نام پیدا نشد!");
                break;
            case 409:
                messageLabel.setStyle("-fx-text-fill: #ffa500;");
                messageLabel.setText("این شماره تماس قبلاً ثبت شده است!");
                break;
            case 415:
                messageLabel.setStyle("-fx-text-fill: #d2691e;");
                messageLabel.setText("نوع اطلاعات ارسالی پشتیبانی نمی‌شود.");
                break;
            case 429:
                messageLabel.setStyle("-fx-text-fill: purple;");
                messageLabel.setText("درخواست بیش از حد مجاز. لطفاً اندکی صبر کنید.");
                break;
            case 500:
                messageLabel.setStyle("-fx-text-fill: gray;");
                messageLabel.setText("خطای سرور! لطفاً بعداً دوباره تلاش کنید.");
                break;
            default:
                messageLabel.setStyle("-fx-text-fill: black;");
                messageLabel.setText("خطای نامشخص رخ داده است.");
        }
    }
    private SignupResponse mockSignupToBackend(String username, String email, String password, String phone, String role) {
        // شرط‌ها صرفاً نمونه هستند
        if (username.equals("bad")) return new SignupResponse(400, null);
        if (phone.equals("0987654321")) return new SignupResponse(409, null);
        if ("forbidden".equals(role)) return new SignupResponse(403, null);
        if (email != null && email.endsWith("@bad.com")) return new SignupResponse(415, null);
        if ("404".equals(phone)) return new SignupResponse(404, null);
        if ("slow".equals(phone)) return new SignupResponse(429, null);
        if ("fail".equals(phone)) return new SignupResponse(500, null);
        return new SignupResponse(200, role); // موفقیت: نقش را ارسال کن
    }
    private void goToProfileByRole(String role) {
        switch (role) {
            case "buyer":
                goToProfile("/org/example/approjectfrontend/BuyerProfile-view.fxml");
                break;
            case "seller":
                goToProfile("/org/example/approjectfrontend/SellerProfile-view.fxml");
                break;
            case "courier":
                goToProfile("/org/example/approjectfrontend/CourierProfile-view.fxml");
                break;
            default:
                messageLabel.setStyle("-fx-text-fill: black;");
                messageLabel.setText("نقش کاربر نامشخص!");
        }
    }

    // این تابع باید تغییر صحنه/Scene را انجام دهد
    private void goToProfile(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage window = (Stage) usernameField.getScene().getWindow();
            window.setScene(new Scene(root));
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}