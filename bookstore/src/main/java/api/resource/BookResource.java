package api.resource;

import api.model.Book;
import api.dao.BookDAO;
import api.exception.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.slf4j.*;
import java.util.*;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {
    private static final Logger logger = LoggerFactory.getLogger(BookResource.class);
    private final BookDAO bookDAO = BookDAO.getInstance();

    // POST /books
    // creates a new book
    @POST
    public Response addBook(Book newBook) {
        logger.info("Creating a new book: {}", newBook.getTitle());
        String message = bookDAO.addBook(newBook);
        return Response.status(Response.Status.CREATED).entity(message).build();
    }

    // GET /books
    // retrieves all books
    @GET
    public Collection<Book> getAllBooks() {
        logger.info("Getting all books");
        Collection<Book> books = bookDAO.getAllBooks();
        if (books.isEmpty()) {
            logger.warn("No books found in the bookstore");
            throw new BookNotFoundException("There are no books registered");
        }
        return books;
    }

    // GET /books/{id}
    // fetches a book by ID
    @GET
    @Path("/{id}")
    public Book getBook(@PathParam("id") int id) {
        logger.info("Retrieving book ID: {}", id);
        Book book = bookDAO.getBook(id);
        if (book == null) {
            logger.warn("Book ID: {} not found", id);
            throw new BookNotFoundException("Book with ID " + id + " not found");
        }
        return book;
    }

    // PUT /books/{id}
    // updates a book
    @PUT
    @Path("/{id}")
    public Response updateBook(@PathParam("id") int id, Book bookUpdateRequest) {
        logger.info("Updating book ID: {}", id);
        String message = bookDAO.updateBook(id, bookUpdateRequest);
        return Response.ok(message).build();
    }

    // DELETE /books/{id}
    // removes a book
    @DELETE
    @Path("/{id}")
    public Response removeBook(@PathParam("id") int id) {
        logger.info("Deleting book ID: {}", id);
        String message = bookDAO.removeBook(id);
        return Response.ok(message).build();
    }
}