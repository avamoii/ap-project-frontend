// مسیر: src/main/java/org/example/approjectfrontend/api/ApiService.java
package org.example.approjectfrontend.api;

import com.google.gson.Gson;
import org.example.approjectfrontend.util.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * این کلاس مسئول تمام ارتباطات با بک‌اند (API) است.
 * هر متد در این کلاس با یکی از اندپوینت‌های سرور صحبت می‌کند.
 */
public class ApiService {

    private static final String API_BASE_URL = "http://localhost:1214";
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final Gson gson = new Gson();

    /**
     * متد برای ارسال درخواست ثبت‌نام کاربر جدید به سرور.
     * @param requestData آبجکتی از نوع RegisterRequest که حاوی تمام اطلاعات ثبت‌نام است.
     * @return یک آبجکت ApiResponse که شامل کد وضعیت و بدنه پاسخ سرور است.
     */
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

    /**
     * متد برای ارسال درخواست ورود (login) به سرور.
     * @param phone شماره تلفن کاربر
     * @param password رمز عبور کاربر
     * @return پاسخ سرور
     */
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
     * متد برای آپدیت پروفایل کاربر.
     * این متد توکن احراز هویت را از SessionManager گرفته و در هدر درخواست ارسال می‌کند.
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
    public static ApiResponse logout() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            // اگر کاربر از قبل لاگین نکرده باشد، نیازی به لاگ‌اوت نیست
            return new ApiResponse(200, "{\"message\":\"Already logged out.\"}");
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/auth/logout"))
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.noBody()) // این درخواست بدنه‌ای ندارد
                    .build();

            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }
    // این متد را به کلاس ApiService.java اضافه کنید

    public static ApiResponse getProfile() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/auth/profile"))
                    .header("Authorization", "Bearer " + token)
                    .GET() // متد GET
                    .build();

            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }
    public static ApiResponse createRestaurant(CreateRestaurantRequest restaurantData) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }

        try {
            String jsonBody = gson.toJson(restaurantData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/restaurants"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token) // <-- ارسال توکن
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }
    public static ApiResponse getMyRestaurants() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/restaurants/mine"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    public static ApiResponse updateRestaurant(Long restaurantId, UpdateRestaurantRequest restaurantData) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            String jsonBody = gson.toJson(restaurantData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/restaurants/" + restaurantId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }
}