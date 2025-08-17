package com.enuygun.qa.api.tests;

import com.enuygun.qa.api.clients.PetApiClient;
import com.enuygun.qa.api.utils.ApiUtils;
import com.enuygun.qa.models.petstore.Pet;
import com.enuygun.qa.models.petstore.Category;
import com.enuygun.qa.models.petstore.Tag;
import com.enuygun.qa.utils.ReportUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Pet API Positive Test Cases
 * Tests all CRUD operations for Pet endpoints with valid data
 * 
 * Test Coverage:
 * - Create Pet (POST /pet)
 * - Get Pet by ID (GET /pet/{petId})
 * - Update Pet (PUT /pet)
 * - Delete Pet (DELETE /pet/{petId})
 * - Find Pets by Status (GET /pet/findByStatus)
 * - Find Pets by Tags (GET /pet/findByTags)
 * 
 * Validations:
 * - Status codes
 * - Response times
 * - JSON schema validation
 * - Required fields presence
 * - Data integrity
 */
public class PetApiTest extends PetApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(PetApiTest.class);
    
    private Pet testPet;
    private long testPetId;
    
    @BeforeMethod
    public void setupTestData() {
        logTestStep("Setting up test data for Pet API tests");
        
        // Generate unique test data
        String uniquePetName = ApiUtils.generateUniquePetName();
        testPetId = ApiUtils.generateRandomPetId();
        
        // Create test pet with all required fields
        testPet = Pet.createValidTestPet(uniquePetName)
            .withId(testPetId);
        
        logger.info("Test pet created: {} (ID: {})", testPet.getName(), testPet.getId());
        ReportUtils.logInfo("Test data initialized: Pet '" + testPet.getName() + "' with ID: " + testPet.getId());
    }
    
    @Test(priority = 1, description = "Create a new pet with valid data")
    public void testCreatePet() {
        ReportUtils.logInfo("Starting test: Create Pet");
        
        Response response = createPet(testPet);
        
        // Validate response
        Assert.assertTrue(validatePetCreation(response, testPet), 
            "Pet creation validation failed");
        
        // Validate JSON schema
        Assert.assertTrue(ApiUtils.validateJsonSchema(response, "schemas/petstore/pet-schema.json"),
            "Pet response schema validation failed");
        
        // Store created pet ID for cleanup
        testPetId = response.jsonPath().getLong("id");
        testPet.setId(testPetId);
        
        ReportUtils.logPass("Pet created successfully with ID: " + testPetId);
    }
    
    @Test(priority = 2, dependsOnMethods = "testCreatePet", 
          description = "Retrieve pet by ID after creation")
    public void testGetPetById() {
        ReportUtils.logInfo("Starting test: Get Pet by ID");
        
        Response response = getPetById(testPetId);
        
        // Validate response
        Assert.assertTrue(validatePetRetrieval(response, testPetId),
            "Pet retrieval validation failed");
        
        // Validate JSON schema
        Assert.assertTrue(ApiUtils.validateJsonSchema(response, "schemas/petstore/pet-schema.json"),
            "Pet response schema validation failed");
        
        // Validate pet data integrity
        String actualName = response.jsonPath().getString("name");
        Assert.assertEquals(actualName, testPet.getName(), 
            "Retrieved pet name doesn't match expected");
        
        String actualStatus = response.jsonPath().getString("status");
        Assert.assertEquals(actualStatus, testPet.getStatus(),
            "Retrieved pet status doesn't match expected");
        
        ReportUtils.logPass("Pet retrieved successfully by ID: " + testPetId);
    }
    
    @Test(priority = 3, dependsOnMethods = "testGetPetById",
          description = "Update existing pet with new data")
    public void testUpdatePet() {
        ReportUtils.logInfo("Starting test: Update Pet");
        
        // Update pet data
        String updatedName = testPet.getName() + "_Updated";
        Pet updatedPet = new Pet()
            .withId(testPetId)
            .withName(updatedName)
            .withCategory(Category.createCat())
            .withPhotoUrl("https://example.com/updated-photo.jpg")
            .withTag(Tag.createGentle())
            .withPendingStatus();
        
        Response response = updatePet(updatedPet);
        
        // Validate response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_OK),
            "Update pet status code validation failed");
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Update pet response time validation failed");
        
        // Validate JSON schema
        Assert.assertTrue(ApiUtils.validateJsonSchema(response, "schemas/petstore/pet-schema.json"),
            "Updated pet response schema validation failed");
        
        // Verify the update by retrieving the pet
        Response getResponse = getPetById(testPetId);
        String actualUpdatedName = getResponse.jsonPath().getString("name");
        Assert.assertEquals(actualUpdatedName, updatedName,
            "Pet name was not updated correctly");
        
        String actualUpdatedStatus = getResponse.jsonPath().getString("status");
        Assert.assertEquals(actualUpdatedStatus, Pet.STATUS_PENDING,
            "Pet status was not updated correctly");
        
        ReportUtils.logPass("Pet updated successfully. New name: " + updatedName);
    }
    
    @Test(priority = 4, description = "Find pets by status - available")
    public void testFindPetsByStatusAvailable() {
        ReportUtils.logInfo("Starting test: Find Pets by Status - Available");
        
        Response response = findPetsByStatus(Pet.STATUS_AVAILABLE);
        
        // Validate response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_OK),
            "Find pets by status response code validation failed");
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Find pets by status response time validation failed");
        
        // Validate JSON schema for array response
        Assert.assertTrue(ApiUtils.validateJsonSchema(response, "schemas/petstore/pet-array-schema.json"),
            "Find pets by status array schema validation failed");
        
        // Validate response is an array
        List<Object> pets = response.jsonPath().getList("$");
        Assert.assertNotNull(pets, "Response should be an array");
        
        // If pets found, validate each has available status
        if (!pets.isEmpty()) {
            List<String> statuses = response.jsonPath().getList("status");
            for (String status : statuses) {
                if (status != null) {  // Some pets might not have status field
                    Assert.assertEquals(status, Pet.STATUS_AVAILABLE,
                        "Found pet with non-available status: " + status);
                }
            }
        }
        
        ReportUtils.logPass("Found " + pets.size() + " pets with 'available' status");
    }
    
    @Test(priority = 5, description = "Find pets by multiple statuses")
    public void testFindPetsByMultipleStatuses() {
        ReportUtils.logInfo("Starting test: Find Pets by Multiple Statuses");
        
        Response response = findPetsByStatus(Pet.STATUS_AVAILABLE, Pet.STATUS_PENDING);
        
        // Validate response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_OK),
            "Find pets by multiple statuses response code validation failed");
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Find pets by multiple statuses response time validation failed");
        
        // Validate JSON schema for array response
        Assert.assertTrue(ApiUtils.validateJsonSchema(response, "schemas/petstore/pet-array-schema.json"),
            "Find pets by multiple statuses array schema validation failed");
        
        // Validate response is an array
        List<Object> pets = response.jsonPath().getList("$");
        Assert.assertNotNull(pets, "Response should be an array");
        
        ReportUtils.logPass("Found " + pets.size() + " pets with 'available' or 'pending' status");
    }
    
    @Test(priority = 6, description = "Find pets by tags")
    public void testFindPetsByTags() {
        ReportUtils.logInfo("Starting test: Find Pets by Tags");
        
        Response response = findPetsByTags("friendly", "playful");
        
        // Validate response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_OK),
            "Find pets by tags response code validation failed");
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Find pets by tags response time validation failed");
        
        // Validate JSON schema for array response
        Assert.assertTrue(ApiUtils.validateJsonSchema(response, "schemas/petstore/pet-array-schema.json"),
            "Find pets by tags array schema validation failed");
        
        // Validate response is an array
        List<Object> pets = response.jsonPath().getList("$");
        Assert.assertNotNull(pets, "Response should be an array");
        
        ReportUtils.logPass("Found " + pets.size() + " pets with 'friendly' or 'playful' tags");
    }
    
    @Test(priority = 7, description = "Create pet with minimal required data")
    public void testCreatePetMinimalData() {
        ReportUtils.logInfo("Starting test: Create Pet with Minimal Data");
        
        // Create pet with only required fields
        Pet minimalPet = Pet.createMinimalValidPet(ApiUtils.generateUniquePetName());
        
        Response response = createPet(minimalPet);
        
        // Validate response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_OK) || 
                         ApiUtils.validateStatusCode(response, STATUS_CREATED),
            "Create minimal pet status code validation failed");
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Create minimal pet response time validation failed");
        
        // Validate required fields are present
        Assert.assertTrue(ApiUtils.validateRequiredFields(response, "id", "name", "photoUrls"),
            "Create minimal pet required fields validation failed");
        
        // Validate JSON schema
        Assert.assertTrue(ApiUtils.validateJsonSchema(response, "schemas/petstore/pet-schema.json"),
            "Minimal pet response schema validation failed");
        
        // Store ID for cleanup
        long minimalPetId = response.jsonPath().getLong("id");
        
        ReportUtils.logPass("Minimal pet created successfully with ID: " + minimalPetId);
        
        // Clean up - delete the created pet
        deletePet(minimalPetId);
    }
    
    @Test(priority = 8, description = "Create different types of pets")
    public void testCreateDifferentPetTypes() {
        ReportUtils.logInfo("Starting test: Create Different Pet Types");
        
        // Test creating different pet types
        Pet dog = Pet.createDog(ApiUtils.generateUniquePetName());
        Pet cat = Pet.createCat(ApiUtils.generateUniquePetName());
        Pet bird = Pet.createBird(ApiUtils.generateUniquePetName());
        
        Pet[] petTypes = {dog, cat, bird};
        long[] createdIds = new long[petTypes.length];
        
        for (int i = 0; i < petTypes.length; i++) {
            Response response = createPet(petTypes[i]);
            
            Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_OK) || 
                             ApiUtils.validateStatusCode(response, STATUS_CREATED),
                "Create " + petTypes[i].getCategory().getName() + " status code validation failed");
            
            Assert.assertTrue(ApiUtils.validateJsonSchema(response, "schemas/petstore/pet-schema.json"),
                petTypes[i].getCategory().getName() + " response schema validation failed");
            
            createdIds[i] = response.jsonPath().getLong("id");
            ReportUtils.logPass(petTypes[i].getCategory().getName() + " created successfully with ID: " + createdIds[i]);
        }
        
        // Clean up - delete created pets
        for (long petId : createdIds) {
            deletePet(petId);
        }
    }
    
    @Test(priority = 9, dependsOnMethods = "testUpdatePet",
          description = "Delete pet after all tests")
    public void testDeletePet() {
        ReportUtils.logInfo("Starting test: Delete Pet");
        
        Response response = deletePet(testPetId);
        
        // Validate response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_OK),
            "Delete pet status code validation failed");
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Delete pet response time validation failed");
        
        // Verify deletion by trying to get the pet (should return 404)
        Response getResponse = getPetById(testPetId);
        Assert.assertTrue(ApiUtils.validateStatusCode(getResponse, STATUS_NOT_FOUND),
            "Pet should not be found after deletion");
        
        ReportUtils.logPass("Pet deleted successfully. ID: " + testPetId);
    }
    
    @Test(priority = 10, description = "Validate response headers for Pet API")
    public void testResponseHeaders() {
        ReportUtils.logInfo("Starting test: Validate Response Headers");
        
        // Create a simple pet for header validation
        Pet headerTestPet = Pet.createMinimalValidPet(ApiUtils.generateUniquePetName());
        Response response = createPet(headerTestPet);
        
        // Validate common headers
        Assert.assertTrue(ApiUtils.validateHeader(response, "Content-Type", null),
            "Content-Type header validation failed");
        
        // Clean up
        if (response.getStatusCode() == STATUS_OK || response.getStatusCode() == STATUS_CREATED) {
            long headerTestPetId = response.jsonPath().getLong("id");
            deletePet(headerTestPetId);
        }
        
        ReportUtils.logPass("Response headers validation completed");
    }
}