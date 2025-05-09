package api.resource;

import api.exception.*;
import api.dao.CustomerDAO;
import api.model.Customer;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.slf4j.*;
import java.util.*;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {
    private static final Logger logger = LoggerFactory.getLogger(CustomerResource.class);
    private final CustomerDAO customerDAO = new CustomerDAO();

    // POST /customers
    // adds a new customer
    @POST
    public Response addCustomer(Customer newCustomer) {
        String result = customerDAO.addCustomer(newCustomer);
        logger.info("Created customer: {}", newCustomer.getName());
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    // GET /customers
    // lists all customers
    @GET
    public Collection<Customer> getAllCustomers() {
        logger.info("Retrieving all customers");
        return customerDAO.getAllCustomers();
    }

    // GET /customers/{id}
    // gets a customer by ID
    @GET
    @Path("/{id}")
    public Customer getCustomer(@PathParam("id") Integer id) {
        logger.info("Retrieving customer by id: {}", id);
        Customer customer = customerDAO.getCustomer(id);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with id " + id + " not found");
        }
        return customer;
    }

    // PUT /customers/{id}
    // modifies customer information
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Integer id, Customer updateRequest) {
        logger.info("Updating customer with id: {}", id);
        String result = customerDAO.updateCustomer(id, updateRequest);
        return Response.ok(result).build();
    }

    // DELETE /customers/{id}
    // removes a customer
    @DELETE
    @Path("/{id}")
    public Response removeCustomer(@PathParam("id") Integer id) {
        logger.info("Deleting customer with id: {}", id);
        String result = customerDAO.removeCustomer(id);
        return Response.ok(result).build();
    }
}