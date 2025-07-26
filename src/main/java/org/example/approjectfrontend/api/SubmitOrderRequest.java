package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * این کلاس مدل داده برای ارسال کل درخواست ثبت سفارش به سرور است.
 */
public class SubmitOrderRequest {
    @SerializedName("delivery_address")
    private final String deliveryAddress;

    @SerializedName("vendor_id")
    private final Long vendorId;

    @SerializedName("items")
    private final List<OrderItemRequestDTO> items;

    public SubmitOrderRequest(String deliveryAddress, Long vendorId, List<OrderItemRequestDTO> items) {
        this.deliveryAddress = deliveryAddress;
        this.vendorId = vendorId;
        this.items = items;
    }

    // Getters
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }
}
