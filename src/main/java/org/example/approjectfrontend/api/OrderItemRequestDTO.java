package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

/**
 * این کلاس مدل داده برای هر آیتم در درخواست ثبت سفارش است.
 * نام فیلدها با @SerializedName دقیقاً مطابق با چیزی است که بک‌اند انتظار دارد.
 */
public class OrderItemRequestDTO {
    @SerializedName("item_id")
    private final Long itemId;

    @SerializedName("quantity")
    private final Integer quantity;

    public OrderItemRequestDTO(Long itemId, Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    // Getters
    public Long getItemId() {
        return itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
