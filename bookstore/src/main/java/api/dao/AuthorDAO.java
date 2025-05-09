package api.dao;

import api.exception.*;
import api.model.Author;
import api.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

// manages author entities - CRUD
public class AuthorDAO {

    // logger
    private static final Logger logger = LoggerFactory.getLogger(BookDAO.class);
    // in-memory storage for authors
    // maps author id to Author object
    private static Map<Integer, Author> authors = new HashMap<>();
    // instance of authordao
    private static AuthorDAO instance;

    // retrieves instance od authordao
    // synchronized for safe creation
    public static synchronized AuthorDAO getInstance() {
        if (instance == null) {
            instance = new AuthorDAO();
        }
        return instance;
    }

//    reusable validation
    private void validateAuthorFields(Author author) {
        ValidationUtils.validateStringField(author.getName(), "Name", false);
        ValidationUtils.validateStringField(author.getBiography(), "Biography", false);
    }

    // Create author
    public String addAuthor(Author newAuthor) {

//        Fields validation
        validateAuthorFields(newAuthor);

        // gives unique id for author
        int newAuthorId = generateNextAuthorId();
        newAuthor.setId(newAuthorId);
        // author stored in hashmap
        authors.put(newAuthorId, newAuthor);

        // log and info of success
        logger.info("Added author ID: {}", newAuthorId);
        return "Author ID: " + newAuthorId + " was created";
    }

    // generate next available author ID
    // stream used for finding max id
    //deafult to 0 if no id's and then it adds 1 to the value
    private int generateNextAuthorId() {
        return authors.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
    }

    // Get all authors
    public Collection<Author> getAllAuthors() {
        // if storage empty
        if (authors.isEmpty()) {
            // info if none found
            logger.info("No authors available for display, returning empty collection.");
            throw new AuthorNotFoundException("There are no authors registered");
        } else {
            // shows num of authors
            logger.info("Fetching all authors, amount of them: {}", authors.size());
        }
        // shows authors
        return authors.values();
    }

    // Find author by id
    public Author getAuthor(int id) {
        // try getting author from hashmapp
        Author author = authors.get(id);
        if (author == null) {
            // if not found
            logger.warn("Author ID: {} not found", id);
            throw new AuthorNotFoundException("Author ID: " + id + " not found");
        } else {
            // if success shows authors data
            logger.info("Fetching author ID: {}", id);
            return author;
        }
    }

    // Update existing author
    public String updateAuthor(int id, Author updateAuthor) {
        // if not found
        if (!authors.containsKey(id)) {
            // shows not found
            logger.warn("Author ID: {} not found.", id);
            throw new AuthorNotFoundException("Author ID: " + id + " not found.");
        }

        // retrieves existing one
        Author existingAuthor = authors.get(id);
        
        // updates name if provided
        if (updateAuthor.getName() != null) {
            ValidationUtils.validateStringField(updateAuthor.getName(), "Name", false);
            existingAuthor.setName(updateAuthor.getName());
        }

        // updates biography if given
        if (updateAuthor.getBiography() != null) {
            ValidationUtils.validateStringField(updateAuthor.getBiography(), "Biography", false);
            existingAuthor.setBiography(updateAuthor.getBiography());
        }

        // shows success result
        logger.info("Modified author ID: {}", id);
        return "Author ID: " + id + " was updated";
    }

    // Delete an author by id
    public String removeAuthor(int id) {

        //check if exist
        if (!authors.containsKey(id)) {
            // info when not found
            logger.warn("Author ID: {} not found.", id);
            throw new AuthorNotFoundException("Author ID: " + id + " not found.");
        }

        // deletes author
        authors.remove(id);
        // give info of successful deletion
        logger.info("Removed author ID: {}", id);
        return "Author ID: " + id + " was deleted";
    }
}
