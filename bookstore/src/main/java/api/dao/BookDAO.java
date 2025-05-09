package api.dao;

import api.exception.AuthorNotFoundException;
import api.exception.BookNotFoundException;
import api.exception.InvalidInputException;
import api.util.ValidationUtils;
import api.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// manages Book entities - CRUD
public class BookDAO {

    // logger 
    private static final Logger logger = LoggerFactory.getLogger(BookDAO.class);
    
    // singleton instance of bookdao
    private static final BookDAO instance = new BookDAO();
    // storage for books
    private final Map<Integer, Book> books = new HashMap<>();
    // reference to authordao - to validate author id
    private final AuthorDAO authorDAO = new AuthorDAO();

    // Singleton instance
    private BookDAO() {
        // logger of creation
        logger.debug("Created new BookDAO instance");
    }

    // Gets the singleton instance
    public static BookDAO getInstance() {
        return instance;
    }

    // Validates the fields of a book
    private void validateBookFields(Book book, boolean allowNullFields) {
        // string validation
        ValidationUtils.validateStringField(book.getTitle(), "Title", allowNullFields);
        ValidationUtils.validateStringField(book.getIsbn(), "ISBN", allowNullFields);
        // numbers validation
        ValidationUtils.validateNumericField(book.getPrice(), "Price", allowNullFields);
        ValidationUtils.validateNumericField(book.getQuantity(), "Quantity", allowNullFields);
        // checks ppublication year - cant be n future (year)
        ValidationUtils.checkNotFuture(book.getPublicationYear(), "Publication year", 2025, allowNullFields);

        // Validate authorId if not null
        if (!allowNullFields || book.getAuthorId() != 0) {
            try {
                // checks if author exist
                authorDAO.getAuthor(book.getAuthorId());
            } catch (AuthorNotFoundException e) {
                // info if not found
                logger.warn("Invalid author ID: {} for book creation.", book.getAuthorId());
                throw new InvalidInputException("Author ID: " + book.getAuthorId() + " does not exist.");
            }
        }
    }

    // Retrieves all books
    public Collection<Book> getAllBooks() {
        // check if no books
        if (books.isEmpty()) {
            logger.info("No books available, returning empty collection.");
        } else {
            // if successful shows number of books
            logger.info("Fetching all books, count: {}", books.size());
        }
        // shows books
        return books.values();
    }

    // Finds a book by its ID
    public synchronized Book getBook(int id) {
        Book book = books.get(id);
        // gives info if book exist
        if (book != null) {
            logger.info("Found book ID: {}", id);
            return book;
        } else {
            // info if not found
            logger.warn("Book ID: {} not found.", id);
            throw new BookNotFoundException("Book ID: " + id + " not found.");
        }
    }

    // Adds a new book 
    public String addBook(Book newBook) {
        // if null
        if (newBook == null) {
            logger.warn("Attempted to add a null book.");
            throw new InvalidInputException("Cannot add a null book.");
        }

        // validation of data - no null fields allowed
        validateBookFields(newBook, false);
        // unique id for new book
        int newBookId = generateNextBookId();
        newBook.setId(newBookId);
        // stores new book
        books.put(newBookId, newBook);
        // gives info of success
        logger.info("Added book ID: {}", newBookId);
        return "Book ID: " + newBookId + " created successfully.";
    }

    // Generates the next available book ID
    // stream used for finding max id
    //deafult to 0 if no id's and then it adds 1 to the value
    private int generateNextBookId() {
        return books.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
    }

    // Updates an existing book's information
    public synchronized String updateBook(int id, Book updateBook) {
        if (!books.containsKey(id)) {
            /// info if not found
            logger.warn("Book ID: {} not found.", id);
            throw new BookNotFoundException("Book ID: " + id + " not found.");
        }

        // validation
        // allows null fileds - no change of the part then
        validateBookFields(updateBook, true);
        Book existingBook = books.get(id);
        // updates field if they are provided
        // title
        if (updateBook.getTitle() != null) {
            existingBook.setTitle(updateBook.getTitle());
        }
        // author id
        if (updateBook.getAuthorId() != 0) {
            existingBook.setAuthorId(updateBook.getAuthorId());
        }
        // ibsn
        if (updateBook.getIsbn() != null) {
            existingBook.setIsbn(updateBook.getIsbn());
        }
        // publication year
        if (updateBook.getPublicationYear() != null) {
            existingBook.setPublicationYear(updateBook.getPublicationYear());
        }
        // price 
        if (updateBook.getPrice() != null) {
            existingBook.setPrice(updateBook.getPrice());
        }
        //quantity
        if (updateBook.getQuantity() != null) {
            existingBook.setQuantity(updateBook.getQuantity());
        }
        // info of success 
        logger.info("Modified book ID: {}", id);
        return "Book ID: " + id + " updated successfully.";
    }

    // Deletes book by its ID
    public String removeBook(int id) {
        // if not found
        if (!books.containsKey(id)) {
            logger.warn("Book ID: {} not found.", id);
            throw new BookNotFoundException("Book ID: " + id + " not found.");
        }

        // deletion
        books.remove(id);
        // nfo of success
        logger.info("Removed book ID: {}", id);
        return "Book ID: " + id + " has been deleted.";
    }
}