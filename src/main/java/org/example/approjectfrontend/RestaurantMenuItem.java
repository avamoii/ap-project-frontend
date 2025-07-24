package org.example.approjectfrontend;

import javafx.scene.image.Image;

public class RestaurantMenuItem {
    private String name;
    private String desc;
    private String price;
    private String supply;
    private String keywords;
    private Image image;
    private int orderCount;

    // سازنده با عکس
    public RestaurantMenuItem(String name, String desc, String price, String supply, String keywords, Image image) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.supply = supply;
        this.keywords = keywords;
        this.image = image;
    }

    // سازنده بدون عکس
    public RestaurantMenuItem(String name, String desc, String price, String supply, String keywords) {
        this(name, desc, price, supply, keywords, null);
    }

    // --- کانستراکتور کامل با orderCount فقط برای کپی‌برداری ---
    public RestaurantMenuItem(String name, String desc, String price, String supply,
                              String keywords, Image image, int orderCount) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.supply = supply;
        this.keywords = keywords;
        this.image = image;
        this.orderCount = orderCount;
    }

    // --- متد clone برای ساختن کپی کامل از شیء ---
    public RestaurantMenuItem cloneItem() {
        return new RestaurantMenuItem(this.name, this.desc, this.price, this.supply,
                this.keywords, this.image, this.orderCount);
    }

    // Getters & Setters
    public String getName() { return name; }
    public String getDesc() { return desc; }
    public String getPrice() { return price; }
    public String getSupply() { return supply; }
    public String getKeywords() { return keywords; }
    public Image getImage() { return image; }
    public int getOrderCount() { return orderCount; }
    public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
    public void setName(String name) { this.name = name; }
    public void setDesc(String desc) { this.desc = desc; }
    public void setPrice(String price) { this.price = price; }
    public void setSupply(String supply) { this.supply = supply; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
    public void setImage(Image image) { this.image = image; }
}
