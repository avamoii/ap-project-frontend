// مسیر: src/main/java/org/example/approjectfrontend/api/UpdateFoodItemRequest.java
package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UpdateFoodItemRequest {
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("price")
    private Integer price;
    @SerializedName("supply")
    private Integer supply;
    @SerializedName("imageBase64")
    private String imageBase64;
    @SerializedName("keywords")
    private List<String> keywords;

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Integer price) { this.price = price; }
    public void setSupply(Integer supply) { this.supply = supply; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
}