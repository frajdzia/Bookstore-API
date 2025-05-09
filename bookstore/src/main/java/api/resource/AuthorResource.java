package api.resource;

import api.dao.AuthorDAO;
import api.model.Author;
import api.exception.*;
import api.dao.BookDAO;
import api.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.slf4j.*;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

    private static final Logger logger = LoggerFactory.getLogger(AuthorResource.class);

    // DAOs
    private final AuthorDAO authorDAO = AuthorDAO.getInstance();
    private final BookDAO bookDAO = BookDAO.getInstance();

    // POST /authors
    // adds new author
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addAuthor(Author newAuthor) {
        try {
            // Call the DAO to create the author
            String message = authorDAO.addAuthor(newAuthor);

            // Log the author creation
            logger.info("Author {} was created", newAuthor.getId());
            logger.info("Author Details: id={}, name={}, biography={}",
                    newAuthor.getId(), newAuthor.getName(), newAuthor.getBiography());

            // Convert the Author object to JSON string
            ObjectMapper mapper = new ObjectMapper();
            String authorJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(newAuthor);

            // Format message
            String formattedMessage = message.replace("Author ID:", "Author").replace("was created", "was created:");

            // Construct the response string
            String response = formattedMessage + "\n" + authorJson;

            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (InvalidInputException e) {
            // InvalidInputException handled by the ExceptionMapper
            throw e;
        } catch (Exception e) {
            // Handle JSON errors or other unexpected ones
            logger.error("Error processing author creation", e);
            throw new RuntimeException("Failed to process author creation", e);
        }
    }

    // GET /authors
    // lists all authors
    @GET
    public Collection<Author> getAllAuthors() {
        logger.info("Retrieving all authors");
        return authorDAO.getAllAuthors();
    }

    // GET /authors/{id}
    // gets author by ID
    @GET
    @Path("/{id}")
    public Author getAuthor(@PathParam("id") int id) {
        logger.info("Retrieving author ID: {}", id);
        Author author = authorDAO.getAuthor(id);
        if (author == null) {
            logger.warn("Author ID: {} not found", id);
            throw new AuthorNotFoundException("Author ID: " + id + " not found");
        }
        return author;
    }

    // PUT /authors/{id}
    // modifies author
    @PUT
    @Path("/{id}")
    public Response updateAuthor(@PathParam("id") int id, Author aupdateRequest) {
        logger.info("Updating author ID: {}", id);
        String result = authorDAO.updateAuthor(id, aupdateRequest);
        return Response.ok(result).build();
    }

    // DELETE /authors/{id}
    // deletes author
    @DELETE
    @Path("/{id}")
    public Response removeAuthor(@PathParam("id") int id) {
        logger.info("Deleting author ID: {}", id);
        String result = authorDAO.removeAuthor(id);
        return Response.ok(result).build();
    }

    // GET /authors/{id}/books
    // fetches books by author
    @GET
    @Path("/{id}/books")
    public Collection<Book> getBooksByAuthorId(@PathParam("id") int id) {
        logger.info("Retrieving books for author ID: {}", id);
        Author author = authorDAO.getAuthor(id);
        if (author == null) {
            logger.warn("Author ID: {} not found", id);
            throw new AuthorNotFoundException("Author ID: " + id + " not found");
        }

        // Filter books whose author field matches
        Collection<Book> books = bookDAO.getAllBooks().stream()
                .filter(book -> book.getAuthorId() == id)
                .collect(Collectors.toList());

        // If no books of author
        if (books.isEmpty()) {
            logger.warn("No books found for author ID: {}", id);
            throw new BookNotFoundException("No books found for author ID: " + id);
        }

        String authorName = author.getName();
        logger.info("Retrieved books by author: {} with author ID: {}", authorName, id);
        return books;
    }
}