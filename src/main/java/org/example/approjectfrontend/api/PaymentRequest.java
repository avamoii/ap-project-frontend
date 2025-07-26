package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

/**
 * این کلاس مدل داده برای ارسال درخواست پرداخت به سرور است.
 */
public class PaymentRequest {
    @SerializedName("order_id")
    private final Long orderId;

    @SerializedName("method")
    private final String method; // "WALLET" or "ONLINE"

    public PaymentRequest(Long orderId, String method) {
        this.orderId = orderId;
        this.method = method;
    }

    // Getters
    public Long getOrderId() {
        return orderId;
    }

    public String getMethod() {
        return method;
    }
}
