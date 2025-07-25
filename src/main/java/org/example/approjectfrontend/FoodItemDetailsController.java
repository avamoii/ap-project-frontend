package org.example.approjectfrontend;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.approjectfrontend.api.FoodItemDTO;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class FoodItemDetailsController {

    @FXML
    private ImageView itemImageView;
    @FXML
    private Label nameLabel, descriptionLabel, priceLabel, supplyLabel, keywordsLabel;

    public void setFoodItem(FoodItemDTO item) {
        if (item == null) return;

        nameLabel.setText(item.getName());
        descriptionLabel.setText(item.getDescription());
        priceLabel.setText(item.getPrice() + " تومان");
        supplyLabel.setText(item.getSupply() + " عدد");
        keywordsLabel.setText(String.join(", ", item.getKeywords()));

        if (item.getImageBase64() != null && !item.getImageBase64().isEmpty()) {
            byte[] decodedBytes = Base64.getDecoder().decode(item.getImageBase64());
            itemImageView.setImage(new Image(new ByteArrayInputStream(decodedBytes)));
        }
    }
}