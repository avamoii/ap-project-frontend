package org.example.approjectfrontend;

public class Coupon {
    private Long id;
    private String coupon_code;
    private String type; // "fixed" یا "percent"
    private Integer value;
    private Integer min_price;
    private Integer user_count;
    private String start_date;  // بهتره String بذاری اگر تاریخ به فرمت yyyy-MM-dd میاد
    private String end_date;

    public Coupon() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getCoupon_code() {
        return coupon_code;
    }
    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Integer getValue() {
        return value;
    }
    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getMin_price() {
        return min_price;
    }
    public void setMin_price(Integer min_price) {
        this.min_price = min_price;
    }

    public Integer getUser_count() {
        return user_count;
    }
    public void setUser_count(Integer user_count) {
        this.user_count = user_count;
    }

    public String getStart_date() {
        return start_date;
    }
    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }
    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}

