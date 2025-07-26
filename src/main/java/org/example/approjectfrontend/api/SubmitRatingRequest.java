package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

/**
 * این کلاس مدل داده برای ارسال درخواست ثبت نظر به سرور است.
 */
public class SubmitRatingRequest {

    @SerializedName("order_id")
    private final Long orderId;

    @SerializedName("rating")
    private final Integer rating;

    @SerializedName("comment")
    private final String comment;

    // imageBase64 در این مرحله پیاده‌سازی نمی‌شود اما می‌توان بعداً اضافه کرد.

    public SubmitRatingRequest(Long orderId, Integer rating, String comment) {
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters
    public Long getOrderId() {
        return orderId;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
