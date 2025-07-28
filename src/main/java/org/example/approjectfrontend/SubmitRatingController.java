package org.example.approjectfrontend;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.*;

public class SubmitRatingController {

    @FXML private Label orderIdLabel, ratingValueLabel, messageLabel;
    @FXML private Slider ratingSlider;
    @FXML private TextArea commentArea;
    @FXML private Button submitButton;

    private OrderDTO currentOrder;
    private Long editingRatingId = null; // برای تشخیص حالت ویرایش

    @FXML
    public void initialize() {
        ratingSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                ratingValueLabel.setText(String.format("%d", newVal.intValue())));
        ratingValueLabel.setText(String.format("%d", (int)ratingSlider.getValue()));
    }

    public void setOrderAndRating(OrderDTO order, Long ratingId) {
        this.currentOrder = order;
        this.editingRatingId = ratingId;
        orderIdLabel.setText("سفارش #" + order.getId());

        if (editingRatingId != null) {
            submitButton.setText("ویرایش نظر");
            loadRatingForEdit();
        } else {
            submitButton.setText("ثبت نهایی نظر");
        }
    }

    private void loadRatingForEdit() {
        new Thread(() -> {
            ApiResponse response = ApiService.getRatingDetails(editingRatingId);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    RatingDTO rating = new Gson().fromJson(response.getBody(), RatingDTO.class);
                    ratingSlider.setValue(rating.getScore());
                    commentArea.setText(rating.getComment());
                } else {
                    messageLabel.setText("خطا در دریافت اطلاعات نظر.");
                }
            });
        }).start();
    }

    @FXML
    private void handleSubmitRating() {
        if (currentOrder == null) {
            messageLabel.setText("خطا: اطلاعات سفارش موجود نیست.");
            return;
        }

        submitButton.setDisable(true);
        submitButton.setText("در حال ارسال...");

        if (editingRatingId == null) {
            // حالت ثبت نظر جدید
            int rating = (int) ratingSlider.getValue();
            String comment = commentArea.getText().trim();
            SubmitRatingRequest ratingRequest = new SubmitRatingRequest(currentOrder.getId(), rating, comment);

            new Thread(() -> {
                ApiResponse response = ApiService.submitRating(ratingRequest);
                Platform.runLater(() -> handleApiResponse(response, "نظر شما با موفقیت ثبت شد!"));
            }).start();
        } else {
            // حالت ویرایش نظر
            UpdateRatingRequest updateRequest = new UpdateRatingRequest();
            updateRequest.setRating((int) ratingSlider.getValue());
            updateRequest.setComment(commentArea.getText().trim());

            new Thread(() -> {
                ApiResponse response = ApiService.updateRating(editingRatingId, updateRequest);
                Platform.runLater(() -> handleApiResponse(response, "نظر شما با موفقیت ویرایش شد!"));
            }).start();
        }
    }

    private void handleApiResponse(ApiResponse response, String successMessage) {
        if (response.getStatusCode() == 200) {
            showAlertAndClose(Alert.AlertType.INFORMATION, "موفقیت", successMessage);
        } else {
            messageLabel.setText("خطا: " + response.getBody());
            submitButton.setDisable(false);
            submitButton.setText(editingRatingId == null ? "ثبت نهایی نظر" : "ویرایش نظر");
        }
    }

    private void showAlertAndClose(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}