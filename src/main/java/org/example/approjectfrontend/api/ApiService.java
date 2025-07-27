package org.example.approjectfrontend.api;

import com.google.gson.Gson;
import org.example.approjectfrontend.util.SessionManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class ApiService {

    private static final String API_BASE_URL = "http://localhost:1215"; // پورت به 1215 اصلاح شد
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final Gson gson = new Gson();

    // --- متدهای احراز هویت ---
    public static ApiResponse register(RegisterRequest requestData) {
        String jsonBody = gson.toJson(requestData);
        // تمام پیشوندهای /api حذف شدند تا با بک‌اند هماهنگ شوند
        return sendPostRequest("/auth/register", jsonBody, false);
    }

    public static ApiResponse login(String phone, String password) {
        LoginRequest loginData = new LoginRequest(phone, password);
        String jsonBody = gson.toJson(loginData);
        return sendPostRequest("/auth/login", jsonBody, false);
    }

    public static ApiResponse logout() {
        return sendPostRequestWithAuth("/auth/logout", "");
    }

    // --- متدهای پروفایل ---
    public static ApiResponse getProfile() {
        return sendGetRequestWithAuth("/auth/profile");
    }

    public static ApiResponse updateProfile(UpdateProfileRequest profileData) {
        String jsonBody = gson.toJson(profileData);
        return sendPutRequestWithAuth("/auth/profile", jsonBody);
    }

    // --- متدهای خریدار ---
    public static ApiResponse getVendors() {
        return sendPostRequestWithAuth("/vendors", "{}");
    }

    public static ApiResponse getRestaurantMenu(long restaurantId) {
        return sendGetRequestWithAuth("/vendors/" + restaurantId);
    }

    public static ApiResponse getFoodItemDetails(long itemId) {
        return sendGetRequestWithAuth("/items/" + itemId);
    }

    public static ApiResponse submitOrder(SubmitOrderRequest orderData) {
        String jsonBody = gson.toJson(orderData);
        return sendPostRequestWithAuth("/orders", jsonBody);
    }

    public static ApiResponse getOrderDetails(long orderId) {
        return sendGetRequestWithAuth("/orders/" + orderId);
    }

    public static ApiResponse getOrderHistory() {
        return sendGetRequestWithAuth("/orders/history");
    }

    // --- متدهای علاقه‌مندی‌ها ---
    public static ApiResponse getFavorites() {
        return sendGetRequestWithAuth("/favorites");
    }

    public static ApiResponse addFavorite(long restaurantId) {
        return sendPutRequestWithAuth("/favorites/" + restaurantId, "");
    }

    public static ApiResponse removeFavorite(long restaurantId) {
        return sendDeleteRequestWithAuth("/favorites/" + restaurantId);
    }

    // --- متدهای عمومی (کیف پول، پرداخت، تراکنش، نظرات) ---
    public static ApiResponse topUpWallet(int amount) {
        TopUpWalletRequest requestData = new TopUpWalletRequest(amount);
        String jsonBody = gson.toJson(requestData);
        return sendPostRequestWithAuth("/wallet/top-up", jsonBody);
    }

    public static ApiResponse makePayment(PaymentRequest paymentRequest) {
        String jsonBody = gson.toJson(paymentRequest);
        return sendPostRequestWithAuth("/payment/online", jsonBody);
    }

    public static ApiResponse getTransactionHistory() {
        return sendGetRequestWithAuth("/transactions");
    }

    public static ApiResponse submitRating(SubmitRatingRequest ratingData) {
        String jsonBody = gson.toJson(ratingData);
        return sendPostRequestWithAuth("/ratings", jsonBody);
    }

    public static ApiResponse getRatingDetails(long ratingId) {
        return sendGetRequestWithAuth("/ratings/" + ratingId);
    }
//
//    public static ApiResponse updateRating(long ratingId, UpdateRatingRequest ratingData) {
//        String jsonBody = gson.toJson(ratingData);
//        return sendPutRequestWithAuth("/ratings/" + ratingId, jsonBody);
//    }

    // --- متدهای پیک ---
    public static ApiResponse getAvailableDeliveries() {
        return sendGetRequestWithAuth("/deliveries/available");
    }

    public static ApiResponse updateDeliveryStatus(long orderId, String status) {
        String jsonBody = gson.toJson(Map.of("status", status));
        return sendPatchRequestWithAuth("/deliveries/" + orderId, jsonBody);
    }

    public static ApiResponse getDeliveryHistory() {
        return sendGetRequestWithAuth("/deliveries/history");
    }

    // --- متدهای صاحب رستوران ---
    public static ApiResponse createRestaurant(CreateRestaurantRequest restaurantData) {
        String jsonBody = gson.toJson(restaurantData);
        return sendPostRequestWithAuth("/restaurants", jsonBody);
    }

    public static ApiResponse createMenu(long restaurantId, String menuTitle) {
        CreateMenuRequest menuRequest = new CreateMenuRequest(menuTitle);
        String requestBody = gson.toJson(menuRequest);
        return sendPostRequestWithAuth("/restaurants/" + restaurantId + "/menu", requestBody);
    }

    public static ApiResponse getMyRestaurants() {
        return sendGetRequestWithAuth("/restaurants/mine");
    }

    public static ApiResponse getRestaurantOrders(long restaurantId) {
        return sendGetRequestWithAuth("/restaurants/" + restaurantId + "/orders");
    }

    public static ApiResponse updateOrderStatus(long orderId, String status) {
        String jsonBody = gson.toJson(Map.of("status", status));
        return sendPatchRequestWithAuth("/restaurants/orders/" + orderId, jsonBody);
    }

    public static ApiResponse addFoodItem(long restaurantId, String menuTitle, AddFoodItemRequest itemData) {
        String jsonBody = gson.toJson(itemData);
        return sendPostRequestWithAuth("/restaurants/" + restaurantId + "/menu/" + menuTitle + "/item", jsonBody);
    }

    public static ApiResponse updateFoodItem(long restaurantId, long itemId, UpdateFoodItemRequest itemData) {
        String jsonBody = gson.toJson(itemData);
        return sendPutRequestWithAuth("/restaurants/" + restaurantId + "/item/" + itemId, jsonBody);
    }

    public static ApiResponse deleteFoodItem(long restaurantId, long itemId) {
        return sendDeleteRequestWithAuth("/restaurants/" + restaurantId + "/item/" + itemId);
    }

    public static ApiResponse removeItemFromMenu(long restaurantId, String menuTitle, long itemId) {
        return sendDeleteRequestWithAuth("/restaurants/" + restaurantId + "/menu/" + menuTitle + "/" + itemId);
    }

    // =======================================================================================
    // --- متدهای کمکی برای ارسال درخواست‌ها (برای جلوگیری از تکرار کد) ---
    // =======================================================================================

    private static ApiResponse sendGetRequestWithAuth(String path) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + path))
                    .header("Authorization", "Bearer " + token)
                    .GET().build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    private static ApiResponse sendPostRequest(String path, String jsonBody, boolean withAuth) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + path))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));
            if (withAuth) {
                String token = SessionManager.getInstance().getToken();
                if (token == null || token.isEmpty()) return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
                builder.header("Authorization", "Bearer " + token);
            }
            HttpResponse<String> httpResponse = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    private static ApiResponse sendPostRequestWithAuth(String path, String jsonBody) {
        return sendPostRequest(path, jsonBody, true);
    }

    private static ApiResponse sendPutRequestWithAuth(String path, String jsonBody) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        try {
            HttpRequest.BodyPublisher bodyPublisher = jsonBody.isEmpty() ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(jsonBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + path))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .PUT(bodyPublisher).build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    private static ApiResponse sendPatchRequestWithAuth(String path, String jsonBody) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + path))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody)).build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    private static ApiResponse sendDeleteRequestWithAuth(String path) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + path))
                    .header("Authorization", "Bearer " + token)
                    .DELETE().build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }
}
