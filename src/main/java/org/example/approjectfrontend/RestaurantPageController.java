package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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

    private final ObservableList<FoodItemDTO> allItems = FXCollections.observableArrayList();
    private final Map<FoodItemDTO, Spinner<Integer>> itemSpinners = new HashMap<>();

    public void initialize() {
        // این متد ظاهر هر سطر از لیست منو را تعریف می‌کند
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
        updateTotalPrice();
    }

    // این متد از BuyerHomeController فراخوانی می‌شود
    public void setRestaurant(RestaurantDTO restaurant) {
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