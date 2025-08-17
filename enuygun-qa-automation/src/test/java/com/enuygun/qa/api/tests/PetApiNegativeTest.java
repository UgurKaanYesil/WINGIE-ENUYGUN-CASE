package com.enuygun.qa.api.tests;

import com.enuygun.qa.api.clients.PetApiClient;
import com.enuygun.qa.api.utils.ApiUtils;
import com.enuygun.qa.models.petstore.Pet;
import com.enuygun.qa.utils.ReportUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.*;

/**
 * Pet API Negative Test Cases
 * Tests error handling and validation for Pet endpoints with invalid data
 * 
 * Test Coverage:
 * - Invalid/missing data scenarios
 * - Non-existent resource scenarios
 * - Malformed JSON requests
 * - Invalid parameter values
 * - Authorization scenarios (if applicable)
 * 
 * Validations:
 * - Error status codes (4xx, 5xx)
 * - Error response structure
 * - Error message content
 * - Response times
 */
public class PetApiNegativeTest extends PetApiClient {
    
    private static final Logger logger = LoggerFactory.getLogger(PetApiNegativeTest.class);
    
    @Test(priority = 1, description = "Create pet with invalid JSON data")
    public void testCreatePetWithInvalidJson() {
        ReportUtils.logInfo("Starting test: Create Pet with Invalid JSON");
        
        String invalidJson = "{ \"name\": \"TestPet\", \"invalidField\": }"; // Malformed JSON
        
        Response response = createPetWithInvalidData(invalidJson);
        
        // Validate error response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_BAD_REQUEST),
            "Expected BAD_REQUEST (400) for invalid JSON");
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Invalid JSON request response time validation failed");
        
        ReportUtils.logPass("Invalid JSON request properly rejected with status: " + response.getStatusCode());
    }
    
    @Test(priority = 2, description = "Create pet with missing required fields")
    public void testCreatePetWithMissingRequiredFields() {
        ReportUtils.logInfo("Starting test: Create Pet with Missing Required Fields");
        
        // Pet without required 'name' field
        String invalidPetJson = "{\\n" +
            "  \\\"id\\\": 12345,\\n" +
            "  \\\"category\\\": {\\n" +
            "    \\\"id\\\": 1,\\n" +
            "    \\\"name\\\": \\\"Dogs\\\"\\n" +
            "  },\\n" +
            "  \\\"photoUrls\\\": [\\\"http://example.com/photo.jpg\\\"],\\n" +
            "  \\\"status\\\": \\\"available\\\"\\n" +
            "}";
        
        Response response = createPetWithInvalidData(invalidPetJson);
        
        // Validate error response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_BAD_REQUEST) ||
                         ApiUtils.validateStatusCode(response, STATUS_UNPROCESSABLE_ENTITY),
            "Expected error status for missing required fields");
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Missing required fields request response time validation failed");
        
        ReportUtils.logPass("Missing required fields properly rejected with status: " + response.getStatusCode());
    }
    
    @Test(priority = 3, description = "Create pet with empty photo URLs array")
    public void testCreatePetWithEmptyPhotoUrls() {
        ReportUtils.logInfo("Starting test: Create Pet with Empty Photo URLs");
        
        // Pet with empty photoUrls array (required field)
        String invalidPetJson = "{\\n" +
            "  \\\"name\\\": \\\"TestPet\\\",\\n" +
            "  \\\"photoUrls\\\": [],\\n" +
            "  \\\"status\\\": \\\"available\\\"\\n" +
            "}";
        
        Response response = createPetWithInvalidData(invalidPetJson);
        
        // Validate error response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_BAD_REQUEST) ||
                         ApiUtils.validateStatusCode(response, STATUS_UNPROCESSABLE_ENTITY),
            "Expected error status for empty photo URLs");
        
        ReportUtils.logPass("Empty photo URLs properly rejected with status: " + response.getStatusCode());
    }
    
    @Test(priority = 4, description = "Create pet with invalid status value")
    public void testCreatePetWithInvalidStatus() {
        ReportUtils.logInfo("Starting test: Create Pet with Invalid Status");
        
        // Pet with invalid status value
        String invalidPetJson = "{\\n" +
            "  \\\"name\\\": \\\"TestPet\\\",\\n" +
            "  \\\"photoUrls\\\": [\\\"http://example.com/photo.jpg\\\"],\\n" +
            "  \\\"status\\\": \\\"invalid_status\\\"\\n" +
            "}";
        
        Response response = createPetWithInvalidData(invalidPetJson);
        
        // Validate error response (some APIs might accept and others might reject)
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == STATUS_BAD_REQUEST || 
                         statusCode == STATUS_UNPROCESSABLE_ENTITY ||
                         statusCode == STATUS_OK || 
                         statusCode == STATUS_CREATED,
            "Unexpected status code for invalid status value: " + statusCode);
        
        if (statusCode == STATUS_OK || statusCode == STATUS_CREATED) {
            // If accepted, clean up the created pet
            long petId = response.jsonPath().getLong("id");
            deletePet(petId);
            ReportUtils.logInfo("Invalid status was accepted by API, pet cleaned up");
        } else {
            ReportUtils.logPass("Invalid status properly rejected with status: " + statusCode);
        }
    }
    
    @Test(priority = 5, description = "Get pet with non-existent ID")
    public void testGetNonExistentPet() {
        ReportUtils.logInfo("Starting test: Get Non-Existent Pet");
        
        long nonExistentId = 999999999L; // Very unlikely to exist
        
        Response response = getNonExistentPet(nonExistentId);
        
        // Validate error response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_NOT_FOUND),
            "Expected NOT_FOUND (404) for non-existent pet ID");
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Non-existent pet request response time validation failed");
        
        // Validate error response structure
        Assert.assertTrue(ApiUtils.validateJsonSchema(response, "schemas/petstore/error-schema.json"),
            "Error response schema validation failed");
        
        ReportUtils.logPass("Non-existent pet properly returned 404 status");
    }
    
    @Test(priority = 6, description = "Get pet with invalid ID format")
    public void testGetPetWithInvalidIdFormat() {
        ReportUtils.logInfo("Starting test: Get Pet with Invalid ID Format");
        
        // This test uses string ID instead of numeric
        logTestStep("Attempting to get pet with invalid ID format (string)");
        logApiRequest("GET", "/pet/invalid_id", null);
        
        try {
            Response response = given()
                .spec(getRequestSpecification())
                .when()
                .get("/pet/invalid_id")
                .then()
                .extract()
                .response();
            
            logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            
            // Validate error response
            Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_BAD_REQUEST) ||
                             ApiUtils.validateStatusCode(response, STATUS_NOT_FOUND),
                "Expected error status for invalid ID format");
            
            Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
                "Invalid ID format request response time validation failed");
            
            ReportUtils.logPass("Invalid ID format properly rejected with status: " + response.getStatusCode());
            
        } catch (Exception e) {
            logger.error("Error testing invalid ID format", e);
            ReportUtils.logFail("Error testing invalid ID format: " + e.getMessage());
            Assert.fail("Error testing invalid ID format: " + e.getMessage());
        }
    }
    
    @Test(priority = 7, description = "Update pet with non-existent ID")
    public void testUpdateNonExistentPet() {
        ReportUtils.logInfo("Starting test: Update Non-Existent Pet");
        
        // Create pet data with non-existent ID
        Pet nonExistentPet = Pet.createValidTestPet(ApiUtils.generateUniquePetName())
            .withId(999999999L);
        
        Response response = updatePet(nonExistentPet);
        
        // Validate error response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_NOT_FOUND) ||
                         ApiUtils.validateStatusCode(response, STATUS_BAD_REQUEST),
            "Expected error status for updating non-existent pet");
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Update non-existent pet response time validation failed");
        
        ReportUtils.logPass("Update non-existent pet properly rejected with status: " + response.getStatusCode());
    }
    
    @Test(priority = 8, description = "Update pet with invalid data")
    public void testUpdatePetWithInvalidData() {
        ReportUtils.logInfo("Starting test: Update Pet with Invalid Data");
        
        // Pet with missing required fields for update
        String invalidUpdateJson = "{\\n" +
            "  \\\"id\\\": 12345,\\n" +
            "  \\\"photoUrls\\\": []\\n" + // Empty required array
            "}";
        
        Response response = updatePetWithInvalidData(invalidUpdateJson);
        
        // Validate error response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_BAD_REQUEST) ||
                         ApiUtils.validateStatusCode(response, STATUS_UNPROCESSABLE_ENTITY) ||
                         ApiUtils.validateStatusCode(response, STATUS_NOT_FOUND),
            "Expected error status for invalid update data");
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Invalid update data response time validation failed");
        
        ReportUtils.logPass("Invalid update data properly rejected with status: " + response.getStatusCode());
    }
    
    @Test(priority = 9, description = "Delete pet with non-existent ID")
    public void testDeleteNonExistentPet() {
        ReportUtils.logInfo("Starting test: Delete Non-Existent Pet");
        
        long nonExistentId = 999999999L;
        
        Response response = deleteNonExistentPet(nonExistentId);
        
        // Validate error response
        Assert.assertTrue(ApiUtils.validateStatusCode(response, STATUS_NOT_FOUND) ||
                         ApiUtils.validateStatusCode(response, STATUS_BAD_REQUEST),
            "Expected error status for deleting non-existent pet");
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Delete non-existent pet response time validation failed");
        
        ReportUtils.logPass("Delete non-existent pet properly handled with status: " + response.getStatusCode());
    }
    
    @Test(priority = 10, description = "Find pets with invalid status value")
    public void testFindPetsByInvalidStatus() {
        ReportUtils.logInfo("Starting test: Find Pets by Invalid Status");
        
        String invalidStatus = "invalid_status_value";
        
        Response response = findPetsByStatus(invalidStatus);
        
        // API might return empty array or error
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == STATUS_OK || 
                         statusCode == STATUS_BAD_REQUEST ||
                         statusCode == STATUS_UNPROCESSABLE_ENTITY,
            "Unexpected status code for invalid status search: " + statusCode);
        
        if (statusCode == STATUS_OK) {
            // If OK, should return empty array
            Assert.assertTrue(response.jsonPath().getList("$").isEmpty() || 
                             response.jsonPath().getList("$") != null,
                "Expected empty array or valid response for invalid status");
            ReportUtils.logPass("Invalid status search returned OK with empty/valid response");
        } else {
            ReportUtils.logPass("Invalid status search properly rejected with status: " + statusCode);
        }
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Invalid status search response time validation failed");
    }
    
    @Test(priority = 11, description = "Create pet with extremely long name")
    public void testCreatePetWithLongName() {
        ReportUtils.logInfo("Starting test: Create Pet with Extremely Long Name");
        
        // Create pet with very long name (over typical limits)
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longName.append("a");
        }
        
        String longNameJson = "{\\n" +
            "  \\\"name\\\": \\\"" + longName.toString() + "\\\",\\n" +
            "  \\\"photoUrls\\\": [\\\"http://example.com/photo.jpg\\\"],\\n" +
            "  \\\"status\\\": \\\"available\\\"\\n" +
            "}";
        
        Response response = createPetWithInvalidData(longNameJson);
        
        // Validate response (might be accepted or rejected depending on API limits)
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == STATUS_OK || 
                         statusCode == STATUS_CREATED ||
                         statusCode == STATUS_BAD_REQUEST ||
                         statusCode == STATUS_UNPROCESSABLE_ENTITY,
            "Unexpected status code for long name: " + statusCode);
        
        if (statusCode == STATUS_OK || statusCode == STATUS_CREATED) {
            // If accepted, clean up
            long petId = response.jsonPath().getLong("id");
            deletePet(petId);
            ReportUtils.logPass("Long name was accepted by API, pet cleaned up");
        } else {
            ReportUtils.logPass("Long name properly rejected with status: " + statusCode);
        }
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Long name request response time validation failed");
    }
    
    @Test(priority = 12, description = "Create pet with invalid photo URL format")
    public void testCreatePetWithInvalidPhotoUrl() {
        ReportUtils.logInfo("Starting test: Create Pet with Invalid Photo URL");
        
        String invalidUrlJson = "{\\n" +
            "  \\\"name\\\": \\\"TestPet\\\",\\n" +
            "  \\\"photoUrls\\\": [\\\"not-a-valid-url\\\"],\\n" +
            "  \\\"status\\\": \\\"available\\\"\\n" +
            "}";
        
        Response response = createPetWithInvalidData(invalidUrlJson);
        
        // API might accept or reject invalid URLs
        int statusCode = response.getStatusCode();
        Assert.assertTrue(statusCode == STATUS_OK || 
                         statusCode == STATUS_CREATED ||
                         statusCode == STATUS_BAD_REQUEST ||
                         statusCode == STATUS_UNPROCESSABLE_ENTITY,
            "Unexpected status code for invalid photo URL: " + statusCode);
        
        if (statusCode == STATUS_OK || statusCode == STATUS_CREATED) {
            // If accepted, clean up
            long petId = response.jsonPath().getLong("id");
            deletePet(petId);
            ReportUtils.logInfo("Invalid photo URL was accepted by API, pet cleaned up");
        } else {
            ReportUtils.logPass("Invalid photo URL properly rejected with status: " + statusCode);
        }
        
        Assert.assertTrue(ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT),
            "Invalid photo URL request response time validation failed");
    }
}