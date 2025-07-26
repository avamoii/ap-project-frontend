package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * این کلاس مدل داده برای یک سفارش است که از سرور دریافت می‌شود.
 */
public class OrderDTO {
    @SerializedName("id") private Long id;
    @SerializedName("delivery_address") private String deliveryAddress;
    @SerializedName("customer_id") private Long customerId;
    @SerializedName("restaurant_id") private Long restaurantId;
    @SerializedName("courier_id") private Long courierId;
    @SerializedName("coupon_id") private Long couponId;
    @SerializedName("item_ids") private List<Long> itemIds;
    @SerializedName("raw_price") private Integer rawPrice;
    @SerializedName("tax_fee") private Integer taxFee;
    @SerializedName("additional_fee") private Integer additionalFee;
    @SerializedName("courier_fee") private Integer courierFee;
    @SerializedName("pay_price") private Integer payPrice;
    @SerializedName("status") private String status;
    @SerializedName("created_at") private String createdAt;
    @SerializedName("updated_at") private String updatedAt;

    // Getters
    public Long getId() { return id; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public Long getCustomerId() { return customerId; }
    public Long getRestaurantId() { return restaurantId; }
    public Long getCourierId() { return courierId; }
    public Long getCouponId() { return couponId; }
    public List<Long> getItemIds() { return itemIds; }
    public Integer getRawPrice() { return rawPrice; }
    public Integer getTaxFee() { return taxFee; }
    public Integer getAdditionalFee() { return additionalFee; }
    public Integer getCourierFee() { return courierFee; }
    public Integer getPayPrice() { return payPrice; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
