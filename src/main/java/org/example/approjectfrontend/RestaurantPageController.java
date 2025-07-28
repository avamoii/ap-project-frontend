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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.*;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantPageController {

    @FXML private Label nameLabel;
    @FXML private ImageView logoView;
    @FXML private ListView<FoodItemDTO> menuListView;
    @FXML private TextField addressField;
    @FXML private Label totalPriceLabel;
    @FXML private Button submitAndPayButton;
    @FXML private Button backButton;
    @FXML private ToggleButton favoriteButton;
    @FXML private TextField couponCodeField;
    @FXML private Button applyCouponButton;
    @FXML private Label couponStatusLabel;

    private RestaurantDTO currentRestaurant;
    private final ObservableList<FoodItemDTO> allItems = FXCollections.observableArrayList();
    private final Map<FoodItemDTO, Spinner<Integer>> itemSpinners = new HashMap<>();
    private Coupon appliedCoupon = null;

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
                spinner.setPrefWidth(80);
                HBox.setHgrow(nameAndPrice, Priority.ALWAYS);
                topHBox.setAlignment(Pos.CENTER_LEFT);
                topHBox.getChildren().addAll(nameAndPrice, spinner, detailsButton);
                descriptionLabel.setWrapText(true);
                descriptionLabel.setStyle("-fx-text-fill: #555; -fx-padding: 0 0 0 10;");
                keywordsLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #777; -fx-padding: 0 0 0 10;");
                descriptionLabel.setVisible(false);
                descriptionLabel.setManaged(false);
                keywordsLabel.setVisible(false);
                keywordsLabel.setManaged(false);
                mainVBox.getChildren().addAll(topHBox, descriptionLabel, keywordsLabel);
                mainVBox.setPadding(new Insets(5));
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
        loadFullMenu(restaurant.getId());
        checkFavoriteStatus();
        loadUserProfile();
    }

    private void loadUserProfile() {
        new Thread(() -> {
            ApiResponse response = ApiService.getUserProfile();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    UserDTO user = new Gson().fromJson(response.getBody(), UserDTO.class);
                    if (user != null && user.getAddress() != null && !user.getAddress().isEmpty()) {
                        addressField.setText(user.getAddress());
                    }
                } else {
                    System.err.println("Failed to load user profile for address pre-fill.");
                }
            });
        }).start();
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
                    showAlert(Alert.AlertType.ERROR, "خطا", "خطا در دریافت منوی رستوران.");
                }
            });
        }).start();
    }

    private void checkFavoriteStatus() {
        favoriteButton.setDisable(true);
        new Thread(() -> {
            ApiResponse response = ApiService.getFavorites();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    List<RestaurantDTO> favorites = new Gson().fromJson(response.getBody(), new TypeToken<List<RestaurantDTO>>() {}.getType());
                    boolean isFavorite = favorites.stream().anyMatch(r -> r.getId().equals(currentRestaurant.getId()));
                    updateFavoriteButtonState(isFavorite);
                }
                favoriteButton.setDisable(false);
            });
        }).start();
    }

    @FXML
    private void handleFavoriteChange(ActionEvent event) {
        if (currentRestaurant == null) return;
        boolean addToFavorites = favoriteButton.isSelected();
        favoriteButton.setDisable(true);
        new Thread(() -> {
            ApiResponse response = addToFavorites ? ApiService.addFavorite(currentRestaurant.getId()) : ApiService.removeFavorite(currentRestaurant.getId());
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    updateFavoriteButtonState(addToFavorites);
                } else {
                    favoriteButton.setSelected(!addToFavorites);
                    showAlert(Alert.AlertType.ERROR, "خطا", "خطا در تغییر وضعیت علاقه‌مندی‌ها.");
                }
                favoriteButton.setDisable(false);
            });
        }).start();
    }

    private void updateFavoriteButtonState(boolean isFavorite) {
        if (isFavorite) {
            favoriteButton.setSelected(true);
            favoriteButton.setText("حذف از علاقه‌مندی‌ها");
            favoriteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        } else {
            favoriteButton.setSelected(false);
            favoriteButton.setText("افزودن به علاقه‌مندی‌ها");
            favoriteButton.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
        }
    }

    @FXML
    private void handleApplyCoupon() {
        String code = couponCodeField.getText().trim();
        if (code.isEmpty()) {
            appliedCoupon = null;
            couponStatusLabel.setText("");
            updateTotalPrice();
            return;
        }
        new Thread(() -> {
            ApiResponse response = ApiService.checkCoupon(code);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    appliedCoupon = new Gson().fromJson(response.getBody(), Coupon.class);
                    couponStatusLabel.setText("کد تخفیف با موفقیت اعمال شد.");
                    couponStatusLabel.setStyle("-fx-text-fill: green;");
                } else {
                    appliedCoupon = null;
                    couponStatusLabel.setText("کد تخفیف نامعتبر است.");
                    couponStatusLabel.setStyle("-fx-text-fill: red;");
                }
                updateTotalPrice();
            });
        }).start();
    }

    private void updateTotalPrice() {
        long total = 0;
        for (Map.Entry<FoodItemDTO, Spinner<Integer>> entry : itemSpinners.entrySet()) {
            total += (long) entry.getKey().getPrice() * entry.getValue().getValue();
        }
        if (appliedCoupon != null && appliedCoupon.getValue() != null) {
            if ("FIXED".equalsIgnoreCase(appliedCoupon.getType())) {
                total -= appliedCoupon.getValue().longValue();
            } else if ("PERCENT".equalsIgnoreCase(appliedCoupon.getType())) {
                BigDecimal totalDecimal = BigDecimal.valueOf(total);
                BigDecimal discountPercent = appliedCoupon.getValue();
                BigDecimal hundred = BigDecimal.valueOf(100);
                BigDecimal discountAmount = totalDecimal.multiply(discountPercent).divide(hundred, BigDecimal.ROUND_HALF_UP);
                total -= discountAmount.longValue();
            }
        }
        totalPriceLabel.setText(String.format("هزینه کل: %,d تومان", Math.max(0, total)));
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
        if (appliedCoupon != null) {
            orderRequest.setCouponId(appliedCoupon.getId());
        }

        new Thread(() -> {
            ApiResponse response = ApiService.submitOrder(orderRequest);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    OrderDTO createdOrder = new Gson().fromJson(response.getBody(), OrderDTO.class);
                    showPaymentMethodDialog(createdOrder.getId());
                } else {
                    // --- **تغییر اصلی اینجاست: نمایش خطای واقعی** ---
                    String errorMessage = "خطا در ثبت سفارش.";
                    try {
                        JsonObject errorJson = new Gson().fromJson(response.getBody(), JsonObject.class);
                        if (errorJson != null && errorJson.has("error")) {
                            errorMessage = errorJson.get("error").getAsString();
                        }
                    } catch (Exception e) {
                        errorMessage = response.getBody(); // اگر پاسخ JSON نبود
                    }
                    showAlert(Alert.AlertType.ERROR, "خطا", errorMessage);
                }
            });
        }).start();
    }

    private void showPaymentMethodDialog(long orderId) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("پرداخت با کیف پول", "پرداخت با کیف پول", "پرداخت با کارت");
        dialog.setTitle("انتخاب روش پرداخت");
        dialog.setHeaderText("سفارش شما با موفقیت ثبت شد.");
        dialog.setContentText("روش پرداخت:");
        dialog.showAndWait().ifPresent(selected -> {
            String method = "پرداخت با کیف پول".equals(selected) ? "WALLET" : "ONLINE";
            processPayment(orderId, method);
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
                        errorMessage = response.getBody();
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