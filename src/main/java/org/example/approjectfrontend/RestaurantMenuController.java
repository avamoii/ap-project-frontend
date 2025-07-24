package org.example.approjectfrontend;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.approjectfrontend.api.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RestaurantMenuController implements Initializable {

    @FXML
    private TableView<FoodItemDTO> menuTable;
    @FXML
    private TableColumn<FoodItemDTO, String> colName, colPrice, colSupply;
    @FXML
    private TableColumn<FoodItemDTO, Void> colEdit, colDelete;
    @FXML
    private TextField itemNameField, itemDescField, itemPriceField, itemSupplyField, itemKeywordsField;
    @FXML
    private Button chooseImageBtn, addItemBtn, backButton;
    @FXML
    private ImageView itemImageView;
    @FXML
    private Label messageLabel, menuTitleLabel;

    private RestaurantDTO currentRestaurant;
    private String currentMenuTitle;
    private final ObservableList<FoodItemDTO> menuItems = FXCollections.observableArrayList();
    private File selectedImageFile = null;
    private FoodItemDTO editingItem = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colPrice.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getPrice())));
        colSupply.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getSupply())));
        menuTable.setItems(menuItems);

        addEditButtonToTable();
        addDeleteButtonToTable();
    }

    public void setRestaurantAndMenu(RestaurantDTO restaurant, String menuTitle) {
        this.currentRestaurant = restaurant;
        this.currentMenuTitle = menuTitle;
        menuTitleLabel.setText("آیتم‌های منوی: " + menuTitle);
        loadMenuItems();
    }

    private void loadMenuItems() {
        if (currentRestaurant == null || currentMenuTitle == null) return;
        menuItems.clear();

        new Thread(() -> {
            ApiResponse response = ApiService.getRestaurantMenu(currentRestaurant.getId());
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    Gson gson = new Gson();
                    JsonObject responseJson = gson.fromJson(response.getBody(), JsonObject.class);
                    if (responseJson.has(currentMenuTitle)) {
                        List<FoodItemDTO> items = gson.fromJson(responseJson.get(currentMenuTitle), new TypeToken<List<FoodItemDTO>>(){}.getType());
                        menuItems.addAll(items);
                    }
                } else {
                    showMessage("خطا در دریافت آیتم‌های منو.", "red");
                }
            });
        }).start();
    }

    @FXML
    private void handleAddOrEditItem() {
        if (editingItem != null) {
            handleUpdateItem();
        } else {
            handleAddItem();
        }
    }

    private void handleAddItem() {
        AddFoodItemRequest requestData = new AddFoodItemRequest();
        if (!collectDataFromForm(requestData)) return;

        new Thread(() -> {
            ApiResponse response = ApiService.addFoodItem(currentRestaurant.getId(), currentMenuTitle, requestData);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    showMessage("آیتم جدید با موفقیت اضافه شد.", "green");
                    clearFields();
                    loadMenuItems();
                } else {
                    showMessage("خطا در ثبت آیتم: " + response.getBody(), "red");
                }
            });
        }).start();
    }

    private void handleUpdateItem() {
        UpdateFoodItemRequest requestData = new UpdateFoodItemRequest();
        if (!collectDataFromForm(requestData)) return;

        new Thread(() -> {
            ApiResponse response = ApiService.updateFoodItem(currentRestaurant.getId(), editingItem.getId(), requestData);
            Platform.runLater(() -> {
                if (response.getStatusCode() == 200) {
                    showMessage("آیتم با موفقیت ویرایش شد.", "green");
                    clearFields();
                    loadMenuItems();
                } else {
                    showMessage("خطا در ویرایش آیتم: " + response.getBody(), "red");
                }
            });
        }).start();
    }

    private <T> boolean collectDataFromForm(T requestData) {
        String name = itemNameField.getText().trim();
        String desc = itemDescField.getText().trim();
        String keywords = itemKeywordsField.getText().trim();
        if (name.isEmpty() || desc.isEmpty() || keywords.isEmpty()) {
            showMessage("تمام فیلدهای متنی باید پر شوند.", "red");
            return false;
        }
        Integer price, supply;
        try {
            price = Integer.parseInt(itemPriceField.getText().trim());
            supply = Integer.parseInt(itemSupplyField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage("قیمت و موجودی باید عدد باشند.", "red");
            return false;
        }

        List<String> keywordsList = Arrays.asList(keywords.split(","));
        if (requestData instanceof AddFoodItemRequest req) {
            req.setName(name);
            req.setDescription(desc);
            req.setKeywords(keywordsList);
            req.setPrice(price);
            req.setSupply(supply);
        } else if (requestData instanceof UpdateFoodItemRequest req) {
            req.setName(name);
            req.setDescription(desc);
            req.setKeywords(keywordsList);
            req.setPrice(price);
            req.setSupply(supply);
        }

        if (selectedImageFile != null) {
            try {
                byte[] fileContent = Files.readAllBytes(selectedImageFile.toPath());
                String imageBase64 = Base64.getEncoder().encodeToString(fileContent);
                if (requestData instanceof AddFoodItemRequest req) req.setImageBase64(imageBase64);
                else if (requestData instanceof UpdateFoodItemRequest req) req.setImageBase64(imageBase64);
            } catch (IOException e) {
                showMessage("مشکل در پردازش تصویر.", "red");
                return false;
            }
        }
        return true;
    }

    private void addEditButtonToTable() {
        colEdit.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("ویرایش");
            {
                btn.setOnAction(event -> {
                    editingItem = getTableView().getItems().get(getIndex());
                    itemNameField.setText(editingItem.getName());
                    itemDescField.setText(editingItem.getDescription());
                    itemPriceField.setText(editingItem.getPrice().toString());
                    itemSupplyField.setText(editingItem.getSupply().toString());
                    itemKeywordsField.setText(String.join(",", editingItem.getKeywords()));
                    addItemBtn.setText("ذخیره تغییرات");
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void addDeleteButtonToTable() {
        colDelete.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("حذف");
            {
                btn.setOnAction(event -> {
                    FoodItemDTO item = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "آیا از حذف آیتم '" + item.getName() + "' مطمئن هستید؟", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            new Thread(() -> {
                                ApiResponse apiResponse = ApiService.deleteFoodItem(currentRestaurant.getId(), item.getId());
                                Platform.runLater(() -> {
                                    if (apiResponse.getStatusCode() == 200) {
                                        showMessage("آیتم با موفقیت حذف شد.", "green");
                                        loadMenuItems();
                                    } else {
                                        showMessage("خطا در حذف آیتم: " + apiResponse.getBody(), "red");
                                    }
                                });
                            }).start();
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب تصویر غذا");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"));
        File file = fileChooser.showOpenDialog(itemImageView.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            itemImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleGoBackToMenus(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuManager-view.fxml"));
        Parent root = loader.load();
        MenuManagerController controller = loader.getController();
        controller.setRestaurant(currentRestaurant);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void clearFields() {
        editingItem = null;
        itemNameField.clear();
        itemDescField.clear();
        itemPriceField.clear();
        itemSupplyField.clear();
        itemKeywordsField.clear();
        itemImageView.setImage(null);
        selectedImageFile = null;
        addItemBtn.setText("افزودن آیتم");
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
        if (color.equals("green")) {
            messageLabel.setStyle("-fx-background-color: #dff0d8; -fx-text-fill: #3c763d; -fx-padding: 10; -fx-background-radius: 5;");
        } else {
            messageLabel.setStyle("-fx-background-color: #f2dede; -fx-text-fill: #a94442; -fx-padding: 10; -fx-background-radius: 5;");
        }
        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        delay.setOnFinished(event -> {
            messageLabel.setVisible(false);
            messageLabel.setManaged(false);
        });
        delay.play();
    }
}