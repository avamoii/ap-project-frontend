package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

public class TransactionDTO {
    @SerializedName("id") private Long id;
    @SerializedName("user_id") private Long userId;
    @SerializedName("order_id") private Long orderId;
    @SerializedName("type") private String type; // DEPOSIT, WITHDRAWAL, PAYMENT
    @SerializedName("amount") private Integer amount;
    @SerializedName("created_at") private String createdAt;

    // --- فیلد جدید ---
    @SerializedName("user_name") private String userName;

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getOrderId() { return orderId; }
    public String getType() { return type; }
    public Integer getAmount() { return amount; }
    public String getCreatedAt() { return createdAt; }
    public String getUserName() { return userName; } // Getter برای فیلد جدید
}