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

    // --- **تغییر اصلی اینجاست** ---
    // این فیلد برای ارسال شناسه کوپن استفاده می‌شود و می‌تواند خالی (null) باشد
    @SerializedName("coupon_id")
    private Long couponId;

    public SubmitOrderRequest(String deliveryAddress, Long vendorId, List<OrderItemRequestDTO> items) {
        this.deliveryAddress = deliveryAddress;
        this.vendorId = vendorId;
        this.items = items;
        this.couponId = null; // مقدار اولیه null است
    }

    // --- **متد Setter جدید برای تنظیم کوپن** ---
    public void setCouponId(Long couponId) {
        this.couponId = couponId;
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