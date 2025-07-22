// مسیر: src/main/java/org/example/approjectfrontend/util/SessionManager.java
package org.example.approjectfrontend.util;

import org.example.approjectfrontend.api.UserDTO;

public class SessionManager {
    private static SessionManager instance;

    private String token;
    private UserDTO currentUser; // <-- فیلد جدید برای نگهداری اطلاعات کاربر

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    // متدهای جدید برای مدیریت اطلاعات کاربر
    public UserDTO getCurrentUser() { return currentUser; }
    public void setCurrentUser(UserDTO user) { this.currentUser = user; }

    public boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    public void clear() {
        this.token = null;
        this.currentUser = null;
    }
}