// مسیر: src/main/java/org/example/approjectfrontend/api/CreateMenuRequest.java
package org.example.approjectfrontend.api;

import com.google.gson.annotations.SerializedName;

public class CreateMenuRequest {
    @SerializedName("title")
    private final String title;

    public CreateMenuRequest(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}