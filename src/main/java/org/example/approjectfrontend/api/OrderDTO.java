package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderDTO {
    @SerializedName("id")
    private Long id;
    @SerializedName("restaurant_id")
    private Long restaurantId;
    @SerializedName("status")
    private String status;
    @SerializedName("pay_price")
    private Integer payPrice;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("item_ids")
    private List<Long> itemIds; // فقط شناسه‌ها را نگه می‌داریم

    // Getters
    public Long getId() { return id; }
    public Long getRestaurantId() { return restaurantId; }
    public String getStatus() { return status; }
    public Integer getPayPrice() { return payPrice; }
    public String getCreatedAt() { return createdAt; }
    public List<Long> getItemIds() { return itemIds; }
}