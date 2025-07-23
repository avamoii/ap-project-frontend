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
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.RestaurantDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;

public class SellerHomeController implements Initializable {
    @FXML
    private VBox restaurantsVBox;
    @FXML
    private Button addNewRestaurantButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadRestaurantsFromServer();
    }

    private void loadRestaurantsFromServer() {
        restaurantsVBox.getChildren().clear();
        restaurantsVBox.setAlignment(Pos.TOP_LEFT);

        new Thread(() -> {
            ApiResponse response = ApiService.getMyRestaurants();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    List<RestaurantDTO> restaurants = gson.fromJson(response.getBody(), new TypeToken<List<RestaurantDTO>>() {}.getType());

                    if (restaurants.isEmpty()) {
                        Label infoLabel = new Label("You haven't registered any restaurants yet.");
                        infoLabel.setStyle("-fx-font-size: 16px;");
                        restaurantsVBox.getChildren().add(infoLabel);
                    } else {
                        for (RestaurantDTO restaurant : restaurants) {
                            HBox card = createRestaurantCard(restaurant);
                            restaurantsVBox.getChildren().add(card);
                        }
                    }
                } else {
                    restaurantsVBox.getChildren().add(new Label("Error fetching restaurant list."));
                }
            });
        }).start();
    }

    private HBox createRestaurantCard(RestaurantDTO restaurant) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
        ImageView imageView = new ImageView();
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        if (restaurant.getLogoBase64() != null && !restaurant.getLogoBase64().isEmpty()) {
            byte[] decodedBytes = Base64.getDecoder().decode(restaurant.getLogoBase64());
            imageView.setImage(new Image(new ByteArrayInputStream(decodedBytes)));
        }
        Label nameLabel = new Label(restaurant.getName());
        box.getChildren().addAll(imageView, nameLabel);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuMenu = new MenuItem("Enter Restaurant Menu");
        MenuItem menuInfo = new MenuItem("Edit Restaurant Info");
        contextMenu.getItems().addAll(menuMenu, menuInfo);
        box.setOnMouseClicked(e -> contextMenu.show(box, e.getScreenX(), e.getScreenY()));
        menuMenu.setOnAction(ev -> openRestaurantMenu(restaurant));
        menuInfo.setOnAction(ev -> openRestaurantInfo(restaurant));
        return box;
    }

    // --- MISSING METHOD 1 ADDED HERE ---
    private void openRestaurantMenu(RestaurantDTO restaurant) {
        // The logic for navigating to the menu page will be implemented here in the future.
        System.out.println("Opening menu for: " + restaurant.getName());
    }

    // --- MISSING METHOD 2 ADDED HERE ---
    private void openRestaurantInfo(RestaurantDTO restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/approjectfrontend/RegisterRestaurant-view.fxml"));
            Parent root = loader.load();
            RegisterRestaurantController controller = loader.getController();
            controller.setRestaurantToEdit(restaurant); // This method now exists
            Stage stage = (Stage) restaurantsVBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToRegisterRestaurantPage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("RegisterRestaurant-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void goToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SellerHome-view.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(root);
    }

    @FXML
    private void goToProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SellerProfile-view.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(root);
    }
}