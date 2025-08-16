package com.enuygun.qa.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.enuygun.qa.base.BaseTestClass;
import com.enuygun.qa.config.ConfigManager;
import com.enuygun.qa.config.TestConfig;
import com.enuygun.qa.models.ApiResponse;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import static org.hamcrest.Matchers.*;

public class FlightApiTests extends BaseTestClass {

    private String apiBaseUrl;
    private RequestSpecification requestSpec;

    @BeforeClass
    public void setupApi() {
        apiBaseUrl = ConfigManager.getApiBaseUrl();
        
        // Setup REST Assured base configuration
        RestAssured.baseURI = apiBaseUrl;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        // Create request specification
        requestSpec = given()
                .contentType("application/json")
                .accept("application/json")
                .header("User-Agent", "Enuygun-QA-Automation")
                .log().all();
                
        // Add API key if available
        String apiKey = ConfigManager.getApiKey();
        if (apiKey != null && !apiKey.equals("your_api_key_here")) {
            requestSpec.header("Authorization", "Bearer " + apiKey);
        }
        
        logger.info("API Test setup completed. Base URL: {}", apiBaseUrl);
        ReportUtils.logInfo("API Base URL: " + apiBaseUrl);
    }

    @Test(groups = {TestConfig.SMOKE_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.HIGH_PRIORITY,
          description = "Verify API health check endpoint")
    public void testApiHealthCheck() {
        logTestStep("Send GET request to health check endpoint");
        
        Response response = requestSpec
                .when()
                .get("/health")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code");
        assertAndLog(response.getStatusCode() == 200, 
                    "Health check API returned status 200", 
                    "Health check API failed with status: " + response.getStatusCode());

        logTestStep("Verify response time is acceptable");
        long responseTime = response.getTime();
        assertAndLog(responseTime < 2000, 
                    "Response time is acceptable: " + responseTime + "ms", 
                    "Response time is too slow: " + responseTime + "ms");

        ReportUtils.logInfo("API Health Check completed successfully");
    }

    @Test(groups = {TestConfig.SMOKE_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.HIGH_PRIORITY,
          description = "Verify flight search API with valid parameters")
    public void testFlightSearchApi() {
        logTestStep("Prepare flight search request parameters");
        
        String requestBody = "{\n" +
                "  \"origin\": \"IST\",\n" +
                "  \"destination\": \"LHR\",\n" +
                "  \"departure_date\": \"2024-12-01\",\n" +
                "  \"passenger_count\": 1,\n" +
                "  \"cabin_class\": \"economy\"\n" +
                "}";

        logTestStep("Send POST request to flight search endpoint");
        
        Response response = requestSpec
                .body(requestBody)
                .when()
                .post("/flights/search")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code");
        assertAndLog(response.getStatusCode() == 200, 
                    "Flight search API returned status 200", 
                    "Flight search API failed with status: " + response.getStatusCode());

        logTestStep("Verify response contains expected fields");
        response.then()
                .body("success", equalTo(true))
                .body("data", notNullValue())
                .body("data.flights", notNullValue());

        logTestStep("Verify response time is acceptable");
        long responseTime = response.getTime();
        assertAndLog(responseTime < 5000, 
                    "Flight search response time is acceptable: " + responseTime + "ms", 
                    "Flight search response time is too slow: " + responseTime + "ms");

        ReportUtils.logInfo("Flight Search API test completed successfully");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify flight search API with invalid origin")
    public void testFlightSearchApiInvalidOrigin() {
        logTestStep("Prepare flight search request with invalid origin");
        
        String requestBody = "{\n" +
                "  \"origin\": \"INVALID\",\n" +
                "  \"destination\": \"LHR\",\n" +
                "  \"departure_date\": \"2024-12-01\",\n" +
                "  \"passenger_count\": 1\n" +
                "}";

        logTestStep("Send POST request with invalid data");
        
        Response response = requestSpec
                .body(requestBody)
                .when()
                .post("/flights/search")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code for invalid request");
        assertAndLog(response.getStatusCode() == 400, 
                    "API correctly returned 400 for invalid origin", 
                    "API did not handle invalid origin properly. Status: " + response.getStatusCode());

        logTestStep("Verify error message is present");
        response.then()
                .body("success", equalTo(false))
                .body("error", notNullValue());

        ReportUtils.logInfo("Invalid origin API test completed successfully");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify flight search API with missing required fields")
    public void testFlightSearchApiMissingFields() {
        logTestStep("Prepare flight search request with missing required fields");
        
        String requestBody = "{\n" +
                "  \"origin\": \"IST\"\n" +
                "}";

        logTestStep("Send POST request with incomplete data");
        
        Response response = requestSpec
                .body(requestBody)
                .when()
                .post("/flights/search")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code for incomplete request");
        assertAndLog(response.getStatusCode() == 400, 
                    "API correctly returned 400 for missing fields", 
                    "API did not handle missing fields properly. Status: " + response.getStatusCode());

        ReportUtils.logInfo("Missing fields API test completed successfully");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify flight details API endpoint")
    public void testFlightDetailsApi() {
        logTestStep("Send GET request to flight details endpoint");
        
        String flightId = "TEST_FLIGHT_123"; // This would typically come from a previous search
        
        Response response = requestSpec
                .pathParam("flightId", flightId)
                .when()
                .get("/flights/{flightId}")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code");
        // Note: This might return 404 if the flight ID doesn't exist, which is expected
        int statusCode = response.getStatusCode();
        assertAndLog(statusCode == 200 || statusCode == 404, 
                    "Flight details API returned expected status: " + statusCode, 
                    "Flight details API returned unexpected status: " + statusCode);

        if (statusCode == 200) {
            logTestStep("Verify response structure for valid flight");
            response.then()
                    .body("data", notNullValue())
                    .body("data.flight_id", notNullValue());
        }

        ReportUtils.logInfo("Flight Details API test completed");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify airports API endpoint")
    public void testAirportsApi() {
        logTestStep("Send GET request to airports endpoint");
        
        Response response = requestSpec
                .when()
                .get("/airports")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code");
        assertAndLog(response.getStatusCode() == 200, 
                    "Airports API returned status 200", 
                    "Airports API failed with status: " + response.getStatusCode());

        logTestStep("Verify response contains airports data");
        response.then()
                .body("data", notNullValue())
                .body("data.airports", notNullValue());

        logTestStep("Verify response time is acceptable");
        long responseTime = response.getTime();
        assertAndLog(responseTime < 3000, 
                    "Airports API response time is acceptable: " + responseTime + "ms", 
                    "Airports API response time is too slow: " + responseTime + "ms");

        ReportUtils.logInfo("Airports API test completed successfully");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify airlines API endpoint")
    public void testAirlinesApi() {
        logTestStep("Send GET request to airlines endpoint");
        
        Response response = requestSpec
                .when()
                .get("/airlines")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code");
        assertAndLog(response.getStatusCode() == 200, 
                    "Airlines API returned status 200", 
                    "Airlines API failed with status: " + response.getStatusCode());

        logTestStep("Verify response contains airlines data");
        response.then()
                .body("data", notNullValue())
                .body("data.airlines", notNullValue());

        ReportUtils.logInfo("Airlines API test completed successfully");
    }

    @Test(groups = {TestConfig.LOAD_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.LOW_PRIORITY,
          description = "Verify API response schema validation")
    public void testApiResponseSchema() {
        logTestStep("Send request and validate response schema");
        
        // Note: This test requires a JSON schema file to be available
        // For demonstration, we'll just verify basic structure
        
        Response response = requestSpec
                .when()
                .get("/airports")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response schema structure");
        response.then()
                .body("$", hasKey("success"))
                .body("$", hasKey("data"))
                .body("success", instanceOf(Boolean.class));

        ReportUtils.logInfo("API Response Schema validation completed");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.LOW_PRIORITY,
          description = "Verify API error handling for unauthorized access")
    public void testUnauthorizedAccess() {
        logTestStep("Send request without authorization");
        
        Response response = given()
                .contentType("application/json")
                .accept("application/json")
                // Intentionally not adding authorization header
                .when()
                .get(apiBaseUrl + "/protected-endpoint")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify unauthorized response");
        // Note: Expected behavior depends on actual API implementation
        int statusCode = response.getStatusCode();
        assertAndLog(statusCode == 401 || statusCode == 403 || statusCode == 404, 
                    "API correctly handled unauthorized access with status: " + statusCode, 
                    "API did not handle unauthorized access properly. Status: " + statusCode);

        ReportUtils.logInfo("Unauthorized access test completed");
    }
}