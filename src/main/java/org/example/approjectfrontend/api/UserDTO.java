package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

public class UserDTO {
    @SerializedName("id") private long id;
    @SerializedName("full_name") private String fullName;
    @SerializedName("phone_number") private String phoneNumber;
    @SerializedName("role") private String role;
    @SerializedName("address") private String address;
    @SerializedName("email") private String email;
    @SerializedName("bank_info") private BankInfoDTO bankInfo;
    @SerializedName("wallet_balance") private Integer walletBalance;

    // --- فیلد جدید برای وضعیت کاربر ---
    @SerializedName("status") private String status; // PENDING, APPROVED, REJECTED

    // Getters
    public long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRole() { return role; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public BankInfoDTO getBankInfo() { return bankInfo; }
    public Integer getWalletBalance() { return walletBalance; }
    public String getStatus() { return status; } // Getter برای فیلد جدید

    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setBankInfo(BankInfoDTO bankInfo) { this.bankInfo = bankInfo; }
    public void setWalletBalance(Integer walletBalance) { this.walletBalance = walletBalance; }
    public void setStatus(String status) { this.status = status; } // Setter برای فیلد جدید
}