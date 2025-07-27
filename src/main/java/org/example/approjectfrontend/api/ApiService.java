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

    private static final String API_BASE_URL = "http://localhost:1215";
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final Gson gson = new Gson();

    // --- متدهای احراز هویت ---
    public static ApiResponse register(RegisterRequest requestData) {
        try {
            String jsonBody = gson.toJson(requestData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/auth/register"))
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
                    .uri(URI.create(API_BASE_URL + "/api/auth/login"))
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

    public static ApiResponse logout() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(200, "{\"message\":\"Already logged out.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/auth/logout"))
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    // --- متدهای پروفایل ---
    public static ApiResponse getProfile() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/auth/profile"))
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

    public static ApiResponse updateProfile(UpdateProfileRequest profileData) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            String jsonBody = gson.toJson(profileData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/auth/profile"))
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

    // --- متدهای خریدار ---
    public static ApiResponse getVendors() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/buyer/vendors"))
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

    public static ApiResponse getRestaurantMenu(long restaurantId) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/buyer/vendors/" + restaurantId))
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

    public static ApiResponse getFoodItemDetails(long itemId) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/buyer/items/" + itemId))
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

    public static ApiResponse submitOrder(SubmitOrderRequest orderData) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            String jsonBody = gson.toJson(orderData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/buyer/orders"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }
    public static ApiResponse getOrderDetails(long orderId) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/buyer/orders/" + orderId))
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

    public static ApiResponse getOrderHistory() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/buyer/orders/history"))
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

    public static ApiResponse submitRating(SubmitRatingRequest ratingData) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            String jsonBody = gson.toJson(ratingData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/buyer/ratings"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    // --- متدهای علاقه‌مندی‌ها ---
    public static ApiResponse getFavorites() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/buyer/favorites"))
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

    public static ApiResponse addFavorite(long restaurantId) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/buyer/favorites/" + restaurantId))
                    .header("Authorization", "Bearer " + token)
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    public static ApiResponse removeFavorite(long restaurantId) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/buyer/favorites/" + restaurantId))
                    .header("Authorization", "Bearer " + token)
                    .DELETE()
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    // --- متدهای عمومی (کیف پول، پرداخت، تراکنش) ---
    public static ApiResponse topUpWallet(int amount) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            TopUpWalletRequest requestData = new TopUpWalletRequest(amount);
            String jsonBody = gson.toJson(requestData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/wallet/top-up"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    public static ApiResponse makePayment(PaymentRequest paymentRequest) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            String jsonBody = gson.toJson(paymentRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/payment/online"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    public static ApiResponse getTransactionHistory() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/transactions"))
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

    // --- متدهای پیک ---
    public static ApiResponse getAvailableDeliveries() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/courier/deliveries/available"))
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

    public static ApiResponse updateDeliveryStatus(long orderId, String status) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            String jsonBody = gson.toJson(Map.of("status", status));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/courier/deliveries/" + orderId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    public static ApiResponse getDeliveryHistory() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/courier/deliveries/history"))
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

    // --- متدهای صاحب رستوران ---
    public static ApiResponse createRestaurant(CreateRestaurantRequest restaurantData) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            String jsonBody = gson.toJson(restaurantData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/restaurant/restaurants"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
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
                    .uri(URI.create(API_BASE_URL + "/api/restaurant/restaurants/mine"))
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

    public static ApiResponse getRestaurantOrders(long restaurantId) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/restaurant/restaurants/" + restaurantId + "/orders"))
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

    public static ApiResponse updateOrderStatus(long orderId, String status) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            String jsonBody = gson.toJson(Map.of("status", status));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/restaurant/orders/" + orderId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    // [جدید] متدهای مدیریت آیتم‌های غذایی که حذف شده بودند
    public static ApiResponse addFoodItem(long restaurantId, String menuTitle, AddFoodItemRequest itemData) {
        String token = SessionManager.getInstance().getToken();
        if (token == null) return new ApiResponse(401, "{\"error\":\"Not logged in\"}");
        try {
            String jsonBody = gson.toJson(itemData);
            String url = API_BASE_URL + "/api/restaurant/restaurants/" + restaurantId + "/menu/" + menuTitle + "/item";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"Server connection error\"}");
        }
    }

    public static ApiResponse updateFoodItem(long restaurantId, long itemId, UpdateFoodItemRequest itemData) {
        String token = SessionManager.getInstance().getToken();
        if (token == null) return new ApiResponse(401, "{\"error\":\"Not logged in\"}");
        try {
            String jsonBody = gson.toJson(itemData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/restaurant/restaurants/" + restaurantId + "/item/" + itemId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            return new ApiResponse(0, "{\"error\":\"Server connection error\"}");
        }
    }

    public static ApiResponse removeItemFromMenu(long restaurantId, String menuTitle, long itemId) {
        String token = SessionManager.getInstance().getToken();
        if (token == null) return new ApiResponse(401, "{\"error\":\"Not logged in\"}");
        try {
            String url = API_BASE_URL + "/api/restaurant/restaurants/" + restaurantId + "/menu/" + menuTitle + "/" + itemId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .DELETE()
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            return new ApiResponse(0, "{\"error\":\"Server connection error\"}");
        }
    }

    public static ApiResponse deleteFoodItem(long restaurantId, long itemId) {
        String token = SessionManager.getInstance().getToken();
        if (token == null) return new ApiResponse(401, "{\"error\":\"Not logged in\"}");
        try {
            String url = API_BASE_URL + "/api/restaurant/restaurants/" + restaurantId + "/item/" + itemId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .DELETE()
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            return new ApiResponse(0, "{\"error\":\"Server connection error\"}");
        }
    }
}
