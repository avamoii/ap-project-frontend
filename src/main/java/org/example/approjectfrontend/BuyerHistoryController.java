package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder; // <-- وارد کردن کلاس گمشده
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea; // <-- وارد کردن کلاس گمشده
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

public class BuyerHistoryController implements Initializable {
    @FXML
    private Button profileBtn;
    @FXML
    private Button homeBtn;
    @FXML
    private Button historyBtn;
    @FXML
    private VBox ordersVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        profileBtn.setOnAction(e -> goToProfile());
        homeBtn.setOnAction(e -> goToHome());
        historyBtn.setDisable(true);
        loadOrderHistory();
    }

    private void loadOrderHistory() {
        ordersVBox.getChildren().clear();
        ordersVBox.setAlignment(Pos.CENTER);

        new Thread(() -> {
            ApiResponse response = ApiService.getOrderHistory();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    ordersVBox.setAlignment(Pos.TOP_LEFT);
                    Gson gson = new Gson();
                    List<OrderDTO> orders = gson.fromJson(response.getBody(), new TypeToken<List<OrderDTO>>() {}.getType());
                    if (orders.isEmpty()) {
                        ordersVBox.getChildren().add(new Label("شما هنوز هیچ سفارشی ثبت نکرده‌اید."));
                    } else {
                        for (OrderDTO order : orders) {
                            HBox card = buildOrderCard(order);
                            ordersVBox.getChildren().add(card);
                        }
                    }
                } else {
                    ordersVBox.getChildren().add(new Label("خطا در دریافت تاریخچه سفارشات."));
                }
            });
        }).start();
    }

    private HBox buildOrderCard(OrderDTO order) {
        HBox box = new HBox(10);
        box.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8;");
        box.setAlignment(Pos.CENTER_LEFT);

        Label orderInfo = new Label(String.format("سفارش #%d - وضعیت: %s - مبلغ: %d تومان",
                order.getId(), order.getStatus(), order.getPayPrice()));
        orderInfo.setStyle("-fx-font-size: 14px;");

        box.getChildren().add(orderInfo);
        box.setOnMouseClicked(e -> showOrderDetails(order.getId()));

        return box;
    }

    private void showOrderDetails(long orderId) {
        new Thread(() -> {
            ApiResponse response = ApiService.getOrderDetails(orderId);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("جزئیات سفارش #" + orderId);
                    alert.setHeaderText("اطلاعات کامل سفارش شما:");

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Object jsonObject = gson.fromJson(response.getBody(), Object.class);
                    String prettyJson = gson.toJson(jsonObject);

                    TextArea textArea = new TextArea(prettyJson);
                    textArea.setEditable(false);
                    textArea.setWrapText(true);

                    alert.getDialogPane().setContent(textArea);
                    alert.setResizable(true);
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("خطا");
                    alert.setContentText("خطا در دریافت جزئیات سفارش: " + response.getBody());
                    alert.showAndWait();
                }
            });
        }).start();
    }

    private void goToProfile() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BuyerProfile-view.fxml"));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BuyerHome-view.fxml"));
            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}