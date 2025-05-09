package api.dao;

import api.exception.*;
import api.util.ValidationUtils;
import api.model.Cart;
import api.model.Book;
import api.model.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

// for CRUD for Cart
public class CartDAO {

    // logger creation
    private static final Logger logger = LoggerFactory.getLogger(CartDAO.class);
    // signleton cart instance
    private static CartDAO instance;
    // in- memory storage
    private static final Map<Integer, Cart> carts = new HashMap<>();

    // reference of book
    private final BookDAO bookDAO = BookDAO.getInstance();
    // reference of customer
    private final CustomerDAO customerDAO = new CustomerDAO();

    // logger for creation
    private CartDAO() {
        logger.debug("Created new CartDAO instance");
    }

    //validation of fields
    private void validateCartFields(Cart cart) {
        int customerId = cart.getCustomerId();

        // if customer not found
        if (customerDAO.getCustomer(customerId) == null) {
            logger.warn("Cannot create cart: Customer ID {} does not exist", customerId);
            throw new CustomerNotFoundException("Customer ID " + customerId + " does not exist");
        }

        // validates data
        ValidationUtils.validateList(cart.getItems(), "Cart items", false);

        // validate quantity of each item
        for (CartItem item : cart.getItems()) {
            ValidationUtils.validateNumericField(item.getQuantity(), "Quantity for book ID: " + item.getBookId(), false);
        }
    }

    // synchronized safe creation
    public static synchronized CartDAO getInstance() {
        // create new cart only if it doesnt exist
        if (instance == null) {
            instance = new CartDAO();
        }
        return instance;
    }

    // Get all carts
    public Collection<Cart> getAllCarts() {
        // if none found
        if (carts.isEmpty()) {
            logger.warn("Cannot retrieve all carts. Empty");
            throw new CartNotFoundException("Cannot retrieve all carts. Empty");
        }
        // shows carts when found
        logger.info("Retrieving all carts");
        return carts.values();
    }

    // Get a cart by customerId
    public Cart getCartByCustomerId(int customerId) {
        if (!carts.containsKey(customerId)) {
            // ifnot if not found
            logger.warn("Cannot retrieve cart for customer ID: {}", customerId);
            throw new CartNotFoundException("Cart for customer ID " + customerId + " not found");
        }
        // success info
        logger.info("Retrieving cart for customer ID: {}", customerId);
        return carts.get(customerId);
    }

    // Create a cart
    public String addCart(Cart newCart) {
        // gget id of customer
        int customerId = newCart.getCustomerId();

        // if already exist
        if (carts.containsKey(customerId)) {
            Cart existingCart = carts.get(customerId);

            if (!existingCart.getItems().isEmpty()) {
                logger.warn("Cart with customer ID: {} already exists and is not empty", customerId);
                throw new InvalidInputException("Cart already exists for customer ID: " + customerId + " and is not empty");
            } else {
                // Overwrite the empty cart
                logger.info("Overwriting empty cart for customer ID: {}", customerId);
            }
        }

        // validate id to ensure if customer exist
        if (customerDAO.getCustomer(customerId) == null) {
            logger.warn("Cannot create cart: Customer ID {} does not exist", customerId);
            throw new CustomerNotFoundException("Customer ID " + customerId + " does not exist");
        }

        // validate cart fields
        validateCartFields(newCart);
        // check if book available
        // deduct from stock if available
        validateBookQuantity(newCart.getItems());

        // store new cart
        carts.put(customerId, newCart);
        logger.info("Cart with customer ID: {} created successfully", customerId);
        return "Cart with customer ID " + customerId + " created successfully";
    }

    // Update a cart
    public String updateCart(int customerId, Cart updateCart) {
        if (!carts.containsKey(customerId)) {
            // if cart  dont exist
            logger.warn("Cart with customer ID: {} not found", customerId);
            throw new CartNotFoundException("Cart with customer ID " + customerId + " not found");
        }

        // check book availability
        // if available deduct from stock
        validateBookQuantity(updateCart.getItems());

        // update items
        Cart existingCart = carts.get(customerId);
        existingCart.setItems(updateCart.getItems());

        // success info
        logger.info("Cart with customer ID: {} was updated", customerId);
        return "Cart with customer ID " + customerId + " updated successfully";
    }

    // Delete a cart
    public synchronized String removeCart(int customerId) {
        if (!carts.containsKey(customerId)) {
            // info if not found
            logger.warn("Cart with customer ID: {} not found", customerId);
            throw new CartNotFoundException("Cart with customer ID " + customerId + " not found");
        }

        // Restore stock for all items in the cart
        Cart cart = carts.get(customerId);
        logger.info("Cart items before delete customer ID: {}: {}", customerId, cart.getItems());
        restoreStockForItems(cart.getItems());

        // synchronized delete
        synchronized (carts) {
            carts.remove(customerId);
            //ibfo of success
            logger.info("Cart with customer ID: {} deleted successfully", customerId);
            return "Cart with customer ID " + customerId + " deleted successfully";
        }
    }

    // Delete a specific item from the cart
    public String removeCartItem(int customerId, int bookId) {
        if (!carts.containsKey(customerId)) {
            // info if not found customers cart
            logger.warn("Cart with customer ID: {} not found", customerId);
            throw new CartNotFoundException("Cart with customer ID " + customerId + " not found");
        }

        // retrieve  cart
        Cart cart = carts.get(customerId);
        CartItem itemToRemove = null;
        
        // find chosen item in cart
        for (CartItem item : cart.getItems()) {
            if (item.getBookId() == bookId) {
                itemToRemove = item;
                break;
            }
        }

        // if not found
        if (itemToRemove == null) {
            logger.warn("Book ID: {} not found in cart for customer ID: {}", bookId, customerId);
            throw new InvalidInputException("Book ID " + bookId + " does not exist");
        }

        // restore stock or removed item
        restoreStockForItem(itemToRemove);
        //remove item from cart
        cart.getItems().remove(itemToRemove);

        // If the cart is now empty remove it from the carts map
        if (cart.getItems().isEmpty()) {
            synchronized (carts) {
                carts.remove(customerId);
            }
            logger.info("Cart for customer ID: {} is now empty and has been removed", customerId);
        }

        // info wheen successful deletetion of item
        logger.info("Book ID: {} removed from cart for customer ID: {}", bookId, customerId);
        return "Item with book ID " + bookId + " deleted successfully";
    }

    // Validate book availability
    public void validateBookQuantity(List<CartItem> items) {
        // Validate stock without modifying it
        for (CartItem item : items) {
            Book book = bookDAO.getBook(item.getBookId()); // Get from DAO

            // check if book exist
            if (book == null) {
                // info if not found
                logger.warn("Book ID: {} not found", item.getBookId());
                throw new BookNotFoundException("Cannot find book ID: " + item.getBookId());
            }

            // check stock
            if (book.getQuantity() < item.getQuantity()) {
                // if more books requested than in stock
                // gives info of not success of operation
                logger.warn("Out of stock for book ID: {} (requested {}, available {})",
                        item.getBookId(), item.getQuantity(), book.getQuantity());
                throw new OutOfStockException("Book ID " + item.getBookId()
                        + " has only " + book.getQuantity() + " in stock");
            }
        }

        // deduct stock when requested books are available
        for (CartItem item : items) {
            Book book = bookDAO.getBook(item.getBookId());
            book.setQuantity(book.getQuantity() - item.getQuantity());
            bookDAO.updateBook(book.getId(), book);
            logger.info("Deducted {} units of book ID: {} from stock (new quantity: {})",
                    item.getQuantity(), item.getBookId(), book.getQuantity());
        }
    }

    // restore stock for a single item
    public void restoreStockForItem(CartItem item) {
        Book book = bookDAO.getBook(item.getBookId());
        book.setQuantity(book.getQuantity() + item.getQuantity());
        bookDAO.updateBook(book.getId(), book);
        logger.info("Restored {} units of book ID: {} to stock (new quantity: {})",
                item.getQuantity(), item.getBookId(), book.getQuantity());
    }

    // restore stock for a list of items
    private void restoreStockForItems(List<CartItem> items) {
        for (CartItem item : items) {
            restoreStockForItem(item);
        }
    }
}
