package com.enuygun.qa.models.petstore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

/**
 * Category POJO model for Petstore API
 * Represents a pet category with id and name
 * 
 * JSON Schema:
 * {
 *   "id": 0,
 *   "name": "string"
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    // Default constructor
    public Category() {
    }
    
    // Constructor with parameters
    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    // Builder pattern methods for fluent API
    public Category withId(Long id) {
        this.id = id;
        return this;
    }
    
    public Category withName(String name) {
        this.name = name;
        return this;
    }
    
    // Validation methods
    public boolean isValid() {
        return name != null && !name.trim().isEmpty();
    }
    
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();
        
        if (name == null || name.trim().isEmpty()) {
            errors.append("Category name cannot be null or empty. ");
        }
        
        return errors.toString().trim();
    }
    
    // Object overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) && 
               Objects.equals(name, category.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
    
    // Static factory methods for common categories
    public static Category createDog() {
        return new Category(1L, "Dogs");
    }
    
    public static Category createCat() {
        return new Category(2L, "Cats");
    }
    
    public static Category createBird() {
        return new Category(3L, "Birds");
    }
    
    public static Category createFish() {
        return new Category(4L, "Fish");
    }
    
    public static Category createReptile() {
        return new Category(5L, "Reptiles");
    }
}