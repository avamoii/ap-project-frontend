package org.example.approjectfrontend;

public class LoginResponse {
    private int statusCode;
    private String role;
    public LoginResponse(int statusCode, String role) {
        this.statusCode = statusCode;
        this.role = role;
    }
    public int getStatusCode() { return statusCode; }
    public String getRole() { return role; }
}
