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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.*;
import org.example.approjectfrontend.util.SessionManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Optional;
import java.util.ResourceBundle;

public class BuyerProfileController implements Initializable {

    @FXML private ImageView profileImageView;
    @FXML private Button uploadButton;
    @FXML private TextField usernameField, emailField, addressField, phoneField;
    @FXML private Button saveButton;
    @FXML private Button logoutButton;
    @FXML private Label messageLabel;
    @FXML private Button profileBtn, homeBtn, historyBtn;
    @FXML private Button rechargeWalletButton;
    @FXML private Label walletBalanceLabel;

    private File profileImageFile = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uploadButton.setOnAction(event -> chooseProfileImage());
        saveButton.setOnAction(event -> handleSaveProfile());
        logoutButton.setOnAction(event -> handleLogout());
        homeBtn.setOnAction(e -> navigateToPage(e, "BuyerHome-view.fxml"));
        historyBtn.setOnAction(this::handleHistoryClick);
        profileBtn.setDisable(true);
        rechargeWalletButton.setOnAction(event -> showRechargeDialog());
        populateUserData();
    }

    private void updateWalletDisplay(int balance) {
        walletBalanceLabel.setText("موجودی کیف پول: " + balance + " تومان");
    }

    private void showRechargeDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("شارژ کیف پول");
        dialog.setHeaderText(null);
        dialog.setContentText("مبلغ مورد نظر (تومان):");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(txt -> {
            try {
                int amount = Integer.parseInt(txt.trim());
                if (amount <= 0) {
                    messageLabel.setStyle("-fx-text-fill: red;");
                    messageLabel.setText("مبلغ باید یک عدد مثبت باشد.");
                    return;
                }

                new Thread(() -> {
                    ApiResponse response = ApiService.topUpWallet(amount);
                    Platform.runLater(() -> {
                        if (response.getStatusCode() == 200) {
                            messageLabel.setStyle("-fx-text-fill: green;");
                            messageLabel.setText("کیف پول با موفقیت شارژ شد!");
                            populateUserData();
                        } else {
                            messageLabel.setStyle("-fx-text-fill: red;");
                            messageLabel.setText("خطا در شارژ کیف پول: " + response.getBody());
                        }
                    });
                }).start();

            } catch (NumberFormatException e) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("لطفاً یک عدد معتبر وارد کنید.");
            }
        });
    }

    private void populateUserData() {
        new Thread(() -> {
            ApiResponse response = ApiService.getProfile();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    UserDTO freshUser = gson.fromJson(response.getBody(), UserDTO.class);
                    SessionManager.getInstance().setCurrentUser(freshUser);

                    usernameField.setText(freshUser.getFullName());
                    phoneField.setText(freshUser.getPhoneNumber());
                    if (freshUser.getEmail() != null) emailField.setText(freshUser.getEmail());
                    if (freshUser.getAddress() != null) addressField.setText(freshUser.getAddress());

                    if (freshUser.getWalletBalance() != null) {
                        updateWalletDisplay(freshUser.getWalletBalance());
                    } else {
                        updateWalletDisplay(0);
                    }

                } else {
                    messageLabel.setStyle("-fx-text-fill: red;");
                    messageLabel.setText("خطا در بارگذاری اطلاعات پروفایل.");
                }
            });
        }).start();
    }

    private void handleSaveProfile() {
        UpdateProfileRequest updateData = new UpdateProfileRequest();
        updateData.setFullName(usernameField.getText().trim());
        updateData.setPhone(phoneField.getText().trim());
        updateData.setEmail(emailField.getText().trim());
        updateData.setAddress(addressField.getText().trim());

        if (profileImageFile != null) {
            try {
                byte[] fileContent = Files.readAllBytes(profileImageFile.toPath());
                updateData.setProfileImageBase64(Base64.getEncoder().encodeToString(fileContent));
            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("خطا در پردازش عکس.");
                return;
            }
        }

        new Thread(() -> {
            ApiResponse response = ApiService.updateProfile(updateData);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("پروفایل با موفقیت آپدیت شد!");
                } else {
                    messageLabel.setStyle("-fx-text-fill: red;");
                    messageLabel.setText("خطا: " + response.getBody());
                }
            });
        }).start();
    }

    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("خروج از حساب");
        alert.setHeaderText("آیا برای خروج از حساب کاربری خود مطمئن هستید؟");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> ApiService.logout()).start();
            SessionManager.getInstance().clear();
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/org/example/approjectfrontend/login-view.fxml"));
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleHistoryClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("انتخاب نوع تاریخچه");
        alert.setHeaderText("کدام تاریخچه را می‌خواهید مشاهده کنید؟");

        ButtonType ordersBtn = new ButtonType("تاریخچه سفارشات");
        ButtonType transactionsBtn = new ButtonType("تاریخچه تراکنش‌ها");
        ButtonType cancelBtn = new ButtonType("انصراف", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(ordersBtn, transactionsBtn, cancelBtn);

        Optional<ButtonType> result = alert.showAndWait();

        result.ifPresent(buttonType -> {
            if (buttonType == ordersBtn) {
                navigateToPage(event, "OrderHistory-view.fxml");
            } else if (buttonType == transactionsBtn) {
                navigateToPage(event, "TransactionHistory-view.fxml");
            }
        });
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

    private void chooseProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(saveButton.getScene().getWindow());
        if (selectedFile != null) {
            profileImageFile = selectedFile;
            profileImageView.setImage(new Image(selectedFile.toURI().toString()));
        }
    }
}
