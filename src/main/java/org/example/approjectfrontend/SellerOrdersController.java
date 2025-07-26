package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.OrderDTO;
import org.example.approjectfrontend.api.RestaurantDTO;

import java.io.IOException;
import java.util.List;

public class SellerOrdersController {
    @FXML private Label restaurantNameLabel;
    @FXML private TableView<OrderDTO> ordersTable;
    @FXML private TableColumn<OrderDTO, String> buyerColumn;
    @FXML private TableColumn<OrderDTO, String> itemsColumn;
    @FXML private TableColumn<OrderDTO, String> addressColumn;
    @FXML private TableColumn<OrderDTO, String> mobileColumn;
    @FXML private TableColumn<OrderDTO, String> statusColumn;
    @FXML private TableColumn<OrderDTO, Integer> totalPriceColumn;
    @FXML private TableColumn<OrderDTO, Void> actionsColumn;

    private RestaurantDTO currentRestaurant;
    private final ObservableList<OrderDTO> ordersData = FXCollections.observableArrayList();

    public void setRestaurant(RestaurantDTO restaurant) {
        this.currentRestaurant = restaurant;
        restaurantNameLabel.setText("سفارشات رستوران: " + restaurant.getName());
        loadOrdersForRestaurant();
    }

    private void loadOrdersForRestaurant() {
        if (currentRestaurant == null) return;

        new Thread(() -> {
            ApiResponse response = ApiService.getRestaurantOrders(currentRestaurant.getId());
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    List<OrderDTO> orders = new Gson().fromJson(response.getBody(), new TypeToken<List<OrderDTO>>(){}.getType());
                    ordersData.setAll(orders);
                } else {
                    ordersTable.setPlaceholder(new Label("خطا در دریافت سفارشات."));
                    System.err.println("Error fetching restaurant orders: " + response.getBody());
                }
            });
        }).start();
    }

    @FXML
    private void initialize() {
        ordersTable.setItems(ordersData);

        // تعریف ستون‌ها
        buyerColumn.setCellValueFactory(data -> new SimpleStringProperty("کاربر " + data.getValue().getCustomerId()));
        itemsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getItemIds().size() + " آیتم"));
        totalPriceColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPayPrice()).asObject());
        addressColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDeliveryAddress()));
        mobileColumn.setCellValueFactory(data -> new SimpleStringProperty("N/A")); // اطلاعات تلفن در OrderDTO نیست
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(getStatusInPersian(data.getValue().getStatus())));

        setActionsColumn();
    }

    private void setActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button acceptBtn = new Button("تایید");
            private final Button rejectBtn = new Button("رد");
            private final HBox pane = new HBox(5, acceptBtn, rejectBtn);

            {
                acceptBtn.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
                rejectBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");

                acceptBtn.setOnAction(e -> {
                    OrderDTO order = getTableView().getItems().get(getIndex());
                    updateStatus(order, "ACCEPTED");
                });
                rejectBtn.setOnAction(e -> {
                    OrderDTO order = getTableView().getItems().get(getIndex());
                    updateStatus(order, "REJECTED");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    OrderDTO order = getTableView().getItems().get(getIndex());
                    // دکمه‌ها فقط برای سفارشاتی که منتظر تایید هستند فعال باشند
                    boolean isActionable = "WAITING_VENDOR".equalsIgnoreCase(order.getStatus());
                    acceptBtn.setDisable(!isActionable);
                    rejectBtn.setDisable(!isActionable);
                    setGraphic(pane);
                }
            }
        });
    }

    private void updateStatus(OrderDTO order, String newStatus) {
        new Thread(() -> {
            ApiResponse response = ApiService.updateOrderStatus(order.getId(), newStatus);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    // برای نمایش تغییر، لیست سفارشات را مجدداً بارگذاری می‌کنیم
                    loadOrdersForRestaurant();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("خطا");
                    alert.setHeaderText("خطا در تغییر وضعیت سفارش");
                    alert.setContentText(response.getBody());
                    alert.showAndWait();
                }
            });
        }).start();
    }

    private String getStatusInPersian(String status) {
        if (status == null) return "نامشخص";
        return switch (status.toUpperCase()) {
            case "SUBMITTED" -> "ثبت شده (پرداخت نشده)";
            case "UNPAID_AND_CANCELLED" -> "پرداخت نشده و لغو شده";
            case "WAITING_VENDOR" -> "در انتظار تایید";
            case "CANCELLED" -> "لغو شده";
            case "FINDING_COURIER" -> "آماده ارسال";
            case "ON_THE_WAY" -> "در مسیر";
            case "COMPLETED" -> "تکمیل شده";
            default -> status;
        };
    }

    @FXML
    private void backToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/approjectfrontend/SellerHome-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
