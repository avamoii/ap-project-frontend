// مسیر: src/main/java/org/example/approjectfrontend/api/UpdateProfileRequest.java
package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("address")
    private String address;

    @SerializedName("profileImageBase64")
    private String profileImageBase64;

    @SerializedName("bank_info")
    private BankInfoDTO bankInfo;

    // Getters and Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setProfileImageBase64(String profileImageBase64) { this.profileImageBase64 = profileImageBase64; }
    public void setBankInfo(BankInfoDTO bankInfo) { this.bankInfo = bankInfo; }
}