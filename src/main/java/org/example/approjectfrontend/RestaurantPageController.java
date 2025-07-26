package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.FoodItemDTO;
import org.example.approjectfrontend.api.RestaurantDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private RestaurantDTO currentRestaurant;
    private final ObservableList<FoodItemDTO> allItems = FXCollections.observableArrayList();
    private final Map<FoodItemDTO, Spinner<Integer>> itemSpinners = new HashMap<>();

    public void initialize() {
        payButton.setOnAction(event -> showPaymentMethodDialog());

        menuListView.setCellFactory(list -> new ListCell<>() {
            private final VBox mainVBox = new VBox(5);
            private final HBox topHBox = new HBox(10);
            private final Label nameAndPrice = new Label();
            private final Spinner<Integer> spinner = new Spinner<>(0, 20, 0);
            private final Button detailsButton = new Button("جزئیات");
            private final Label descriptionLabel = new Label();
            private final Label keywordsLabel = new Label();

            {
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

                spinner.setPrefWidth(80);
                HBox.setHgrow(nameAndPrice, Priority.ALWAYS);
                box.setAlignment(Pos.CENTER_LEFT);
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

    private long getCurrentOrderTotalPrice() {
        long total = 0;
        for (Map.Entry<FoodItemDTO, Spinner<Integer>> entry : itemSpinners.entrySet()) {
            total += (long) entry.getKey().getPrice() * entry.getValue().getValue();
        }
        return total;
    }

    @FXML
    private void handleOrderButton() {
        Map<FoodItemDTO, Integer> selectedItems = itemSpinners.entrySet().stream()
                .filter(entry -> entry.getValue().getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getValue()));

        if (selectedItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "خطا", "لطفاً حداقل یک آیتم از منو را انتخاب کنید.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "موفقیت", "سفارش شما با موفقیت ثبت شد (شبیه‌سازی شده).");
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
            Stage stage = new Stage();
            stage.setTitle("پرداخت با کارت");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "خطا", "خطا در باز کردن فرم پرداخت!");
        }
    }

    private void handleWalletPayment(long amount) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/approjectfrontend/WalletPayment-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("پرداخت با کیف پول");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "خطا", "خطا در باز کردن فرم پرداخت با کیف پول!");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}