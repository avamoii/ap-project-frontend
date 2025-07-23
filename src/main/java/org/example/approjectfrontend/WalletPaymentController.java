package org.example.approjectfrontend;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.approjectfrontend.api.UserDTO;
import org.example.approjectfrontend.util.SessionManager;

public class WalletPaymentController {
    @FXML
    private Label walletBalanceLabel, statusLabel;

    private long walletBalance = 40000L; // مقدار تستی و ثابت

    private long amount; // مبلغ سفارش

    public void setAmount(long amount) {
        this.amount = amount;
    }

    @FXML
    public void initialize() {
        walletBalanceLabel.setText("موجودی کیف پول: " + walletBalance + " تومان ");
    }

    @FXML
    private void handleConfirmPayment() {
        if (walletBalance < amount) {
            statusLabel.setText("موجودی شما برای این پرداخت کافی نیست.");
        } else {
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("پرداخت موفقیت‌آمیز بود!");
            // اینجا اگر خواستی walletBalance رو کم کن و UI رو آپدیت کن
        }
    }
}
