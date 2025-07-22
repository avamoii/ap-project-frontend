package org.example.approjectfrontend;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.example.approjectfrontend.Restaurant;
import org.example.approjectfrontend.RestaurantMenuItem;


import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class RestaurantPageController {

    @FXML
    private Label nameLabel;
    @FXML
    private ImageView logoView;
    @FXML
    private ListView<String> menuListView;
    @FXML
    private TextField addressField;
    @FXML
    private Label totalPriceLabel;

    public void initialize() {
        // فعال کردن انتخاب چندتایی
        menuListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Listener برای جمع مبلغ کل هر بار که انتخاب تغییر کرد
        menuListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String>) change -> {
            updateTotalPrice();
        });
    }
    @FXML
    private void handleOrderButton() {
        // دریافت آیتم‌های انتخاب شده
        ObservableList<String> selectedItems = menuListView.getSelectionModel().getSelectedItems();

        // دریافت آدرس
        String address = addressField.getText(); // مطمئن شوید که addressField تعریف شده و @FXML بهش اضافه شده

        // بررسی اینکه کاربر یک یا چند آیتم را انتخاب کرده است
        if (selectedItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("خطا");
            alert.setHeaderText(null);
            alert.setContentText("لطفاً حداقل یک آیتم از منو را انتخاب کنید.");
            alert.showAndWait();
            return;
        }

        // بررسی آدرس
        if (address == null || address.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("خطا");
            alert.setHeaderText(null);
            alert.setContentText("لطفاً آدرس را وارد کنید.");
            alert.showAndWait();
            return;
        }

        System.out.println("سفارش شما به این صورت است:");
        System.out.println("آدرس: " + address);
        System.out.println("آیتم‌های انتخاب شده:");
        selectedItems.forEach(System.out::println);

        // بازخورد به کاربر بعد از ثبت موفق
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("موفقیت");
        alert.setHeaderText(null);
        alert.setContentText("سفارش شما ثبت شد!");
        alert.showAndWait();
    }
    private void updateTotalPrice() {
        int total = 0;
        for (String itemStr : menuListView.getSelectionModel().getSelectedItems()) {
            // فرض: آیتم به فرمت "name - price تومان"
            try {
                String[] parts = itemStr.split("-");
                if (parts.length >= 2) {
                    String pricePart = parts[1].trim().replace("تومان", "").trim();
                    int price = Integer.parseInt(pricePart);
                    total += price;
                }
            } catch (Exception e) {
                // نادیده گرفتن خطاهای تبدیل
            }
        }
        totalPriceLabel.setText("هزینه کل: " + total + " تومان");
    }

    // این متد برای گرفتن رستوران و نمایش اطلاعاتش
    public void setRestaurant(Restaurant restaurant) {
        if (restaurant == null) return;
        // نمایش نام رستوران
        nameLabel.setText(restaurant.getName());
        // نمایش لوگو
        if (restaurant.getLogo() != null) {
            logoView.setImage(restaurant.getLogo());
        }
        // پاک‌کردن آیتم‌های قبلی (اگه قبلا ست شده)
        menuListView.getItems().clear();
        // ساختن لیست منو
        if (restaurant.getMenuItems() != null) {
            menuListView.setItems(FXCollections.observableArrayList(
                    restaurant.getMenuItems() // فرض بر این که getMenuItems() لیستی از RestaurantMenuItem می‌دهد
                            .stream()
                            .map(item -> item.getName() + " - " + item.getPrice() + " تومان")
                            .toList()
            ));
        }
    }

    // متد کمکی برای ساختن هر آیتم از منو به طور دلخواه خودت
    private VBox buildMenuItemRow(RestaurantMenuItem item) {
        Label itemName = new Label(item.getName() + " - " + item.getPrice() + " تومان");
        itemName.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        VBox vBox = new VBox(itemName);
        // هر عنصر UI دیگه‌ای خواستی اینجا اضافه کن
        return vBox;
    }
}
