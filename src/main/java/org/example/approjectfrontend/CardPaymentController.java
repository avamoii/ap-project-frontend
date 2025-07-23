package org.example.approjectfrontend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CardPaymentController {
    @FXML
    private TextField cardNumberField;
    @FXML
    private Label errorLabel;

    @FXML
    private void handleCardPayment() {
        String cardNo = cardNumberField.getText().trim();
        if (cardNo.isEmpty() || cardNo.length() != 16) {
            errorLabel.setText("شماره کارت معتبر وارد کنید (۱۶ رقم)");
            return;
        }
        // فراخوانی متد پرداخت یا شبیه‌سازی پرداخت
        errorLabel.setStyle("-fx-text-fill: green");
        errorLabel.setText("پرداخت با موفقیت انجام شد!");
        // اینجا می‌تونی پنجره رو ببندی یا به صفحه اصلی برگردی
    }
}
