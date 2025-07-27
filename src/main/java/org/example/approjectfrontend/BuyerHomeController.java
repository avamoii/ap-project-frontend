package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.FoodItemDTO;
import org.example.approjectfrontend.api.RestaurantDTO;
import org.example.approjectfrontend.util.SessionManager;

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
    @FXML
    private VBox favoritesVBox; // [جدید] اتصال به VBox علاقه‌مندی‌ها در FXML

    private List<RestaurantDTO> allRestaurants;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRestaurants();
        loadFavorites(); // [جدید] فراخوانی متد برای بارگذاری علاقه‌مندی‌ها

        homeBtn.setDisable(true);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> handleSearch());
    }

    // [اصلاح] متدهای زیر برای رفع خطای LoadException اضافه شده‌اند
    @FXML
    private void goToProfile(ActionEvent event) {
        navigateToPage(event, "BuyerProfile-view.fxml");
    }

    @FXML
    private void goToHistory(ActionEvent event) {
        showHistoryChoiceDialog();
    }

    @FXML
    private void logout(ActionEvent event) {
        SessionManager.getInstance().clear();
        new Thread(ApiService::logout).start();
        navigateToPage(event, "login-view.fxml");
    }

    private void showHistoryChoiceDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("انتخاب نوع تاریخچه");
        alert.setHeaderText("کدام تاریخچه را می‌خواهید مشاهده کنید؟");
        alert.setContentText("لطفا یک گزینه را انتخاب کنید:");

        ButtonType ordersBtn = new ButtonType("تاریخچه سفارشات");
        ButtonType transactionsBtn = new ButtonType("تاریخچه تراکنش‌ها");
        ButtonType cancelBtn = new ButtonType("انصراف", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(ordersBtn, transactionsBtn, cancelBtn);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == ordersBtn) {
                navigateToPage(new ActionEvent(historyBtn, null), "OrderHistory-view.fxml");
            } else if (result.get() == transactionsBtn) {
                navigateToPage(new ActionEvent(historyBtn, null), "TransactionHistory-view.fxml");
            }
        }
    }

    private void loadRestaurants() {
        restaurantListVBox.getChildren().clear();
        restaurantListVBox.setAlignment(Pos.CENTER);
        restaurantListVBox.getChildren().add(new ProgressIndicator());

        new Thread(() -> {
            ApiResponse response = ApiService.getVendors();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    allRestaurants = gson.fromJson(response.getBody(), new TypeToken<List<RestaurantDTO>>() {}.getType());
                    displayRestaurants(allRestaurants, restaurantListVBox, "رستورانی یافت نشد.");
                } else {
                    restaurantListVBox.getChildren().clear();
                    restaurantListVBox.getChildren().add(new Label("خطا در دریافت لیست رستوران‌ها."));
                }
            });
        }).start();
    }

    // [جدید] متد برای بارگذاری و نمایش رستوران‌های مورد علاقه
    private void loadFavorites() {
        favoritesVBox.getChildren().clear();
        favoritesVBox.setAlignment(Pos.CENTER);
        favoritesVBox.getChildren().add(new ProgressIndicator());

        new Thread(() -> {
            ApiResponse response = ApiService.getFavorites();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    List<RestaurantDTO> favoriteRestaurants = gson.fromJson(response.getBody(), new TypeToken<List<RestaurantDTO>>() {}.getType());
                    displayRestaurants(favoriteRestaurants, favoritesVBox, "هنوز رستوران مورد علاقه‌ای اضافه نکرده‌اید.");
                } else {
                    favoritesVBox.getChildren().clear();
                    favoritesVBox.getChildren().add(new Label("خطا در دریافت علاقه‌مندی‌ها."));
                }
            });
        }).start();
    }

    // [اصلاح] این متد اکنون عمومی‌تر شده تا برای هر دو لیست استفاده شود
    private void displayRestaurants(List<RestaurantDTO> restaurants, VBox container, String emptyMessage) {
        container.getChildren().clear();
        if (restaurants == null || restaurants.isEmpty()) {
            container.setAlignment(Pos.CENTER);
            container.getChildren().add(new Label(emptyMessage));
        } else {
            container.setAlignment(Pos.TOP_LEFT);
            for (RestaurantDTO restaurant : restaurants) {
                HBox card = buildRestaurantCard(restaurant);
                container.getChildren().add(card);
            }
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (allRestaurants == null) return;

        if (searchText.isEmpty()) {
            displayRestaurants(allRestaurants, restaurantListVBox, "رستورانی یافت نشد.");
            return;
        }

        List<RestaurantDTO> filteredRestaurants = allRestaurants.stream()
                .filter(r -> r.getName() != null && r.getName().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        displayRestaurants(filteredRestaurants, restaurantListVBox, "هیچ رستورانی با این نام یافت نشد.");
    }

    private HBox buildRestaurantCard(RestaurantDTO restaurant) {
        HBox box = new HBox(10);
        box.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        box.setMinHeight(60);
        box.setAlignment(Pos.CENTER_LEFT);

        ImageView logoView = new ImageView();
        if (restaurant.getLogoBase64() != null && !restaurant.getLogoBase64().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(restaurant.getLogoBase64());
                logoView.setImage(new Image(new ByteArrayInputStream(decodedBytes)));
            } catch (Exception e) {
                // Handle error
            }
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

            Stage stage = new Stage();
            stage.setTitle(restaurant.getName());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToPage(ActionEvent event, String fxmlFile) {
        try {
            URL fxmlLocation = getClass().getResource(fxmlFile);
            if (fxmlLocation == null) {
                System.err.println("Could not find FXML file: " + fxmlFile);
                return;
            }

            Parent root = FXMLLoader.load(fxmlLocation);
            Node sourceNode = (Node) event.getSource();
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
