package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestaurantPageController {

    @FXML private Label nameLabel;
    @FXML private ImageView logoView;
    @FXML private ListView<FoodItemDTO> menuListView;
    @FXML private TextField addressField;
    @FXML private Label totalPriceLabel;
    @FXML private Button submitAndPayButton;
    @FXML private Button backButton;
    @FXML private TextField couponCodeField;
    @FXML private Button applyCouponButton;
    @FXML private Label couponStatusLabel;
    @FXML private ToggleButton favoriteButton;

    private Coupon appliedCoupon = null; // یا فقط تخفیف عددی، بسته به پیاده‌سازی تو

    private RestaurantDTO currentRestaurant;
    private final ObservableList<FoodItemDTO> allItems = FXCollections.observableArrayList();
    private final Map<FoodItemDTO, Spinner<Integer>> itemSpinners = new HashMap<>();
    private List<Long> favoriteRestaurantIds = new ArrayList<>();
    public void initialize() {
        menuListView.setCellFactory(list -> new ListCell<>() {
            private final VBox mainVBox = new VBox(5);
            private final HBox topHBox = new HBox(10);
            private final Label nameAndPrice = new Label();
            private final Spinner<Integer> spinner = new Spinner<>(0, 20, 0);
            private final Button detailsButton = new Button("جزئیات");
            private final Label descriptionLabel = new Label();
            private final Label keywordsLabel = new Label();

            {
                // تنظیمات ردیف اصلی (نام، اسپینر، دکمه)
                spinner.setPrefWidth(80);
                HBox.setHgrow(nameAndPrice, Priority.ALWAYS);
                topHBox.setAlignment(Pos.CENTER_LEFT);
                topHBox.getChildren().addAll(nameAndPrice, spinner, detailsButton);

                // تنظیمات لیبل‌های جزئیات
                descriptionLabel.setWrapText(true);
                descriptionLabel.setStyle("-fx-text-fill: #555; -fx-padding: 0 0 0 10;");
                keywordsLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #777; -fx-padding: 0 0 0 10;");

                // مخفی کردن لیبل‌های جزئیات در ابتدا
                descriptionLabel.setVisible(false);
                descriptionLabel.setManaged(false);
                keywordsLabel.setVisible(false);
                keywordsLabel.setManaged(false);

                // اضافه کردن همه چیز به کانتینر اصلی
                mainVBox.getChildren().addAll(topHBox, descriptionLabel, keywordsLabel);
                mainVBox.setPadding(new Insets(5));

                // اتصال رویدادها
                spinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
                detailsButton.setOnAction(event -> {
                    boolean isVisible = descriptionLabel.isVisible();
                    descriptionLabel.setVisible(!isVisible);
                    descriptionLabel.setManaged(!isVisible);
                    keywordsLabel.setVisible(!isVisible);
                    keywordsLabel.setManaged(!isVisible);
                });
            }

            @Override
            protected void updateItem(FoodItemDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nameAndPrice.setText(item.getName() + " - " + item.getPrice() + " تومان");
                    descriptionLabel.setText("توضیحات: " + item.getDescription());
                    keywordsLabel.setText("کلمات کلیدی: " + String.join(", ", item.getKeywords()));
                    descriptionLabel.setVisible(false);
                    descriptionLabel.setManaged(false);
                    keywordsLabel.setVisible(false);
                    keywordsLabel.setManaged(false);
                    spinner.getValueFactory().setValue(0);
                    itemSpinners.put(item, spinner);
                    setGraphic(mainVBox);
                }
            }
        });
        updateTotalPrice();
    }

    public void setRestaurant(RestaurantDTO restaurant) {
        this.currentRestaurant = restaurant;
        if (restaurant == null) return;
        nameLabel.setText(restaurant.getName());
        if (restaurant.getLogoBase64() != null && !restaurant.getLogoBase64().isEmpty()) {
            byte[] decodedBytes = Base64.getDecoder().decode(restaurant.getLogoBase64());
            logoView.setImage(new Image(new ByteArrayInputStream(decodedBytes)));
        }
        if (restaurant.getAddress() != null) {
            addressField.setText(restaurant.getAddress());
        }
        loadFullMenu(restaurant.getId());
        checkFavoriteStatus();
    }

    private void loadFullMenu(long restaurantId) {
        new Thread(() -> {
            ApiResponse response = ApiService.getRestaurantMenu(restaurantId);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    JsonObject responseJson = gson.fromJson(response.getBody(), JsonObject.class);
                    allItems.clear();
                    for (Map.Entry<String, JsonElement> entry : responseJson.entrySet()) {
                        if (!entry.getKey().equals("vendor") && !entry.getKey().equals("menu_titles")) {
                            List<FoodItemDTO> items = gson.fromJson(entry.getValue(), new TypeToken<List<FoodItemDTO>>() {}.getType());
                            allItems.addAll(items);
                        }
                    }
                    menuListView.setItems(allItems);
                } else {
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
    private void handleApplyCoupon(ActionEvent event) {
        String code = couponCodeField.getText().trim();

        if (code.isEmpty()) {
            couponStatusLabel.setText("کد تخفیف را وارد کنید.");
            appliedCoupon = null;
            updateTotalPrice();
            return;
        }

        // بخش Mock: شبیه‌ساز کد تخفیف
        // مثلاً code="ghaza10" اگر وارد شد ۱۰ درصد Discount بده،
        // یا code="takhfif50" پنجاه هزار تومن.
        if (code.equalsIgnoreCase("ghaza10")) {
            appliedCoupon = new Coupon();
            appliedCoupon.setCoupon_code("ghaza10");
            appliedCoupon.setType("percent");
            appliedCoupon.setValue(10); // یعنی ۱۰ درصد
            couponStatusLabel.setText("کد ۱۰٪ تخفیف اعمال شد ✅");
        } else if (code.equalsIgnoreCase("takhfif50")) {
            appliedCoupon = new Coupon();
            appliedCoupon.setCoupon_code("takhfif50");
            appliedCoupon.setType("fixed");
            appliedCoupon.setValue(50000); // ۵۰ هزار تومن
            couponStatusLabel.setText("کد ۵۰هزار تومانی اعمال شد ✅");
        } else {
            appliedCoupon = null;
            couponStatusLabel.setText("کد نامعتبر است ❌");
        }

        updateTotalPrice();
    }

    @FXML
    private void handleSubmitAndPay(ActionEvent event) {
        List<OrderItemRequestDTO> selectedItems = new ArrayList<>();
        for (Map.Entry<FoodItemDTO, Spinner<Integer>> entry : itemSpinners.entrySet()) {
            if (entry.getValue().getValue() > 0) {
                selectedItems.add(new OrderItemRequestDTO(entry.getKey().getId(), entry.getValue().getValue()));
            }
        }

        if (selectedItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "خطا", "لطفاً حداقل یک آیتم از منو را انتخاب کنید.");
            return;
        }
        String address = addressField.getText().trim();
        if (address.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "خطا", "لطفاً آدرس تحویل سفارش را وارد کنید.");
            return;
        }

        SubmitOrderRequest orderRequest = new SubmitOrderRequest(address, currentRestaurant.getId(), selectedItems);

        new Thread(() -> {
            ApiResponse response = ApiService.submitOrder(orderRequest);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    OrderDTO createdOrder = new Gson().fromJson(response.getBody(), OrderDTO.class);
                    showPaymentMethodDialog(createdOrder.getId());
                } else {
                    String errorMessage = "خطا در ثبت سفارش.";
                    try {
                        JsonObject errorJson = new Gson().fromJson(response.getBody(), JsonObject.class);
                        if (errorJson != null && errorJson.has("error")) {
                            errorMessage = errorJson.get("error").getAsString();
                        }
                    } catch (Exception e) {
                        System.err.println("Could not parse error response: " + response.getBody());
                    }
                    showAlert(Alert.AlertType.ERROR, "خطا", errorMessage);
                }
            });
        }).start();
    }
    @FXML
    private void handleFavoriteChange(ActionEvent event) {
        if (currentRestaurant == null) return;

        boolean isSelected = favoriteButton.isSelected();
        favoriteButton.setDisable(true); // غیرفعال کردن دکمه تا پایان عملیات

        new Thread(() -> {
            ApiResponse response;
            if (isSelected) {
                response = ApiService.addFavorite(currentRestaurant.getId());
            } else {
                response = ApiService.removeFavorite(currentRestaurant.getId());
            }

            Platform.runLater(() -> {
                if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
                    // به‌روزرسانی لیست علاقه‌مندی‌ها و وضعیت دکمه
                    checkFavoriteStatus();
                } else {
                    // در صورت خطا، وضعیت دکمه را به حالت قبل برمی‌گردانیم
                    favoriteButton.setSelected(!isSelected);
                    showAlert(Alert.AlertType.ERROR, "خطا", "خطا در تغییر وضعیت علاقه‌مندی: " + response.getBody());
                }
                favoriteButton.setDisable(false); // فعال کردن مجدد دکمه
            });
        }).start();
    }
    private void updateFavoriteButtonState() {
        if (currentRestaurant != null && favoriteRestaurantIds.contains(currentRestaurant.getId())) {
            favoriteButton.setSelected(true);
            favoriteButton.setText("حذف از علاقه‌مندی‌ها");
        } else {
            favoriteButton.setSelected(false);
            favoriteButton.setText("افزودن به علاقه‌مندی‌ها");
        }
    }
    private void checkFavoriteStatus() {
        new Thread(() -> {
            ApiResponse response = ApiService.getFavorites();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    // پاسخ سرور یک آبجکت JSON است که کلید data آن حاوی لیست است
                    JsonObject bodyJson = new Gson().fromJson(response.getBody(), JsonObject.class);
                    JsonElement dataElement = bodyJson.get("data");

                    List<RestaurantDTO> favorites = new Gson().fromJson(dataElement, new TypeToken<List<RestaurantDTO>>() {}.getType());

                    if (favorites != null) {
                        favoriteRestaurantIds = favorites.stream().map(RestaurantDTO::getId).collect(Collectors.toList());
                    } else {
                        favoriteRestaurantIds = new ArrayList<>();
                    }
                    updateFavoriteButtonState();
                }
            });
        }).start();
    }



    private void showPaymentMethodDialog(long orderId) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("پرداخت با کیف پول", "پرداخت با کیف پول", "پرداخت با کارت");
        dialog.setTitle("انتخاب روش پرداخت");
        dialog.setHeaderText("سفارش شما با موفقیت ثبت شد. لطفاً روش پرداخت را انتخاب کنید.");
        dialog.setContentText("روش پرداخت:");

        dialog.showAndWait().ifPresent(selected -> {
            String method = "";
            if ("پرداخت با کیف پول".equals(selected)) {
                method = "WALLET";
            } else if ("پرداخت با کارت".equals(selected)) {
                method = "ONLINE";
            }

            if (!method.isEmpty()) {
                processPayment(orderId, method);
            }
        });
    }

    private void processPayment(long orderId, String method) {
        PaymentRequest paymentRequest = new PaymentRequest(orderId, method);
        new Thread(() -> {
            ApiResponse response = ApiService.makePayment(paymentRequest);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    showAlert(Alert.AlertType.INFORMATION, "موفقیت", "پرداخت با موفقیت انجام شد!");
                    Stage stage = (Stage) submitAndPayButton.getScene().getWindow();
                    stage.close();
                } else {
                    String errorMessage = "پرداخت ناموفق بود.";
                    try {
                        JsonObject errorJson = new Gson().fromJson(response.getBody(), JsonObject.class);
                        if (errorJson != null && errorJson.has("error")) {
                            errorMessage = errorJson.get("error").getAsString();
                        }
                    } catch (Exception e) {
                        System.err.println("Could not parse error response: " + response.getBody());
                    }
                    showAlert(Alert.AlertType.ERROR, "خطا در پرداخت", errorMessage);
                }
            });
        }).start();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
