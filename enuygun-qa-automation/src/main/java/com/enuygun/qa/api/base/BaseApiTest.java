package com.enuygun.qa.api.base;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import com.enuygun.qa.config.ConfigManager;
import com.enuygun.qa.utils.ReportUtils;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Base API Test class for common setup and configuration
 * Provides shared functionality for all API test classes
 * 
 * Features:
 * - REST Assured configuration and setup
 * - Request/Response specifications with builders
 * - Comprehensive logging of all HTTP requests and responses
 * - Common validation methods
 * - ExtentReports integration
 * - Error handling and assertion helpers
 */
public abstract class BaseApiTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseApiTest.class);
    
    // Base URL constants
    protected static final String PETSTORE_BASE_URL = "https://petstore.swagger.io/v2";
    
    // Common endpoints
    protected static final String PET_ENDPOINT = "/pet";
    protected static final String STORE_ENDPOINT = "/store";
    protected static final String USER_ENDPOINT = "/user";
    
    // Timeout constants (milliseconds)
    protected static final int DEFAULT_RESPONSE_TIME_LIMIT = 3000;
    protected static final int SLOW_RESPONSE_TIME_LIMIT = 5000;
    
    // Content type constants
    protected static final String JSON_CONTENT_TYPE = "application/json";
    protected static final String XML_CONTENT_TYPE = "application/xml";
    
    // Status code constants
    protected static final int STATUS_OK = 200;
    protected static final int STATUS_CREATED = 201;
    protected static final int STATUS_BAD_REQUEST = 400;
    protected static final int STATUS_NOT_FOUND = 404;
    protected static final int STATUS_METHOD_NOT_ALLOWED = 405;
    protected static final int STATUS_UNPROCESSABLE_ENTITY = 422;
    protected static final int STATUS_INTERNAL_SERVER_ERROR = 500;
    
    // Logging streams for capturing request/response details
    private static ByteArrayOutputStream requestLogStream;
    private static ByteArrayOutputStream responseLogStream;
    private static PrintStream requestPrintStream;
    private static PrintStream responsePrintStream;
    
    @BeforeClass(alwaysRun = true)
    public void setUpApiTestSuite() {
        try {
            logger.info("Setting up API test suite...");
            
            // Configure REST Assured base settings
            RestAssured.baseURI = PETSTORE_BASE_URL;
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
            
            // Setup logging streams for detailed request/response logging
            setupLoggingStreams();
            
            // Configure REST Assured with custom logging
            RestAssured.config = RestAssuredConfig.config()
                .logConfig(LogConfig.logConfig()
                    .enableLoggingOfRequestAndResponseIfValidationFails()
                    .enablePrettyPrinting(true));
            
            // Add global filters for comprehensive logging
            RestAssured.filters(
                new RequestLoggingFilter(requestPrintStream),
                new ResponseLoggingFilter(responsePrintStream)
            );
            
            logger.info("API test suite setup completed successfully");
            logger.info("Base URL: {}", PETSTORE_BASE_URL);
            
        } catch (Exception e) {
            logger.error("Failed to setup API test suite", e);
            throw new RuntimeException("API test suite setup failed", e);
        }
    }
    
    @BeforeMethod(alwaysRun = true)
    public void setUpApiTest() {
        logger.info("Setting up individual API test...");
        
        // Clear logging streams for each test
        if (requestLogStream != null) {
            requestLogStream.reset();
        }
        if (responseLogStream != null) {
            responseLogStream.reset();
        }
        
        logger.debug("API test setup completed");
    }
    
    /**
     * Creates a pre-configured RequestSpecification with common settings
     * @return RequestSpecification with default configuration
     */
    protected RequestSpecification getRequestSpecification() {
        return given()
            .contentType(JSON_CONTENT_TYPE)
            .accept(JSON_CONTENT_TYPE)
            .header("User-Agent", "Enuygun-QA-Automation/1.0.0")
            .log().all(); // Log all request details
    }
    
    /**
     * Creates a RequestSpecification with custom content type
     * @param contentType Content-Type header value
     * @return RequestSpecification with specified content type
     */
    protected RequestSpecification getRequestSpecification(String contentType) {
        return given()
            .contentType(contentType)
            .accept(contentType)
            .header("User-Agent", "Enuygun-QA-Automation/1.0.0")
            .log().all();
    }
    
    /**
     * Creates a pre-configured ResponseSpecification for success scenarios
     * @return ResponseSpecification for 2xx responses
     */
    protected ResponseSpecification getSuccessResponseSpecification() {
        return expect()
            .statusCode(anyOf(equalTo(200), equalTo(201), equalTo(202)))
            .contentType(JSON_CONTENT_TYPE)
            .time(lessThan((long) DEFAULT_RESPONSE_TIME_LIMIT))
            .log().all(); // Log all response details
    }
    
    /**
     * Creates a ResponseSpecification for specific status code
     * @param expectedStatusCode Expected HTTP status code
     * @return ResponseSpecification for specified status code
     */
    protected ResponseSpecification getResponseSpecification(int expectedStatusCode) {
        return expect()
            .statusCode(expectedStatusCode)
            .time(lessThan((long) DEFAULT_RESPONSE_TIME_LIMIT))
            .log().all();
    }
    
    /**
     * Creates a ResponseSpecification for error scenarios
     * @param expectedStatusCode Expected error status code
     * @return ResponseSpecification for error responses
     */
    protected ResponseSpecification getErrorResponseSpecification(int expectedStatusCode) {
        return expect()
            .statusCode(expectedStatusCode)
            .contentType(anyOf(containsString(JSON_CONTENT_TYPE), containsString(XML_CONTENT_TYPE)))
            .time(lessThan((long) DEFAULT_RESPONSE_TIME_LIMIT))
            .log().all();
    }
    
    /**
     * Validates response time is within acceptable limits
     * @param responseTimeMs Actual response time in milliseconds
     * @param expectedLimit Expected time limit in milliseconds
     * @param operation Description of the operation for logging
     */
    protected void validateResponseTime(long responseTimeMs, long expectedLimit, String operation) {
        logger.info("Response time for {}: {}ms (limit: {}ms)", operation, responseTimeMs, expectedLimit);
        
        if (responseTimeMs <= expectedLimit) {
            ReportUtils.logPass(String.format("Response time validation passed: %dms <= %dms for %s", 
                responseTimeMs, expectedLimit, operation));
        } else {
            ReportUtils.logFail(String.format("Response time validation failed: %dms > %dms for %s", 
                responseTimeMs, expectedLimit, operation));
            throw new AssertionError(String.format("Response time %dms exceeded limit %dms for operation: %s", 
                responseTimeMs, expectedLimit, operation));
        }
    }
    
    /**
     * Validates that response contains required headers
     * @param headerName Header name to validate
     * @param expectedValue Expected header value (null to just check presence)
     */
    protected void validateResponseHeader(String headerName, String expectedValue) {
        logger.debug("Validating response header: {} = {}", headerName, expectedValue);
        
        if (expectedValue != null) {
            expect().header(headerName, expectedValue);
            ReportUtils.logPass(String.format("Header validation passed: %s = %s", headerName, expectedValue));
        } else {
            expect().header(headerName, notNullValue());
            ReportUtils.logPass(String.format("Header presence validation passed: %s exists", headerName));
        }
    }
    
    /**
     * Helper method for logging test steps
     * @param stepDescription Description of the test step
     */
    protected void logTestStep(String stepDescription) {
        logger.info("Test Step: {}", stepDescription);
        ReportUtils.logInfo("Step: " + stepDescription);
    }
    
    /**
     * Helper method for logging API request details
     * @param method HTTP method
     * @param endpoint API endpoint
     * @param requestBody Request body (optional)
     */
    protected void logApiRequest(String method, String endpoint, Object requestBody) {
        logger.info("API Request: {} {}", method, endpoint);
        if (requestBody != null) {
            logger.info("Request Body: {}", requestBody);
        }
        
        ReportUtils.logInfo(String.format("API Call: %s %s", method, endpoint));
        if (requestBody != null) {
            ReportUtils.logInfo("Request Body: " + requestBody.toString());
        }
    }
    
    /**
     * Helper method for logging API response details
     * @param statusCode Response status code
     * @param responseTime Response time in milliseconds
     * @param responseBody Response body (optional)
     */
    protected void logApiResponse(int statusCode, long responseTime, String responseBody) {
        logger.info("API Response: Status {} | Time {}ms", statusCode, responseTime);
        if (responseBody != null && !responseBody.isEmpty()) {
            logger.info("Response Body: {}", responseBody);
        }
        
        ReportUtils.logInfo(String.format("Response: Status %d | Time %dms", statusCode, responseTime));
        if (responseBody != null && !responseBody.isEmpty()) {
            ReportUtils.logInfo("Response Body: " + responseBody);
        }
    }
    
    /**
     * Assert with logging for better test reporting
     * @param condition Condition to assert
     * @param successMessage Message to log on success
     * @param failureMessage Message to log on failure
     */
    protected void assertAndLog(boolean condition, String successMessage, String failureMessage) {
        if (condition) {
            logger.info("Assertion passed: {}", successMessage);
            ReportUtils.logPass(successMessage);
        } else {
            logger.error("Assertion failed: {}", failureMessage);
            ReportUtils.logFail(failureMessage);
            throw new AssertionError(failureMessage);
        }
    }
    
    /**
     * Setup logging streams for capturing detailed request/response logs
     */
    private void setupLoggingStreams() {
        try {
            requestLogStream = new ByteArrayOutputStream();
            responseLogStream = new ByteArrayOutputStream();
            
            requestPrintStream = new PrintStream(requestLogStream, true, "UTF-8");
            responsePrintStream = new PrintStream(responseLogStream, true, "UTF-8");
            
            logger.debug("Logging streams setup completed");
        } catch (Exception e) {
            logger.warn("Failed to setup logging streams: {}", e.getMessage());
            // Continue without detailed logging streams
        }
    }
    
    /**
     * Get captured request log for current test
     * @return Request log as string
     */
    protected String getCapturedRequestLog() {
        if (requestLogStream != null) {
            return requestLogStream.toString();
        }
        return "Request log not available";
    }
    
    /**
     * Get captured response log for current test
     * @return Response log as string
     */
    protected String getCapturedResponseLog() {
        if (responseLogStream != null) {
            return responseLogStream.toString();
        }
        return "Response log not available";
    }
    
    /**
     * Log test information
     * @param message Information message
     */
    protected void logTestInfo(String message) {
        logger.info(message);
        ReportUtils.logInfo(message);
    }
    
    /**
     * Log test warning
     * @param message Warning message
     */
    protected void logTestWarning(String message) {
        logger.warn(message);
        ReportUtils.logInfo("WARNING: " + message);
    }
    
    /**
     * Log test error
     * @param message Error message
     */
    protected void logTestError(String message) {
        logger.error(message);
        ReportUtils.logFail("ERROR: " + message);
    }
}