package org.example.approjectfrontend;

public class MockUser {    private String username;
    private String password;
    private String email;
    private String phone;
    private String role;
    private String address;

    public MockUser(String username, String password, String email, String phone, String role, String address) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }


    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public String getAddress() { return address; }
    public void setAddress(String address) {
        this.address = address;
    }
}
