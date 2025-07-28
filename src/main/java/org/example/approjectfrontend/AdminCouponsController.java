package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.beans.property.ReadOnlyObjectWrapper; // <-- **تغییر اصلی اینجاست**

import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.CreateCouponRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AdminCouponsController {

    @FXML private TableView<Coupon> couponsTable;
    @FXML private TableColumn<Coupon, Void> colSerial;
    @FXML private TableColumn<Coupon, Long> colId;
    @FXML private TableColumn<Coupon, String> colCouponCode;
    @FXML private TableColumn<Coupon, String> colType;
    @FXML private TableColumn<Coupon, BigDecimal> colValue;
    @FXML private TableColumn<Coupon, Integer> colMinPrice;
    @FXML private TableColumn<Coupon, Integer> colUserCount;
    @FXML private TableColumn<Coupon, String> colStartDate;
    @FXML private TableColumn<Coupon, String> colEndDate;
    @FXML private Button btnAddCoupon, btnEditCoupon, btnDeleteCoupon;

    private final ObservableList<Coupon> couponList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        couponsTable.setItems(couponList);

        btnAddCoupon.setOnAction(e -> showCreateOrEditCouponDialog(null));
        btnEditCoupon.setOnAction(e -> {
            Coupon selectedCoupon = couponsTable.getSelectionModel().getSelectedItem();
            if (selectedCoupon != null) {
                showCreateOrEditCouponDialog(selectedCoupon);
            } else {
                new Alert(Alert.AlertType.WARNING, "لطفاً ابتدا یک کوپن را برای ویرایش انتخاب کنید.").show();
            }
        });
        btnDeleteCoupon.setOnAction(e -> {
            Coupon selectedCoupon = couponsTable.getSelectionModel().getSelectedItem();
            if (selectedCoupon != null) {
                deleteCoupon(selectedCoupon);
            } else {
                new Alert(Alert.AlertType.WARNING, "لطفاً ابتدا یک کوپن را برای حذف انتخاب کنید.").show();
            }
        });

        loadCoupons();
    }

    private void setupTableColumns() {
        colSerial.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        // --- **تغییر اصلی و کلیدی اینجاست: استفاده از لامبدا به جای PropertyValueFactory** ---
        colId.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        colCouponCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCouponCode()));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        colValue.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getValue()));
        colMinPrice.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getMinPrice()));
        colUserCount.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getUserCount()));
        colStartDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartDate()));
        colEndDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndDate()));
    }

    private void loadCoupons() {
        couponsTable.setPlaceholder(new ProgressIndicator());
        new Thread(() -> {
            ApiResponse response = ApiService.getAdminCoupons();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    List<Coupon> coupons = new Gson().fromJson(response.getBody(), new TypeToken<List<Coupon>>() {}.getType());
                    couponList.setAll(coupons);
                } else {
                    couponsTable.setPlaceholder(new Label("خطا در دریافت لیست کوپن‌ها."));
                }
            });
        }).start();
    }

    private void deleteCoupon(Coupon coupon) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "آیا از حذف کوپن '" + coupon.getCouponCode() + "' مطمئن هستید؟", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                new Thread(() -> {
                    ApiResponse apiResponse = ApiService.deleteCoupon(coupon.getId());
                    Platform.runLater(() -> {
                        if (apiResponse.getStatusCode() == 200) {
                            loadCoupons();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "خطا در حذف کوپن: " + apiResponse.getBody()).show();
                        }
                    });
                }).start();
            }
        });
    }

    private void showCreateOrEditCouponDialog(Coupon coupon) {
        Dialog<CreateCouponRequest> dialog = new Dialog<>();
        dialog.setTitle(coupon == null ? "ایجاد کوپن جدید" : "ویرایش کوپن");
        dialog.setHeaderText("لطفاً اطلاعات کوپن را وارد کنید.");

        ButtonType saveButtonType = new ButtonType("ذخیره", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField codeField = new TextField();
        codeField.setPromptText("کد تخفیف");
        ChoiceBox<String> typeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("FIXED", "PERCENT"));
        TextField valueField = new TextField();
        valueField.setPromptText("مقدار (برای درصدی: 1-100)");
        TextField minPriceField = new TextField();
        minPriceField.setPromptText("حداقل خرید (تومان)");
        TextField userCountField = new TextField();
        userCountField.setPromptText("تعداد مجاز استفاده");
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        if (coupon != null) {
            codeField.setText(coupon.getCouponCode());
            typeChoiceBox.setValue(coupon.getType());
            if (coupon.getValue() != null) valueField.setText(coupon.getValue().toString());
            if (coupon.getMinPrice() != null) minPriceField.setText(coupon.getMinPrice().toString());
            if (coupon.getUserCount() != null) userCountField.setText(coupon.getUserCount().toString());
            if (coupon.getStartDate() != null) startDatePicker.setValue(LocalDate.parse(coupon.getStartDate()));
            if (coupon.getEndDate() != null) endDatePicker.setValue(LocalDate.parse(coupon.getEndDate()));
        }

        grid.add(new Label("کد:"), 0, 0); grid.add(codeField, 1, 0);
        grid.add(new Label("نوع:"), 0, 1); grid.add(typeChoiceBox, 1, 1);
        grid.add(new Label("مقدار:"), 0, 2); grid.add(valueField, 1, 2);
        grid.add(new Label("حداقل خرید:"), 0, 3); grid.add(minPriceField, 1, 3);
        grid.add(new Label("تعداد مجاز:"), 0, 4); grid.add(userCountField, 1, 4);
        grid.add(new Label("تاریخ شروع:"), 0, 5); grid.add(startDatePicker, 1, 5);
        grid.add(new Label("تاریخ پایان:"), 0, 6); grid.add(endDatePicker, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    CreateCouponRequest request = new CreateCouponRequest();
                    request.setCouponCode(codeField.getText());
                    request.setType(typeChoiceBox.getValue());
                    request.setValue(new BigDecimal(valueField.getText()));
                    if (!minPriceField.getText().trim().isEmpty()) request.setMinPrice(Integer.parseInt(minPriceField.getText().trim()));
                    if (!userCountField.getText().trim().isEmpty()) request.setUserCount(Integer.parseInt(userCountField.getText().trim()));
                    if (startDatePicker.getValue() != null) request.setStartDate(startDatePicker.getValue().toString());
                    if (endDatePicker.getValue() != null) request.setEndDate(endDatePicker.getValue().toString());
                    return request;
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "لطفاً مقادیر را به درستی وارد کنید.").show();
                    return null;
                }
            }
            return null;
        });

        Optional<CreateCouponRequest> result = dialog.showAndWait();
        result.ifPresent(couponRequest -> {
            if (coupon == null) {
                new Thread(() -> {
                    ApiResponse response = ApiService.createCoupon(couponRequest);
                    Platform.runLater(() -> {
                        if (response.getStatusCode() == 201) {
                            loadCoupons();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "خطا در ایجاد کوپن: " + response.getBody()).show();
                        }
                    });
                }).start();
            } else {
                new Thread(() -> {
                    ApiResponse response = ApiService.updateCoupon(coupon.getId(), couponRequest);
                    Platform.runLater(() -> {
                        if (response.getStatusCode() == 200) {
                            loadCoupons();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "خطا در ویرایش کوپن: " + response.getBody()).show();
                        }
                    });
                }).start();
            }
        });
    }
}