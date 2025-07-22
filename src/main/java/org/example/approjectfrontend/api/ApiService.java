// مسیر: src/main/java/org/example/approjectfrontend/api/ApiService.java
package org.example.approjectfrontend.api;

import com.google.gson.Gson;
import org.example.approjectfrontend.util.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiService {

    private static final String API_BASE_URL = "http://localhost:1214";
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final Gson gson = new Gson();

    public static ApiResponse register(RegisterRequest requestData) {
        try {
            String jsonBody = gson.toJson(requestData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/auth/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    public static ApiResponse login(String phone, String password) {
        try {
            LoginRequest loginData = new LoginRequest(phone, password);
            String jsonBody = gson.toJson(loginData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    /**
     * متد جدید برای آپدیت پروفایل کاربر
     * @param profileData اطلاعات جدید پروفایل برای ارسال
     * @return پاسخ سرور
     */
    public static ApiResponse updateProfile(UpdateProfileRequest profileData) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }

        try {
            String jsonBody = gson.toJson(profileData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/auth/profile"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token) // ارسال توکن برای احراز هویت
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody)) // استفاده از متد PUT
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }
}