package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.FoodItemDTO;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        historyBtn.setStyle("-fx-background-color: #1e7e44;");
        profileBtn.setOnAction(e -> navigateToPage(e, "BuyerProfile-view.fxml"));
        homeBtn.setOnAction(e -> navigateToPage(e, "BuyerHome-view.fxml"));
        historyBtn.setOnAction(e -> showHistoryChoiceDialog());

        ordersListView.setItems(orderList);
        itemsListView.setItems(itemNames);

        if (detailsPane != null) {
            detailsPane.setVisible(false);
            detailsPane.setManaged(false);
        }

        ordersListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(OrderDTO order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) {
                    setGraphic(null);
                } else {
                    setGraphic(buildOrderCard(order));
                }
            }
        });

        ordersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayOrderDetails(newSelection);
            }
        });

        loadOrderHistory();
    }

    private VBox buildOrderCard(OrderDTO order) {
        Label orderInfo = new Label(String.format("سفارش #%d - مبلغ: %,d تومان",
                order.getId(), order.getPayPrice()));
        orderInfo.setStyle("-fx-font-size: 14px;");

        Label statusDisplayLabel = new Label("وضعیت: " + getStatusInPersian(order.getStatus()));
        statusDisplayLabel.setStyle("-fx-text-fill: #333; -fx-font-size: 12px;");

        VBox content = new VBox(5, orderInfo, statusDisplayLabel);
        content.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8;");
        content.setAlignment(Pos.CENTER_LEFT);

        if ("COMPLETED".equalsIgnoreCase(order.getStatus())) {
            if (order.getRatingId() == null) {
                Button rateButton = new Button("ثبت نظر");
                rateButton.setOnAction(e -> openRatingWindow(order, null));
                content.getChildren().add(rateButton);
            } else {
                Button editButton = new Button("ویرایش نظر");
                editButton.setOnAction(e -> openRatingWindow(order, order.getRatingId()));

                Button deleteButton = new Button("حذف نظر");
                deleteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
                deleteButton.setOnAction(e -> deleteRating(order.getRatingId()));

                HBox buttons = new HBox(10, editButton, deleteButton);
                content.getChildren().add(buttons);
            }
        }

        content.setOnMouseClicked(e -> {
            if (!(e.getTarget() instanceof Button)) {
                ordersListView.getSelectionModel().select(order);
            }
        });

        return content;
    }

    private void openRatingWindow(OrderDTO order, Long ratingId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SubmitRating-view.fxml"));
            Parent root = loader.load();

            SubmitRatingController controller = loader.getController();
            // --- اصلاح اصلی اینجاست: فراخوانی متد جدید ---
            controller.setOrderAndRating(order, ratingId);

            Stage stage = new Stage();
            stage.setTitle("ثبت/ویرایش نظر برای سفارش #" + order.getId());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadOrderHistory();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteRating(Long ratingId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "آیا از حذف این نظر مطمئن هستید؟", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                new Thread(() -> {
                    ApiResponse apiResponse = ApiService.deleteRating(ratingId);
                    Platform.runLater(() -> {
                        if (apiResponse.getStatusCode() == 200) {
                            loadOrderHistory(); // لیست را رفرش کن
                        } else {
                            new Alert(Alert.AlertType.ERROR, "خطا در حذف نظر: " + apiResponse.getBody()).show();
                        }
                    });
                }).start();
            }
        });
    }

    private void displayOrderDetails(OrderDTO order) {
        if (detailsPane != null && mainSplitPane != null) {
            detailsPane.setVisible(true);
            detailsPane.setManaged(true);
            mainSplitPane.setDividerPositions(0.5);
        }

        orderIdLabel.setText(String.valueOf(order.getId()));
        statusLabel.setText(getStatusInPersian(order.getStatus()));
        priceLabel.setText(String.format("%,d تومان", order.getPayPrice()));
        addressLabel.setText(order.getDeliveryAddress());

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
        ordersListView.setPlaceholder(new ProgressIndicator());
        new Thread(() -> {
            ApiResponse response = ApiService.getOrderHistory();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    List<OrderDTO> orders = new Gson().fromJson(response.getBody(), new TypeToken<List<OrderDTO>>() {}.getType());
                    if (orders == null || orders.isEmpty()) {
                        ordersListView.setPlaceholder(new Label("شما تاکنون سفارشی ثبت نکرده‌اید."));
                    } else {
                        orderList.setAll(orders);
                    }
                } else {
                    ordersListView.setPlaceholder(new Label("خطا در دریافت تاریخچه سفارشات."));
                }
            });
        }).start();
    }

    private void showHistoryChoiceDialog() {
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
                loadOrderHistory();
            } else if (result.get() == transactionsBtn) {
                navigateToPage(new ActionEvent(historyBtn, null), "TransactionHistory-view.fxml");
            }
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

    @FXML
    public void goToProfile(ActionEvent event) {
        navigateToPage(event, "BuyerProfile-view.fxml");
    }

    @FXML
    public void goToHome(ActionEvent event) {
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
}