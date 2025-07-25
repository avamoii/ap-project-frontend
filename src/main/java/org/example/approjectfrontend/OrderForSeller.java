package org.example.approjectfrontend;

import java.util.List;

public class OrderForSeller {
    private List<RestaurantMenuItem> items;
    private int totalPrice;
    private String buyerUsername;
    private String buyerAddress;
    private String buyerPhone;
    private String status;
    public OrderForSeller(List<RestaurantMenuItem> items, int totalPrice,
                          String buyerUsername, String buyerAddress, String buyerPhone, String status) {
        this.items = items;
        this.totalPrice = totalPrice;
        this.buyerUsername = buyerUsername;
        this.buyerAddress = buyerAddress;
        this.buyerPhone = buyerPhone;
        this.status = status;
    }

    public List<RestaurantMenuItem> getItems() { return items; }
    public int getTotalPrice() { return totalPrice; }
    public String getBuyerUsername() { return buyerUsername; }
    public String getBuyerAddress() { return buyerAddress; }
    public String getBuyerPhone() { return buyerPhone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

