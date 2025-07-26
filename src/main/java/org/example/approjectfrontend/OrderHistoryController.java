package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.OrderDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class OrderHistoryController {
    @FXML
    private Button profileBtn;
    @FXML
    private Button homeBtn;
    @FXML
    private Button historyBtn;
    @FXML
    private ListView<OrderDTO> ordersListView;

    private final ObservableList<OrderDTO> orderList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        historyBtn.setStyle("-fx-background-color: #1e7e44;"); // دکمه فعال تاریخچه
        ordersListView.setItems(orderList);

        // تنظیم نحوه نمایش هر آیتم در لیست
        ordersListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(OrderDTO order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) {
                    setGraphic(null);
                } else {
                    VBox box = new VBox(5);
                    box.setStyle("-fx-padding: 10; -fx-background-color: #fcfcff; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
                    Label priceLabel = new Label("مبلغ کل: " + order.getPayPrice() + " تومان");
                    Label statusLabel = new Label("وضعیت: " + getStatusInPersian(order.getStatus()));
                    Label dateLabel = new Label("تاریخ ثبت: " + order.getCreatedAt().substring(0, 10)); // فقط تاریخ نمایش داده شود
                    box.getChildren().addAll(priceLabel, statusLabel, dateLabel);
                    setGraphic(box);
                }
            }
        });

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        new Thread(() -> {
            ApiResponse response = ApiService.getOrderHistory();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    List<OrderDTO> orders = new Gson().fromJson(response.getBody(), new TypeToken<List<OrderDTO>>() {}.getType());
                    orderList.setAll(orders);
                    if (orders.isEmpty()) {
                        ordersListView.setPlaceholder(new Label("شما تاکنون سفارشی ثبت نکرده‌اید."));
                    }
                } else {
                    ordersListView.setPlaceholder(new Label("خطا در دریافت تاریخچه سفارشات."));
                    System.err.println("Error fetching order history: " + response.getBody());
                }
            });
        }).start();
    }

    private String getStatusInPersian(String status) {
        if (status == null) return "نامشخص";
        return switch (status.toUpperCase()) {
            case "SUBMITTED" -> "ثبت شده";
            case "UNPAID_AND_CANCELLED" -> "پرداخت نشده و لغو شده";
            case "WAITING_VENDOR" -> "در انتظار تایید رستوران";
            case "CANCELLED" -> "لغو شده";
            case "FINDING_COURIER" -> "در جستجوی پیک";
            case "ON_THE_WAY" -> "در مسیر";
            case "COMPLETED" -> "تکمیل شده";
            default -> status;
        };
    }

    @FXML
    private void goToProfile(ActionEvent event) {
        navigateToPage(event, "BuyerProfile-view.fxml");
    }

    @FXML
    private void goToHome(ActionEvent event) {
        navigateToPage(event, "BuyerHome-view.fxml");
    }

    @FXML
    private void handleHistoryClick(ActionEvent event) {
        // چون در همین صفحه هستیم، کاری انجام نمی‌دهیم
    }

    private void navigateToPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
