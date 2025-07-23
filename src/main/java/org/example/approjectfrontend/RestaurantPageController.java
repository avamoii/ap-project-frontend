package org.example.approjectfrontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantPageController {

    @FXML
    private Label nameLabel;
    @FXML
    private ImageView logoView;
    @FXML
    private ListView<RestaurantMenuItem> menuListView;
    @FXML
    private TextField addressField;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Button payButton;
    private Restaurant restaurant;

    public void initialize() {
        // دکمه پرداخت: بازکردن دیالوگ انتخاب روش پرداخت
        payButton.setOnAction(event -> showPaymentMethodDialog());

        // سلول سفارشی برای نمایش آیتم، اسپینر انتخاب تعداد، و به‌روزرسانی قیمت کل
        menuListView.setCellFactory(list -> new ListCell<>() {
            private final Label nameAndPrice = new Label();
            private final Spinner<Integer> spinner = new Spinner<>(0, 20, 0);
            private final HBox box = new HBox(10, nameAndPrice, spinner);

            {
                spinner.setPrefWidth(60);
                box.setAlignment(Pos.CENTER_LEFT);

                spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (getItem() != null) {
                        getItem().setOrderCount(newVal);
                        updateTotalPrice();
                    }
                });
            }

            @Override
            protected void updateItem(RestaurantMenuItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nameAndPrice.setText(item.getName() + " - " + item.getPrice() + " تومان");
                    spinner.getValueFactory().setValue(item.getOrderCount());
                    setGraphic(box);
                }
            }
        });

        updateTotalPrice(); // مقداردهی اولیه
    }

    private void showPaymentMethodDialog() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("پرداخت با کارت", "پرداخت با کارت", "پرداخت با کیف پول");
        dialog.setTitle("انتخاب روش پرداخت");
        dialog.setHeaderText(null);
        dialog.setContentText("روش پرداخت را انتخاب کنید:");

        dialog.showAndWait().ifPresent(selected -> {
            if ("پرداخت با کارت".equals(selected)) {
                handleCardPayment();
            } else if ("پرداخت با کیف پول".equals(selected)) {
                handleWalletPayment(getCurrentOrderTotalPrice());
            }
        });
    }

    private void handleCardPayment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CardPayment-view.fxml"));
            Parent root = loader.load();

            CardPaymentController controller = loader.getController();
            // controller.setAmount(getCurrentOrderTotalPrice()); // اگر نیاز به انتقال مبلغ داشتی

            Stage stage = new Stage();
            stage.setTitle("پرداخت با کارت");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "خطا", null, "خطا در باز کردن فرم پرداخت!");
        }
    }

    private void handleWalletPayment(long amount) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/approjectfrontend/WalletPayment-view.fxml"));
            Parent root = loader.load();

            WalletPaymentController controller = loader.getController();
            controller.setAmount(amount);

            Stage stage = new Stage();
            stage.setTitle("پرداخت با کیف پول");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "خطا", null, "خطا در باز کردن فرم پرداخت با کیف پول!");
        }
    }
    @FXML
    private void handleOrderButton() {
        // فرض: menuListView از نوع ListView<RestaurantMenuItem> است
        List<RestaurantMenuItem> selectedItems = menuListView.getItems().stream()
                .filter(item -> item.getOrderCount() > 0)
                .map(RestaurantMenuItem::cloneItem)
                .toList();

        String address = addressField.getText();

        if (selectedItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "خطا", null, "لطفاً حداقل یک آیتم از منو را انتخاب کنید.");
            return;
        }

        if (address == null || address.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "خطا", null, "لطفاً آدرس را وارد کنید.");
            return;
        }

        int totalPrice = selectedItems.stream()
                .mapToInt(item -> item.getOrderCount() * Integer.parseInt(item.getPrice()))
                .sum();

        // فرض: سازنده Order شما مناسب این پارامترها باشد
        Order newOrder = new Order(
                this.restaurant.getName(),
                address,
                selectedItems,
                totalPrice
        );
        OrderRepository.ORDERS.add(newOrder);

        showAlert(Alert.AlertType.INFORMATION, "موفقیت", null, "سفارش شما ثبت شد!");

        // صفر کردن تعداد سفارش آیتم‌های منو
        menuListView.getItems().forEach(item -> item.setOrderCount(0));
        menuListView.refresh();
        updateTotalPrice();
    }


    private void updateTotalPrice() {
        int total = menuListView.getItems().stream()
                .mapToInt(item -> item.getOrderCount() * Integer.parseInt(item.getPrice()))
                .sum();
        totalPriceLabel.setText("هزینه کل: " + total + " تومان");
    }

    // مقدار کل فعلی سفارش بدون ثبت
    private int getCurrentOrderTotalPrice() {
        return menuListView.getItems().stream()
                .mapToInt(item -> item.getOrderCount() * Integer.parseInt(item.getPrice()))
                .sum();
    }

    // متد دریافت رستوران و ست کردن محتوا
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        if (restaurant == null) return;

        nameLabel.setText(restaurant.getName());
        if (restaurant.getLogo() != null)
            logoView.setImage(restaurant.getLogo());
        else
            logoView.setImage(null);

        // پر کردن منو با خود آبجکت‌ها (نه رشته)
        menuListView.setItems(FXCollections.observableArrayList(restaurant.getMenuItems()));

        // ریست کردن تعداد سفارش
        menuListView.getItems().forEach(item -> item.setOrderCount(0));
        menuListView.refresh();

        updateTotalPrice();
    }

    // نمایش پیام
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
