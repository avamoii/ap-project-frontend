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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.RegisterRequest;
import org.example.approjectfrontend.api.UserDTO;
import org.example.approjectfrontend.util.SessionManager;

import java.io.IOException;

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
        String fullName = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = null;

        if (buyerRadio.isSelected()) {
            role = "BUYER";
        } else if (sellerRadio.isSelected()) {
            role = "SELLER";
        } else if (courierRadio.isSelected()) {
            role = "COURIER";
        }

        if (fullName.isEmpty() || password.isEmpty() || phone.isEmpty() || role == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("لطفاً فیلدهای نام کامل، رمز عبور، تلفن و نقش را پر کنید.");
            return;
        }

        final String finalRole = role;

        RegisterRequest requestData = new RegisterRequest(fullName, phone, password, finalRole);
        if (!email.isEmpty()) {
            requestData.setEmail(email);
        }

        new Thread(() -> {
            ApiResponse response = ApiService.register(requestData);
            int statusCode = response.getStatusCode();
            String responseBody = response.getBody();

            Platform.runLater(() -> {
                if (statusCode == 200) {
                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("ثبت‌نام با موفقیت انجام شد! 🎉");

                    try {
                        Gson gson = new Gson();
                        JsonObject resBody = gson.fromJson(responseBody, JsonObject.class);
                        String receivedToken = resBody.get("token").getAsString();
                        JsonObject userJson = resBody.getAsJsonObject("user");
                        UserDTO user = gson.fromJson(userJson, UserDTO.class);

                        // ذخیره توکن و اطلاعات کاربر در SessionManager
                        SessionManager.getInstance().setToken(receivedToken);
                        SessionManager.getInstance().setCurrentUser(user);

                        System.out.println("User '" + user.getFullName() + "' registered. Token stored.");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    goToProfileByRole(finalRole.toLowerCase());

                } else {
                    try {
                        JsonObject errorBody = new Gson().fromJson(responseBody, JsonObject.class);
                        String errorMessage = errorBody.get("error").getAsString();
                        messageLabel.setStyle("-fx-text-fill: red;");
                        messageLabel.setText(errorMessage);
                    } catch (Exception e) {
                        messageLabel.setStyle("-fx-text-fill: red;");
                        messageLabel.setText("خطای ناشناخته از سمت سرور. کد وضعیت: " + statusCode);
                    }
                }
            });
        }).start();
    }

    private void goToProfileByRole(String role) {
        String fxmlPath = "";
        switch (role) {
            case "buyer":
                fxmlPath = "/org/example/approjectfrontend/BuyerProfile-view.fxml";
                break;
            case "seller":
                fxmlPath = "/org/example/approjectfrontend/SellerProfile-view.fxml";
                break;
            case "courier":
                fxmlPath = "/org/example/approjectfrontend/courierProfile-view.fxml";
                break;
            default:
                messageLabel.setStyle("-fx-text-fill: black;");
                messageLabel.setText("نقش کاربر نامشخص!");
                return;
        }
        goToProfile(fxmlPath);
    }

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