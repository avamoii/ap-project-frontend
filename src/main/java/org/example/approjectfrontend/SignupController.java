package org.example.approjectfrontend;

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
        currentStage.setScene(loginScene);}
      catch (IOException e) {
          e.printStackTrace();
      }

    }

    @FXML
    void handleSignup(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String phone = phoneField.getText();
        String role = null;
        if (buyerRadio.isSelected()) {
            role = "buyer";
        } else if (sellerRadio.isSelected()) {
            role = "seller";
        } else if (courierRadio.isSelected()) {
            role = "courier";
        }
        if (role == null) {
            messageLabel.setText("لطفاً یکی از نقش‌ها را انتخاب کنید.");
            return;
        }

       else if (username.isEmpty() || password.isEmpty() || phone.isEmpty()) {

            messageLabel.setText("ورود اطلاعات(بجز ایمیل) اجباری است.");
            return;
        }
        else if (!phone.matches("^09\\d{9}$")) {
            messageLabel.setText("شماره تماس را صحیح وارد نمایید. (مانند 09111111111)");
            return;
        }
       else if ( !email.isEmpty() &&  !email.matches(".+@.+\\..+")) {
            messageLabel.setText("ایمیل معتبر وارد کنید.");
            return;
        }
       else{
           messageLabel.setText("ثبت نام موفقیت امیز بود.");
        }

    }

}
