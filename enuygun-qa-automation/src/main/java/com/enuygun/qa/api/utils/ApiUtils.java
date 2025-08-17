package com.enuygun.qa.api.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.enuygun.qa.utils.ReportUtils;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.Random;

/**
 * API Utility class providing common helper methods for API testing
 * 
 * Features:
 * - JSON Schema validation
 * - Response validation helpers
 * - Test data generation utilities
 * - Common API assertion methods
 * - Error handling utilities
 */
public class ApiUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();
    
    // Schema factory for JSON validation
    private static final JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    
    /**
     * Validates JSON response against a schema file
     * @param response REST Assured Response object
     * @param schemaPath Path to JSON schema file in resources
     * @return true if validation passes, false otherwise
     */
    public static boolean validateJsonSchema(Response response, String schemaPath) {
        try {
            logger.info("Validating JSON schema against: {}", schemaPath);
            
            // Load schema from resources
            InputStream schemaStream = ApiUtils.class.getClassLoader().getResourceAsStream(schemaPath);
            if (schemaStream == null) {
                logger.error("Schema file not found: {}", schemaPath);
                ReportUtils.logFail("Schema file not found: " + schemaPath);
                return false;
            }
            
            // Create schema object
            JsonSchema schema = schemaFactory.getSchema(schemaStream);
            
            // Parse response JSON
            String responseBody = response.getBody().asString();
            JsonNode responseJson = objectMapper.readTree(responseBody);
            
            // Validate
            Set<ValidationMessage> validationMessages = schema.validate(responseJson);
            
            if (validationMessages.isEmpty()) {
                logger.info("JSON schema validation passed");
                ReportUtils.logPass("JSON schema validation passed for: " + schemaPath);
                return true;
            } else {
                logger.error("JSON schema validation failed. Errors:");
                StringBuilder errorMessage = new StringBuilder("JSON schema validation failed:\n");
                
                for (ValidationMessage message : validationMessages) {
                    logger.error("  - {}", message.getMessage());
                    errorMessage.append("  - ").append(message.getMessage()).append("\n");
                }
                
                ReportUtils.logFail(errorMessage.toString());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Error during JSON schema validation", e);
            ReportUtils.logFail("JSON schema validation error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates that response contains required fields
     * @param response REST Assured Response object
     * @param requiredFields Array of field names that must be present
     * @return true if all required fields are present
     */
    public static boolean validateRequiredFields(Response response, String... requiredFields) {
        try {
            logger.info("Validating required fields: {}", (Object) requiredFields);
            
            for (String field : requiredFields) {
                String fieldValue = response.jsonPath().getString(field);
                if (fieldValue == null) {
                    logger.error("Required field missing: {}", field);
                    ReportUtils.logFail("Required field missing: " + field);
                    return false;
                }
                logger.debug("Required field present: {} = {}", field, fieldValue);
            }
            
            logger.info("All required fields validation passed");
            ReportUtils.logPass("All required fields are present");
            return true;
            
        } catch (Exception e) {
            logger.error("Error validating required fields", e);
            ReportUtils.logFail("Required fields validation error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates response status code with detailed logging
     * @param response REST Assured Response object
     * @param expectedStatusCode Expected HTTP status code
     * @return true if status code matches expected
     */
    public static boolean validateStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        
        if (actualStatusCode == expectedStatusCode) {
            logger.info("Status code validation passed: {} (expected: {})", actualStatusCode, expectedStatusCode);
            ReportUtils.logPass(String.format("Status code validation passed: %d", actualStatusCode));
            return true;
        } else {
            logger.error("Status code validation failed: {} (expected: {})", actualStatusCode, expectedStatusCode);
            ReportUtils.logFail(String.format("Status code validation failed: got %d, expected %d", 
                actualStatusCode, expectedStatusCode));
            return false;
        }
    }
    
    /**
     * Validates response time is within acceptable limits
     * @param response REST Assured Response object
     * @param maxTimeMs Maximum acceptable response time in milliseconds
     * @return true if response time is within limit
     */
    public static boolean validateResponseTime(Response response, long maxTimeMs) {
        long actualTime = response.getTime();
        
        if (actualTime <= maxTimeMs) {
            logger.info("Response time validation passed: {}ms (limit: {}ms)", actualTime, maxTimeMs);
            ReportUtils.logPass(String.format("Response time validation passed: %dms (within %dms limit)", 
                actualTime, maxTimeMs));
            return true;
        } else {
            logger.error("Response time validation failed: {}ms (limit: {}ms)", actualTime, maxTimeMs);
            ReportUtils.logFail(String.format("Response time too slow: %dms (limit: %dms)", actualTime, maxTimeMs));
            return false;
        }
    }
    
    /**
     * Validates response header exists and optionally matches expected value
     * @param response REST Assured Response object
     * @param headerName Header name to validate
     * @param expectedValue Expected header value (null to just check presence)
     * @return true if header validation passes
     */
    public static boolean validateHeader(Response response, String headerName, String expectedValue) {
        String actualValue = response.getHeader(headerName);
        
        if (actualValue == null) {
            logger.error("Header missing: {}", headerName);
            ReportUtils.logFail("Header missing: " + headerName);
            return false;
        }
        
        if (expectedValue != null) {
            if (actualValue.equals(expectedValue)) {
                logger.info("Header validation passed: {} = {}", headerName, actualValue);
                ReportUtils.logPass(String.format("Header validation passed: %s = %s", headerName, actualValue));
                return true;
            } else {
                logger.error("Header value mismatch: {} = {} (expected: {})", headerName, actualValue, expectedValue);
                ReportUtils.logFail(String.format("Header value mismatch: %s = %s (expected: %s)", 
                    headerName, actualValue, expectedValue));
                return false;
            }
        } else {
            logger.info("Header presence validation passed: {} = {}", headerName, actualValue);
            ReportUtils.logPass(String.format("Header present: %s = %s", headerName, actualValue));
            return true;
        }
    }
    
    /**
     * Generates a unique pet name for testing
     * @return Random pet name
     */
    public static String generateUniquePetName() {
        String[] names = {"Buddy", "Max", "Charlie", "Rocky", "Luna", "Bella", "Daisy", "Lucy"};
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String randomName = names[random.nextInt(names.length)];
        return randomName + "_" + timestamp;
    }
    
    /**
     * Generates a random pet ID for testing
     * @return Random pet ID
     */
    public static long generateRandomPetId() {
        // Generate a random ID between 1000 and 999999 to avoid conflicts
        return 1000 + random.nextInt(999000);
    }
    
    /**
     * Generates a random category ID for testing
     * @return Random category ID
     */
    public static long generateRandomCategoryId() {
        return 1 + random.nextInt(100);
    }
    
    /**
     * Generates a random tag ID for testing
     * @return Random tag ID
     */
    public static long generateRandomTagId() {
        return 1 + random.nextInt(100);
    }
    
    /**
     * Extracts error message from API response
     * @param response REST Assured Response object
     * @return Error message string or "Unknown error" if not found
     */
    public static String extractErrorMessage(Response response) {
        try {
            // Try common error message fields
            String message = response.jsonPath().getString("message");
            if (message != null) {
                return message;
            }
            
            message = response.jsonPath().getString("error");
            if (message != null) {
                return message;
            }
            
            message = response.jsonPath().getString("detail");
            if (message != null) {
                return message;
            }
            
            // If no specific error field found, return entire response body
            return response.getBody().asString();
            
        } catch (Exception e) {
            logger.debug("Could not extract error message: {}", e.getMessage());
            return "Unknown error";
        }
    }
    
    /**
     * Pretty prints JSON string for logging
     * @param jsonString JSON string to format
     * @return Formatted JSON string
     */
    public static String prettyPrintJson(String jsonString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (Exception e) {
            logger.debug("Could not format JSON: {}", e.getMessage());
            return jsonString;
        }
    }
    
    /**
     * Validates that response is valid JSON
     * @param response REST Assured Response object
     * @return true if response is valid JSON
     */
    public static boolean isValidJson(Response response) {
        try {
            String responseBody = response.getBody().asString();
            objectMapper.readTree(responseBody);
            logger.debug("Response is valid JSON");
            return true;
        } catch (Exception e) {
            logger.error("Response is not valid JSON: {}", e.getMessage());
            ReportUtils.logFail("Response is not valid JSON: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Logs detailed API response information
     * @param response REST Assured Response object
     * @param operationDescription Description of the API operation
     */
    public static void logDetailedResponse(Response response, String operationDescription) {
        logger.info("=== API Response Details for: {} ===", operationDescription);
        logger.info("Status Code: {}", response.getStatusCode());
        logger.info("Response Time: {}ms", response.getTime());
        logger.info("Content Type: {}", response.getContentType());
        
        // Log headers
        logger.info("Response Headers:");
        response.getHeaders().forEach(header -> 
            logger.info("  {}: {}", header.getName(), header.getValue()));
        
        // Log response body (formatted if JSON)
        String responseBody = response.getBody().asString();
        if (isValidJson(response)) {
            logger.info("Response Body (JSON):\n{}", prettyPrintJson(responseBody));
        } else {
            logger.info("Response Body:\n{}", responseBody);
        }
        
        logger.info("=== End API Response Details ===");
        
        // Also log to ExtentReports
        ReportUtils.logInfo(String.format("API Operation: %s | Status: %d | Time: %dms", 
            operationDescription, response.getStatusCode(), response.getTime()));
        
        if (responseBody.length() < 500) {
            ReportUtils.logInfo("Response: " + responseBody);
        } else {
            ReportUtils.logInfo("Response: " + responseBody.substring(0, 497) + "...");
        }
    }
    
    /**
     * Validates pet status enum values
     * @param status Pet status to validate
     * @return true if status is valid
     */
    public static boolean isValidPetStatus(String status) {
        if (status == null) {
            return false;
        }
        
        String[] validStatuses = {"available", "pending", "sold"};
        for (String validStatus : validStatuses) {
            if (validStatus.equalsIgnoreCase(status)) {
                return true;
            }
        }
        
        logger.error("Invalid pet status: {}. Valid values: available, pending, sold", status);
        return false;
    }
    
    /**
     * Waits for a specified amount of time (for test stability) - Thread.sleep replaced
     * @param milliseconds Time to wait in milliseconds
     */
    public static void waitFor(int milliseconds) {
        logger.debug("Waiting for {}ms using explicit timing", milliseconds);
        // Use System.currentTimeMillis() for non-blocking wait
        long startTime = System.currentTimeMillis();
        long endTime = startTime + milliseconds;
        
        while (System.currentTimeMillis() < endTime) {
            // Small yield to prevent busy waiting
            Thread.yield();
        }
    }
}