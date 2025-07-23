package org.example.approjectfrontend;

import java.util.List;

public class Order {
    private String restaurantName;
    private String address;
    private List<RestaurantMenuItem> items;
    private int totalPrice;

    public Order(String restaurantName, String address, List<RestaurantMenuItem> items, int totalPrice) {
        this.restaurantName = restaurantName;
        this.address = address;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    public String getRestaurantName() { return restaurantName; }
    public String getAddress() { return address; }
    public List<RestaurantMenuItem> getItems() { return items; }
    public int getTotalPrice() { return totalPrice; }

    @Override
    public String toString() {
        return "رستوران: " + restaurantName +
                " | آدرس: " + address +
                " | مبلغ کل: " + totalPrice + " تومان";
    }
}
