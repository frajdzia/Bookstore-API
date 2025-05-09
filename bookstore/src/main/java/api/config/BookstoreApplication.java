package api.config;

import api.resource.*;
import api.exception.*;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

// JAX-RS application configuration for the Bookstore API.
// Registers all resource classes and exception mappers.
@ApplicationPath("/api")
public class BookstoreApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // Resource classes
        classes.add(BookResource.class);
        classes.add(AuthorResource.class);
        classes.add(CustomerResource.class);
        classes.add(CartResource.class);
        classes.add(OrderResource.class);

        // Global exception mapper with custom exceptions
        classes.add(CustomExceptionMapper.class);
        return classes;
    }
}