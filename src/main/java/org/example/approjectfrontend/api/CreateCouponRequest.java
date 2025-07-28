package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class CreateCouponRequest {
    @SerializedName("coupon_code")
    private String couponCode;
    @SerializedName("type")
    private String type;
    @SerializedName("value")
    private BigDecimal value;
    @SerializedName("min_price")
    private Integer minPrice;
    @SerializedName("user_count")
    private Integer userCount;
    @SerializedName("start_date")
    private String startDate; // "YYYY-MM-DD"
    @SerializedName("end_date")
    private String endDate;   // "YYYY-MM-DD"

    // Setters
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public void setType(String type) { this.type = type; }
    public void setValue(BigDecimal value) { this.value = value; }
    public void setMinPrice(Integer minPrice) { this.minPrice = minPrice; }
    public void setUserCount(Integer userCount) { this.userCount = userCount; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}