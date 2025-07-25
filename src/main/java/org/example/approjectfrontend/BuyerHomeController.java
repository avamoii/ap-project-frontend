package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.FoodItemDTO;
import org.example.approjectfrontend.api.RestaurantDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class BuyerHomeController implements Initializable {
    @FXML
    private Button profileBtn;
    @FXML
    private Button homeBtn;
    @FXML
    private Button historyBtn;
    @FXML
    private TextField searchField;
    @FXML
    private VBox restaurantListVBox;
    // اضافه کردن لیست کامل رستوران‌ها
    private List<RestaurantDTO> allRestaurants;
    private List<FoodItemDTO> allFoodItems;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRestaurants();
        profileBtn.setOnAction(e -> goToProfile());
        historyBtn.setOnAction(e -> goToHistory());
        homeBtn.setDisable(true);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> handleSearch());

    }

    private void loadRestaurants() {
        restaurantListVBox.getChildren().clear();
        restaurantListVBox.setAlignment(Pos.CENTER);

        new Thread(() -> {
            ApiResponse response = ApiService.getVendors();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    restaurantListVBox.setAlignment(Pos.TOP_LEFT);
                    Gson gson = new Gson();
                    List<RestaurantDTO> restaurants = gson.fromJson(response.getBody(), new TypeToken<List<RestaurantDTO>>() {}.getType());
                    if (restaurants.isEmpty()) {
                        restaurantListVBox.getChildren().add(new Label("در حال حاضر هیچ رستورانی فعال نیست."));
                    } else {
                        for (RestaurantDTO restaurant : restaurants) {
                            HBox card = buildRestaurantCard(restaurant);
                            restaurantListVBox.getChildren().add(card);
                        }
                    }
                } else {
                    restaurantListVBox.getChildren().add(new Label("خطا در دریافت لیست رستوران‌ها."));
                }
            });
        }).start();
    }
    private void showRestaurants(List<RestaurantDTO> restaurants) {
        restaurantListVBox.getChildren().clear();
        if (restaurants == null || restaurants.isEmpty()) {
            restaurantListVBox.getChildren().add(new Label("در حال حاضر هیچ رستورانی فعال نیست."));
        } else {
            for (RestaurantDTO restaurant : restaurants) {
                HBox card = buildRestaurantCard(restaurant);
                restaurantListVBox.getChildren().add(card);
            }
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (allRestaurants == null) return;

        // رستوران‌هایی که با نام پیدا می‌شن
        Set<Long> restaurantIdsByName = allRestaurants.stream()
                .filter(r -> r.getName() != null && r.getName().toLowerCase().contains(searchText))
                .map(RestaurantDTO::getId)
                .collect(Collectors.toSet());

        // رستوران‌هایی که بر اساس غذا پیدا می‌شن
        Set<Long> restaurantIdsByFood = new HashSet<>();
        if (allFoodItems != null) {
            allFoodItems.stream()
                    .filter(food ->
                            (food.getName() != null && food.getName().toLowerCase().contains(searchText)) ||
                                    (food.getKeywords() != null && food.getKeywords().stream()
                                            .anyMatch(k -> k != null && k.toLowerCase().contains(searchText)))
                    )
                    .map(FoodItemDTO::getRestaurantId)
                    .forEach(restaurantIdsByFood::add);
        }

        // ادغام نتایج
        Set<Long> finalRestaurantIds = new HashSet<>(restaurantIdsByName);
        finalRestaurantIds.addAll(restaurantIdsByFood);

        List<RestaurantDTO> filtered;
        if (searchText.isEmpty()) {
            filtered = allRestaurants;
        } else {
            filtered = allRestaurants.stream()
                    .filter(r -> finalRestaurantIds.contains(r.getId()))
                    .toList();
        }

        showRestaurants(filtered);
    }


    private HBox buildRestaurantCard(RestaurantDTO restaurant) {
        HBox box = new HBox(10);
        box.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        box.setMinHeight(60);
        box.setAlignment(Pos.CENTER_LEFT);

        ImageView logoView = new ImageView();
        if (restaurant.getLogoBase64() != null && !restaurant.getLogoBase64().isEmpty()) {
            byte[] decodedBytes = Base64.getDecoder().decode(restaurant.getLogoBase64());
            logoView.setImage(new Image(new ByteArrayInputStream(decodedBytes)));
        }
        logoView.setFitHeight(40);
        logoView.setFitWidth(40);

        Label nameLabel = new Label(restaurant.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        box.getChildren().addAll(logoView, nameLabel);

        box.setOnMouseClicked(e -> openRestaurantPage(restaurant));
        return box;
    }

    private void openRestaurantPage(RestaurantDTO restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RestaurantPage-view.fxml"));
            Parent root = loader.load();
            RestaurantPageController controller = loader.getController();
            controller.setRestaurant(restaurant);

            // باز کردن صفحه رستوران در یک پنجره جدید (Stage)
            Stage stage = new Stage();
            stage.setTitle(restaurant.getName());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToProfile() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BuyerProfile-view.fxml"));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToHistory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BuyerHistory-view.fxml"));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}