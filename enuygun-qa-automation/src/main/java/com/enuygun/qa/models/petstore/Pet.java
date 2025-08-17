package com.enuygun.qa.models.petstore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Pet POJO model for Petstore API
 * Represents a pet with all required and optional fields
 * 
 * JSON Schema:
 * {
 *   "id": 0,
 *   "category": {
 *     "id": 0,
 *     "name": "string"
 *   },
 *   "name": "doggie",
 *   "photoUrls": [
 *     "string"
 *   ],
 *   "tags": [
 *     {
 *       "id": 0,
 *       "name": "string"
 *     }
 *   ],
 *   "status": "available"
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pet {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("category")
    private Category category;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("photoUrls")
    private List<String> photoUrls;
    
    @JsonProperty("tags")
    private List<Tag> tags;
    
    @JsonProperty("status")
    private String status;
    
    // Pet status constants
    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_SOLD = "sold";
    
    // Default constructor
    public Pet() {
        this.photoUrls = new ArrayList<>();
        this.tags = new ArrayList<>();
    }
    
    // Constructor with required fields
    public Pet(String name, List<String> photoUrls) {
        this();
        this.name = name;
        this.photoUrls = photoUrls != null ? new ArrayList<>(photoUrls) : new ArrayList<>();
    }
    
    // Constructor with all fields
    public Pet(Long id, Category category, String name, List<String> photoUrls, List<Tag> tags, String status) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.photoUrls = photoUrls != null ? new ArrayList<>(photoUrls) : new ArrayList<>();
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<String> getPhotoUrls() {
        return photoUrls;
    }
    
    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls != null ? new ArrayList<>(photoUrls) : new ArrayList<>();
    }
    
    public List<Tag> getTags() {
        return tags;
    }
    
    public void setTags(List<Tag> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Builder pattern methods for fluent API
    public Pet withId(Long id) {
        this.id = id;
        return this;
    }
    
    public Pet withCategory(Category category) {
        this.category = category;
        return this;
    }
    
    public Pet withName(String name) {
        this.name = name;
        return this;
    }
    
    public Pet withPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls != null ? new ArrayList<>(photoUrls) : new ArrayList<>();
        return this;
    }
    
    public Pet withPhotoUrl(String photoUrl) {
        if (this.photoUrls == null) {
            this.photoUrls = new ArrayList<>();
        }
        this.photoUrls.add(photoUrl);
        return this;
    }
    
    public Pet withTags(List<Tag> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        return this;
    }
    
    public Pet withTag(Tag tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tag);
        return this;
    }
    
    public Pet withStatus(String status) {
        this.status = status;
        return this;
    }
    
    public Pet withAvailableStatus() {
        this.status = STATUS_AVAILABLE;
        return this;
    }
    
    public Pet withPendingStatus() {
        this.status = STATUS_PENDING;
        return this;
    }
    
    public Pet withSoldStatus() {
        this.status = STATUS_SOLD;
        return this;
    }
    
    // Validation methods
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() && 
               photoUrls != null && !photoUrls.isEmpty() &&
               isValidStatus(status);
    }
    
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();
        
        if (name == null || name.trim().isEmpty()) {
            errors.append("Pet name cannot be null or empty. ");
        }
        
        if (photoUrls == null || photoUrls.isEmpty()) {
            errors.append("Pet must have at least one photo URL. ");
        }
        
        if (!isValidStatus(status)) {
            errors.append("Pet status must be one of: available, pending, sold. ");
        }
        
        return errors.toString().trim();
    }
    
    private boolean isValidStatus(String status) {
        if (status == null) {
            return true; // Status is optional for creation
        }
        return STATUS_AVAILABLE.equals(status) || 
               STATUS_PENDING.equals(status) || 
               STATUS_SOLD.equals(status);
    }
    
    // Utility methods
    public boolean hasCategory() {
        return category != null;
    }
    
    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }
    
    public boolean hasPhotoUrls() {
        return photoUrls != null && !photoUrls.isEmpty();
    }
    
    public boolean isAvailable() {
        return STATUS_AVAILABLE.equals(status);
    }
    
    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }
    
    public boolean isSold() {
        return STATUS_SOLD.equals(status);
    }
    
    // Object overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(id, pet.id) &&
               Objects.equals(category, pet.category) &&
               Objects.equals(name, pet.name) &&
               Objects.equals(photoUrls, pet.photoUrls) &&
               Objects.equals(tags, pet.tags) &&
               Objects.equals(status, pet.status);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, category, name, photoUrls, tags, status);
    }
    
    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", category=" + category +
                ", name='" + name + '\'' +
                ", photoUrls=" + photoUrls +
                ", tags=" + tags +
                ", status='" + status + '\'' +
                '}';
    }
    
    // Static factory methods for common pet types
    public static Pet createDog(String name) {
        return new Pet()
            .withName(name)
            .withCategory(Category.createDog())
            .withPhotoUrl("https://example.com/dog.jpg")
            .withTag(Tag.createFriendly())
            .withTag(Tag.createLoyal())
            .withAvailableStatus();
    }
    
    public static Pet createCat(String name) {
        return new Pet()
            .withName(name)
            .withCategory(Category.createCat())
            .withPhotoUrl("https://example.com/cat.jpg")
            .withTag(Tag.createCalm())
            .withTag(Tag.createAffectionate())
            .withAvailableStatus();
    }
    
    public static Pet createBird(String name) {
        return new Pet()
            .withName(name)
            .withCategory(Category.createBird())
            .withPhotoUrl("https://example.com/bird.jpg")
            .withTag(Tag.createIntelligent())
            .withTag(Tag.createPlayful())
            .withAvailableStatus();
    }
    
    // Static method for creating test pet with all required fields
    public static Pet createValidTestPet(String name) {
        List<String> photoUrls = new ArrayList<>();
        photoUrls.add("https://example.com/photo1.jpg");
        photoUrls.add("https://example.com/photo2.jpg");
        
        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.createFriendly());
        tags.add(Tag.createPlayful());
        
        return new Pet()
            .withName(name)
            .withCategory(Category.createDog())
            .withPhotoUrls(photoUrls)
            .withTags(tags)
            .withAvailableStatus();
    }
    
    // Static method for creating minimal valid pet
    public static Pet createMinimalValidPet(String name) {
        List<String> photoUrls = new ArrayList<>();
        photoUrls.add("https://example.com/photo.jpg");
        
        return new Pet()
            .withName(name)
            .withPhotoUrls(photoUrls);
    }
}