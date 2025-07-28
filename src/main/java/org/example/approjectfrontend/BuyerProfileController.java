package org.example.approjectfrontend;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.example.approjectfrontend.api.ApiResponse;
import org.example.approjectfrontend.api.ApiService;
import org.example.approjectfrontend.api.TopUpWalletRequest;
import org.example.approjectfrontend.api.UserDTO;
import org.example.approjectfrontend.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class BuyerProfileController implements Initializable {
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label addressLabel;
    @FXML private Label walletBalanceLabel;
    @FXML private Button homeBtn;
    @FXML private Button historyBtn;
    @FXML private Button profileBtn;
    @FXML private Button chargeWalletButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        profileBtn.setStyle("-fx-background-color: #1e7e44;");
        homeBtn.setOnAction(e -> navigateToPage(e, "BuyerHome-view.fxml"));
        historyBtn.setOnAction(e -> navigateToPage(e, "BuyerHistory-view.fxml"));

        // Load user data when the page is initialized
        loadUserProfile();
    }

    private void loadUserProfile() {
        new Thread(() -> {
            ApiResponse response = ApiService.getUserProfile();
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    UserDTO user = new Gson().fromJson(response.getBody(), UserDTO.class);
                    updateUI(user);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user profile.");
                }
            });
        }).start();
    }

    private void updateUI(UserDTO user) {
        if (user == null) return;
        nameLabel.setText(user.getFullName());
        emailLabel.setText(user.getEmail());
        phoneLabel.setText(user.getPhoneNumber());
        addressLabel.setText(user.getAddress() != null ? user.getAddress() : "Not set");
        walletBalanceLabel.setText(String.format("%,d Toman", user.getWalletBalance()));
    }

    @FXML
    private void handleChargeWallet() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Charge Wallet");
        dialog.setHeaderText("Enter the amount you want to add to your wallet.");
        dialog.setContentText("Amount (Toman):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                int amount = Integer.parseInt(amountStr);
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a positive number.");
                    return;
                }

                // Create the request object
                TopUpWalletRequest topUpRequest = new TopUpWalletRequest();
                topUpRequest.setAmount(amount);

                // Call the ApiService in a new thread
                new Thread(() -> {
                    ApiResponse response = ApiService.topUpWallet(topUpRequest);
                    Platform.runLater(() -> {
                        if (response.getStatusCode() == 200) {
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Your wallet has been charged successfully.");
                            // Refresh the profile to show the new balance
                            loadUserProfile();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Failed", "Could not charge the wallet. " + response.getBody());
                        }
                    });
                }).start();

            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number.");
            }
        });
    }

    private void navigateToPage(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}