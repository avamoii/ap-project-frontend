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
    private Button homeButton, myRestaurantButton, profileButton;
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
    private void handleRegisterOrUpdate() {
        if (editingRestaurant != null) {
            handleUpdate();
        } else {
            handleRegister();
        }
    }

    private void handleUpdate() {
        UpdateRestaurantRequest updateData = new UpdateRestaurantRequest();
        updateData.setName(nameField.getText().trim());
        updateData.setAddress(addressField.getText().trim());
        updateData.setPhone(phoneField.getText().trim());
        try {
            if (!taxFeeField.getText().trim().isEmpty()) updateData.setTaxFee(Integer.parseInt(taxFeeField.getText().trim()));
            if (!additionalFeeField.getText().trim().isEmpty()) updateData.setAdditionalFee(Integer.parseInt(additionalFeeField.getText().trim()));
        } catch (NumberFormatException e) {
            showMessage("مالیات و هزینه اضافی باید عدد باشند.", "red");
            return;
        }

        if (logoFile != null) {
            try {
                byte[] fileContent = Files.readAllBytes(logoFile.toPath());
                updateData.setLogoBase64(Base64.getEncoder().encodeToString(fileContent));
            } catch (IOException e) {
                showMessage("خطا در پردازش فایل لوگو.", "red");
                return;
            }
        }

        new Thread(() -> {
            ApiResponse response = ApiService.updateRestaurant(editingRestaurant.getId(), updateData);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    showMessage("رستوران با موفقیت آپدیت شد!", "green");
                } else {
                    showMessage("خطا در آپدیت رستوران: " + response.getBody(), "red");
                }
            });
        }).start();
    }

    private void handleRegister() {
        CreateRestaurantRequest requestData = new CreateRestaurantRequest();
        requestData.setName(nameField.getText().trim());
        requestData.setAddress(addressField.getText().trim());
        requestData.setPhone(phoneField.getText().trim());
        try {
            if (!taxFeeField.getText().trim().isEmpty()) requestData.setTaxFee(Integer.parseInt(taxFeeField.getText().trim()));
            if (!additionalFeeField.getText().trim().isEmpty()) requestData.setAdditionalFee(Integer.parseInt(additionalFeeField.getText().trim()));
        } catch (NumberFormatException e) {
            showMessage("مالیات و هزینه اضافی باید عدد باشند.", "red");
            return;
        }

        if (logoFile != null) {
            try {
                byte[] fileContent = Files.readAllBytes(logoFile.toPath());
                requestData.setLogoBase64(Base64.getEncoder().encodeToString(fileContent));
            } catch (IOException e) {
                showMessage("خطا در پردازش فایل لوگو.", "red");
                return;
            }
        }

        new Thread(() -> {
            ApiResponse response = ApiService.createRestaurant(requestData);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 201) {
                    showMessage("رستوران با موفقیت ثبت شد!", "green");
                    clearFields();
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

    private void clearFields() {
        nameField.clear();
        addressField.clear();
        phoneField.clear();
        taxFeeField.clear();
        additionalFeeField.clear();
        logoImageView.setImage(null);
        logoFile = null;
    }

    private void showMessage(String msg, String color) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
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