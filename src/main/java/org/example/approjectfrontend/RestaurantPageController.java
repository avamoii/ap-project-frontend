package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.FoodItemDTO;
import org.example.approjectfrontend.api.RestaurantDTO;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class RestaurantPageController {

    @FXML
    private Label nameLabel;
    @FXML
    private ImageView logoView;
    @FXML
    private ListView<FoodItemDTO> menuListView;
    @FXML
    private TextField addressField;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Button payButton;
    private Restaurant restaurant;

    private final ObservableList<FoodItemDTO> allItems = FXCollections.observableArrayList();
    private final Map<FoodItemDTO, Spinner<Integer>> itemSpinners = new HashMap<>();

    public void initialize() {
        // دکمه پرداخت: بازکردن دیالوگ انتخاب روش پرداخت
        payButton.setOnAction(event -> showPaymentMethodDialog());

        // سلول سفارشی برای نمایش آیتم، اسپینر انتخاب تعداد، و به‌روزرسانی قیمت کل
        menuListView.setCellFactory(list -> new ListCell<>() {
            private final Label nameAndPrice = new Label();
            private final Spinner<Integer> spinner = new Spinner<>(0, 20, 0);
            private final HBox box = new HBox(10, nameAndPrice, spinner);

            {
                spinner.setPrefWidth(80);
                HBox.setHgrow(nameAndPrice, Priority.ALWAYS); // باعث می‌شود نام آیتم تمام فضای خالی را بگیرد
                box.setAlignment(Pos.CENTER_LEFT);
                // هر بار که مقدار اسپینر تغییر می‌کند، قیمت کل آپدیت می‌شود
                spinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
            }

            @Override
            protected void updateItem(FoodItemDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nameAndPrice.setText(item.getName() + " - " + item.getPrice() + " تومان");
                    spinner.getValueFactory().setValue(0);
                    itemSpinners.put(item, spinner); // اسپینر را به آیتم متصل می‌کنیم تا بعداً قیمت را محاسبه کنیم
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

    // این متد از BuyerHomeController فراخوانی می‌شود
    public void setRestaurant(RestaurantDTO restaurant) {
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

        // اطلاعات اولیه رستوران را نمایش می‌دهد
        nameLabel.setText(restaurant.getName());
        if (restaurant.getLogoBase64() != null && !restaurant.getLogoBase64().isEmpty()) {
            byte[] decodedBytes = Base64.getDecoder().decode(restaurant.getLogoBase64());
            logoView.setImage(new Image(new ByteArrayInputStream(decodedBytes)));
        }
        if (restaurant.getAddress() != null) {
            addressField.setText(restaurant.getAddress());
        }

        // **مهم‌ترین بخش:** حالا منوی کامل را از سرور درخواست می‌کند
        loadFullMenu(restaurant.getId());
    }

    private void loadFullMenu(long restaurantId) {
        new Thread(() -> {
            // از متد getRestaurantMenu که اندپوینت صحیح را فراخوانی می‌کند، استفاده می‌کنیم
            ApiResponse response = ApiService.getRestaurantMenu(restaurantId);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    JsonObject responseJson = gson.fromJson(response.getBody(), JsonObject.class);

                    allItems.clear();
                    // روی تمام منوهای داخل پاسخ JSON حلقه می‌زنیم
                    for (Map.Entry<String, JsonElement> entry : responseJson.entrySet()) {
                        // کلیدهای اضافی را نادیده می‌گیریم
                        if (!entry.getKey().equals("vendor") && !entry.getKey().equals("menu_titles")) {
                            // لیست آیتم‌های هر منو را استخراج می‌کنیم
                            List<FoodItemDTO> items = gson.fromJson(entry.getValue(), new TypeToken<List<FoodItemDTO>>(){}.getType());
                            allItems.addAll(items);
                        }
                    }
                    // لیست نهایی آیتم‌ها را به ListView متصل می‌کنیم تا نمایش داده شوند
                    menuListView.setItems(allItems);
                } else {
                    // می‌توانید یک لیبل خطا در FXML اضافه کرده و آن را در اینجا نمایش دهید
                    System.err.println("Error fetching menu: " + response.getBody());
                }
            });
        }).start();
    }

    private void updateTotalPrice() {
        long total = 0;
        for (Map.Entry<FoodItemDTO, Spinner<Integer>> entry : itemSpinners.entrySet()) {
            total += (long) entry.getKey().getPrice() * entry.getValue().getValue();
        }
        totalPriceLabel.setText("هزینه کل: " + total + " تومان");
    }

    @FXML
    private void handleOrderButton() {
        // منطق ثبت سفارش در آینده در اینجا تکمیل خواهد شد
        System.out.println("دکمه ثبت سفارش کلیک شد.");
    }
}