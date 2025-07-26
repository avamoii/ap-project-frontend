package org.example.approjectfrontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminCouponsController {

    @FXML
    private TableView<Coupon> couponsTable;
    @FXML
    private TableColumn<Coupon, Void> colSerial;
    @FXML
    private TableColumn<Coupon, Long> colId;
    @FXML
    private TableColumn<Coupon, String> colCouponCode;
    @FXML
    private TableColumn<Coupon, String> colType;
    @FXML
    private TableColumn<Coupon, Integer> colValue;
    @FXML
    private TableColumn<Coupon, Integer> colMinPrice;
    @FXML
    private TableColumn<Coupon, Integer> colUserCount;
    @FXML
    private TableColumn<Coupon, String> colStartDate;
    @FXML
    private TableColumn<Coupon, String> colEndDate;

    private final ObservableList<Coupon> couponList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // ستون "ردیف"
        colSerial.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
                setStyle("-fx-alignment: CENTER;");
            }
        });

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCouponCode.setCellValueFactory(new PropertyValueFactory<>("coupon_code"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        colMinPrice.setCellValueFactory(new PropertyValueFactory<>("min_price"));
        colUserCount.setCellValueFactory(new PropertyValueFactory<>("user_count"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("start_date"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("end_date"));

        // داده‌ی تستی (در عمل، دیتا را از سرور بگیر)
        Coupon c1 = new Coupon();
        c1.setId(1L);
        c1.setCoupon_code("OFF20");
        c1.setType("percent");
        c1.setValue(20);
        c1.setMin_price(200000);
        c1.setUser_count(1);
        c1.setStart_date("2025-08-01");
        c1.setEnd_date("2025-08-31");

        Coupon c2 = new Coupon();
        c2.setId(2L);
        c2.setCoupon_code("WELCOMEFIX");
        c2.setType("fixed");
        c2.setValue(50000);
        c2.setMin_price(100000);
        c2.setUser_count(2);
        c2.setStart_date("2025-07-20");
        c2.setEnd_date("2025-08-15");

        couponList.addAll(c1, c2);

        couponsTable.setItems(couponList);
    }
}
