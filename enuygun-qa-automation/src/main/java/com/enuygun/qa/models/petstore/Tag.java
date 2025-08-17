package com.enuygun.qa.models.petstore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

/**
 * Tag POJO model for Petstore API
 * Represents a pet tag with id and name
 * 
 * JSON Schema:
 * {
 *   "id": 0,
 *   "name": "string"
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    // Default constructor
    public Tag() {
    }
    
    // Constructor with parameters
    public Tag(Long id, String name) {
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
    public Tag withId(Long id) {
        this.id = id;
        return this;
    }
    
    public Tag withName(String name) {
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
            errors.append("Tag name cannot be null or empty. ");
        }
        
        return errors.toString().trim();
    }
    
    // Object overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id) && 
               Objects.equals(name, tag.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
    
    // Static factory methods for common tags
    public static Tag createFriendly() {
        return new Tag(1L, "friendly");
    }
    
    public static Tag createPlayful() {
        return new Tag(2L, "playful");
    }
    
    public static Tag createGentle() {
        return new Tag(3L, "gentle");
    }
    
    public static Tag createEnergetic() {
        return new Tag(4L, "energetic");
    }
    
    public static Tag createCalm() {
        return new Tag(5L, "calm");
    }
    
    public static Tag createLoyal() {
        return new Tag(6L, "loyal");
    }
    
    public static Tag createIntelligent() {
        return new Tag(7L, "intelligent");
    }
    
    public static Tag createAffectionate() {
        return new Tag(8L, "affectionate");
    }
}