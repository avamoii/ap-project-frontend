package org.example.approjectfrontend;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.*;
import org.example.approjectfrontend.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class BuyerProfileController implements Initializable {
    // --- فیلدهای FXML بر اساس فایل جدید شما ---
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private Label walletBalanceLabel;
    @FXML private Label messageLabel;
    @FXML private Button saveButton;
    @FXML private Button logoutButton;
    @FXML private Button rechargeWalletButton;
    @FXML private Button homeBtn;
    @FXML private Button historyBtn;
    @FXML private Button profileBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        profileBtn.setStyle("-fx-background-color: #191919; -fx-text-fill: #fff;");
        homeBtn.setOnAction(e -> navigateToPage(e, "BuyerHome-view.fxml"));
        // **توجه:** نام فایل تاریخچه سفارشات BuyerHistory-view.fxml است
        historyBtn.setOnAction(e -> navigateToPage(e, "OrderHistory-view.fxml"));

        // اتصال رویدادها به دکمه‌های جدید
        saveButton.setOnAction(e -> handleUpdateProfile());
        logoutButton.setOnAction(e -> handleLogout(e));
        rechargeWalletButton.setOnAction(e -> handleChargeWallet());

        loadUserProfile();
    }

    private void loadUserProfile() {
        new Thread(() -> {
            ApiResponse response = ApiService.getUserProfile();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    UserDTO user = new Gson().fromJson(response.getBody(), UserDTO.class);
                    updateUI(user);
                } else {
                    showAlert(Alert.AlertType.ERROR, "خطا", "خطا در دریافت اطلاعات پروفایل.");
                }
            });
        }).start();
    }

    private void updateUI(UserDTO user) {
        if (user == null) return;
        usernameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhoneNumber());
        addressField.setText(user.getAddress() != null ? user.getAddress() : "");
        walletBalanceLabel.setText(String.format("موجودی کیف پول: %,d تومان", user.getWalletBalance()));
    }

    @FXML
    private void handleUpdateProfile() {
        UpdateProfileRequest requestData = new UpdateProfileRequest();
        requestData.setFullName(usernameField.getText());
        requestData.setEmail(emailField.getText());
        requestData.setAddress(addressField.getText());

        new Thread(() -> {
            ApiResponse response = ApiService.updateProfile(requestData);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    messageLabel.setText("پروفایل با موفقیت به‌روز شد.");
                    messageLabel.setStyle("-fx-text-fill: #218838;");
                } else {
                    messageLabel.setText("خطا در به‌روزرسانی پروفایل.");
                    messageLabel.setStyle("-fx-text-fill: #d22;");
                }
            });
        }).start();
    }

    @FXML
    private void handleChargeWallet() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("شارژ کیف پول");
        dialog.setHeaderText("مبلغ مورد نظر برای افزایش موجودی را وارد کنید.");
        dialog.setContentText("مبلغ (تومان):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                int amount = Integer.parseInt(amountStr);
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "مبلغ نامعتبر", "لطفاً یک عدد مثبت وارد کنید.");
                    return;
                }

                TopUpWalletRequest topUpRequest = new TopUpWalletRequest();
                topUpRequest.setAmount(amount);

                new Thread(() -> {
                    ApiResponse response = ApiService.topUpWallet(topUpRequest);
                    Platform.runLater(() -> {
                        if (response.getStatusCode() == 200) {
                            showAlert(Alert.AlertType.INFORMATION, "موفق", "کیف پول شما با موفقیت شارژ شد.");
                            loadUserProfile(); // رفرش کردن اطلاعات برای نمایش موجودی جدید
                        } else {
                            showAlert(Alert.AlertType.ERROR, "ناموفق", "خطا در شارژ کیف پول: " + response.getBody());
                        }
                    });
                }).start();

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "ورودی نامعتبر", "لطفاً فقط عدد وارد کنید.");
            }
        });
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.getInstance().clear();
        new Thread(ApiService::logout).start();
        navigateToPage(event, "Login-view.fxml");
    }

    private void navigateToPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}