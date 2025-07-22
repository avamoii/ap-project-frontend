// مسیر: src/main/java/org/example/approjectfrontend/api/LoginRequest.java
package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

/**
 * این کلاس مدل داده برای ارسال درخواست ورود (login) به سرور است.
 * فیلدهای آن دقیقاً با JSON مورد انتظار بک‌اند مطابقت دارد.
 */
public class LoginRequest {

    // بک‌اند شما فیلد phone را بدون آندرلاین انتظار دارد
    @SerializedName("phone")
    private final String phone;

    @SerializedName("password")
    private final String password;

    public LoginRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    // Getters
    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }
}