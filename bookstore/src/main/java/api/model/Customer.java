package api.model;

// Represents a customer in the bookstore system
public class Customer {
    private int id;
    private String name;
    private String email;
    private String password;

    // Default constructor
    public Customer() {}

    // Constructor
    public Customer(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Gets the ID
    public int getId() {
        return id;
    }

    // Sets the ID
    public void setId(int id) {
        this.id = id;
    }

    // Gets the name
    public String getName() {
        return name;
    }

    // Sets the name
    public void setName(String name) {
        this.name = name;
    }

    // Gets the email
    public String getEmail() {
        return email;
    }

    // Sets the email
    public void setEmail(String email) {
        this.email = email;
    }

    // Gets the password
    public String getPassword() {
        return password;
    }

    // Sets the password
    public void setPassword(String password) {
        this.password = password;
    }
}