// مسیر: src/main/java/org/example/approjectfrontend/api/ApiResponse.java
package org.example.approjectfrontend.api;

/**
 * یک کلاس عمومی برای نگهداری پاسخ‌های دریافتی از سرور.
 * این کلاس به ما کمک می‌کند تا هم کد وضعیت HTTP و هم بدنه پاسخ را به راحتی مدیریت کنیم.
 */
public class ApiResponse {
    private final int statusCode;
    private final String body;

    public ApiResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}