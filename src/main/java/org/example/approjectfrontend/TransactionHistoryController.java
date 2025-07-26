package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.TransactionDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TransactionHistoryController {
    @FXML private Button profileBtn;
    @FXML private Button homeBtn;
    @FXML private Button historyBtn;
    @FXML private ListView<TransactionDTO> transactionsListView;

    private final ObservableList<TransactionDTO> transactionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        historyBtn.setStyle("-fx-background-color: #1e7e44;");
        transactionsListView.setItems(transactionList);

        transactionsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(TransactionDTO transaction, boolean empty) {
                super.updateItem(transaction, empty);
                if (empty || transaction == null) {
                    setGraphic(null);
                } else {
                    VBox box = new VBox(5);
                    box.setStyle("-fx-padding: 10; -fx-background-color: #fcfcff; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
                    Label amountLabel = new Label("مبلغ: " + transaction.getAmount() + " تومان");
                    Label typeLabel = new Label("نوع: " + getTransactionTypeInPersian(transaction.getType()));
                    Label dateLabel = new Label("تاریخ: " + transaction.getCreatedAt().substring(0, 10));

                    if (transaction.getAmount() > 0) {
                        amountLabel.setStyle("-fx-text-fill: green;");
                    } else {
                        amountLabel.setStyle("-fx-text-fill: red;");
                    }

                    box.getChildren().addAll(amountLabel, typeLabel, dateLabel);
                    setGraphic(box);
                }
            }
        });

        loadTransactionHistory();
    }

    private void loadTransactionHistory() {
        new Thread(() -> {
            ApiResponse response = ApiService.getTransactionHistory();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    List<TransactionDTO> transactions = new Gson().fromJson(response.getBody(), new TypeToken<List<TransactionDTO>>() {}.getType());
                    transactionList.setAll(transactions);
                    if (transactions.isEmpty()) {
                        transactionsListView.setPlaceholder(new Label("شما تاکنون تراکنشی نداشته‌اید."));
                    }
                } else {
                    transactionsListView.setPlaceholder(new Label("خطا در دریافت تاریخچه تراکنش‌ها."));
                    System.err.println("Error fetching transaction history: " + response.getBody());
                }
            });
        }).start();
    }

    private String getTransactionTypeInPersian(String type) {
        if (type == null) return "نامشخص";
        return switch (type.toUpperCase()) {
            case "DEPOSIT" -> "واریز";
            case "WITHDRAWAL" -> "برداشت";
            case "PAYMENT" -> "پرداخت سفارش";
            default -> type;
        };
    }

    @FXML
    private void goToProfile() {
        navigateToPage("BuyerProfile-view.fxml");
    }

    @FXML
    private void goToHome() {
        navigateToPage("BuyerHome-view.fxml");
    }

    @FXML
    private void handleHistoryClick() {
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
                navigateToPage("OrderHistory-view.fxml");
            } else if (result.get() == transactionsBtn) {
                // Already here, do nothing
            }
        }
    }

    private void navigateToPage(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}