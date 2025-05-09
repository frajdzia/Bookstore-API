package api.model;

import java.util.List;

// Represents an order in the bookstore system
public class Order {
    private int id;
    private Integer customerId;
    private List<CartItem> items;

    // Default constructor
    public Order() {}

    // Constructor
    public Order(int id, Integer customerId, List<CartItem> items) {
        this.id = id;
        this.customerId = customerId;
        this.items = items;
    }

    // Gets the ID
    public int getId() {
        return id;
    }

    // Sets the ID
    public void setId(int id) {
        this.id = id;
    }

    // Gets the customer ID
    public Integer getCustomerId() {
        return customerId;
    }

    // Sets the customer ID
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    // Gets the list of items
    public List<CartItem> getItems() {
        return items;
    }

    // Sets the list of items
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}