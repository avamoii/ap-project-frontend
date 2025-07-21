package org.example.approjectfrontend;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private String name;
    private Image logo;
    private List<RestaurantMenuItem> menuItems;

    public Restaurant(String name, Image logo) {
        this.name = name;
        this.logo = logo;
        this.menuItems = new ArrayList<>();
    }
    public String getName() { return name; }
    public Image getLogo() { return logo; }
    public List<RestaurantMenuItem> getMenuItems() { return menuItems; }
    public void setMenuItems(List<RestaurantMenuItem> menuItems) { this.menuItems = menuItems; }

}