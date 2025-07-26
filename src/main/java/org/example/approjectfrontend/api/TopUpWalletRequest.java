package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

/**
 * این کلاس مدل داده برای ارسال درخواست شارژ کیف پول به سرور است.
 */
public class TopUpWalletRequest {
    @SerializedName("amount")
    private final Integer amount;

    public TopUpWalletRequest(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }
}
