package org.example.approjectfrontend;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SellerHomeController implements Initializable {
    @FXML
    private Button homeButton;
    @FXML
    private Button myRestaurantButton;
    @FXML
    private Button profileButton;
    @FXML
    private VBox restaurantsVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        restaurantsVBox.getChildren().clear();

        for (Restaurant restaurant : DataManager.restaurants) {
            HBox card = createRestaurantCard(restaurant);
            restaurantsVBox.getChildren().add(card);
        } //اپدیت لیست داینامیک بعد از حذف یا اضافه رستوران بدون نیاز به رفرش
        DataManager.restaurants.addListener((ListChangeListener<Restaurant>) change -> {
            restaurantsVBox.getChildren().clear();
            for (Restaurant restaurant : DataManager.restaurants) {
                HBox card = createRestaurantCard(restaurant);
                restaurantsVBox.getChildren().add(card);
            }
        });
    }
    private HBox createRestaurantCard(Restaurant restaurant) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView(restaurant.getLogo());
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        Label nameLabel = new Label(restaurant.getName());

        box.getChildren().addAll(imageView, nameLabel);

        // استایل ساده
        box.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
        // ---- اضافه کردن ContextMenu ----
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuMenu = new MenuItem("ورود به منو رستوران");
        MenuItem menuInfo = new MenuItem("اطلاعات رستوران");
        contextMenu.getItems().addAll(menuMenu, menuInfo);

        // روی هر کلیک (چپ یا راست) کارت منو باز شود
        box.setOnMouseClicked(e -> {
            if (e.getButton().name().equals("PRIMARY") || e.getButton().name().equals("SECONDARY")) {
                contextMenu.show(box, e.getScreenX(), e.getScreenY());
            }
        });

        // اکشن برای هر گزینه
        menuMenu.setOnAction(ev -> openRestaurantMenu(restaurant));
        menuInfo.setOnAction(ev -> openRestaurantInfo(restaurant));

        return box;
    }
    private void openRestaurantMenu(Restaurant restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RestaurantMenu-view.fxml"));
            Parent root = loader.load();

            RestaurantMenuController controller = loader.getController();
            controller.setRestaurant(restaurant);

            Scene scene = restaurantsVBox.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openRestaurantInfo(Restaurant restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterRestaurant-view.fxml"));
            Parent root = loader.load();

            RegisterRestaurantController controller = loader.getController();
            controller.setRestaurant(restaurant);

            Scene scene = restaurantsVBox.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goToMyRestaurant(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("RegisterRestaurant-view.fxml"));
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