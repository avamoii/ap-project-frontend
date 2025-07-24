package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.RestaurantDTO;

import java.io.IOException;

public class MenuManagerController {
    @FXML
    private Label restaurantNameLabel;
    @FXML
    private ListView<String> menusListView;
    @FXML
    private TextField newMenuTitleField;
    @FXML
    private Label messageLabel;

    private RestaurantDTO currentRestaurant;
    private final ObservableList<String> menuTitles = FXCollections.observableArrayList();

    public void setRestaurant(RestaurantDTO restaurant) {
        this.currentRestaurant = restaurant;
        restaurantNameLabel.setText("مدیریت منوهای: " + restaurant.getName());
        loadMenuTitles();

        menusListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedMenu = menusListView.getSelectionModel().getSelectedItem();
                if (selectedMenu != null) {
                    try {
                        goToFoodItemsPage(selectedMenu, (Node) event.getSource());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void loadMenuTitles() {
        menuTitles.clear();
        new Thread(() -> {
            ApiResponse response = ApiService.getRestaurantMenu(currentRestaurant.getId());
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    JsonObject body = new Gson().fromJson(response.getBody(), JsonObject.class);
                    JsonArray titles = body.getAsJsonArray("menu_titles");
                    if (titles != null) {
                        for (int i = 0; i < titles.size(); i++) {
                            menuTitles.add(titles.get(i).getAsString());
                        }
                    }
                    menusListView.setItems(menuTitles);
                    if (menuTitles.isEmpty()) {
                        menusListView.setPlaceholder(new Label("هیچ منویی یافت نشد. اولین منو را بسازید."));
                    }
                }
            });
        }).start();
    }

    @FXML
    private void handleCreateMenu() {
        String title = newMenuTitleField.getText().trim();
        if (title.isEmpty()) return;
        new Thread(() -> {
            ApiResponse response = ApiService.createMenu(currentRestaurant.getId(), title);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    newMenuTitleField.clear();
                    loadMenuTitles();
                } else {
                    showMessage("خطا: " + response.getBody(), "red");
                }
            });
        }).start();
    }

    private void goToFoodItemsPage(String menuTitle, Node sourceNode) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FoodItemManager-view.fxml"));
        Parent root = loader.load();
        FoodItemManagerController controller = loader.getController();
        controller.setRestaurantAndMenu(currentRestaurant, menuTitle);
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void handleGoBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SellerHome-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void showMessage(String text, String color) {
        messageLabel.setText(text);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }
}