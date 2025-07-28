package org.example.approjectfrontend;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

// این کلاس مدل داده‌های کوپن است که در جدول نمایش داده می‌شود
public class Coupon {

    // --- تغییر اصلی: استفاده از @SerializedName برای هماهنگی با بک‌اند ---
    @SerializedName("id")
    private Long id;
    @SerializedName("couponCode")
    private String couponCode;
    @SerializedName("type")
    private String type; // "FIXED" or "PERCENT"
    @SerializedName("value")
    private BigDecimal value;
    @SerializedName("minPrice")
    private Integer minPrice;
    @SerializedName("userCount")
    private Integer userCount;
    @SerializedName("startDate")
    private String startDate;
    @SerializedName("endDate")
    private String endDate;

    // Getters
    public Long getId() { return id; }
    public String getCouponCode() { return couponCode; }
    public String getType() { return type; }
    public BigDecimal getValue() { return value; }
    public Integer getMinPrice() { return minPrice; }
    public Integer getUserCount() { return userCount; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }

    // Setters (برای استفاده در PropertyValueFactory)
    public void setId(Long id) { this.id = id; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public void setType(String type) { this.type = type; }
    public void setValue(BigDecimal value) { this.value = value; }
    public void setMinPrice(Integer minPrice) { this.minPrice = minPrice; }
    public void setUserCount(Integer userCount) { this.userCount = userCount; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}