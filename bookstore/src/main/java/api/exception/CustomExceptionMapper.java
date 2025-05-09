package api.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// exceptions mapper
@Provider
public class CustomExceptionMapper implements ExceptionMapper<Throwable> {
    // logger creation
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        // info of processing
        logger.debug("Processing exception: {}", exception.getClass().getSimpleName());

        ErrorResponse errorResponse;
        Response.Status status;

        // Handle custom exceptions
        
//        Author exception
        if (exception instanceof AuthorNotFoundException) {
            errorResponse = new ErrorResponse("Author Not Found", exception.getMessage());
            status = Response.Status.NOT_FOUND;
        }
        
//        Book exception
        else if (exception instanceof BookNotFoundException) {
            errorResponse = new ErrorResponse("Book Not Found", exception.getMessage());
            status = Response.Status.NOT_FOUND;
        } 
        
//        Customer exception
        else if (exception instanceof CustomerNotFoundException) {
            errorResponse = new ErrorResponse("Customer Not Found", exception.getMessage());
            status = Response.Status.NOT_FOUND;
        }
        
//        Cart exception
        else if (exception instanceof CartNotFoundException) {
            errorResponse = new ErrorResponse("Cart Not Found", exception.getMessage());
            status = Response.Status.NOT_FOUND;
        }
        
//        Order exception
        else if (exception instanceof OrderNotFoundException) {
            errorResponse = new ErrorResponse("Order Not Found", exception.getMessage());
            status = Response.Status.NOT_FOUND;
        }       
        
//        Invalid input
        else if (exception instanceof InvalidInputException) {
            errorResponse = new ErrorResponse("Invalid Input", exception.getMessage());
            status = Response.Status.BAD_REQUEST;
        }
        
//        Out of stock exception
        else if (exception instanceof OutOfStockException) {
            errorResponse = new ErrorResponse("Out of Stock", exception.getMessage());
            status = Response.Status.BAD_REQUEST;
        }

//        Unexpected exception
        else {
            logger.error("Unexpected error occurred: {}", exception.getMessage(), exception);
            errorResponse = new ErrorResponse(
                "Server Error",
                "An unexpected error occurred."
            );
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }

        logger.info("Mapped {} to HTTP status {} with response: {}", 
                    exception.getClass().getSimpleName(), status.getStatusCode(), errorResponse.getError());

        // exception response
        return Response.status(status)
                       .entity(errorResponse)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }

//    Exception/error response message
    private static class ErrorResponse {
        private final String error;
        private final String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message != null ? message : "No additional details available";
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}