package org.example.approjectfrontend;

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
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.UpdateProfileRequest;
import org.example.approjectfrontend.api.UserDTO;
import org.example.approjectfrontend.util.SessionManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Optional;
import java.util.ResourceBundle;

public class BuyerProfileController implements Initializable {

    @FXML
    private ImageView profileImageView;
    @FXML
    private Button uploadButton;
    @FXML
    private TextField usernameField, emailField, addressField, phoneField;
    @FXML
    private Button saveButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label messageLabel;
    @FXML
    private Button profileBtn, homeBtn, historyBtn;

    private File profileImageFile = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uploadButton.setOnAction(event -> chooseProfileImage());
        saveButton.setOnAction(event -> handleSaveProfile());
        logoutButton.setOnAction(event -> handleLogout());
        homeBtn.setOnAction(e -> goToHome());
        historyBtn.setOnAction(e -> goToHistory());
        profileBtn.setDisable(true);
        populateUserData();
    }

    private void populateUserData() {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            usernameField.setText(currentUser.getFullName());
            phoneField.setText(currentUser.getPhoneNumber());
            if (currentUser.getEmail() != null) emailField.setText(currentUser.getEmail());
            if (currentUser.getAddress() != null) addressField.setText(currentUser.getAddress());
        }
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

    private void goToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BuyerHome-view.fxml"));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToHistory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BuyerHistory-view.fxml"));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
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