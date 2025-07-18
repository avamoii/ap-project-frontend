package org.example.approjectfrontend;
import javafx.scene.image.Image;


public class Restaurant {
    private String name;
    private Image logo;
    private boolean completed; // آیا اطلاعات کامل است؟

    public Restaurant(String name, Image logo, boolean completed) {
        this.name = name;
        this.logo = logo;
        this.completed = completed;
    }
    public String getName() { return name; }
    public Image getLogo() { return logo; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}

