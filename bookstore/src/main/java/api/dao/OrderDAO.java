package api.dao;

import api.exception.*;
import api.model.Book;
import api.model.Order;
import api.model.Cart;
import api.model.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

// manages Order entities in memory
public class OrderDAO {

    // logger creation
    private static final Logger logger = LoggerFactory.getLogger(OrderDAO.class);

    // singleton instance
    private static final OrderDAO instance = new OrderDAO();

    // in-memory storage for orders
    private final Map<Integer, List<Order>> orders = new HashMap<>(); // maps customerId to their orders

    // reference to cart
    private final CartDAO cartDAO = CartDAO.getInstance();

    // reference to book
    private final BookDAO bookDAO = BookDAO.getInstance();

    // logger for creation
    private OrderDAO() {
        logger.debug("Created new OrderDAO instance");
    }

    // gets the singleton instance
    public static OrderDAO getInstance() {
        return instance;
    }

    // creates an order for a customer
    public synchronized Order placeOrder(int customerId) {
        // get the customer's cart
        Cart cart = cartDAO.getCartByCustomerId(customerId);

        // if cart not found
        if (cart == null) {
            logger.warn("Cannot place order: no cart found for customer ID: {}", customerId);
            throw new CartNotFoundException("Cart not found for customer ID: " + customerId);
        }

        // get cart items
        List<CartItem> items = cart.getItems();

        // if cart is empty
        if (items == null || items.isEmpty()) {
            logger.warn("Cannot place order: cart is empty for customer ID: {}", customerId);
            throw new InvalidInputException("Cart is empty for customer ID: " + customerId);
        }

        // check stock and update quantities
        for (CartItem item : items) {
            Book book = bookDAO.getBook(item.getBookId());

            // check if enough stock
            if (book.getQuantity() < item.getQuantity()) {
                logger.warn("Out of stock for book ID: {} (requested {}, available {})",
                        item.getBookId(), item.getQuantity(), book.getQuantity());
                throw new OutOfStockException("Book ID " + item.getBookId() + " has only " + book.getQuantity() + " in stock");
            }

            // deduct stock
            book.setQuantity(book.getQuantity() - item.getQuantity());
            bookDAO.updateBook(book.getId(), book);
            logger.info("Updated stock for book ID: {}, new quantity: {}", book.getId(), book.getQuantity());
        }

        // create the order
        int orderId = generateNextOrderId(customerId);
        Order newOrder = new Order(orderId, customerId, new ArrayList<>(items));
        orders.computeIfAbsent(customerId, k -> new ArrayList<>()).add(newOrder);
        logger.info("Order ID: {} created for customer ID: {}", orderId, customerId);

        // clear the cart after order placement
        cartDAO.removeCart(customerId);
        logger.info("Cart cleared for customer ID: {} after order placement", customerId);

        return newOrder;
    }

    // retrieves all orders for a customer
    public List<Order> getOrdersByCustomerId(int customerId) {
        // get orders for customer
        List<Order> customerOrders = orders.getOrDefault(customerId, new ArrayList<>());

        // if no orders found
        if (customerOrders.isEmpty()) {
            logger.warn("No orders found for customer ID: {}", customerId);
            throw new OrderNotFoundException("No orders found for customer ID: " + customerId);
        }

        // success info
        logger.info("Fetched {} orders for customer ID: {}", customerOrders.size(), customerId);
        return customerOrders;
    }

    // finds an order by customer ID and order ID
    public Order getOrder(int customerId, int orderId) {
        // get orders for customer
        List<Order> customerOrders = orders.getOrDefault(customerId, new ArrayList<>());
        Order order = customerOrders.stream()
                .filter(o -> o.getId() == orderId)
                .findFirst()
                .orElse(null);

        // if order not found
        if (order == null) {
            logger.warn("Order ID: {} not found for customer ID: {}", orderId, customerId);
            throw new OrderNotFoundException("Order ID: " + orderId + " not found for customer ID: " + customerId);
        }

        // success info
        logger.info("Found order ID: {} for customer ID: {}", orderId, customerId);
        return order;
    }

    // generates the next order ID for a customer
    private int generateNextOrderId(int customerId) {
        // get orders for customer
        List<Order> customerOrders = orders.getOrDefault(customerId, new ArrayList<>());
        // find max id and increment
        return customerOrders.stream()
                .map(Order::getId)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }
}