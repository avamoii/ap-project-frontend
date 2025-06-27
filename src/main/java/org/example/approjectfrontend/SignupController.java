package org.example.approjectfrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class SignupController {

    @FXML
    private PasswordField passwordField;
@FXML
private TextField emailField;
    @FXML
    private TextField usernameField;

    @FXML
    void gotoLogin(ActionEvent event) {
      try {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/approjectfrontend/login-view.fxml"));

        Parent loginRoot = loader.load();
        Scene loginScene = new Scene(loginRoot);

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(loginScene);}
      catch (IOException e) {
          e.printStackTrace();
      }

    }

    @FXML
    void handleSignup(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {

            System.out.println("پر کردن هر دو فیلد الزامی است!");
            return;
        }


        // پاک کردن فیلدها پس از موفقیت
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
    }

}
