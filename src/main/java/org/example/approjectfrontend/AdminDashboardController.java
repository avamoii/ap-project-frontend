package org.example.approjectfrontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class AdminDashboardController {

    @FXML
    private StackPane contentPane;

    // نمایش لیست کاربران (ابتدایی‌ترین بخش)
    @FXML
    public void initialize() {
        // بصورت پیش‌فرض مثلا کاربران رو نمایش بده
        showUsers();
    }

    @FXML
    private void showUsers() {
        loadView("AdminUsers-view.fxml");
    }

    @FXML
    private void showOrders() {
        loadView("AdminOrders-view.fxml");
    }

    @FXML
    private void showTransactions() {
        loadView("AdminTransactions-view.fxml");
    }

    // متد کمکی برای بارگذاری FXML داخل contentPane
    private void loadView(String fxmlFile) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
            // به دلخواه پیغام خطا برای یوزر هم می‌تونی اینجا بذاری
        }
    }
}
