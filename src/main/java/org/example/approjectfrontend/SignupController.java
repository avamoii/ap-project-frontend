package org.example.approjectfrontend;
import org.example.approjectfrontend.MockUser;
import org.example.approjectfrontend.UserDataStore;
import org.example.approjectfrontend.AddressController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;


public class SignupController {

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField phoneField;
    @FXML
    private Label messageLabel;
    @FXML
    private RadioButton buyerRadio;
    @FXML
    private RadioButton sellerRadio;
    @FXML
    private RadioButton courierRadio;


    @FXML
    void gotoLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/approjectfrontend/Login-view.fxml"));
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSignup(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = null;

        if (buyerRadio.isSelected()) {
            role = "buyer";
        } else if (sellerRadio.isSelected()) {
            role = "seller";
        } else if (courierRadio.isSelected()) {
            role = "courier";
        }

        if (role == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("لطفاً یکی از نقش‌ها را انتخاب کنید.");
            return;
        }

        if (username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("ورود اطلاعات (بجز ایمیل) اجباری است.");
            return;
        }

        if (!phone.matches("^09\\d{9}$")) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("شماره تماس را صحیح وارد نمایید. (مانند 09111111111)");
            return;
        }

        if (!email.isEmpty() && !email.matches(".+@.+\\..+")) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("ایمیل معتبر وارد کنید.");
            return;
        }

        // چک تکراری نبودن شماره تلفن
        for (MockUser user : UserDataStore.mockUserList) {
            if (user.getPhone().equals(phone)) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("این شماره تماس قبلاً ثبت شده است!");
                return;
            }
        }

        // چک تکراری نبودن ایمیل (اگر وارد شده باشد)
        if (!email.isEmpty()) {
            for (MockUser user : UserDataStore.mockUserList) {
                if (email.equalsIgnoreCase(user.getEmail())) {
                    messageLabel.setStyle("-fx-text-fill: red;");
                    messageLabel.setText("این ایمیل قبلاً ثبت شده است!");
                    return;
                }
            }
        }

        // نام کاربری می‌تواند تکراری باشد؛ بنابراین چک نمی‌شود

        MockUser newUser = new MockUser(username, password, email, phone, role, null);
        UserDataStore.mockUserList.add(newUser);

        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("ثبت‌نام با موفقیت انجام شد! 🎉");

        if (role.equals("buyer") || role.equals("seller")) {
            goToAddressPage(event, newUser);
        } else {
            clearFields();
        }
    }
    private void goToAddressPage(ActionEvent event, MockUser user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/approjectfrontend/Address-view.fxml"));
            Parent addressRoot = loader.load();

            // دسترسی به کنترلر صفحه آدرس
            AddressController addressController = loader.getController();
            addressController.setUser(user);

            Scene addressScene = new Scene(addressRoot);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(addressScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        phoneField.clear();
        buyerRadio.setSelected(false);
        sellerRadio.setSelected(false);
        courierRadio.setSelected(false);
    }


}