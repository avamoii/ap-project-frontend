package org.example.approjectfrontend;

public class SignupResponse {
    private int statusCode;
    private String role;

    public SignupResponse(int statusCode, String role) {
        this.statusCode = statusCode;
        this.role = role;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getRole() {
        return role;
    }
}

