package org.example.approjectfrontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
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
    private ListView<Order> ordersListView;

    @FXML
    public void initialize() {
        homeBtn.setOnAction(e -> goToHome());
        historyBtn.setOnAction(e -> handleHistoryClick());
        profileBtn.setOnAction(e -> goToProfile());
        ordersListView.setItems(OrderRepository.ORDERS);

        ordersListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) {
                    setGraphic(null);
                } else {
                    VBox box = new VBox(
                            new Label("رستوران: " + order.getRestaurantName()),
                            new Label("آدرس: " + order.getAddress()),
                            new Label("سفارش: " + getOrderSummary(order)), // <-- خلاصه سفارش
                            new Label("مبلغ کل: " + order.getTotalPrice() + " تومان"),
                            new Label("تعداد آیتم: " + order.getItems().stream().mapToInt(RestaurantMenuItem::getOrderCount).sum()),
                            // === نمای وضعیت سفارش به فارسی ===
                            new Label("وضعیت: " + getStatusFa(order.getStatus()))
                    );
                    box.setStyle("-fx-padding: 10; -fx-background-color: #fcfcff; -fx-border-radius: 8; -fx-spacing: 7;");
                    setGraphic(box);
                }
            }
        });
    }

    /**
     * متد خلاصه سازی سفارش به شکل "دو سوپ و سه جوجه"
     */
    private String getOrderSummary(Order order) {
        List<RestaurantMenuItem> items = order.getItems();
        List<String> parts = new ArrayList<>();
        for (RestaurantMenuItem item : items) {
            int n = item.getOrderCount();
            if (n > 0) {
                String persianNum = persianNumber(n);
                parts.add(persianNum + " " + item.getName());
            }
        }
        if (parts.isEmpty())
            return "ندارد";
        return String.join(" و ", parts);
    }

    /**
     * تبدیل عدد به فارسی ـ تا عدد ۱۰
     */
    private String persianNumber(int number) {
        return switch (number) {
            case 1 -> "یک";
            case 2 -> "دو";
            case 3 -> "سه";
            case 4 -> "چهار";
            case 5 -> "پنج";
            case 6 -> "شش";
            case 7 -> "هفت";
            case 8 -> "هشت";
            case 9 -> "نه";
            case 10 -> "ده";
            default -> String.valueOf(number);
        };
    }
    private String getStatusFa(String status) {
        return switch (status) {
            case "NEW" -> "در انتظار تایید رستوران";
            case "ACCEPTED" -> "در حال آماده‌سازی";
            case "REJECTED" -> "رد شده";
            default -> "نامشخص";
        };
    }
    private void goToProfile() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BuyerProfile-view.fxml"));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
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
