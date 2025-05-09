package api.dao;

import api.exception.*;
import api.util.ValidationUtils;
import api.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

// for CRUD for Customer
public class CustomerDAO {

    // logger creation
    private static final Logger logger = LoggerFactory.getLogger(CustomerDAO.class);

    // in-memory storage
    private static Map<Integer, Customer> customers = new HashMap<>();

    // validation of fields
    private void validateCustomerFields(Customer customer, boolean allowNullOrEmptyFields) {
        // validate name: required for creation, optional for updates
        if (!allowNullOrEmptyFields || (customer.getName() != null && !customer.getName().isEmpty())) {
            ValidationUtils.validateStringField(customer.getName(), "Name", false);
        }

        // validate email: required for creation, optional for updates
        if (!allowNullOrEmptyFields || (customer.getEmail() != null && !customer.getEmail().isEmpty())) {
            ValidationUtils.validateEmailFormat(customer.getEmail(), "Email", false);
        }

        // validate password: required for creation, optional for updates
        if (!allowNullOrEmptyFields || (customer.getPassword() != null && !customer.getPassword().isEmpty())) {
            ValidationUtils.checkMinLength(customer.getPassword(), "Password", 6, false);
        }
    }
    
    // get all customers
    public Collection<Customer> getAllCustomers() {
        // if none found
        if (customers.isEmpty()) {
            logger.warn("Cannot retrieve any customers. Empty");
            throw new CustomerNotFoundException("Cannot find any customers.");
        }

        // shows customers when found
        logger.info("Retrieving all customers");
        return customers.values();
    }

    // get customer by id
    public Customer getCustomer(int id) {
        // retrieve customer
        Customer customer = customers.get(id);

        // if not found
        if (customer == null) {
            logger.warn("Cannot retrieve customer ID: {}", id);
            throw new CustomerNotFoundException("Cannot find customer ID: " + id);
        }

        // success info
        logger.info("Retrieving customer with ID: {}", id);
        return customer;
    }

    // create a customer
    public String addCustomer(Customer newCustomer) {
        // validate customer fields
        validateCustomerFields(newCustomer, false);
        
        // generate new id
        int newCustomerId = generateNextCustomerId();
        newCustomer.setId(newCustomerId);

        // store new customer
        customers.put(newCustomerId, newCustomer);

        // success info
        logger.info("Created customer ID: {}", newCustomerId);
        return "Customer ID: " + newCustomerId + " was created";
    }

    // generate next customer id
    private int generateNextCustomerId() {
        // find max id and increment
        return customers.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
    }

    // update a customer
    public String updateCustomer(int id, Customer updateCustomer) {
        // check if customer exists
        if (customers.containsKey(id)) {
            // retrieve existing customer
            Customer existingCustomer = customers.get(id);

            // validate updated fields
            validateCustomerFields(updateCustomer, true);

            // update fields if provided
            if (updateCustomer.getName() != null && !updateCustomer.getName().isEmpty()) {
                existingCustomer.setName(updateCustomer.getName());
            }
            if (updateCustomer.getEmail() != null && !updateCustomer.getEmail().isEmpty()) {
                existingCustomer.setEmail(updateCustomer.getEmail());
            }
            if (updateCustomer.getPassword() != null && !updateCustomer.getPassword().isEmpty()) {
                existingCustomer.setPassword(updateCustomer.getPassword());
            }

            // success info
            logger.info("Updated customer ID: {} successfully", id);
            return "Customer ID: " + id + " was updated";
        } else {
            // info if not found
            logger.warn("Customer ID: {} was not found", id);
            throw new CustomerNotFoundException("Customer ID: " + id + " was not found");
        }
    }

    // delete a customer
    public String removeCustomer(int id) {
        // check if customer exists
        if (customers.containsKey(id)) {
            // remove customer
            customers.remove(id);

            // success info
            logger.info("Removed customer ID: {}", id);
            return "Customer ID: " + id + " was deleted";
        } else {
            // info if not found
            logger.warn("Customer ID: {} was not found", id);
            throw new CustomerNotFoundException("Customer ID: " + id + " was not found");
        }
    }
}