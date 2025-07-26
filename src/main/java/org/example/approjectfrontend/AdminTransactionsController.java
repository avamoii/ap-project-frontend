package org.example.approjectfrontend;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdminTransactionsController {
    @FXML
    private TableView<TransactionForAdmin> transactionsTable;
    @FXML
    private TableColumn<TransactionForAdmin, Number> colSerial;
    @FXML
    private TableColumn<TransactionForAdmin, Integer> colId;
    @FXML
    private TableColumn<TransactionForAdmin, Integer> colOrderId;
    @FXML
    private TableColumn<TransactionForAdmin, Integer> colAmount;
    @FXML
    private TableColumn<TransactionForAdmin, String> colStatus;
    @FXML
    private TableColumn<TransactionForAdmin, String> colDate;

    // مقادیر نمونه برای تست اولیه جدول
    private final ObservableList<TransactionForAdmin> transactionList = FXCollections.observableArrayList(
            new TransactionForAdmin(5001, 105, 210000, "موفق", "1403/05/02"),
            new TransactionForAdmin(5002, 102, 125000, "ناموفق", "1403/05/01"),
            new TransactionForAdmin(5003, 107, 315000, "در انتظار", "1403/04/31")
    );

    @FXML
    public void initialize() {
        // شماره ردیف (سریال) اتوماتیک
        colSerial.setCellValueFactory(col ->
                new ReadOnlyObjectWrapper<>(transactionsTable.getItems().indexOf(col.getValue()) + 1));
        colSerial.setSortable(false);

        colId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        colOrderId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getOrderId()));
        colAmount.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getAmount()));
        colStatus.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getStatus()));
        colDate.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getDate()));

        // نمایش مبلغ با فرمت مناسب ("تومان" و جداکننده هزارگان)
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

        // ست کردن داده‌ها
        transactionsTable.setItems(transactionList);
    }

    // مدل ساده تراکنش ــ می‌توانی در فایل جداگانه هم تعریفش کنی اگر پروژه بزرگ شد
    public static class TransactionForAdmin {
        private final int id;
        private final int orderId;
        private final int amount;
        private final String status;
        private final String date;

        public TransactionForAdmin(int id, int orderId, int amount, String status, String date) {
            this.id = id;
            this.orderId = orderId;
            this.amount = amount;
            this.status = status;
            this.date = date;
        }

        public int getId() { return id; }
        public int getOrderId() { return orderId; }
        public int getAmount() { return amount; }
        public String getStatus() { return status; }
        public String getDate() { return date; }
    }
}
