package org.example.approjectfrontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class TransactionHistoryController {
    @FXML
    private Button profileBtn;
    @FXML private Button homeBtn;
    @FXML
    private Button historyBtn;
    @FXML
    public void initialize() {
        homeBtn.setOnAction(e -> goToHome());
        historyBtn.setOnAction(e -> handleHistoryClick());
        profileBtn.setOnAction(e -> goToProfile());
    }
    private void goToProfile() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BuyerProfile-view.fxml"));
            // فرض: دستیابی به stage از طریق یک کامپوننت صفحه فعلی
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace(); // برای رفع خطاها
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
    private void handleHistoryClick() {
        // ساخت یک دیالوگ با دو گزینه
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("انتخاب نوع تاریخچه");
        alert.setHeaderText("کدام تاریخچه را می‌خواهید مشاهده کنید؟");

        ButtonType ordersBtn = new ButtonType("تاریخچه سفارشات");
        ButtonType transactionsBtn = new ButtonType("تاریخچه تراکنش‌ها");
        ButtonType cancelBtn = new ButtonType("انصراف", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(ordersBtn, transactionsBtn, cancelBtn);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == ordersBtn) {
                goToOrderHistory();
            } else if (result.get() == transactionsBtn) {
                goToTransactionHistory();
            }
        }
    }
    private void goToOrderHistory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("OrderHistory-view.fxml"));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToTransactionHistory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("TransactionHistory-view.fxml"));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
