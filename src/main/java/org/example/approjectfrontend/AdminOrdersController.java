package org.example.approjectfrontend;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdminOrdersController {
    @FXML
    private TableView<OrderForAdmin> ordersTable;
    @FXML
    private TableColumn<OrderForAdmin, Number> colSerial;
    @FXML
    private TableColumn<OrderForAdmin, Integer> colId;
    @FXML
    private TableColumn<OrderForAdmin, String> colUser;
    @FXML
    private TableColumn<OrderForAdmin, String> colRestaurant;
    @FXML
    private TableColumn<OrderForAdmin, String> colStatus;
    @FXML
    private TableColumn<OrderForAdmin, Integer> colAmount;

    private final ObservableList<OrderForAdmin> orderList = FXCollections.observableArrayList(
            new OrderForAdmin(101, "علی احمدی", "رستوران پارس", "درحال پردازش", 280000),
            new OrderForAdmin(102, "زهرا خطیبی", "خانه‌برگر", "تحویل داده‌شده", 165000),
            new OrderForAdmin(103, "کاوه طاهری", "تاووک", "لغو شده", 195000)
    );

    @FXML
    public void initialize() {
        // شماره ردیف (سریال)
        colSerial.setCellValueFactory(col ->
                new ReadOnlyObjectWrapper<>(ordersTable.getItems().indexOf(col.getValue()) + 1));
        colSerial.setSortable(false);

        colId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        colUser.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getUser()));
        colRestaurant.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getRestaurant()));
        colStatus.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getStatus()));
        colAmount.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getAmount()));

        // نمایش مبلغ با فرمت مناسب
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

        ordersTable.setItems(orderList);
    }

    // مدل سفارش ادمین: OrderForAdmin
    public static class OrderForAdmin {
        private final int id;
        private final String user;
        private final String restaurant;
        private final String status;
        private final int amount;

        public OrderForAdmin(int id, String user, String restaurant, String status, int amount) {
            this.id = id;
            this.user = user;
            this.restaurant = restaurant;
            this.status = status;
            this.amount = amount;
        }

        public int getId() { return id; }
        public String getUser() { return user; }
        public String getRestaurant() { return restaurant; }
        public String getStatus() { return status; }
        public int getAmount() { return amount; }
    }
}
