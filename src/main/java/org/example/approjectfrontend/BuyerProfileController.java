package org.example.approjectfrontend;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
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
    @FXML private Button profileBtn;
    @FXML private Button homeBtn;
    @FXML
    private Button historyBtn;
    private File profileImageFile = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uploadButton.setOnAction(event -> chooseProfileImage());
        saveButton.setOnAction(event -> handleSaveProfile());
        logoutButton.setOnAction(event -> handleLogout());
        homeBtn.setOnAction(e -> goToHome());
        historyBtn.setOnAction(e -> goToHistory());
        profileBtn.setDisable(true);
    }    private void goToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BuyerHome-view.fxml"));
            // فرض: دستیابی به stage از طریق یک کامپوننت صفحه فعلی
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace(); // برای رفع خطاها
        }
    }

    private void goToHistory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BuyerHistory-view.fxml"));
            // فرض: دستیابی به stage از طریق یک کامپوننت صفحه فعلی
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace(); // برای رفع خطاها
        }
    }
    private void handleLogout() {
        try {

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("Login-view.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage = (javafx.stage.Stage) logoutButton.getScene().getWindow();

            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    private void chooseProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب عکس پروفایل");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("عکس", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (file != null) {
            profileImageFile = file;
            profileImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    private void handleSaveProfile() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();


        int statusCode = mockSaveProfileToBackend(username, email, address, phone, profileImageFile);

        switch (statusCode) {
            case 200:
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("پروفایل با موفقیت ذخیره شد!");
                break;
            case 400:
                messageLabel.setStyle("-fx-text-fill: orange;");
                messageLabel.setText("ورودی نامعتبر است.");
                break;
            case 401:
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("شما مجاز به ثبت تغییرات نیستید.");
                break;
            case 403:
                messageLabel.setStyle("-fx-text-fill: #ff4c4c;");
                messageLabel.setText("دسترسی غیرمجاز.");
                break;
            case 404:
                messageLabel.setStyle("-fx-text-fill: blue;");
                messageLabel.setText("سرویس پیدا نشد.");
                break;
            case 409:
                messageLabel.setStyle("-fx-text-fill: #ffa500;");
                messageLabel.setText("شماره تماس تکراری است.");
                break;
            case 415:
                messageLabel.setStyle("-fx-text-fill: #d2691e;");
                messageLabel.setText("فرمت فایل پشتیبانی نمی‌شود.");
                break;
            case 429:
                messageLabel.setStyle("-fx-text-fill: purple;");
                messageLabel.setText("درخواست بیش از حد مجاز. لطفاً بعداً امتحان کنید.");
                break;
            case 500:
                messageLabel.setStyle("-fx-text-fill: #888;");
                messageLabel.setText("خطای سرور.");
                break;
            default:
                messageLabel.setStyle("-fx-text-fill: black;");
                messageLabel.setText("خطای ناشناخته.");
        }
    }

    // شبیه‌ساز پاسخ سرور (تابع mock)
    private int mockSaveProfileToBackend(String username, String email, String address, String phone, File imgFile) {
        if (username.equals("bad")) return 400;
        if (phone.equals("401")) return 401;
        if (phone.equals("403")) return 403;
        if (phone.equals("404")) return 404;
        if (phone.equals("409")) return 409;
        if (imgFile != null && !(imgFile.getName().endsWith("png") || imgFile.getName().endsWith("jpg") || imgFile.getName().endsWith("jpeg"))) return 415;
        if (phone.equals("429")) return 429;
        if (phone.equals("500")) return 500;
        return 200;
    }
}
