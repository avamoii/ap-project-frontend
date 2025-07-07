package org.example.approjectfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ProfileController {

    @FXML
    private ImageView profileImageView;
    @FXML
    private Button uploadButton;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField bankNameField;
    @FXML
    private TextField bankAccountField;
    @FXML
    private Button saveButton;
    @FXML
    private Label messageLabel;

    private File selectedImageFile = null;

    @FXML
    public void initialize() {
        // دکمه آپلود عکس
        uploadButton.setOnAction(this::handleUploadButton);

        // دکمه ذخیره پروفایل
        saveButton.setOnAction(this::handleSaveButton);
    }

    /** آپلود عکس پروفایل */
    private void handleUploadButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب عکس پروفایل");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("تصاویر JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("تصاویر PNG", "*.png"),
                new FileChooser.ExtensionFilter("تمام تصاویر", "*.jpg", "*.jpeg", "*.png")
        );
        Window window = uploadButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            try {
                Image image = new Image(new FileInputStream(file));
                profileImageView.setImage(image);
                selectedImageFile = file;
            } catch (FileNotFoundException e) {
                messageLabel.setText("خطا در بارگذاری تصویر");
            }
        }
    }

    /** ذخیره پروفایل */
    private void handleSaveButton(ActionEvent event) {
        messageLabel.setText(""); // پاک کردن پیام قبلی

        if (addressField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty() ||
                bankNameField.getText().trim().isEmpty() ||
                bankAccountField.getText().trim().isEmpty()) {

            messageLabel.setText("لطفاً تمام فیلد‌ها را کامل پر کنید."); // پیام خطا به فارسی
            return;
        }

        // اگر لازم است اینجا ذخیره اطلاعات را انجام بده
        // مثلاً فراخوانی به backend یا ذخیره لوکال و...

        messageLabel.setStyle("-fx-text-fill: #27ae60;"); // سبز
        messageLabel.setText("پروفایل با موفقیت ذخیره شد!");

        // اگر نیاز داری عکس هم ذخیره شود می‌تونی selectedImageFile را به backend ارسال کنی
        // اگر کاربر عکسی انتخاب نکرده بود، selectedImageFile هنوز null هست (موردی ندارد)
    }
}
