package org.example.approjectfrontend;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

public class AddressController {
    private MockUser currentUser;

    @FXML
    private TextField addressField;
    @FXML
    private Button submitButton;
    @FXML
    private Label messageLabel;

    public void setUser(MockUser user) {
        this.currentUser = user;
        // اگر قبلاً آدرسی ثبت شده بود، در فیلد نمایش بده (مثلاً برای ویرایش)
        if(user != null && user.getAddress() != null) {
            addressField.setText(user.getAddress());
        }
    }

    @FXML
    public void initialize() {
        // مقداردهی اولیه اگر نیاز بود
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String address = addressField.getText().trim();

        if (address.isEmpty()) {
            messageLabel.setText("لطفاً آدرس را وارد کنید!");
            return;
        }

        currentUser.setAddress(address);
        messageLabel.setText("آدرس ثبت شد!");


    }


}
