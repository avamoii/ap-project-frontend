package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.OrderDTO;

import java.util.List;

public class AdminOrdersController {
    @FXML
    private TableView<OrderDTO> ordersTable;
    @FXML
    private TableColumn<OrderDTO, Number> colSerial;
    @FXML
    private TableColumn<OrderDTO, Long> colId;
    @FXML
    private TableColumn<OrderDTO, String> colUser;
    @FXML
    private TableColumn<OrderDTO, String> colRestaurant;
    @FXML
    private TableColumn<OrderDTO, String> colStatus;
    @FXML
    private TableColumn<OrderDTO, Integer> colAmount;

    private final ObservableList<OrderDTO> orderList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        ordersTable.setItems(orderList);
        loadOrders();
    }

    private void setupTableColumns() {
        colSerial.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(ordersTable.getItems().indexOf(col.getValue()) + 1));
        colId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        colUser.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCustomerName()));
        colRestaurant.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRestaurantName()));
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(translateStatus(cell.getValue().getStatus())));
        colAmount.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getPayPrice()).asObject());

        colAmount.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,d تومان", item));
                }
            }
        });
    }

    private void loadOrders() {
        ordersTable.setPlaceholder(new ProgressIndicator());
        new Thread(() -> {
            ApiResponse response = ApiService.getAdminOrders();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    List<OrderDTO> orders = new Gson().fromJson(response.getBody(), new TypeToken<List<OrderDTO>>() {}.getType());
                    orderList.setAll(orders);
                } else {
                    ordersTable.setPlaceholder(new Label("خطا در دریافت لیست سفارشات: " + response.getBody()));
                }
            });
        }).start();
    }

    private String translateStatus(String status) {
        if (status == null) return "نامشخص";
        return switch (status.toUpperCase()) {
            case "SUBMITTED" -> "ثبت شده";
            case "UNPAID_AND_CANCELLED" -> "لغو شده (پرداخت نشده)";
            case "WAITING_VENDOR" -> "منتظر تایید رستوران";
            case "CANCELLED" -> "لغو شده";
            case "FINDING_COURIER" -> "در جستجوی پیک";
            case "ON_THE_WAY" -> "در مسیر";
            case "COMPLETED" -> "تکمیل شده";
            default -> status;
        };
    }
}