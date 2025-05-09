package api.model;

// Represents author in the bookstore system
public class Author {
    private int id;
    private String name;
    private String biography;

    // Default constructor
    public Author() {}

    // Constructor
    public Author(int id, String name, String biography) {
        this.id = id;
        this.name = name;
        this.biography = biography;
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

    // Gets the biography
    public String getBiography() {
        return biography;
    }

    // Sets the biography
    public void setBiography(String biography) {
        this.biography = biography;
    }
}