package com.enuygun.qa.config;

public class TestConfig {
    
    // Test Categories
    public static final String SMOKE_TEST = "Smoke";
    public static final String REGRESSION_TEST = "Regression";
    public static final String API_TEST = "API";
    public static final String UI_TEST = "UI";
    public static final String LOAD_TEST = "Load";
    public static final String DATA_TEST = "Data";
    
    // Test Priorities
    public static final int HIGH_PRIORITY = 1;
    public static final int MEDIUM_PRIORITY = 2;
    public static final int LOW_PRIORITY = 3;
    
    // Test Data Keys
    public static final String VALID_EMAIL = "valid.email";
    public static final String VALID_PASSWORD = "valid.password";
    public static final String INVALID_EMAIL = "invalid.email";
    public static final String INVALID_PASSWORD = "invalid.password";
    
    // Element Wait Times (in seconds)
    public static final int SHORT_WAIT = 5;
    public static final int MEDIUM_WAIT = 10;
    public static final int LONG_WAIT = 30;
    
    // Retry Configuration
    public static final int MAX_RETRY_COUNT = 3;
    public static final int RETRY_INTERVAL = 1000; // milliseconds
    
    // Screenshot Configuration
    public static final boolean TAKE_SCREENSHOT_ON_PASS = false;
    public static final boolean TAKE_SCREENSHOT_ON_FAIL = true;
    public static final int SCREENSHOT_CLEANUP_DAYS = 7;
    
    // API Configuration
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final int API_SUCCESS_STATUS = 200;
    public static final int API_CREATED_STATUS = 201;
    public static final int API_BAD_REQUEST_STATUS = 400;
    public static final int API_UNAUTHORIZED_STATUS = 401;
    public static final int API_NOT_FOUND_STATUS = 404;
    public static final int API_INTERNAL_SERVER_ERROR_STATUS = 500;
    
    // Load Test Configuration
    public static final int DEFAULT_VIRTUAL_USERS = 10;
    public static final int DEFAULT_RAMP_UP_TIME = 30; // seconds
    public static final int DEFAULT_TEST_DURATION = 300; // seconds
    public static final double ACCEPTABLE_ERROR_RATE = 0.05; // 5%
    public static final int ACCEPTABLE_RESPONSE_TIME = 2000; // milliseconds
    
    // Data Analysis Configuration
    public static final String CSV_DELIMITER = ",";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    // Browser Configuration
    public static final String CHROME_BROWSER = "chrome";
    public static final String FIREFOX_BROWSER = "firefox";
    public static final String EDGE_BROWSER = "edge";
    public static final String SAFARI_BROWSER = "safari";
    
    // Environment Configuration
    public static final String DEV_ENVIRONMENT = "dev";
    public static final String TEST_ENVIRONMENT = "test";
    public static final String STAGING_ENVIRONMENT = "staging";
    public static final String PROD_ENVIRONMENT = "prod";
    
    // File Extensions
    public static final String PNG_EXTENSION = ".png";
    public static final String JPG_EXTENSION = ".jpg";
    public static final String CSV_EXTENSION = ".csv";
    public static final String JSON_EXTENSION = ".json";
    public static final String XML_EXTENSION = ".xml";
    
    // Regular Expressions
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String PHONE_REGEX = "^[+]?[1-9]\\d{1,14}$";
    public static final String DATE_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";
    
    // Common Test Data
    public static final String[] SAMPLE_DEPARTURE_CITIES = {"Istanbul", "Ankara", "Izmir", "Antalya", "Adana"};
    public static final String[] SAMPLE_ARRIVAL_CITIES = {"London", "Paris", "Amsterdam", "Berlin", "Rome"};
    public static final String[] SAMPLE_AIRLINES = {"Turkish Airlines", "Pegasus", "Onur Air", "SunExpress"};
    
    // Error Messages
    public static final String DRIVER_NOT_INITIALIZED = "WebDriver not initialized";
    public static final String ELEMENT_NOT_FOUND = "Element not found";
    public static final String PAGE_NOT_LOADED = "Page not loaded properly";
    public static final String API_REQUEST_FAILED = "API request failed";
    public static final String DATA_FILE_NOT_FOUND = "Test data file not found";
    public static final String SCREENSHOT_FAILED = "Failed to take screenshot";
    
    // Success Messages
    public static final String TEST_PASSED = "Test passed successfully";
    public static final String API_REQUEST_SUCCESS = "API request successful";
    public static final String DATA_VALIDATION_SUCCESS = "Data validation successful";
    public static final String SCREENSHOT_TAKEN = "Screenshot taken successfully";
    
    private TestConfig() {
        // Private constructor to prevent instantiation
    }
}