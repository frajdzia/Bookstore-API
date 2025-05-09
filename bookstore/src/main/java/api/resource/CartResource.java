package api.resource;

import api.dao.CartDAO;
import api.exception.*;
import api.model.Cart;
import api.model.CartItem;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import api.dao.BookDAO;
import api.model.Book;

import java.util.*;

@Path("/customers/{customerId}/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

    private static final Logger logger = LoggerFactory.getLogger(CartResource.class);
    private final CartDAO cartDAO = CartDAO.getInstance();
    private final BookDAO bookDAO = BookDAO.getInstance();

    // POST /customers/{customerId}/cart/items
    // adds a new cart with items
    @POST
    @Path("/items")
    public Response addCart(@PathParam("customerId") int customerId, List<CartItem> items) {
        logger.info("Creating a new cart for customer ID: {}", customerId);
        Cart newCart = new Cart(customerId, items);
        newCart.setItems(items);
        String result = cartDAO.addCart(newCart);
        // success info
        logger.info("Cart created successfully for customer ID: {}", customerId);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    // GET /customers/{customerId}/cart
    // gets a customer's cart
    @GET
    public Cart getCart(@PathParam("customerId") int customerId) {
        logger.info("Getting cart for customer ID: {}", customerId);
        Cart cart = cartDAO.getCartByCustomerId(customerId);
        return cart;
    }

    // PUT /customers/{customerId}/cart/items/{bookId}
    // modifies an item's quantity in the cart
    @PUT
    @Path("/items/{bookId}")
    public Response updateCartItem(@PathParam("customerId") int customerId, @PathParam("bookId") int bookId, CartItem updateItem) {
        logger.info("Updating item in cart for customer ID: {}, book ID: {}", customerId, bookId);
        Cart cart = cartDAO.getCartByCustomerId(customerId);
        if (cart == null) {
            logger.warn("Cart for customer ID: {} not found", customerId);
            throw new CartNotFoundException("Cart not found for customer ID: " + customerId);
        }

        // Check current quantity to adjust stock
        CartItem existingItem = null;
        for (CartItem item : cart.getItems()) {
            if (item.getBookId() == bookId) {
                existingItem = item;
                break;
            }
        }

        Book book = bookDAO.getBook(bookId);
        // info if bbook not found
        if (book == null) {
            logger.warn("Book ID: {} not found", bookId);
            throw new BookNotFoundException("Cannot find book ID: " + bookId);
        }

        // if invalid quantity
        if (updateItem.getQuantity() <= 0) {
            logger.warn("Invalid quantity: {} for book ID: {}", updateItem.getQuantity(), bookId);
            throw new InvalidInputException("Quantity must be greater than 0");
        }

        // Temporarily restore stock for the existing item
        int restoredQuantity = 0;
        if (existingItem != null) {
            restoredQuantity = existingItem.getQuantity();
            cartDAO.restoreStockForItem(existingItem);
            logger.info("Temporarily restored {} units of book ID: {} to stock (new quantity: {})",
                    restoredQuantity, bookId, book.getQuantity());
        }

        List<CartItem> itemsToCheck = new ArrayList<>();
        // Add the updated item
        CartItem tempItem = new CartItem(bookId, updateItem.getQuantity());
        itemsToCheck.add(tempItem);

        // Add quantities of other cart items with the same bookId
        for (CartItem item : cart.getItems()) {
            if (item.getBookId() == bookId && item != existingItem) {
                itemsToCheck.add(new CartItem(bookId, item.getQuantity()));
            }
        }

        // Validate stock and deduct using validateBookQuantity
        boolean stockCheckFailed = false;
        try {
            cartDAO.validateBookQuantity(itemsToCheck);
        } catch (Exception e) {
            // If stock check fails, revert the restoration
            stockCheckFailed = true;
            if (restoredQuantity > 0) {
                book.setQuantity(book.getQuantity() - restoredQuantity);
                bookDAO.updateBook(book.getId(), book);
                logger.info("Reverted restoration: Deducted {} units of book ID: {} from stock (new quantity: {})",
                        restoredQuantity, bookId, book.getQuantity());
            }
            if (e instanceof OutOfStockException) {
                throw e; // Re-throw OutOfStockException
            } else {
                logger.error("Unexpected error during stock check for book ID: {}", bookId, e);
                throw new RuntimeException("Unexpected error during stock check", e);
            }
        }

        if (existingItem != null) {
            // Update existing item
            existingItem.setQuantity(updateItem.getQuantity());
            logger.info("Book ID: {} quantity updated for customer ID: {}", bookId, customerId);
        } else {
            // Add new item
            CartItem newItem = new CartItem(bookId, updateItem.getQuantity());
            cart.getItems().add(newItem);
            logger.info("Book ID: {} added to cart for customer ID: {}", bookId, customerId);
        }

        cartDAO.updateCart(customerId, cart);

        return Response.ok("Item with book ID " + bookId + (existingItem != null ? " updated" : " added") + " successfully").build();
    }

    // DELETE /customers/{customerId}/cart/items/{bookId}
    // deletes an item from the cart
    @DELETE
    @Path("/items/{bookId}")
    public Response removeCartItem(@PathParam("customerId") int customerId, @PathParam("bookId") int bookId) {
        logger.info("Attempting to delete book ID: {} from cart for customer ID: {}", bookId, customerId);
        String result = cartDAO.removeCartItem(customerId, bookId);
        return Response.ok(result).build();
    }
}
