// مسیر: src/main/java/org/example/approjectfrontend/api/FoodItemDTO.java
package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * این کلاس مدل داده برای یک آیتم غذا است که از سرور دریافت می‌شود.
 * فیلدهای آن دقیقاً با JSON ارسالی از بک‌اند مطابقت دارد.
 */
public class FoodItemDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private Integer price;

    @SerializedName("supply")
    private Integer supply;

    @SerializedName("image_base64")
    private String imageBase64;

    @SerializedName("keywords")
    private List<String> keywords;

    @SerializedName("restaurant_id")
    private Long restaurantId;

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getPrice() { return price; }
    public Integer getSupply() { return supply; }
    public String getImageBase64() { return imageBase64; }
    public List<String> getKeywords() { return keywords; }
    public Long getRestaurantId() { return restaurantId; }
}