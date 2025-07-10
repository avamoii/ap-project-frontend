package org.example.approjectfrontend;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;


public class MainController implements Initializable {
    @FXML
    private AnchorPane contentArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Parent pane = FXMLLoader.load(getClass().getResource("/org/example/approjectfrontend/Signup-view.fxml"));
            contentArea.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

