package org.example.approjectfrontend;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CourierHomeController implements Initializable {
    @FXML
    private Button homeBtn;
    @FXML private Button profileBtn;
    @FXML
    private ListView<Order> deliveriesListView;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        homeBtn.setOnAction(e -> goToHome());
        profileBtn.setOnAction(e -> goToProfile());
        // فقط سفارش‌هایی که در انتظار پیک هستند
        ObservableList<Order> waitingOrders = OrderRepository.ORDERS.filtered(
                order -> "WAITING_FOR_DELIVERY".equals(order.getStatus())
        );
        deliveriesListView.setItems(waitingOrders);

        deliveriesListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) {
                    setGraphic(null);
                } else {
                    VBox infoBox = new VBox(
                            new Label("رستوران: " + order.getRestaurantName()),
                            new Label("آدرس: " + order.getAddress()),
                            new Label("مبلغ: " + order.getTotalPrice() + " تومان")
                    );
                    Button acceptBtn = new Button("تایید دریافت");
                    Button rejectBtn = new Button("رد سفارش");

                    acceptBtn.setOnAction(e -> acceptOrder(order));
                    rejectBtn.setOnAction(e -> rejectOrder(order));

                    HBox btnBox = new HBox(10, acceptBtn, rejectBtn);
                    VBox box = new VBox(10, infoBox, btnBox);
                    box.setStyle("-fx-padding: 10; -fx-background-radius: 8; -fx-background-color: #f2fff0;");
                    setGraphic(box);
                }
            }
        });
    }
    private void acceptOrder(Order order) {
        order.setStatus("IN_DELIVERY"); // یا هر وضعیت مورد نظر پروژه شما
        deliveriesListView.refresh();
        // اگر نیاز به آپدیت سرور هست همین‌جا call کن
    }
    private void rejectOrder(Order order) {
        order.setStatus("CANCELLED");
        deliveriesListView.refresh();
        // اگر نیاز به آپدیت سرور هست همین‌جا call کن
    }
    private void goToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourierHome-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void goToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CourierProfile-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
