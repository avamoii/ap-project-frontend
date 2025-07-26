package org.example.approjectfrontend;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class AdminUsersController {

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Number> colSerial;
    @FXML
    private TableColumn<User, Integer> colId;
    @FXML
    private TableColumn<User, String> colName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, String> colStatus;
    @FXML
    private TableColumn<User, Void> colActions;

    // داده‌های نمونه برای نمایش
    private final ObservableList<User> userList = FXCollections.observableArrayList(
            new User(1, "علی", "ali@email.com", "فعال"),
            new User(2, "زهرا", "zahra@email.com", "غیرفعال"),
            new User(3, "سینا", "sina@email.com", "منتظر تایید")
    );

    @FXML
    public void initialize() {
        // ست کردن شماره ردیف
        colSerial.setCellValueFactory(col ->
                new ReadOnlyObjectWrapper<>(usersTable.getItems().indexOf(col.getValue()) + 1)
        );
        colSerial.setSortable(false);

        colId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        colName.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getName()));
        colEmail.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getEmail()));
        colStatus.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getStatus()));

        // ستون عملیات با دو دکمه (مثال: تایید و حذف)
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button approveButton = new Button("تایید");
            private final Button deleteButton = new Button("حذف");
            private final HBox pane = new HBox(8, approveButton, deleteButton);

            {
                approveButton.setStyle("-fx-background-color: #44bfa3; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 8;");
                deleteButton.setStyle("-fx-background-color: #f36b7f; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 8;");
                approveButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    // عملیات تایید کاربر
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "کاربر \"" + user.getName() + "\" تایید شد.");
                    alert.showAndWait();
                });
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    usersTable.getItems().remove(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        usersTable.setItems(userList);
    }

    // کلاس کاربر ساده برای تست (این رو یا با مدل واقعی خودت جایگزین کن یا گسترش بده)
    public static class User {
        private final int id;
        private final String name;
        private final String email;
        private final String status;

        public User(int id, String name, String email, String status) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.status = status;
        }
        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getStatus() { return status; }
    }
}
