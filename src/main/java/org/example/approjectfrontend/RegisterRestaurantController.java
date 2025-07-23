package org.example.approjectfrontend;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;

public class RegisterRestaurantController implements Initializable {
    @FXML
    private TextField nameField, addressField, phoneField, taxFeeField, additionalFeeField;
    @FXML
    private ImageView logoImageView;
    @FXML
    private Button chooseLogoButton, registerButton;
    @FXML
    private Label messageLabel;

    private File logoFile = null;
    private RestaurantDTO editingRestaurant = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void setRestaurantToEdit(RestaurantDTO restaurant) {
        this.editingRestaurant = restaurant;
        populateDataForEdit();
    }

    private void populateDataForEdit() {
        if (editingRestaurant == null) {
            registerButton.setText("ثبت رستوران");
            return;
        }

        registerButton.setText("ذخیره تغییرات");
        nameField.setText(editingRestaurant.getName());
        addressField.setText(editingRestaurant.getAddress());
        phoneField.setText(editingRestaurant.getPhone());
        if (editingRestaurant.getTaxFee() != null) taxFeeField.setText(editingRestaurant.getTaxFee().toString());
        if (editingRestaurant.getAdditionalFee() != null) additionalFeeField.setText(editingRestaurant.getAdditionalFee().toString());

        if (editingRestaurant.getLogoBase64() != null && !editingRestaurant.getLogoBase64().isEmpty()) {
            byte[] decodedBytes = Base64.getDecoder().decode(editingRestaurant.getLogoBase64());
            logoImageView.setImage(new Image(new ByteArrayInputStream(decodedBytes)));
        }
    }

    @FXML
    private void handleRegisterOrUpdate(ActionEvent event) {
        System.out.println("DEBUG: handleRegisterOrUpdate method entered.");
        if (editingRestaurant != null) {
            handleUpdate(event);
        } else {
            handleRegister(event);
        }
    }

    private void handleUpdate(ActionEvent event) {
        // این بخش بعداً تکمیل می‌شود
        System.out.println("DEBUG: handleUpdate method called.");
        showMessage("منطق ویرایش هنوز پیاده‌سازی نشده است.", "blue");
    }

    private void handleRegister(ActionEvent event) {
        System.out.println("DEBUG: handleRegister method entered.");

        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            System.out.println("DEBUG: Validation failed: Required fields are empty.");
            showMessage("نام، آدرس و تلفن رستوران اجباری هستند.", "red");
            return;
        }

        System.out.println("DEBUG: Basic validation passed.");
        CreateRestaurantRequest requestData = new CreateRestaurantRequest();
        requestData.setName(name);
        requestData.setAddress(address);
        requestData.setPhone(phone);

        try {
            if (!taxFeeField.getText().trim().isEmpty()) {
                requestData.setTaxFee(Integer.parseInt(taxFeeField.getText().trim()));
            }
            if (!additionalFeeField.getText().trim().isEmpty()) {
                requestData.setAdditionalFee(Integer.parseInt(additionalFeeField.getText().trim()));
            }
        } catch (NumberFormatException e) {
            System.out.println("DEBUG: Validation failed: Fees are not numbers.");
            showMessage("مالیات و هزینه اضافی باید عدد باشند.", "red");
            return;
        }

        if (logoFile != null) {
            try {
                System.out.println("DEBUG: Processing logo file...");
                byte[] fileContent = Files.readAllBytes(logoFile.toPath());
                requestData.setLogoBase64(Base64.getEncoder().encodeToString(fileContent));
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("خطا در پردازش فایل لوگو.", "red");
                return;
            }
        }

        System.out.println("DEBUG: All data collected. Starting API call thread...");
        new Thread(() -> {
            System.out.println("DEBUG: API call thread started.");
            ApiResponse response = ApiService.createRestaurant(requestData);
            System.out.println("DEBUG: API call finished with status code: " + response.getStatusCode());
            Platform.runLater(() -> {
                if (response.getStatusCode() == 201) {
                    showMessage("رستوران با موفقیت ثبت شد! در حال بازگشت به صفحه اصلی...", "green");
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            Platform.runLater(() -> {
                                try {
                                    goToHome(event);
                                } catch (IOException e) { e.printStackTrace(); }
                            });
                        } catch (InterruptedException e) { e.printStackTrace(); }
                    }).start();
                } else {
                    showMessage("خطا در ثبت رستوران: " + response.getBody(), "red");
                }
            });
        }).start();
    }

    @FXML
    private void chooseLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("تصاویر", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(chooseLogoButton.getScene().getWindow());
        if (selectedFile != null) {
            logoFile = selectedFile;
            logoImageView.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    private void showMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    @FXML
    private void goToHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SellerHome-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void goToProfile(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SellerProfile-view.fxml"));
        Scene scene = ((Node) event.getSource()).getScene();
        scene.setRoot(root);
    }
}