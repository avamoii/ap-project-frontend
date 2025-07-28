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
    @FXML private ToggleButton favoriteButton;

    private RestaurantDTO currentRestaurant;
    private final ObservableList<FoodItemDTO> allItems = FXCollections.observableArrayList();
    private final Map<FoodItemDTO, Spinner<Integer>> itemSpinners = new HashMap<>();
    private List<Long> favoriteRestaurantIds = new ArrayList<>();

    public void initialize() {
        // (این متد بدون تغییر باقی می‌ماند)
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
        if (restaurant.getAddress() != null) {
            addressField.setText(restaurant.getAddress());
        }
        loadFullMenu(restaurant.getId());
        // --- تغییر کلیدی: وضعیت اولیه دکمه را از سرور می‌خوانیم ---
        checkFavoriteStatus();
    }

    // --- متد جدید برای بررسی وضعیت رستوران در لیست علاقه‌مندی‌ها ---
    private void checkFavoriteStatus() {
        favoriteButton.setDisable(true); // غیرفعال کردن دکمه تا زمان دریافت پاسخ
        new Thread(() -> {
            ApiResponse response = ApiService.getFavorites();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    List<RestaurantDTO> favorites = new Gson().fromJson(response.getBody(), new TypeToken<List<RestaurantDTO>>() {}.getType());
                    boolean isFavorite = favorites.stream().anyMatch(r -> r.getId().equals(currentRestaurant.getId()));
                    updateFavoriteButtonState(isFavorite);
                }
                favoriteButton.setDisable(false); // فعال کردن مجدد دکمه
            });
        }).start();
    }

    // --- متد جدید برای مدیریت کلیک روی دکمه ---
    @FXML
    private void handleFavoriteChange(ActionEvent event) {
        if (currentRestaurant == null) return;

        boolean addToFavorites = favoriteButton.isSelected();
        favoriteButton.setDisable(true);

        new Thread(() -> {
            ApiResponse response;
            if (addToFavorites) {
                response = ApiService.addFavorite(currentRestaurant.getId());
            } else {
                response = ApiService.removeFavorite(currentRestaurant.getId());
            }

            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    updateFavoriteButtonState(addToFavorites);
                } else {
                    // در صورت خطا، دکمه را به حالت اولیه برمی‌گردانیم
                    favoriteButton.setSelected(!addToFavorites);
                    showAlert(Alert.AlertType.ERROR, "خطا", "خطا در تغییر وضعیت علاقه‌مندی‌ها.");
                }
                favoriteButton.setDisable(false);
            });
        }).start();
    }

    // --- متد جدید برای به‌روزرسانی ظاهر دکمه ---
    private void updateFavoriteButtonState(boolean isFavorite) {
        if (isFavorite) {
            favoriteButton.setSelected(true);
            favoriteButton.setText("حذف از علاقه‌مندی‌ها");
            favoriteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;"); // رنگ قرمز برای حذف
        } else {
            favoriteButton.setSelected(false);
            favoriteButton.setText("افزودن به علاقه‌مندی‌ها");
            favoriteButton.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;"); // رنگ سبز برای افزودن
        }
    }

    // (بقیه متدهای کلاس بدون تغییر باقی می‌مانند)
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
                    showAlert(Alert.AlertType.ERROR, "خطا", "خطا در ثبت سفارش.");
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
                    showAlert(Alert.AlertType.ERROR, "خطا در پرداخت", "پرداخت ناموفق بود.");
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