package backend;

/**
 * Model class representing a Category in the system.
 * Matches the 'Categories' database table columns.
 */
public class Category {
    private int categoryId;
    private String categoryName;

    // Default constructor
    public Category() {}

    // Parameterized constructor
    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Getters and Setters
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // Override toString so JComboBox displays the category name correctly
    @Override
    public String toString() {
        return categoryName;
    }
}
