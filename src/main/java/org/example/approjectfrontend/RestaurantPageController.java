package org.example.approjectfrontend;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

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

    public void initialize() {
        // سلول سفارشی برای انتخاب تعداد هر آیتم
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

        // مقدار اولیه مجموع هزینه
        updateTotalPrice();
    }

    @FXML
    private void handleOrderButton() {
        String address = addressField.getText();

        // فیلتر آیتم‌هایی که تعدادشان > 0 است
        var orderedItems = menuListView.getItems().filtered(item -> item.getOrderCount() > 0);

        if (orderedItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "خطا", null, "لطفاً حداقل یک آیتم از منو را انتخاب کنید.");
            return;
        }

        if (address == null || address.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "خطا", null, "لطفاً آدرس را وارد کنید.");
            return;
        }

        System.out.println("سفارش شما به این صورت است:");
        System.out.println("آدرس: " + address);
        System.out.println("آیتم‌های سفارش داده‌شده:");
        orderedItems.forEach(item ->
                System.out.println(item.getName() + " × " + item.getOrderCount() +
                        " = " + (Integer.parseInt(item.getPrice()) * item.getOrderCount()) + " تومان"));


        showAlert(Alert.AlertType.INFORMATION, "موفقیت", null, "سفارش شما ثبت شد!");

        // (اختیاری) ریست فرم:
        // menuListView.getItems().forEach(item -> item.setOrderCount(0));
        // menuListView.refresh();
        // addressField.clear();
        // updateTotalPrice();
    }

    private void updateTotalPrice() {
        int total = menuListView.getItems().stream()
                .mapToInt(item -> item.getOrderCount() * Integer.parseInt(item.getPrice()))

                .sum();
        totalPriceLabel.setText("هزینه کل: " + total + " تومان");
    }

    // متد دریافت رستوران و ست کردن محتوا
    public void setRestaurant(Restaurant restaurant) {
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
