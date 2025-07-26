// مسیر: src/main/java/org/example/approjectfrontend/api/OrderItemRequestDTO.java
package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

public class OrderItemRequestDTO {
    @SerializedName("item_id")
    private Long itemId;

    @SerializedName("quantity")
    private Integer quantity;

    // Constructor, Getters and Setters
    public OrderItemRequestDTO(Long itemId, Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}