package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.UserDTO;
import org.example.approjectfrontend.util.SessionManager;

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
            Parent signupRoot = FXMLLoader.load(getClass().getResource("/org/example/approjectfrontend/Signup-view.fxml"));
            Scene signupScene = new Scene(signupRoot);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(signupScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String phone = phonenumberField.getText().trim();
        String password = passwordField.getText().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("شماره تلفن و رمز عبور نباید خالی باشند.");
            return;
        }

        new Thread(() -> {
            ApiResponse response = ApiService.login(phone, password);
            int statusCode = response.getStatusCode();
            String responseBody = response.getBody();

            Platform.runLater(() -> {
                if (statusCode == 200) {
                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("ورود موفقیت‌آمیز بود.");

                    try {
                        Gson gson = new Gson();
                        JsonObject bodyJson = gson.fromJson(responseBody, JsonObject.class);

                        String token = bodyJson.get("token").getAsString();
                        JsonObject userJson = bodyJson.getAsJsonObject("user");
                        UserDTO user = gson.fromJson(userJson, UserDTO.class);

                        SessionManager.getInstance().setToken(token);
                        SessionManager.getInstance().setCurrentUser(user);
                        System.out.println("Login successful for " + user.getFullName() + ". Token stored.");

                        goToHomeByRole(user.getRole().toLowerCase());

                    } catch (Exception e) {
                        e.printStackTrace();
                        messageLabel.setStyle("-fx-text-fill: red;");
                        messageLabel.setText("خطا در پردازش پاسخ سرور.");
                    }
                } else {
                    try {
                        Gson gson = new Gson();
                        JsonObject errorBody = gson.fromJson(responseBody, JsonObject.class);
                        String errorMessage = errorBody.get("error").getAsString();
                        messageLabel.setStyle("-fx-text-fill: red;");
                        messageLabel.setText(errorMessage);
                    } catch (Exception e) {
                        messageLabel.setStyle("-fx-text-fill: red;");
                        messageLabel.setText("خطای ناشناخته. کد: " + statusCode);
                    }
                }
            });
        }).start();
    }

    private void goToHomeByRole(String role) {
        String fxmlPath;
        switch (role) {
            case "buyer":
                fxmlPath = "/org/example/approjectfrontend/BuyerHome-view.fxml";
                break;
            case "seller":
                fxmlPath = "/org/example/approjectfrontend/SellerHome-view.fxml";
                break;
            case "courier":
                fxmlPath = "/org/example/approjectfrontend/CourierHome-view.fxml";
                break;
            // --- **تغییر اصلی و کلیدی اینجاست** ---
            // یک case جدید برای نقش ادمین اضافه می‌کنیم
            case "admin":
                fxmlPath = "/org/example/approjectfrontend/AdminDashboard-view.fxml";
                break;
            default:
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("نقش کاربر نامشخص است!");
                return;
        }
        goToPage(fxmlPath);
    }

    private void goToPage(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage window = (Stage) phonenumberField.getScene().getWindow();
            window.setScene(new Scene(root));
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}