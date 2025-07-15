package org.example.approjectfrontend;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterRestaurantController implements Initializable {
    @FXML
    private Button homeButton;
    @FXML
    private Button myRestaurantButton;
    @FXML
    private Button profileButton;
    @FXML
    private TextField nameField, addressField, phoneField, taxFeeField, additionalFeeField;
    @FXML
    private ImageView logoImageView;
    @FXML
    private Button chooseLogoButton, registerButton;
    @FXML
    private Label messageLabel;

    private File logoFile = null; // فایل لوگو

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chooseLogoButton.setOnAction(event -> chooseLogo());
        registerButton.setOnAction(event -> handleRegister());
    }
    @FXML
    private void chooseLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب لوگو");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("تصاویر", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(chooseLogoButton.getScene().getWindow());
        if (selectedFile != null) {
            logoFile = selectedFile;
            logoImageView.setImage(new Image(selectedFile.toURI().toString()));
        }
    }
    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();

        // اعتبارسنجی اولیه
        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            handleApiResponse(400);
            return;
        }

        // قسمت موک: کاملاً تصادفی - یا با توجه به مقدار خاصی
        // مثلاً اگه نام با "A" شروع بشه بگو موفق بود!، اگه با "X" شروع بشه بگو خطا
        int statusCode = mockStatusCode(name);

        handleApiResponse(statusCode);
    }

    // فانکشن موک انتخاب کد وضعیت (برای تست)
    private int mockStatusCode(String name) {
        if (name.startsWith("A")) return 201; // موفق
        if (name.startsWith("X")) return 500; // سرور
        if (name.startsWith("E")) return 401; // دسترسی
        if (name.length() < 2) return 409;    // تکراری
return 400;
    }

    private void handleApiResponse(int statusCode) {
        switch (statusCode) {
            case 201:
                showMessage("رستوران با موفقیت ثبت شد!", "green");
                break;
            case 400:
                showMessage("ورودی نامعتبر است. اطلاعات را تصحیح کنید.", "orange");
                break;
            case 401:
                showMessage("شما مجاز به ثبت این رستوران نیستید.", "red");
                break;
            case 403:
                showMessage("دسترسی غیرمجاز.", "#ff4c4c");
                break;
            case 404:
                showMessage("سرویس یافت نشد.", "blue");
                break;
            case 409:
                showMessage("رستورانی با این اطلاعات قبلاً ثبت شده است.", "#ffa500");
                break;
            case 415:
                showMessage("فرمت فایل لوگو پشتیبانی نمی‌شود.", "#d2691e");
                break;
            case 429:
                showMessage("درخواست بیش از حد مجاز. لطفاً صبر کنید.", "purple");
                break;
            case 500:
                showMessage("خطای سرور.", "#888");
                break;
            default:
                showMessage("خطای ناشناخته. لطفاً دوباره تلاش کنید.", "black");
                break;
        }
    }

    private void showMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    private void goToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SellerHome-view.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(root);
    }



    @FXML
    private void goToProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SellerProfile-view.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(root);
    }
}
