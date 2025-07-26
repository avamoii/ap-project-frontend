package org.example.approjectfrontend;

import java.util.List;

public class Order {
    private String restaurantName;
    private String address;
    private List<RestaurantMenuItem> items;
    private int totalPrice;
    private String status;
    public Order(String restaurantName, String address, List<RestaurantMenuItem> items, int totalPrice,String status) {
        this.restaurantName = restaurantName;
        this.address = address;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public String getRestaurantName() { return restaurantName; }
    public String getAddress() { return address; }
    public List<RestaurantMenuItem> getItems() { return items; }
    public int getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    @Override
    public String toString() {
        return "رستوران: " + restaurantName +
                " | آدرس: " + address +
                " | مبلغ کل: " + totalPrice + " تومان";
    }
}
