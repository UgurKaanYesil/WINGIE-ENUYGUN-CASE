package com.enuygun.qa.api.clients;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.enuygun.qa.api.base.BaseApiTest;
import com.enuygun.qa.models.petstore.Pet;
import com.enuygun.qa.api.utils.ApiUtils;
import com.enuygun.qa.utils.ReportUtils;

import java.util.List;

import static io.restassured.RestAssured.*;

/**
 * Pet API Client wrapper for Petstore API operations
 * Provides high-level methods for Pet CRUD operations with built-in logging and validation
 * 
 * Features:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Comprehensive logging of requests and responses
 * - Built-in error handling and validation
 * - Support for both positive and negative test scenarios
 * - Fluent API design for easy test writing
 */
public class PetApiClient extends BaseApiTest {
    
    private static final Logger logger = LoggerFactory.getLogger(PetApiClient.class);
    
    // Pet API endpoints
    private static final String CREATE_PET_ENDPOINT = "/pet";
    private static final String GET_PET_BY_ID_ENDPOINT = "/pet/{petId}";
    private static final String UPDATE_PET_ENDPOINT = "/pet";
    private static final String DELETE_PET_ENDPOINT = "/pet/{petId}";
    private static final String FIND_PETS_BY_STATUS_ENDPOINT = "/pet/findByStatus";
    private static final String FIND_PETS_BY_TAGS_ENDPOINT = "/pet/findByTags";
    private static final String UPLOAD_PET_IMAGE_ENDPOINT = "/pet/{petId}/uploadImage";
    
    /**
     * Creates a new pet in the store
     * @param pet Pet object to create
     * @return Response from the API
     */
    public Response createPet(Pet pet) {
        logTestStep("Creating new pet: " + pet.getName());
        logApiRequest("POST", CREATE_PET_ENDPOINT, pet);
        
        try {
            RequestSpecification request = getRequestSpecification();
            
            Response response = request
                .body(pet)
                .when()
                .post(CREATE_PET_ENDPOINT)
                .then()
                .extract()
                .response();
            
            logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            ApiUtils.logDetailedResponse(response, "Create Pet");
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error creating pet", e);
            ReportUtils.logFail("Failed to create pet: " + e.getMessage());
            throw new RuntimeException("Failed to create pet", e);
        }
    }
    
    /**
     * Retrieves a pet by ID
     * @param petId Pet ID to retrieve
     * @return Response from the API
     */
    public Response getPetById(long petId) {
        logTestStep("Retrieving pet by ID: " + petId);
        logApiRequest("GET", GET_PET_BY_ID_ENDPOINT.replace("{petId}", String.valueOf(petId)), null);
        
        try {
            RequestSpecification request = getRequestSpecification();
            
            Response response = request
                .pathParam("petId", petId)
                .when()
                .get(GET_PET_BY_ID_ENDPOINT)
                .then()
                .extract()
                .response();
            
            logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            ApiUtils.logDetailedResponse(response, "Get Pet by ID: " + petId);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error retrieving pet by ID: {}", petId, e);
            ReportUtils.logFail("Failed to retrieve pet by ID " + petId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve pet by ID: " + petId, e);
        }
    }
    
    /**
     * Updates an existing pet
     * @param pet Pet object with updated information
     * @return Response from the API
     */
    public Response updatePet(Pet pet) {
        logTestStep("Updating pet: " + pet.getName() + " (ID: " + pet.getId() + ")");
        logApiRequest("PUT", UPDATE_PET_ENDPOINT, pet);
        
        try {
            RequestSpecification request = getRequestSpecification();
            
            Response response = request
                .body(pet)
                .when()
                .put(UPDATE_PET_ENDPOINT)
                .then()
                .extract()
                .response();
            
            logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            ApiUtils.logDetailedResponse(response, "Update Pet: " + pet.getId());
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error updating pet", e);
            ReportUtils.logFail("Failed to update pet: " + e.getMessage());
            throw new RuntimeException("Failed to update pet", e);
        }
    }
    
    /**
     * Deletes a pet by ID
     * @param petId Pet ID to delete
     * @return Response from the API
     */
    public Response deletePet(long petId) {
        logTestStep("Deleting pet by ID: " + petId);
        logApiRequest("DELETE", DELETE_PET_ENDPOINT.replace("{petId}", String.valueOf(petId)), null);
        
        try {
            RequestSpecification request = getRequestSpecification();
            
            Response response = request
                .pathParam("petId", petId)
                .when()
                .delete(DELETE_PET_ENDPOINT)
                .then()
                .extract()
                .response();
            
            logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            ApiUtils.logDetailedResponse(response, "Delete Pet: " + petId);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error deleting pet by ID: {}", petId, e);
            ReportUtils.logFail("Failed to delete pet by ID " + petId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete pet by ID: " + petId, e);
        }
    }
    
    /**
     * Finds pets by status
     * @param status Pet status (available, pending, sold)
     * @return Response from the API
     */
    public Response findPetsByStatus(String status) {
        logTestStep("Finding pets by status: " + status);
        logApiRequest("GET", FIND_PETS_BY_STATUS_ENDPOINT + "?status=" + status, null);
        
        try {
            RequestSpecification request = getRequestSpecification();
            
            Response response = request
                .queryParam("status", status)
                .when()
                .get(FIND_PETS_BY_STATUS_ENDPOINT)
                .then()
                .extract()
                .response();
            
            logApiResponse(response.getStatusCode(), response.getTime(), 
                "Array of " + response.jsonPath().getList("$").size() + " pets");
            ApiUtils.logDetailedResponse(response, "Find Pets by Status: " + status);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error finding pets by status: {}", status, e);
            ReportUtils.logFail("Failed to find pets by status " + status + ": " + e.getMessage());
            throw new RuntimeException("Failed to find pets by status: " + status, e);
        }
    }
    
    /**
     * Finds pets by multiple statuses
     * @param statuses Array of pet statuses
     * @return Response from the API
     */
    public Response findPetsByStatus(String... statuses) {
        String statusParam = String.join(",", statuses);
        logTestStep("Finding pets by multiple statuses: " + statusParam);
        logApiRequest("GET", FIND_PETS_BY_STATUS_ENDPOINT + "?status=" + statusParam, null);
        
        try {
            RequestSpecification request = getRequestSpecification();
            
            for (String status : statuses) {
                request.queryParam("status", status);
            }
            
            Response response = request
                .when()
                .get(FIND_PETS_BY_STATUS_ENDPOINT)
                .then()
                .extract()
                .response();
            
            logApiResponse(response.getStatusCode(), response.getTime(), 
                "Array of " + response.jsonPath().getList("$").size() + " pets");
            ApiUtils.logDetailedResponse(response, "Find Pets by Multiple Statuses: " + statusParam);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error finding pets by multiple statuses: {}", statusParam, e);
            ReportUtils.logFail("Failed to find pets by statuses " + statusParam + ": " + e.getMessage());
            throw new RuntimeException("Failed to find pets by statuses: " + statusParam, e);
        }
    }
    
    /**
     * Finds pets by tags
     * @param tags Array of tag names
     * @return Response from the API
     */
    public Response findPetsByTags(String... tags) {
        String tagsParam = String.join(",", tags);
        logTestStep("Finding pets by tags: " + tagsParam);
        logApiRequest("GET", FIND_PETS_BY_TAGS_ENDPOINT + "?tags=" + tagsParam, null);
        
        try {
            RequestSpecification request = getRequestSpecification();
            
            for (String tag : tags) {
                request.queryParam("tags", tag);
            }
            
            Response response = request
                .when()
                .get(FIND_PETS_BY_TAGS_ENDPOINT)
                .then()
                .extract()
                .response();
            
            logApiResponse(response.getStatusCode(), response.getTime(), 
                "Array of " + response.jsonPath().getList("$").size() + " pets");
            ApiUtils.logDetailedResponse(response, "Find Pets by Tags: " + tagsParam);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error finding pets by tags: {}", tagsParam, e);
            ReportUtils.logFail("Failed to find pets by tags " + tagsParam + ": " + e.getMessage());
            throw new RuntimeException("Failed to find pets by tags: " + tagsParam, e);
        }
    }
    
    /**
     * Creates a pet with invalid data for negative testing
     * @param invalidPetJson Invalid JSON string
     * @return Response from the API
     */
    public Response createPetWithInvalidData(String invalidPetJson) {
        logTestStep("Creating pet with invalid data");
        logApiRequest("POST", CREATE_PET_ENDPOINT, invalidPetJson);
        
        try {
            RequestSpecification request = getRequestSpecification();
            
            Response response = request
                .body(invalidPetJson)
                .when()
                .post(CREATE_PET_ENDPOINT)
                .then()
                .extract()
                .response();
            
            logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            ApiUtils.logDetailedResponse(response, "Create Pet with Invalid Data");
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error creating pet with invalid data", e);
            ReportUtils.logFail("Failed to create pet with invalid data: " + e.getMessage());
            throw new RuntimeException("Failed to create pet with invalid data", e);
        }
    }
    
    /**
     * Attempts to get a non-existent pet for negative testing
     * @param nonExistentPetId ID that doesn't exist
     * @return Response from the API
     */
    public Response getNonExistentPet(long nonExistentPetId) {
        logTestStep("Attempting to retrieve non-existent pet ID: " + nonExistentPetId);
        logApiRequest("GET", GET_PET_BY_ID_ENDPOINT.replace("{petId}", String.valueOf(nonExistentPetId)), null);
        
        try {
            RequestSpecification request = getRequestSpecification();
            
            Response response = request
                .pathParam("petId", nonExistentPetId)
                .when()
                .get(GET_PET_BY_ID_ENDPOINT)
                .then()
                .extract()
                .response();
            
            logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            ApiUtils.logDetailedResponse(response, "Get Non-Existent Pet: " + nonExistentPetId);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error attempting to retrieve non-existent pet: {}", nonExistentPetId, e);
            ReportUtils.logFail("Failed to attempt retrieving non-existent pet " + nonExistentPetId + ": " + e.getMessage());
            throw new RuntimeException("Failed to attempt retrieving non-existent pet: " + nonExistentPetId, e);
        }
    }
    
    /**
     * Updates pet with invalid data for negative testing
     * @param invalidPetJson Invalid JSON string
     * @return Response from the API
     */
    public Response updatePetWithInvalidData(String invalidPetJson) {
        logTestStep("Updating pet with invalid data");
        logApiRequest("PUT", UPDATE_PET_ENDPOINT, invalidPetJson);
        
        try {
            RequestSpecification request = getRequestSpecification();
            
            Response response = request
                .body(invalidPetJson)
                .when()
                .put(UPDATE_PET_ENDPOINT)
                .then()
                .extract()
                .response();
            
            logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            ApiUtils.logDetailedResponse(response, "Update Pet with Invalid Data");
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error updating pet with invalid data", e);
            ReportUtils.logFail("Failed to update pet with invalid data: " + e.getMessage());
            throw new RuntimeException("Failed to update pet with invalid data", e);
        }
    }
    
    /**
     * Attempts to delete a non-existent pet for negative testing
     * @param nonExistentPetId ID that doesn't exist
     * @return Response from the API
     */
    public Response deleteNonExistentPet(long nonExistentPetId) {
        logTestStep("Attempting to delete non-existent pet ID: " + nonExistentPetId);
        logApiRequest("DELETE", DELETE_PET_ENDPOINT.replace("{petId}", String.valueOf(nonExistentPetId)), null);
        
        try {
            RequestSpecification request = getRequestSpecification();
            
            Response response = request
                .pathParam("petId", nonExistentPetId)
                .when()
                .delete(DELETE_PET_ENDPOINT)
                .then()
                .extract()
                .response();
            
            logApiResponse(response.getStatusCode(), response.getTime(), response.getBody().asString());
            ApiUtils.logDetailedResponse(response, "Delete Non-Existent Pet: " + nonExistentPetId);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error attempting to delete non-existent pet: {}", nonExistentPetId, e);
            ReportUtils.logFail("Failed to attempt deleting non-existent pet " + nonExistentPetId + ": " + e.getMessage());
            throw new RuntimeException("Failed to attempt deleting non-existent pet: " + nonExistentPetId, e);
        }
    }
    
    /**
     * Validates that a pet was created successfully
     * @param response Response from create pet API call
     * @param expectedPet Expected pet data
     * @return true if validation passes
     */
    public boolean validatePetCreation(Response response, Pet expectedPet) {
        logger.info("Validating pet creation response");
        
        // Validate status code
        if (!ApiUtils.validateStatusCode(response, STATUS_OK) && !ApiUtils.validateStatusCode(response, STATUS_CREATED)) {
            return false;
        }
        
        // Validate response time
        if (!ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT)) {
            return false;
        }
        
        // Validate required fields
        if (!ApiUtils.validateRequiredFields(response, "id", "name", "photoUrls")) {
            return false;
        }
        
        // Validate pet data matches expected
        String actualName = response.jsonPath().getString("name");
        if (!expectedPet.getName().equals(actualName)) {
            ReportUtils.logFail(String.format("Pet name mismatch: expected '%s', got '%s'", 
                expectedPet.getName(), actualName));
            return false;
        }
        
        ReportUtils.logPass("Pet creation validation completed successfully");
        return true;
    }
    
    /**
     * Validates that a pet retrieval was successful
     * @param response Response from get pet API call
     * @param expectedPetId Expected pet ID
     * @return true if validation passes
     */
    public boolean validatePetRetrieval(Response response, long expectedPetId) {
        logger.info("Validating pet retrieval response for ID: {}", expectedPetId);
        
        // Validate status code
        if (!ApiUtils.validateStatusCode(response, STATUS_OK)) {
            return false;
        }
        
        // Validate response time
        if (!ApiUtils.validateResponseTime(response, DEFAULT_RESPONSE_TIME_LIMIT)) {
            return false;
        }
        
        // Validate pet ID matches
        Long actualId = response.jsonPath().getLong("id");
        if (expectedPetId != actualId) {
            ReportUtils.logFail(String.format("Pet ID mismatch: expected %d, got %d", expectedPetId, actualId));
            return false;
        }
        
        ReportUtils.logPass("Pet retrieval validation completed successfully");
        return true;
    }
}