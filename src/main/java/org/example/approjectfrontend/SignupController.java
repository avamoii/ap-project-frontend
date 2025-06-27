package org.example.approjectfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class SignupController {

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    void gotoLogin(ActionEvent event) {

    }

    @FXML
    void handleSignup(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {

            System.out.println("پر کردن هر دو فیلد الزامی است!");
            return;
        }


        // پاک کردن فیلدها پس از موفقیت
        usernameField.clear();
        passwordField.clear();
    }

}
