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
import javafx.scene.control.Alert; // Alert اضافه شد
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
                        restaurantsVBox.setAlignment(Pos.CENTER);
                        Label infoLabel = new Label("شما هنوز رستورانی ثبت نکرده‌اید.");
                        restaurantsVBox.getChildren().add(infoLabel);
                    } else {
                        for (RestaurantDTO restaurant : restaurants) {
                            HBox card = createRestaurantCard(restaurant);
                            restaurantsVBox.getChildren().add(card);
                        }
                    }
                } else {
                    restaurantsVBox.setAlignment(Pos.CENTER);
                    restaurantsVBox.getChildren().add(new Label("خطا در دریافت لیست رستوران‌ها."));
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

        // --- شروع تغییرات ---
        ContextMenu contextMenu = new ContextMenu();

        // آیتم جدید برای مشاهده سفارشات
        MenuItem menuViewOrders = new MenuItem("مشاهده سفارشات");
        menuViewOrders.setOnAction(event -> openRestaurantOrders(restaurant)); // فراخوانی متد جدید

        // آیتم‌های موجود
        MenuItem menuManage = new MenuItem("مشاهده و مدیریت منو");
        MenuItem menuInfo = new MenuItem("ویرایش اطلاعات رستوران");

        // اضافه کردن همه آیتم‌ها به منوی کلیک راست
        contextMenu.getItems().addAll(menuViewOrders, menuManage, menuInfo); // menuViewOrders اضافه شد

        box.setOnMouseClicked(e -> contextMenu.show(box, e.getScreenX(), e.getScreenY()));

        // Action ها برای آیتم‌های موجود (بدون تغییر)
        menuManage.setOnAction(ev -> openRestaurantMenu(restaurant));
        menuInfo.setOnAction(ev -> openRestaurantInfo(restaurant));
        // --- پایان تغییرات ---

        return box;
    }

    // متد باز کردن صفحه سفارشات رستوران
    private void openRestaurantOrders(RestaurantDTO restaurant) {
        try {
            // مسیر فایل FXML صفحه سفارشات
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/approjectfrontend/SellerOrders-view.fxml"));
            Parent root = loader.load();

            // دریافت کنترلر صفحه سفارشات
            SellerOrderController controller = loader.getController();
            // ارسال اطلاعات رستوران انتخاب شده به کنترلر سفارشات
            controller.setRestaurant(restaurant);

            // گرفتن پنجره فعلی (Stage)
            // از restaurantsVBox استفاده می‌کنیم چون در این کلاس تعریف شده و گره‌ای از صحنه فعلی است.
            Stage stage = (Stage) restaurantsVBox.getScene().getWindow();
            // تغییر صحنه (Scene) به صفحه سفارشات
            stage.setScene(new Scene(root));
            // نمایش پنجره
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // نمایش یک پیام خطا به کاربر در صورت بروز مشکل
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("خطای بارگذاری");
            alert.setHeaderText(null);
            alert.setContentText("امکان باز کردن صفحه سفارشات وجود ندارد. لطفاً دوباره تلاش کنید.");
            alert.showAndWait();
        }
    }


    private void openRestaurantMenu(RestaurantDTO restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/approjectfrontend/MenuManager-view.fxml"));
            Parent root = loader.load();
            MenuManagerController controller = loader.getController();
            controller.setRestaurant(restaurant);
            Stage stage = (Stage) restaurantsVBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openRestaurantInfo(RestaurantDTO restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/approjectfrontend/RegisterRestaurant-view.fxml"));
            Parent root = loader.load();
            RegisterRestaurantController controller = loader.getController();
            controller.setRestaurantToEdit(restaurant);
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
