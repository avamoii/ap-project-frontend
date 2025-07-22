package org.example.approjectfrontend;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.approjectfrontend.api.*;
import org.example.approjectfrontend.util.SessionManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import javafx.stage.Stage;
import java.util.ResourceBundle;

public class SellerProfileController implements Initializable {

    @FXML
    private ImageView profileImageView;
    @FXML
    private Button uploadButton;
    @FXML
    private TextField usernameField, emailField, addressField, phoneField,
            bankNameField, accountNumberField, brandNameField, additionalInformationField;
    @FXML
    private Button saveButton;
    @FXML
    private Button logoutButton, homeButton, myRestaurantButton, profileButton;
    @FXML
    private Label messageLabel;

    private File profileImageFile = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uploadButton.setOnAction(event -> chooseProfileImage());
        saveButton.setOnAction(event -> handleSaveProfile());
        logoutButton.setOnAction(event -> handleLogout());
        profileButton.setDisable(true);
        populateUserData();
    }

    private void populateUserData() {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            usernameField.setText(currentUser.getFullName());
            phoneField.setText(currentUser.getPhoneNumber());
            if (currentUser.getEmail() != null) emailField.setText(currentUser.getEmail());
            if (currentUser.getAddress() != null) addressField.setText(currentUser.getAddress());

            BankInfoDTO bankInfo = currentUser.getBankInfo();
            if (bankInfo != null) {
                if (bankInfo.getBankName() != null) bankNameField.setText(bankInfo.getBankName());
                if (bankInfo.getAccountNumber() != null) accountNumberField.setText(bankInfo.getAccountNumber());
            }
        }
    }

    private void handleSaveProfile() {
        UpdateProfileRequest updateData = new UpdateProfileRequest();
        updateData.setFullName(usernameField.getText().trim());
        updateData.setPhone(phoneField.getText().trim());
        updateData.setEmail(emailField.getText().trim());
        updateData.setAddress(addressField.getText().trim());

        BankInfoDTO bankInfo = new BankInfoDTO();
        bankInfo.setBankName(bankNameField.getText().trim());
        bankInfo.setAccountNumber(accountNumberField.getText().trim());
        updateData.setBankInfo(bankInfo);

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
        SessionManager.getInstance().clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/approjectfrontend/Login-view.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
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

    @FXML
    private void goToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SellerHome-view.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(root);
    }

    @FXML
    private void goToMyRestaurant(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("RegisterRestaurant-view.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(root);
    }
}