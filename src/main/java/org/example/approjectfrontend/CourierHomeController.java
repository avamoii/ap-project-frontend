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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.OrderDTO;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CourierHomeController implements Initializable {
    @FXML private Button homeBtn;
    @FXML private Button profileBtn;
    @FXML private Button historyBtn; // دکمه جدید
    @FXML private ListView<OrderDTO> deliveriesListView;

    private final ObservableList<OrderDTO> availableDeliveries = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        homeBtn.setDisable(true);
        profileBtn.setOnAction(e -> goToProfile());
        historyBtn.setOnAction(e -> goToHistory()); // رویداد کلیک برای دکمه تاریخچه

        deliveriesListView.setItems(availableDeliveries);

        deliveriesListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(OrderDTO order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) {
                    setGraphic(null);
                } else {
                    VBox infoBox = new VBox(
                            new Label("سفارش #" + order.getId()),
                            new Label("آدرس: " + order.getDeliveryAddress()),
                            new Label("مبلغ کل: " + order.getPayPrice() + " تومان")
                    );
                    Button acceptBtn = new Button("قبول کردن سفارش");
                    acceptBtn.setOnAction(e -> acceptOrder(order, acceptBtn));

                    HBox btnBox = new HBox(10, acceptBtn);
                    VBox box = new VBox(10, infoBox, btnBox);
                    box.setStyle("-fx-padding: 10; -fx-background-radius: 8; -fx-background-color: #f2fff0; -fx-border-color: #d4edd9; -fx-border-radius: 8;");
                    setGraphic(box);
                }
            }
        });

        loadAvailableDeliveries();
    }

    private void loadAvailableDeliveries() {
        availableDeliveries.clear();
        deliveriesListView.setPlaceholder(new ProgressIndicator());

        new Thread(() -> {
            ApiResponse response = ApiService.getAvailableDeliveries();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    List<OrderDTO> orders = new Gson().fromJson(response.getBody(), new TypeToken<List<OrderDTO>>(){}.getType());
                    if (orders == null || orders.isEmpty()) {
                        deliveriesListView.setPlaceholder(new Label("در حال حاضر سفارشی برای ارسال موجود نیست."));
                    } else {
                        availableDeliveries.setAll(orders);
                    }
                } else {
                    deliveriesListView.setPlaceholder(new Label("خطا در دریافت سفارشات: " + response.getBody()));
                }
            });
        }).start();
    }

    private void acceptOrder(OrderDTO order, Button button) {
        button.setDisable(true);
        button.setText("در حال پردازش...");

        new Thread(() -> {
            ApiResponse response = ApiService.updateDeliveryStatus(order.getId(), "ACCEPTED");
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    showAlert(Alert.AlertType.INFORMATION, "موفقیت", "سفارش #" + order.getId() + " با موفقیت به شما اختصاص یافت.");
                    loadAvailableDeliveries();
                } else {
                    showAlert(Alert.AlertType.ERROR, "خطا", "خطا در قبول سفارش: " + response.getBody());
                    button.setDisable(false);
                    button.setText("قبول کردن سفارش");
                }
            });
        }).start();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void goToHome() {
        loadAvailableDeliveries();
    }

    private void goToProfile() {
        navigateToPage("courierProfile-view.fxml");
    }

    private void goToHistory() {
        navigateToPage("CourierHistory-view.fxml");
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
}
