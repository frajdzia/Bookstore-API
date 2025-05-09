package api.model;

// Represents book in the bookstore system
public class Book {
    private Integer id;
    private String title;
    private int authorId;
    private String isbn;
    private Integer publicationYear;
    private Double price;
    private Integer quantity;

    // Default constructor
    public Book() {}

    // Constructor
    public Book(Integer id, String title, int authorId, String isbn, Integer publicationYear, Double price,
                Integer quantity) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.price = price;
        this.quantity = quantity;
    }

    // Gets the ID
    public Integer getId() {
        return id;
    }

    // Sets the ID
    public void setId(Integer id) {
        this.id = id;
    }

    // Gets the title
    public String getTitle() {
        return title;
    }

    // Sets the title
    public void setTitle(String title) {
        this.title = title;
    }

    // Gets the ID of the author who wrote the book
    public int getAuthorId() {
        return authorId;
    }

    // Sets the ID of the author who wrote the book
    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    // Gets the ISBN
    public String getIsbn() {
        return isbn;
    }

    // Sets the ISBN
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    // Gets the publication year
    public Integer getPublicationYear() {
        return publicationYear;
    }

    // Sets the publication year
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    // Gets the price
    public Double getPrice() {
        return price;
    }

    // Sets the price
    public void setPrice(Double price) {
        this.price = price;
    }

    // Gets the quantity in stock
    public Integer getQuantity() {
        return quantity;
    }

    // Sets the quantity in stock
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}