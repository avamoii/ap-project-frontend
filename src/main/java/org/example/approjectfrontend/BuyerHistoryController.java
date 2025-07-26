package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.OrderDTO;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BuyerHistoryController implements Initializable {
    @FXML private Button profileBtn;
    @FXML private Button homeBtn;
    @FXML private Button historyBtn;
    @FXML private ListView<OrderDTO> ordersListView; // --- اصلاح اصلی: استفاده از ListView به جای VBox ---

    private final ObservableList<OrderDTO> orderList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        profileBtn.setOnAction(e -> navigateToPage("BuyerProfile-view.fxml"));
        homeBtn.setOnAction(e -> navigateToPage("BuyerHome-view.fxml"));
        historyBtn.setOnAction(e -> showHistoryChoiceDialog());
        historyBtn.setStyle("-fx-background-color: #1e7e44;");

        // اتصال لیست به ListView
        ordersListView.setItems(orderList);

        // --- اصلاح اصلی: استفاده از CellFactory برای رندر کردن هر آیتم ---
        ordersListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(OrderDTO order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) {
                    setGraphic(null);
                } else {
                    // ساخت کارت گرافیکی برای هر سفارش در اینجا انجام می‌شود
                    setGraphic(buildOrderCard(order));
                }
            }
        });

        loadOrderHistory();
    }

    private void showHistoryChoiceDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("انتخاب نوع تاریخچه");
        alert.setHeaderText("کدام تاریخچه را می‌خواهید مشاهده کنید؟");
        alert.setContentText("لطفا یک گزینه را انتخاب کنید:");

        ButtonType ordersBtn = new ButtonType("تاریخچه سفارشات");
        ButtonType transactionsBtn = new ButtonType("تاریخچه تراکنش‌ها");
        ButtonType cancelBtn = new ButtonType("انصراف", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(ordersBtn, transactionsBtn, cancelBtn);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == ordersBtn) {
                loadOrderHistory();
            } else if (result.get() == transactionsBtn) {
                navigateToPage("TransactionHistory-view.fxml");
            }
        }
    }

    private void loadOrderHistory() {
        ordersListView.setPlaceholder(new ProgressIndicator());

        new Thread(() -> {
            ApiResponse response = ApiService.getOrderHistory();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    List<OrderDTO> orders = gson.fromJson(response.getBody(), new TypeToken<List<OrderDTO>>() {}.getType());
                    if (orders == null || orders.isEmpty()) {
                        ordersListView.setPlaceholder(new Label("شما هنوز هیچ سفارشی ثبت نکرده‌اید."));
                    } else {
                        orderList.setAll(orders);
                    }
                } else {
                    ordersListView.setPlaceholder(new Label("خطا در دریافت تاریخچه سفارشات."));
                }
            });
        }).start();
    }

    private VBox buildOrderCard(OrderDTO order) {
        Label orderInfo = new Label(String.format("سفارش #%d - مبلغ: %,d تومان",
                order.getId(), order.getPayPrice()));
        orderInfo.setStyle("-fx-font-size: 14px;");

        Label debugStatusLabel = new Label("وضعیت خام: " + order.getStatus() + " | وضعیت فارسی: " + getStatusInPersian(order.getStatus()));
        debugStatusLabel.setStyle("-fx-text-fill: grey; -fx-font-size: 10px;");

        VBox content = new VBox(5, orderInfo, debugStatusLabel);
        content.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8;");
        content.setAlignment(Pos.CENTER_LEFT);

        if ("COMPLETED".equalsIgnoreCase(order.getStatus())) {
            Button rateButton = new Button("ثبت نظر");
            rateButton.setOnAction(e -> openRatingWindow(order));
            content.getChildren().add(rateButton);
        }

        content.setOnMouseClicked(e -> {
            if (!(e.getTarget() instanceof Button)) {
                showOrderDetails(order.getId());
            }
        });
        return content;
    }

    private void openRatingWindow(OrderDTO order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SubmitRating-view.fxml"));
            Parent root = loader.load();

            SubmitRatingController controller = loader.getController();
            controller.setOrder(order);

            Stage stage = new Stage();
            stage.setTitle("ثبت نظر برای سفارش #" + order.getId());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadOrderHistory();

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void navigateToPage(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
