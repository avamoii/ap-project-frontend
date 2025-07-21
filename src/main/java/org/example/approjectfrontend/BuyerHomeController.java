package org.example.approjectfrontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class BuyerHomeController {
    @FXML private Button profileBtn;
    @FXML private Button homeBtn;
    @FXML
    private Button historyBtn;
    @FXML private TextField searchField;
    @FXML private VBox restaurantListVBox;
    @FXML
    public void initialize() {
        loadRestaurants(DataManager.restaurants);
        profileBtn.setOnAction(e -> goToProfile());
        historyBtn.setOnAction(e -> goToHistory());
        searchField.setOnAction(e -> doSearch());
    }
    // این متد، لیست را داینامیک می‌سازد:
    private void loadRestaurants(List<Restaurant> restaurantList) {
        restaurantListVBox.getChildren().clear();
        for (Restaurant r : restaurantList) {
            HBox card = buildRestaurantCard(r);
            restaurantListVBox.getChildren().add(card);
        }
    }

    // ساخت هر کارت مستطیلی با لوگو و نام
    private HBox buildRestaurantCard(Restaurant restaurant) {
        HBox box = new HBox(10); // فاصله بین لوگو و متن‌ها
        box.setStyle("-fx-background-color: #eee; -fx-padding: 10 15 10 15; -fx-background-radius: 8;");
        box.setMinHeight(60);

        // لوگو
        ImageView logoView = new ImageView();
        if (restaurant.getLogo() != null) {
            logoView.setImage(restaurant.getLogo());
        } else {
            logoView.setImage(new Image("file:nologo.png")); // عکس پیش‌فرض
        }
        logoView.setFitHeight(40);
        logoView.setFitWidth(40);
        logoView.setPreserveRatio(true);

        // نام رستوران
        Label nameLabel = new Label(restaurant.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        box.getChildren().addAll(logoView, nameLabel);

        return box;
    }


    private void goToProfile() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BuyerProfile-view.fxml"));
            // فرض: دستیابی به stage از طریق یک کامپوننت صفحه فعلی
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace(); // برای رفع خطاها
        }
    }

    private void goToHistory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("BuyerHistory-view.fxml"));
            // فرض: دستیابی به stage از طریق یک کامپوننت صفحه فعلی
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace(); // برای رفع خطاها
        }
    }
    private void doSearch() {
        String query = searchField.getText();
        // اینجا کار مورد نظر برای سرچ را انجام بده
        System.out.println("User searched for: " + query);

    }
}
