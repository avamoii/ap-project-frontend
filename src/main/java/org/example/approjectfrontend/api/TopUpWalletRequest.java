package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

public class TopUpWalletRequest {
    @SerializedName("amount")
    private Integer amount;

    /**
     * A no-argument constructor is added to allow instantiation
     * with `new TopUpWalletRequest()`.
     */
    public TopUpWalletRequest() {
    }

    /**
     * The missing setter method is added so the amount can be set
     * after the object is created.
     */
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }
}