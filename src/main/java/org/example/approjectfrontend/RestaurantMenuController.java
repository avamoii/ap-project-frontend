package org.example.approjectfrontend;
import org.example.approjectfrontend.RestaurantMenuItem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import java.io.File;

public class RestaurantMenuController {

    @FXML private TableView<RestaurantMenuItem> menuTable;
    @FXML private TableColumn<RestaurantMenuItem, String> colName;
    @FXML private TableColumn<RestaurantMenuItem, String> colPrice;
    @FXML private TableColumn<RestaurantMenuItem, String> colSupply;
    @FXML private TableColumn<RestaurantMenuItem, Void> colEdit;
    @FXML private TableColumn<RestaurantMenuItem, Void> colDelete;

    @FXML private TextField itemNameField, itemDescField, itemPriceField, itemSupplyField, itemKeywordsField;
    @FXML private Button chooseImageBtn, addItemBtn,clearMenuBtn;
    @FXML private ImageView itemImageView;
    private Restaurant restaurant;
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        // اینجا می‌تونی اطلاعات را load or show کنی
    }
    private ObservableList<RestaurantMenuItem> menuItems = FXCollections.observableArrayList();
    private RestaurantMenuItem editingItem = null;
    private Image selectedImage = null; // عکس انتخابی موقت


    @FXML
    private void initialize() {
        menuTable.setItems(menuItems);
        colName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colPrice.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrice()));
        colSupply.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSupply()));
        clearMenuBtn.setOnAction(e -> handleClearMenu());

        addEditButtonToTable();
        addDeleteButtonToTable();

        addItemBtn.setOnAction(e -> handleAddOrEditItem());
        chooseImageBtn.setOnAction(e -> handleChooseImage());
    }
    private void handleClearMenu() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "آیا از حذف کل آیتم‌های منو مطمئن هستید؟", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            menuItems.clear();
            menuTable.refresh();
            showAlert("تمام آیتم‌های منو حذف شدند.", Alert.AlertType.INFORMATION);
            clearFields();
            editingItem = null;
            addItemBtn.setText("افزودن آیتم");
        }
    }


    private void addEditButtonToTable() {
        colEdit.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("✏️");
            {
                btn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
                btn.setOnAction(event -> {
                    RestaurantMenuItem item = getTableView().getItems().get(getIndex());
                    itemNameField.setText(item.getName());
                    itemDescField.setText(item.getDesc());
                    itemPriceField.setText(item.getPrice());
                    itemSupplyField.setText(item.getSupply());
                    itemKeywordsField.setText(item.getKeywords());

                    itemImageView.setImage(item.getImage()); // نمایش عکس قبلی
                    selectedImage = item.getImage();         // ذخیره عکس فعلی آیتم

                    editingItem = item;
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
            private final Button btn = new Button("🗑️");
            {
                btn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
                btn.setOnAction(event -> {
                    RestaurantMenuItem item = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "آیا از حذف این آیتم مطمئن هستید؟", ButtonType.YES, ButtonType.NO);
                    alert.setHeaderText(null);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        menuItems.remove(item);
                        showAlert("آیتم حذف شد.", Alert.AlertType.INFORMATION);
                        clearFields();
                        editingItem = null;
                        addItemBtn.setText("افزودن آیتم");
                    }
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
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png")
        );
        File file = fileChooser.showOpenDialog(itemImageView.getScene().getWindow());
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            itemImageView.setImage(image);
            selectedImage = image; // عکس انتخابی را ذخیره کن
        }
    }

    // افزودن یا ویرایش آیتم
    private void handleAddOrEditItem() {
        String name = itemNameField.getText().trim();
        String desc = itemDescField.getText().trim();
        String price = itemPriceField.getText().trim();
        String supply = itemSupplyField.getText().trim();
        String keywords = itemKeywordsField.getText().trim();


        if (editingItem != null) { // حالت ویرایش
            editingItem.setName(name);
            editingItem.setDesc(desc);
            editingItem.setPrice(price);
            editingItem.setSupply(supply);
            editingItem.setKeywords(keywords);
            // اگر عکس جدید انتخاب شده، جایگزین شود
            if (selectedImage != null) {
                editingItem.setImage(selectedImage);
            }
            menuTable.refresh();
            showAlert("تغییرات آیتم ذخیره شد.", Alert.AlertType.INFORMATION);
            editingItem = null;
            addItemBtn.setText("افزودن آیتم");
            clearFields();
            return;
        }

        // حالت افزودن آیتم جدید
        int responseCode = addMenuItemToServer(new RestaurantMenuItem(name, desc, price, supply, keywords, selectedImage));
        switch (responseCode) {
            case 200:
                menuItems.add(new RestaurantMenuItem(name, desc, price, supply, keywords, selectedImage));
                showAlert("آیتم جدید با موفقیت به منو اضافه شد.", Alert.AlertType.INFORMATION);
                clearFields();
                break;
            case 400:
                showAlert("ورودی نامعتبر! لطفاً اطلاعات را درست وارد کنید.", Alert.AlertType.ERROR);
                break;
            case 401:
                showAlert("احراز هویت نشده‌اید! لطفاً مجدداً وارد شوید.", Alert.AlertType.ERROR);
                break;
            case 403:
                showAlert("شما اجازه این عملیات را ندارید.", Alert.AlertType.ERROR);
                break;
            case 404:
                showAlert("آدرس یا سرویس مورد نظر پیدا نشد.", Alert.AlertType.ERROR);
                break;
            case 409:
                showAlert("این آیتم قبلاً در منو ثبت شده است.", Alert.AlertType.ERROR);
                break;
            case 415:
                showAlert("فرمت اطلاعات یا عکس ارسال شده صحیح نیست.", Alert.AlertType.ERROR);
                break;
            case 429:
                showAlert("درخواست‌های بیش از حد! لطفاً کمی صبر کنید.", Alert.AlertType.ERROR);
                break;
            case 500:
                showAlert("خطای سرور! بعداً دوباره تلاش کنید.", Alert.AlertType.ERROR);
                break;
            default:
                showAlert("خطای پیش‌بینی نشده!", Alert.AlertType.ERROR);
        }
    }

    // نمونه کد برای شبیه‌سازی پاسخ سرور
    private int addMenuItemToServer(RestaurantMenuItem item) {
        if (item.getName().equalsIgnoreCase("پیتزا"))
            return 409;
        if (!item.getPrice().matches("\\d+"))
            return 400;
        return 200;
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void clearFields() {
        itemNameField.clear();
        itemDescField.clear();
        itemPriceField.clear();
        itemSupplyField.clear();
        itemKeywordsField.clear();
        itemImageView.setImage(null);
        selectedImage = null; // عکس موقت پاک شود
    }
}
