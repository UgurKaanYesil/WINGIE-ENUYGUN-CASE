package com.enuygun.qa.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.enuygun.qa.base.BaseTestClass;
import com.enuygun.qa.config.ConfigManager;
import com.enuygun.qa.config.TestConfig;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class HotelApiTests extends BaseTestClass {

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
        
        logger.info("Hotel API Test setup completed. Base URL: {}", apiBaseUrl);
        ReportUtils.logInfo("Hotel API Base URL: " + apiBaseUrl);
    }

    @Test(groups = {TestConfig.SMOKE_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.HIGH_PRIORITY,
          description = "Verify hotel search API with valid parameters")
    public void testHotelSearchApi() {
        logTestStep("Prepare hotel search request parameters");
        
        String requestBody = "{\n" +
                "  \"destination\": \"Istanbul\",\n" +
                "  \"checkin_date\": \"2024-12-01\",\n" +
                "  \"checkout_date\": \"2024-12-03\",\n" +
                "  \"guest_count\": 2,\n" +
                "  \"room_count\": 1\n" +
                "}";

        logTestStep("Send POST request to hotel search endpoint");
        
        Response response = requestSpec
                .body(requestBody)
                .when()
                .post("/hotels/search")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code");
        assertAndLog(response.getStatusCode() == 200, 
                    "Hotel search API returned status 200", 
                    "Hotel search API failed with status: " + response.getStatusCode());

        logTestStep("Verify response contains expected fields");
        response.then()
                .body("success", equalTo(true))
                .body("data", notNullValue())
                .body("data.hotels", notNullValue());

        logTestStep("Verify response time is acceptable");
        long responseTime = response.getTime();
        assertAndLog(responseTime < 5000, 
                    "Hotel search response time is acceptable: " + responseTime + "ms", 
                    "Hotel search response time is too slow: " + responseTime + "ms");

        ReportUtils.logInfo("Hotel Search API test completed successfully");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify hotel search API with invalid destination")
    public void testHotelSearchApiInvalidDestination() {
        logTestStep("Prepare hotel search request with invalid destination");
        
        String requestBody = "{\n" +
                "  \"destination\": \"\",\n" +
                "  \"checkin_date\": \"2024-12-01\",\n" +
                "  \"checkout_date\": \"2024-12-03\",\n" +
                "  \"guest_count\": 2,\n" +
                "  \"room_count\": 1\n" +
                "}";

        logTestStep("Send POST request with invalid destination");
        
        Response response = requestSpec
                .body(requestBody)
                .when()
                .post("/hotels/search")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code for invalid request");
        assertAndLog(response.getStatusCode() == 400, 
                    "API correctly returned 400 for invalid destination", 
                    "API did not handle invalid destination properly. Status: " + response.getStatusCode());

        logTestStep("Verify error message is present");
        response.then()
                .body("success", equalTo(false))
                .body("error", notNullValue());

        ReportUtils.logInfo("Invalid destination API test completed successfully");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify hotel search API with invalid date range")
    public void testHotelSearchApiInvalidDateRange() {
        logTestStep("Prepare hotel search request with invalid date range");
        
        String requestBody = "{\n" +
                "  \"destination\": \"Istanbul\",\n" +
                "  \"checkin_date\": \"2024-12-03\",\n" +
                "  \"checkout_date\": \"2024-12-01\",\n" +
                "  \"guest_count\": 2,\n" +
                "  \"room_count\": 1\n" +
                "}";

        logTestStep("Send POST request with checkout date before checkin date");
        
        Response response = requestSpec
                .body(requestBody)
                .when()
                .post("/hotels/search")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code for invalid date range");
        assertAndLog(response.getStatusCode() == 400, 
                    "API correctly returned 400 for invalid date range", 
                    "API did not handle invalid date range properly. Status: " + response.getStatusCode());

        ReportUtils.logInfo("Invalid date range API test completed successfully");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify hotel details API endpoint")
    public void testHotelDetailsApi() {
        logTestStep("Send GET request to hotel details endpoint");
        
        String hotelId = "TEST_HOTEL_123"; // This would typically come from a previous search
        
        Response response = requestSpec
                .pathParam("hotelId", hotelId)
                .when()
                .get("/hotels/{hotelId}")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code");
        // Note: This might return 404 if the hotel ID doesn't exist, which is expected
        int statusCode = response.getStatusCode();
        assertAndLog(statusCode == 200 || statusCode == 404, 
                    "Hotel details API returned expected status: " + statusCode, 
                    "Hotel details API returned unexpected status: " + statusCode);

        if (statusCode == 200) {
            logTestStep("Verify response structure for valid hotel");
            response.then()
                    .body("data", notNullValue())
                    .body("data.hotel_id", notNullValue());
        }

        ReportUtils.logInfo("Hotel Details API test completed");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify hotel amenities API endpoint")
    public void testHotelAmenitiesApi() {
        logTestStep("Send GET request to hotel amenities endpoint");
        
        Response response = requestSpec
                .when()
                .get("/hotels/amenities")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code");
        assertAndLog(response.getStatusCode() == 200, 
                    "Hotel amenities API returned status 200", 
                    "Hotel amenities API failed with status: " + response.getStatusCode());

        logTestStep("Verify response contains amenities data");
        response.then()
                .body("data", notNullValue())
                .body("data.amenities", notNullValue());

        ReportUtils.logInfo("Hotel Amenities API test completed successfully");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify hotel availability API endpoint")
    public void testHotelAvailabilityApi() {
        logTestStep("Prepare hotel availability request parameters");
        
        String requestBody = "{\n" +
                "  \"hotel_id\": \"TEST_HOTEL_123\",\n" +
                "  \"checkin_date\": \"2024-12-01\",\n" +
                "  \"checkout_date\": \"2024-12-03\",\n" +
                "  \"guest_count\": 2\n" +
                "}";

        logTestStep("Send POST request to hotel availability endpoint");
        
        Response response = requestSpec
                .body(requestBody)
                .when()
                .post("/hotels/availability")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code");
        // Status could be 200 (available) or 404 (hotel not found)
        int statusCode = response.getStatusCode();
        assertAndLog(statusCode == 200 || statusCode == 404, 
                    "Hotel availability API returned expected status: " + statusCode, 
                    "Hotel availability API returned unexpected status: " + statusCode);

        if (statusCode == 200) {
            logTestStep("Verify availability response structure");
            response.then()
                    .body("data", notNullValue())
                    .body("data.available", notNullValue());
        }

        ReportUtils.logInfo("Hotel Availability API test completed");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify hotel reviews API endpoint")
    public void testHotelReviewsApi() {
        logTestStep("Send GET request to hotel reviews endpoint");
        
        String hotelId = "TEST_HOTEL_123";
        
        Response response = requestSpec
                .pathParam("hotelId", hotelId)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .when()
                .get("/hotels/{hotelId}/reviews")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response status code");
        int statusCode = response.getStatusCode();
        assertAndLog(statusCode == 200 || statusCode == 404, 
                    "Hotel reviews API returned expected status: " + statusCode, 
                    "Hotel reviews API returned unexpected status: " + statusCode);

        if (statusCode == 200) {
            logTestStep("Verify reviews response structure");
            response.then()
                    .body("data", notNullValue())
                    .body("data.reviews", notNullValue());
        }

        ReportUtils.logInfo("Hotel Reviews API test completed");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.LOW_PRIORITY,
          description = "Verify hotel search with maximum guest count")
    public void testHotelSearchMaxGuests() {
        logTestStep("Prepare hotel search request with maximum guest count");
        
        String requestBody = "{\n" +
                "  \"destination\": \"Istanbul\",\n" +
                "  \"checkin_date\": \"2024-12-01\",\n" +
                "  \"checkout_date\": \"2024-12-03\",\n" +
                "  \"guest_count\": 10,\n" +
                "  \"room_count\": 5\n" +
                "}";

        logTestStep("Send POST request with high guest count");
        
        Response response = requestSpec
                .body(requestBody)
                .when()
                .post("/hotels/search")
                .then()
                .log().all()
                .extract().response();

        logTestStep("Verify response for high guest count search");
        int statusCode = response.getStatusCode();
        assertAndLog(statusCode == 200 || statusCode == 400, 
                    "Hotel search with high guest count returned expected status: " + statusCode, 
                    "Hotel search with high guest count failed unexpectedly: " + statusCode);

        ReportUtils.logInfo("Hotel Search Max Guests API test completed");
    }

    @Test(groups = {TestConfig.LOAD_TEST, TestConfig.API_TEST}, 
          priority = TestConfig.LOW_PRIORITY,
          description = "Verify hotel search API performance")
    public void testHotelSearchPerformance() {
        logTestStep("Prepare hotel search request for performance test");
        
        String requestBody = "{\n" +
                "  \"destination\": \"Istanbul\",\n" +
                "  \"checkin_date\": \"2024-12-01\",\n" +
                "  \"checkout_date\": \"2024-12-03\",\n" +
                "  \"guest_count\": 2,\n" +
                "  \"room_count\": 1\n" +
                "}";

        logTestStep("Send multiple requests to measure performance");
        
        long totalTime = 0;
        int requestCount = 5;
        
        for (int i = 0; i < requestCount; i++) {
            Response response = requestSpec
                    .body(requestBody)
                    .when()
                    .post("/hotels/search");
            
            totalTime += response.getTime();
        }
        
        long averageTime = totalTime / requestCount;
        
        logTestStep("Verify average response time");
        assertAndLog(averageTime < 3000, 
                    "Average response time is acceptable: " + averageTime + "ms", 
                    "Average response time is too slow: " + averageTime + "ms");

        ReportUtils.logInfo("Hotel Search Performance test completed. Average time: " + averageTime + "ms");
    }
}