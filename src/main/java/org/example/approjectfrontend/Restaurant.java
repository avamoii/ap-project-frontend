package org.example.approjectfrontend;
import javafx.scene.image.Image;
public class Restaurant {
    private String name;
    private Image logo;
    // آیا اطلاعات کامل است؟

    public Restaurant(String name, Image logo) {
        this.name = name;
        this.logo = logo;

    }
    public String getName() { return name; }
    public Image getLogo() { return logo; }
}