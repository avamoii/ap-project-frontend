// مسیر: src/main/java/org/example/approjectfrontend/api/SubmitOrderRequest.java
package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SubmitOrderRequest {
    @SerializedName("delivery_address")
    private String deliveryAddress;

    @SerializedName("vendor_id")
    private Long vendorId;

    @SerializedName("coupon_id")
    private Long couponId; // این فیلد می‌تواند null باشد

    @SerializedName("items")
    private List<OrderItemRequestDTO> items;

    // Getters and Setters
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public List<OrderItemRequestDTO> getItems() { return items; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }
}