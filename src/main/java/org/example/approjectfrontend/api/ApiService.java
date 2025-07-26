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

    // --- Authentication & Profile Endpoints ---

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

    public static ApiResponse logout() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(200, "{\"message\":\"Already logged out.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/auth/logout"))
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

    public static ApiResponse getProfile() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/auth/profile"))
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
                    .uri(URI.create(API_BASE_URL + "/auth/profile"))
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

    // --- Restaurant & Vendor Endpoints ---

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

    public static ApiResponse getVendors() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/vendors"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(httpResponse.statusCode(), httpResponse.body());
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(0, "{\"error\":\"خطا در اتصال به سرور.\"}");
        }
    }

    // --- Menu and Food Item Endpoints ---

    public static ApiResponse getRestaurantMenu(long restaurantId) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/vendors/" + restaurantId))
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

    public static ApiResponse createMenu(long restaurantId, String title) {
        String token = SessionManager.getInstance().getToken();
        if (token == null) return new ApiResponse(401, "{\"error\":\"Not logged in\"}");
        try {
            String jsonBody = gson.toJson(new CreateMenuRequest(title));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/restaurants/" + restaurantId + "/menu"))
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

    public static ApiResponse addFoodItem(long restaurantId, String menuTitle, AddFoodItemRequest itemData) {
        String token = SessionManager.getInstance().getToken();
        if (token == null) return new ApiResponse(401, "{\"error\":\"Not logged in\"}");
        try {
            String jsonBody = gson.toJson(itemData);
            String url = API_BASE_URL + "/restaurants/" + restaurantId + "/menu/" + menuTitle + "/item";
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
                    .uri(URI.create(API_BASE_URL + "/restaurants/" + restaurantId + "/item/" + itemId))
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
            String url = API_BASE_URL + "/restaurants/" + restaurantId + "/menu/" + menuTitle + "/" + itemId;
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
            String url = API_BASE_URL + "/restaurants/" + restaurantId + "/item/" + itemId;
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


    public static ApiResponse getFoodItemDetails(long itemId) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/items/" + itemId))
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
            String jsonBody = new Gson().toJson(orderData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/orders"))
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
    public static ApiResponse getOrderHistory() {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/orders/history"))
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

    public static ApiResponse getOrderDetails(long orderId) {
        String token = SessionManager.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            return new ApiResponse(401, "{\"error\":\"User not logged in.\"}");
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/orders/" + orderId))
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
}