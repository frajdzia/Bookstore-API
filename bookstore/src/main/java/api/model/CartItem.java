package api.model;

// Represents an item in a cart
public class CartItem {
    private int bookId;
    private int quantity;

    // Default constructor
    public CartItem() {}

    // Constructor
    public CartItem(int bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    // Gets the book ID
    public int getBookId() {
        return bookId;
    }

    // Sets the book ID
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    // Gets the quantity
    public int getQuantity() {
        return quantity;
    }

    // Sets the quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}