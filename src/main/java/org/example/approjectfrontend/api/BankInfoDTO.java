// مسیر: src/main/java/org/example/approjectfrontend/api/BankInfoDTO.java
package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

public class BankInfoDTO {
    @SerializedName("bank_name")
    private String bankName;

    @SerializedName("account_number")
    private String accountNumber;

    // Getters and Setters...
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
}