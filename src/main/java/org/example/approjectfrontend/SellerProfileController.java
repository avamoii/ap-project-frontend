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
import org.example.approjectfrontend.api.*;
import org.example.approjectfrontend.util.SessionManager;
 import javafx.stage.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerProfileController implements Initializable {

    @FXML
    private ImageView profileImageView;
    @FXML
    private Button uploadButton;
    @FXML
    private TextField usernameField, emailField, addressField, phoneField,
            bankNameField, accountNumberField;
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
        UserDTO cachedUser = SessionManager.getInstance().getCurrentUser();
        if (cachedUser != null) {
            usernameField.setText(cachedUser.getFullName());
            phoneField.setText(cachedUser.getPhoneNumber());
            if (cachedUser.getEmail() != null) emailField.setText(cachedUser.getEmail());
            if (cachedUser.getAddress() != null) addressField.setText(cachedUser.getAddress());
        }

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

                    BankInfoDTO bankInfo = freshUser.getBankInfo();
                    if (bankInfo != null) {
                        if (bankInfo.getBankName() != null) bankNameField.setText(bankInfo.getBankName());
                        if (bankInfo.getAccountNumber() != null) accountNumberField.setText(bankInfo.getAccountNumber());
                    }
                } else {
                    messageLabel.setStyle("-fx-text-fill: red;");
                    messageLabel.setText("خطا در بارگذاری آخرین اطلاعات پروفایل.");
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