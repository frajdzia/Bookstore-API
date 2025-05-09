package api.resource;

import api.dao.CustomerDAO;
import api.model.Order;
import api.dao.OrderDAO;
import api.exception.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.slf4j.*;
import java.util.*;

@Path("/customers/{customerId}/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private static final Logger logger = LoggerFactory.getLogger(OrderResource.class);
    private final OrderDAO orderDAO = OrderDAO.getInstance();
    private final CustomerDAO customerDAO = new CustomerDAO();

    // Verifies that a customer exists
    // gives info if not
    private void validateCustomerExists(int customerId) {
        if (customerDAO.getCustomer(customerId) == null) {
            logger.warn("Customer ID: {} not found", customerId);
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found");
        }
    }

    // POST /customers/{customerId}/orders
    // creates an order for a customer
    @POST
    public Response placeOrder(@PathParam("customerId") int customerId) {
        logger.info("Placing order for customer ID: {}", customerId);
        try {
            validateCustomerExists(customerId);
            Order newOrder = orderDAO.placeOrder(customerId);
            // success info
            logger.info("Order ID: {} placed for customer ID: {}", newOrder.getId(), customerId);
            return Response.status(Response.Status.CREATED).entity(newOrder).build();
        } catch (CustomerNotFoundException | CartNotFoundException e) {
            // info if customer/cart not found
            logger.warn("Failed to place order for customer ID: {}", customerId);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    // GET /customers/{customerId}/orders
    // lists all orders for a customer
    @GET
    public List<Order> getOrdersByCustomerId(@PathParam("customerId") int customerId) {
        validateCustomerExists(customerId);
        logger.info("Retrieving all orders for customer ID: {}", customerId);
        return orderDAO.getOrdersByCustomerId(customerId);
    }

    // GET /customers/{customerId}/orders/{orderId}
    // gets an order by ID
    @GET
    @Path("/{orderId}")
    public Order getOrder(@PathParam("customerId") int customerId, @PathParam("orderId") int orderId) {

        validateCustomerExists(customerId);

        logger.info("Retrieving order ID: {} for customer ID: {}", orderId, customerId);
        Order order = orderDAO.getOrder(customerId, orderId);
        if (order == null) {
            // if order is not found for customer
            logger.warn("Order ID: {} not found for customer ID: {}", orderId, customerId);
            throw new NotFoundException("Order ID: " + orderId + " not found for customer ID: " + customerId);
        }
        return order;
    }
}
