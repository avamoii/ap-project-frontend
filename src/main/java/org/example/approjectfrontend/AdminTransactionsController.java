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
import org.example.approjectfrontend.api.TransactionDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminTransactionsController {
    @FXML
    private TableView<TransactionDTO> transactionsTable;
    @FXML
    private TableColumn<TransactionDTO, Number> colSerial;
    @FXML
    private TableColumn<TransactionDTO, Long> colId;
    @FXML
    private TableColumn<TransactionDTO, Long> colOrderId;
    @FXML
    private TableColumn<TransactionDTO, Integer> colAmount;
    @FXML
    private TableColumn<TransactionDTO, String> colStatus; // This column will show transaction TYPE
    @FXML
    private TableColumn<TransactionDTO, String> colDate;

    // **نکته**: برای نمایش نام کاربر، باید یک ستون جدید به فایل FXML اضافه کنید.
    // مثلاً: <TableColumn fx:id="colUser" text="کاربر"/>

    private final ObservableList<TransactionDTO> transactionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        transactionsTable.setItems(transactionList);
        loadTransactions();
    }

    private void setupTableColumns() {
        colSerial.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(transactionsTable.getItems().indexOf(col.getValue()) + 1));
        colId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        colOrderId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getOrderId()));
        colAmount.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getAmount()));
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(translateType(cell.getValue().getType())));
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(formatDate(cell.getValue().getCreatedAt())));

        colAmount.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%,d تومان", Math.abs(item)));
                    if (item < 0) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle("-fx-text-fill: green;");
                    }
                }
            }
        });
    }

    private void loadTransactions() {
        transactionsTable.setPlaceholder(new ProgressIndicator());
        new Thread(() -> {
            ApiResponse response = ApiService.getAdminTransactions();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    List<TransactionDTO> transactions = new Gson().fromJson(response.getBody(), new TypeToken<List<TransactionDTO>>() {}.getType());
                    transactionList.setAll(transactions);
                } else {
                    transactionsTable.setPlaceholder(new Label("خطا در دریافت لیست تراکنش‌ها: " + response.getBody()));
                }
            });
        }).start();
    }

    private String translateType(String type) {
        if (type == null) return "نامشخص";
        return switch (type.toUpperCase()) {
            case "DEPOSIT" -> "واریز به کیف پول";
            case "WITHDRAWAL" -> "برداشت";
            case "PAYMENT" -> "پرداخت سفارش";
            default -> type;
        };
    }

    private String formatDate(String dateTimeStr) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_DATE_TIME);
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
        } catch (Exception e) {
            return dateTimeStr;
        }
    }
}