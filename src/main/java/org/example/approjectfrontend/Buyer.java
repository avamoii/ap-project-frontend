package org.example.approjectfrontend;

public class Buyer {
    private String username;
    private String email;
    private String address;
    private String phone;

    public Buyer(String username, String email, String address, String phone) {
        this.username = username;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }

    // Getter ها
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
}
