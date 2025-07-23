package org.example.approjectfrontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public class BuyerHomeController {
    @FXML private Button profileBtn;
    @FXML private Button homeBtn;
    @FXML
    private Button historyBtn;
    @FXML private TextField searchField;
    @FXML private VBox restaurantListVBox;
    @FXML
    public void initialize() {
        if (DataManager.restaurants.isEmpty()) {
            // ساخت آیتم‌های فهرست غذا
            RestaurantMenuItem item1 = new RestaurantMenuItem(
                    "کباب کوبیده",
                    "کوبیده گوشت گوسفندی داغ برنجی",
                    "135000",    // قیمت
                    "15",        // موجودی
                    "کباب،برنج"
            );
            RestaurantMenuItem item2 = new RestaurantMenuItem(
                    "زرشک‌پلو با مرغ",
                    "مرغ سرخ‌شده با برنج و زرشک ویژه",
                    "145000",
                    "12",
                    "مرغ،زرشک،برنج"
            );
            // سازنده رستوران (با یا بدون لوگو)
            Restaurant rest = new Restaurant("کترینگ تستی", null);
            rest.setMenuItems(List.of(item1, item2));

            DataManager.restaurants.add(rest); // به لیست اضافه می‌شود
        }

        loadRestaurants(DataManager.restaurants);

        loadRestaurants(DataManager.restaurants);
        profileBtn.setOnAction(e -> goToProfile());
        historyBtn.setOnAction(e -> handleHistoryClick());
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
        }
        logoView.setFitHeight(40);
        logoView.setFitWidth(40);
        logoView.setPreserveRatio(true);

        // نام رستوران
        Label nameLabel = new Label(restaurant.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        box.getChildren().addAll(logoView, nameLabel);
        box.setOnMouseClicked(e -> openRestaurantPage(restaurant));
        //  کمی استایل برای روشن شدن هنگام هاور:
        box.setOnMouseEntered(e -> box.setStyle("-fx-background-color: #ddd; -fx-padding: 10 15 10 15; -fx-background-radius: 8;"));
        box.setOnMouseExited(e -> box.setStyle("-fx-background-color: #eee; -fx-padding: 10 15 10 15; -fx-background-radius: 8;"));

        return box;
    }
    private void openRestaurantPage(Restaurant restaurant) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("RestaurantPage-view.fxml"));
            Parent root = loader.load();

            // گرفتن کنترلر صفحه دوم
            RestaurantPageController controller = loader.getController();
            controller.setRestaurant(restaurant);

            // بازکردن در یک Stage جدید (پنجره جدید):
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
            // فرض: دستیابی به stage از طریق یک کامپوننت صفحه فعلی
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace(); // برای رفع خطاها
        }
    }

    private void handleHistoryClick() {
        // ساخت یک دیالوگ با دو گزینه
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("انتخاب نوع تاریخچه");
        alert.setHeaderText("کدام تاریخچه را می‌خواهید مشاهده کنید؟");

        ButtonType ordersBtn = new ButtonType("تاریخچه سفارشات");
        ButtonType transactionsBtn = new ButtonType("تاریخچه تراکنش‌ها");
        ButtonType cancelBtn = new ButtonType("انصراف", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(ordersBtn, transactionsBtn, cancelBtn);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == ordersBtn) {
                goToOrderHistory();
            } else if (result.get() == transactionsBtn) {
                goToTransactionHistory();
            }
        }
    }
    private void goToOrderHistory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("OrderHistory-view.fxml"));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToTransactionHistory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("TransactionHistory-view.fxml"));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSearch() {
        String query = searchField.getText();
        // اینجا کار مورد نظر برای سرچ را انجام بده
        System.out.println("User searched for: " + query);

    }
}
