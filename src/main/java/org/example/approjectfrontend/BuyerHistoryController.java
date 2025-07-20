package org.example.approjectfrontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class BuyerHistoryController {
    @FXML private Button profileBtn;
    @FXML private Button homeBtn;
    @FXML
    private Button historyBtn;

    @FXML
    public void initialize() {
        profileBtn.setOnAction(e -> goToProfile());
        homeBtn.setOnAction(e -> goToHome());
        historyBtn.setDisable(true);
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
            // فرض: دستیابی به stage از طریق یک کامپوننت صفحه فعلی
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace(); // برای رفع خطاها
        }
    }
}
