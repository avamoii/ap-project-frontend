package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.UserDTO;

import java.util.List;

public class AdminUsersController {

    @FXML
    private TableView<UserDTO> usersTable;
    @FXML
    private TableColumn<UserDTO, Number> colSerial;
    @FXML
    private TableColumn<UserDTO, Long> colId;
    @FXML
    private TableColumn<UserDTO, String> colName;
    @FXML
    private TableColumn<UserDTO, String> colEmail;
    @FXML
    private TableColumn<UserDTO, String> colStatus;
    @FXML
    private TableColumn<UserDTO, Void> colActions;

    private final ObservableList<UserDTO> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        usersTable.setItems(userList);
        loadUsers();
    }

    private void setupTableColumns() {
        colSerial.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(usersTable.getItems().indexOf(col.getValue()) + 1));
        colId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFullName()));
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(translateStatus(cell.getValue().getStatus())));

        // --- **تغییر اصلی اینجاست** ---
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button approveButton = new Button("تایید");
            private final Button rejectButton = new Button("رد");
            private final HBox pane = new HBox(8, approveButton, rejectButton);

            {
                approveButton.setStyle("-fx-background-color: #44bfa3; -fx-text-fill: white;");
                rejectButton.setStyle("-fx-background-color: #f36b7f; -fx-text-fill: white;");

                // افزودن رویداد کلیک برای دکمه تایید
                approveButton.setOnAction(event -> {
                    UserDTO user = getTableView().getItems().get(getIndex());
                    updateUserStatus(user, "approved");
                });

                // افزودن رویداد کلیک برای دکمه رد
                rejectButton.setOnAction(event -> {
                    UserDTO user = getTableView().getItems().get(getIndex());
                    updateUserStatus(user, "rejected");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    UserDTO user = getTableView().getItems().get(getIndex());
                    // فقط برای کاربران در انتظار، دکمه‌ها فعال باشند
                    if ("PENDING".equalsIgnoreCase(user.getStatus())) {
                        approveButton.setDisable(false);
                        rejectButton.setDisable(false);
                    } else {
                        approveButton.setDisable(true);
                        rejectButton.setDisable(true);
                    }
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadUsers() {
        usersTable.setPlaceholder(new ProgressIndicator());
        new Thread(() -> {
            ApiResponse response = ApiService.getAdminUsers();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    List<UserDTO> users = new Gson().fromJson(response.getBody(), new TypeToken<List<UserDTO>>() {}.getType());
                    userList.setAll(users);
                } else {
                    usersTable.setPlaceholder(new Label("خطا در دریافت لیست کاربران: " + response.getBody()));
                }
            });
        }).start();
    }

    // --- **متد جدید برای ارسال درخواست آپدیت** ---
    private void updateUserStatus(UserDTO user, String newStatus) {
        new Thread(() -> {
            ApiResponse response = ApiService.updateUserStatus(user.getId(), newStatus);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    // پس از موفقیت، لیست کاربران را رفرش می‌کنیم تا وضعیت جدید نمایش داده شود
                    loadUsers();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("خطا");
                    alert.setContentText("خطا در تغییر وضعیت کاربر: " + response.getBody());
                    alert.showAndWait();
                }
            });
        }).start();
    }

    private String translateStatus(String status) {
        if (status == null) return "نامشخص";
        return switch (status.toUpperCase()) {
            case "PENDING" -> "منتظر تایید";
            case "APPROVED" -> "تایید شده";
            case "REJECTED" -> "رد شده";
            default -> status;
        };
    }
}