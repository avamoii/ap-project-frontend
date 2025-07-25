package org.example.approjectfrontend;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.RestaurantDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SellerOrderController {
    @FXML private Label restaurantNameLabel;
    @FXML private TableView<OrderForSeller> ordersTable;
    @FXML
    private TableColumn<OrderForSeller, String> buyerColumn, itemsColumn, addressColumn, mobileColumn, statusColumn;
    @FXML private TableColumn<OrderForSeller, Integer> totalPriceColumn;
    @FXML private TableColumn<OrderForSeller, Void> actionsColumn;

    private RestaurantDTO currentRestaurant;

    public void setRestaurant(RestaurantDTO restaurant) {
        currentRestaurant = restaurant;
        restaurantNameLabel.setText("سفارشات " + restaurant.getName());
        loadOrdersForRestaurant(restaurant); // اینجا داده‌ها لود می‌شوند
    }

    // متد getMockOrdersForRestaurant به طور کامل حذف شده است.

    private void loadOrdersForRestaurant(RestaurantDTO restaurant) {
        // در این قسمت باید داده‌های واقعی از بک‌اند یا API دریافت شود.
        // فعلاً برای اینکه برنامه اجرا شود و جدول خالی نمایش داده شود، یک لیست خالی ایجاد می‌کنیم.
        List<OrderForSeller> ordersList = new ArrayList<>();

        // بعداً وقتی بک‌اند آماده شد، کد زیر را جایگزین می‌کنید:
        // List<OrderForSeller> ordersList = yourApiService.getOrdersForRestaurant(restaurant.getId());

        ObservableList<OrderForSeller> orders = FXCollections.observableArrayList(ordersList);
        ordersTable.setItems(orders);
    }

    @FXML
    private void initialize() {
        // تعریف ستون‌ها
        buyerColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBuyerUsername()));
        itemsColumn.setCellValueFactory(data -> {
            String items = data.getValue().getItems()
                    .stream()
                    .map(RestaurantMenuItem::getName)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(items);
        });
        totalPriceColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTotalPrice()).asObject());
        addressColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBuyerAddress()));
        mobileColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBuyerPhone()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(getStatusFa(data.getValue().getStatus())));

        // تنظیم ستون اکشن‌ها (دکمه‌های تایید/رد)
        setActionsColumn();

        // توجه: دیگر نیازی به فراخوانی getMockOrdersForRestaurant در اینجا نیست.
        // loadOrdersForRestaurant در setRestaurant صدا زده می‌شود.
    }

    private void setActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button acceptBtn = new Button("تایید");
            private final Button rejectBtn = new Button("رد");

            {
                acceptBtn.setOnAction(e -> {
                    // اطمینان از اینکه ردیف خالی نیست
                    if (getTableView().getItems().get(getIndex()) != null) {
                        updateStatus(getTableView().getItems().get(getIndex()), "ACCEPTED");
                    }
                });
                rejectBtn.setOnAction(e -> {
                    // اطمینان از اینکه ردیف خالی نیست
                    if (getTableView().getItems().get(getIndex()) != null) {
                        updateStatus(getTableView().getItems().get(getIndex()), "REJECTED");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    OrderForSeller order = getTableView().getItems().get(getIndex());
                    boolean isNew = "NEW".equals(order.getStatus());
                    acceptBtn.setDisable(!isNew);
                    rejectBtn.setDisable(!isNew);
                    // استفاده از HBox برای قرار دادن دکمه‌ها کنار هم
                    setGraphic(new HBox(8, acceptBtn, rejectBtn));
                }
            }
        });
    }

    private void updateStatus(OrderForSeller order, String newStatus) {
        // اینجا باید منطق به‌روزرسانی وضعیت در بک‌اند یا دیتابیس پیاده‌سازی شود
        order.setStatus(newStatus); // به‌روزرسانی در UI
        ordersTable.refresh(); // بازخوانی جدول برای نمایش تغییر
    }

    private String getStatusFa(String status) {
        return switch (status) {
            case "NEW" -> "در انتظار تایید رستوران";
            case "ACCEPTED" -> "در حال اماده سازی";
            case "REJECTED" -> "رد شده";
            default -> ""; // برای حالتی که وضعیت ناشناخته باشد
        };
    }

    @FXML
    private void backToHome() throws Exception {
        // این متد برای بازگشت به صفحه اصلی رستوران‌ها است
        Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/org/example/approjectfrontend/SellerHome-view.fxml"));
        Stage stage = (Stage) restaurantNameLabel.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
