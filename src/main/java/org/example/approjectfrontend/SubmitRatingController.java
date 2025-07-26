package org.example.approjectfrontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.OrderDTO;
import org.example.approjectfrontend.api.SubmitRatingRequest;

public class SubmitRatingController {

    @FXML private Label orderIdLabel;
    @FXML private Slider ratingSlider;
    @FXML private Label ratingValueLabel;
    @FXML private TextArea commentArea;
    @FXML private Button submitButton;
    @FXML private Label messageLabel;

    private OrderDTO currentOrder;

    @FXML
    public void initialize() {
        // نمایش مقدار فعلی اسلایدر
        ratingSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                ratingValueLabel.setText(String.format("%d", newVal.intValue())));
        ratingValueLabel.setText(String.format("%d", (int)ratingSlider.getValue()));
    }

    /**
     * این متد از صفحه تاریخچه فراخوانی می‌شود تا اطلاعات سفارش به این صفحه منتقل شود.
     */
    public void setOrder(OrderDTO order) {
        this.currentOrder = order;
        orderIdLabel.setText("سفارش #" + order.getId());
    }

    @FXML
    private void handleSubmitRating() {
        if (currentOrder == null) {
            messageLabel.setText("خطا: اطلاعات سفارش موجود نیست.");
            return;
        }

        int rating = (int) ratingSlider.getValue();
        String comment = commentArea.getText().trim();

        if (comment.isEmpty()) {
            messageLabel.setText("لطفاً نظر خود را وارد کنید.");
            return;
        }

        submitButton.setDisable(true);
        submitButton.setText("در حال ارسال...");

        SubmitRatingRequest ratingRequest = new SubmitRatingRequest(currentOrder.getId(), rating, comment);

        new Thread(() -> {
            ApiResponse response = ApiService.submitRating(ratingRequest);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    showAlertAndClose(Alert.AlertType.INFORMATION, "موفقیت", "نظر شما با موفقیت ثبت شد!");
                } else {
                    messageLabel.setText("خطا: " + response.getBody());
                    submitButton.setDisable(false);
                    submitButton.setText("ثبت نهایی نظر");
                }
            });
        }).start();
    }

    private void showAlertAndClose(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();

        // بستن پنجره پس از نمایش پیام
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}
