// مسیر: src/main/java/org/example/approjectfrontend/api/RegisterRequest.java
package org.example.approjectfrontend.api;

// این import جدید را اضافه کنید
import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    // با استفاده از @SerializedName مشخص می‌کنیم که نام این فیلد در JSON چه باشد
    @SerializedName("full_name")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("password")
    private String password;

    @SerializedName("role")
    private String role;

    @SerializedName("address")
    private String address;

    @SerializedName("email")
    private String email;

    @SerializedName("profileImageBase64")
    private String profileImageBase64;

    // برای فیلدهای تو در تو نیز این کار را انجام دهید
    @SerializedName("bank_info")
    private BankInfoDTO bankInfo;

    // Constructor و بقیه متدها بدون تغییر باقی می‌مانند
    public RegisterRequest(String fullName, String phone, String password, String role) {
        this.fullName = fullName;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getProfileImageBase64() { return profileImageBase64; }
    public BankInfoDTO getBankInfo() { return bankInfo; }

    // Setters
    public void setAddress(String address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setProfileImageBase64(String profileImageBase64) { this.profileImageBase64 = profileImageBase64; }
    public void setBankInfo(BankInfoDTO bankInfo) { this.bankInfo = bankInfo; }
}