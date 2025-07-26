package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.OrderDTO;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * کنترلر برای نمایش تاریخچه سفارشات خریدار.
 * این صفحه لیست سفارشات را نمایش می‌دهد و امکان ناوبری به بخش‌های دیگر را فراهم می‌کند.
 */
public class BuyerHistoryController implements Initializable {
    @FXML
    private Button profileBtn;
    @FXML
    private Button homeBtn;
    @FXML
    private Button historyBtn;
    @FXML
    private VBox ordersVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // تنظیم رویدادهای کلیک برای دکمه‌های ناوبری
        profileBtn.setOnAction(e -> navigateToPage("BuyerProfile-view.fxml"));
        homeBtn.setOnAction(e -> navigateToPage("BuyerHome-view.fxml"));
        historyBtn.setOnAction(e -> showHistoryChoiceDialog()); // کلیک روی دکمه تاریخچه هم دیالوگ را نشان می‌دهد

        // استایل‌دهی به دکمه فعال (تاریخچه)
        historyBtn.setStyle("-fx-background-color: #1e7e44;");

        // به جای بارگذاری مستقیم، دیالوگ انتخاب را نمایش می‌دهیم تا کاربر تصمیم بگیرد.
        // این کار را در Platform.runLater قرار می‌دهیم تا اطمینان حاصل شود که صحنه به طور کامل آماده شده است.
        Platform.runLater(this::showHistoryChoiceDialog);
    }

    /**
     * یک دیالوگ برای انتخاب بین تاریخچه سفارشات و تراکنش‌ها نمایش می‌دهد و بر اساس انتخاب کاربر عمل می‌کند.
     */
    private void showHistoryChoiceDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("انتخاب نوع تاریخچه");
        alert.setHeaderText("کدام تاریخچه را می‌خواهید مشاهده کنید؟");
        alert.setContentText("لطفا یک گزینه را انتخاب کنید:");

        ButtonType ordersBtn = new ButtonType("تاریخچه سفارشات");
        ButtonType transactionsBtn = new ButtonType("تاریخچه تراکنش‌ها");
        ButtonType cancelBtn = new ButtonType("بازگشت", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(ordersBtn, transactionsBtn, cancelBtn);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == ordersBtn) {
                // اگر کاربر تاریخچه سفارشات را انتخاب کرد، محتوای آن را بارگذاری می‌کنیم.
                loadOrderHistory();
            } else if (result.get() == transactionsBtn) {
                // اگر تاریخچه تراکنش‌ها انتخاب شد، به صفحه مربوطه می‌رویم.
                navigateToPage("TransactionHistory-view.fxml");
            } else {
                // اگر کاربر "بازگشت" را زد، به صفحه اصلی برمی‌گردیم.
                navigateToPage("BuyerHome-view.fxml");
            }
        } else {
            // اگر کاربر دیالوگ را بدون انتخاب بست، باز هم به صفحه اصلی برمی‌گردیم.
            navigateToPage("BuyerHome-view.fxml");
        }
    }

    /**
     * داده‌های تاریخچه سفارشات را از API دریافت کرده و در VBox نمایش می‌دهد.
     */
    private void loadOrderHistory() {
        ordersVBox.getChildren().clear();
        ordersVBox.setAlignment(Pos.CENTER);
        ordersVBox.getChildren().add(new ProgressIndicator()); // نمایش لودینگ

        new Thread(() -> {
            ApiResponse response = ApiService.getOrderHistory();
            Platform.runLater(() -> {
                ordersVBox.getChildren().clear(); // حذف لودینگ
                if (response.getStatusCode() == 200) {
                    ordersVBox.setAlignment(Pos.TOP_LEFT);
                    Gson gson = new Gson();
                    List<OrderDTO> orders = gson.fromJson(response.getBody(), new TypeToken<List<OrderDTO>>() {}.getType());
                    if (orders == null || orders.isEmpty()) {
                        ordersVBox.setAlignment(Pos.CENTER);
                        ordersVBox.getChildren().add(new Label("شما هنوز هیچ سفارشی ثبت نکرده‌اید."));
                    } else {
                        for (OrderDTO order : orders) {
                            HBox card = buildOrderCard(order);
                            ordersVBox.getChildren().add(card);
                        }
                    }
                } else {
                    ordersVBox.setAlignment(Pos.CENTER);
                    ordersVBox.getChildren().add(new Label("خطا در دریافت تاریخچه سفارشات."));
                }
            });
        }).start();
    }

    /**
     * یک کارت گرافیکی برای نمایش اطلاعات یک سفارش ایجاد می‌کند.
     * @param order آبجکت سفارش
     * @return یک HBox که کارت سفارش است.
     */
    private HBox buildOrderCard(OrderDTO order) {
        HBox box = new HBox(10);
        box.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8;");
        box.setAlignment(Pos.CENTER_LEFT);

        Label orderInfo = new Label(String.format("سفارش #%d - وضعیت: %s - مبلغ: %,d تومان",
                order.getId(), getStatusInPersian(order.getStatus()), order.getPayPrice()));
        orderInfo.setStyle("-fx-font-size: 14px;");

        box.getChildren().add(orderInfo);
        // با کلیک روی هر کارت، جزئیات کامل آن نمایش داده می‌شود
        box.setOnMouseClicked(e -> showOrderDetails(order.getId()));

        return box;
    }

    /**
     * جزئیات کامل یک سفارش را در یک پنجره Alert نمایش می‌دهد.
     * @param orderId شناسه سفارش
     */
    private void showOrderDetails(long orderId) {
        new Thread(() -> {
            ApiResponse response = ApiService.getOrderDetails(orderId);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("جزئیات سفارش #" + orderId);
                    alert.setHeaderText("اطلاعات کامل سفارش شما:");

                    // برای نمایش زیباتر JSON
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    Object jsonObject = gson.fromJson(response.getBody(), Object.class);
                    String prettyJson = gson.toJson(jsonObject);

                    TextArea textArea = new TextArea(prettyJson);
                    textArea.setEditable(false);
                    textArea.setWrapText(true);

                    alert.getDialogPane().setContent(textArea);
                    alert.setResizable(true);
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("خطا");
                    alert.setContentText("خطا در دریافت جزئیات سفارش: " + response.getBody());
                    alert.showAndWait();
                }
            });
        }).start();
    }

    /**
     * متد کمکی برای ناوبری بین صفحات مختلف.
     * @param fxmlFile نام فایل FXML مقصد
     */
    private void navigateToPage(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * وضعیت سفارش را از انگلیسی به فارسی ترجمه می‌کند.
     * @param status وضعیت انگلیسی
     * @return معادل فارسی
     */
    private String getStatusInPersian(String status) {
        if (status == null) return "نامشخص";
        return switch (status.toUpperCase()) {
            case "SUBMITTED" -> "ثبت شده";
            case "UNPAID_AND_CANCELLED" -> "پرداخت نشده و لغو شده";
            case "WAITING_VENDOR" -> "در انتظار تایید رستوران";
            case "CANCELLED" -> "لغو شده";
            case "FINDING_COURIER" -> "در جستجوی پیک";
            case "ON_THE_WAY" -> "در مسیر";
            case "COMPLETED" -> "تکمیل شده";
            default -> status;
        };
    }
}
