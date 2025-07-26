package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.OrderDTO;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CourierHistoryController implements Initializable {
    @FXML private Button homeBtn;
    @FXML private Button historyBtn;
    @FXML private Button profileBtn;
    @FXML private ListView<OrderDTO> historyListView;

    private final ObservableList<OrderDTO> historyList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        historyBtn.setDisable(true); // غیرفعال کردن دکمه صفحه فعلی
        homeBtn.setOnAction(e -> navigateToPage("CourierHome-view.fxml"));
        profileBtn.setOnAction(e -> navigateToPage("courierProfile-view.fxml"));

        historyListView.setItems(historyList);
        historyListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(OrderDTO order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) {
                    setGraphic(null);
                } else {
                    VBox box = new VBox(5);
                    box.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");
                    Label orderIdLabel = new Label("سفارش #" + order.getId());
                    Label statusLabel = new Label("وضعیت: " + getStatusInPersian(order.getStatus()));
                    Label addressLabel = new Label("آدرس: " + order.getDeliveryAddress());
                    box.getChildren().addAll(orderIdLabel, statusLabel, addressLabel);

                    // --- تغییر اصلی: افزودن دکمه برای سفارشات در حال ارسال ---
                    if ("ON_THE_WAY".equalsIgnoreCase(order.getStatus())) {
                        Button deliveredButton = new Button("تحویل داده شد");
                        deliveredButton.setOnAction(e -> markAsDelivered(order, deliveredButton));
                        box.getChildren().add(deliveredButton);
                    }

                    setGraphic(box);
                }
            }
        });

        loadHistory();
    }

    private void loadHistory() {
        historyListView.setPlaceholder(new ProgressIndicator());
        new Thread(() -> {
            ApiResponse response = ApiService.getDeliveryHistory();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    List<OrderDTO> orders = new Gson().fromJson(response.getBody(), new TypeToken<List<OrderDTO>>() {}.getType());
                    if (orders == null || orders.isEmpty()) {
                        historyListView.setPlaceholder(new Label("تاریخچه ارسالی برای شما یافت نشد."));
                    } else {
                        historyList.setAll(orders);
                    }
                } else {
                    historyListView.setPlaceholder(new Label("خطا در دریافت تاریخچه: " + response.getBody()));
                }
            });
        }).start();
    }

    /**
     * وضعیت یک سفارش را به "تحویل شده" تغییر می‌دهد.
     * @param order سفارشی که باید به‌روز شود.
     * @param button دکمه‌ای که کلیک شده است.
     */
    private void markAsDelivered(OrderDTO order, Button button) {
        button.setDisable(true);
        button.setText("در حال ثبت...");

        new Thread(() -> {
            // طبق مستندات API، برای تحویل دادن سفارش، وضعیت "delivered" ارسال می‌شود.
            ApiResponse response = ApiService.updateDeliveryStatus(order.getId(), "DELIVERED");
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    showAlert(Alert.AlertType.INFORMATION, "موفقیت", "وضعیت سفارش #" + order.getId() + " با موفقیت ثبت شد.");
                    // لیست را مجدداً بارگذاری می‌کنیم تا وضعیت جدید نمایش داده شود.
                    loadHistory();
                } else {
                    showAlert(Alert.AlertType.ERROR, "خطا", "خطا در ثبت وضعیت: " + response.getBody());
                    button.setDisable(false);
                    button.setText("تحویل داده شد");
                }
            });
        }).start();
    }

    private String getStatusInPersian(String status) {
        if (status == null) return "نامشخص";
        return switch (status.toUpperCase()) {
            case "ON_THE_WAY" -> "در مسیر";
            case "COMPLETED" -> "تکمیل شده";
            default -> status;
        };
    }

    private void navigateToPage(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
