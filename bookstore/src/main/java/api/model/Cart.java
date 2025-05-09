package api.model;

import java.util.ArrayList;
import java.util.List;

// Represents a cart in the bookstore system
public class Cart {
    private Integer customerId;
    private List<CartItem> items;

    // Default constructor
    public Cart() {}

    // Constructor
    public Cart(Integer customerId, List<CartItem> items) {
        this.customerId = customerId;
        this.items = new ArrayList<>();
    }

    // Gets the customer ID
    public Integer getCustomerId() {
        return customerId;
    }

    // Sets the customer ID
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    // Gets the list of cart items
    public List<CartItem> getItems() {
        return items;
    }

    // Sets the list of cart items
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}