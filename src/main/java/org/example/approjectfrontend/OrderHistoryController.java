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
import org.example.approjectfrontend.api.FoodItemDTO;
import org.example.approjectfrontend.api.OrderDTO;

import java.io.IOException;
import java.util.List;

public class OrderHistoryController {
    @FXML private Button profileBtn;
    @FXML private Button homeBtn;
    @FXML private Button historyBtn;
    @FXML private ListView<OrderDTO> ordersListView;
    @FXML private SplitPane mainSplitPane;
    @FXML private VBox detailsPane;
    @FXML private Label orderIdLabel;
    @FXML private Label statusLabel;
    @FXML private Label priceLabel;
    @FXML private Label addressLabel;
    @FXML private ListView<String> itemsListView;

    private final ObservableList<OrderDTO> orderList = FXCollections.observableArrayList();
    private final ObservableList<String> itemNames = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        historyBtn.setStyle("-fx-background-color: #1e7e44;");
        ordersListView.setItems(orderList);
        itemsListView.setItems(itemNames);

        // در ابتدا بخش جزئیات را مخفی می‌کنیم
        detailsPane.setVisible(false);
        detailsPane.setManaged(false);

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
                    Label dateLabel = new Label("تاریخ ثبت: " + order.getCreatedAt().substring(0, 10));
                    box.getChildren().addAll(priceLabel, dateLabel);
                    setGraphic(box);
                }
            }
        });

        // با یک بار کلیک، جزئیات نمایش داده می‌شود
        ordersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayOrderDetails(newSelection);
            }
        });

        loadOrderHistory();
    }

    private void displayOrderDetails(OrderDTO order) {
        // بخش جزئیات را نمایان می‌کنیم
        detailsPane.setVisible(true);
        detailsPane.setManaged(true);
        mainSplitPane.setDividerPositions(0.5); // تقسیم صفحه به دو نیم

        // اطلاعات اصلی را فوراً نمایش می‌دهیم
        orderIdLabel.setText(String.valueOf(order.getId()));
        statusLabel.setText(getStatusInPersian(order.getStatus()));
        priceLabel.setText(order.getPayPrice() + " تومان");
        addressLabel.setText(order.getDeliveryAddress());

        // دریافت نام آیتم‌ها
        itemNames.clear();
        itemsListView.setPlaceholder(new Label("در حال بارگذاری آیتم‌ها..."));
        for (Long itemId : order.getItemIds()) {
            new Thread(() -> {
                ApiResponse itemResponse = ApiService.getFoodItemDetails(itemId);
                Platform.runLater(() -> {
                    if (itemResponse.getStatusCode() == 200) {
                        FoodItemDTO foodItem = new Gson().fromJson(itemResponse.getBody(), FoodItemDTO.class);
                        itemNames.add(foodItem.getName());
                    } else {
                        itemNames.add("آیتم با شناسه " + itemId + " (یافت نشد)");
                    }
                });
            }).start();
        }
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
