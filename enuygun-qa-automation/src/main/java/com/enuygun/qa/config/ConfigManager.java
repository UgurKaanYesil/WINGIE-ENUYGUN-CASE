package com.enuygun.qa.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static Properties properties;
    private static final String CONFIG_FILE = "test-config.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try {
            properties = new Properties();
            InputStream inputStream = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            
            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Configuration loaded successfully from: {}", CONFIG_FILE);
            } else {
                logger.warn("Configuration file not found: {}. Using default values.", CONFIG_FILE);
                setDefaultProperties();
            }
        } catch (IOException e) {
            logger.error("Failed to load configuration file: {}", CONFIG_FILE, e);
            setDefaultProperties();
        }
    }

    private static void setDefaultProperties() {
        properties = new Properties();
        
        // Default browser settings
        properties.setProperty("browser.default", "chrome");
        properties.setProperty("browser.headless", "false");
        
        // Default timeout settings
        properties.setProperty("timeout.default", "30");
        properties.setProperty("timeout.page.load", "60");
        properties.setProperty("timeout.implicit", "10");
        
        // Default URLs
        properties.setProperty("url.base", "https://www.enuygun.com");
        properties.setProperty("url.api.base", "https://api.enuygun.com");
        
        // Default directories
        properties.setProperty("dir.screenshots", "screenshots");
        properties.setProperty("dir.reports", "reports");
        properties.setProperty("dir.testdata", "test-data");
        
        // Default environment
        properties.setProperty("environment", "dev");
        
        // Default test date settings (days from current date)
        properties.setProperty("test.date.departure.offset", "30");
        properties.setProperty("test.date.return.offset", "37");
        properties.setProperty("test.date.format", "dd.MM.yyyy");
        
        // Default flight search settings
        properties.setProperty("flight.search.origin", "Istanbul");
        properties.setProperty("flight.search.destination", "Ankara");
        properties.setProperty("flight.filter.time.start", "10:00");
        properties.setProperty("flight.filter.time.end", "17:00");
        
        logger.info("Default configuration properties set");
    }

    public static String getProperty(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null) {
            logger.debug("Using system property for {}: {}", key, systemProperty);
            return systemProperty;
        }
        
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property not found: {}", key);
        }
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public static int getIntProperty(String key, int defaultValue) {
        try {
            String value = getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.error("Invalid integer value for property {}: {}", key, getProperty(key));
            return defaultValue;
        }
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        try {
            String value = getProperty(key);
            return value != null ? Boolean.parseBoolean(value) : defaultValue;
        } catch (Exception e) {
            logger.error("Invalid boolean value for property {}: {}", key, getProperty(key));
            return defaultValue;
        }
    }

    // Browser Configuration
    public static String getBrowser() {
        return getProperty("browser.default", "chrome");
    }

    public static boolean isHeadless() {
        return getBooleanProperty("browser.headless", false);
    }

    // Timeout Configuration
    public static int getDefaultTimeout() {
        return getIntProperty("timeout.default", 10);
    }

    public static int getPageLoadTimeout() {
        return getIntProperty("timeout.page.load", 30);
    }

    public static int getImplicitTimeout() {
        return getIntProperty("timeout.implicit", 5);
    }

    // URL Configuration
    public static String getBaseUrl() {
        return getProperty("url.base", "https://www.enuygun.com");
    }

    public static String getApiBaseUrl() {
        return getProperty("url.api.base", "https://api.enuygun.com");
    }

    // Directory Configuration
    public static String getScreenshotDirectory() {
        return getProperty("dir.screenshots", "screenshots");
    }

    public static String getReportsDirectory() {
        return getProperty("dir.reports", "reports");
    }

    public static String getTestDataDirectory() {
        return getProperty("dir.testdata", "test-data");
    }

    // Environment Configuration
    public static String getEnvironment() {
        return getProperty("environment", "dev");
    }

    // Database Configuration (if needed)
    public static String getDatabaseUrl() {
        return getProperty("db.url");
    }

    public static String getDatabaseUsername() {
        return getProperty("db.username");
    }

    public static String getDatabasePassword() {
        return getProperty("db.password");
    }

    // API Configuration
    public static String getApiKey() {
        return getProperty("api.key");
    }

    public static String getApiSecret() {
        return getProperty("api.secret");
    }

    public static int getApiTimeout() {
        return getIntProperty("api.timeout", 30);
    }

    // Load Testing Configuration
    public static String getLoadTestBaseUrl() {
        return getProperty("loadtest.base.url", getBaseUrl());
    }

    public static int getLoadTestUsers() {
        return getIntProperty("loadtest.users", 10);
    }

    public static int getLoadTestDuration() {
        return getIntProperty("loadtest.duration", 60);
    }

    // Test Data Configuration
    public static String getTestDataFile(String fileName) {
        return getTestDataDirectory() + "/" + fileName;
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
        logger.debug("Property set: {} = {}", key, value);
    }

    public static void reloadProperties() {
        logger.info("Reloading configuration properties...");
        loadProperties();
    }

    public static void printAllProperties() {
        logger.info("Current configuration properties:");
        properties.forEach((key, value) -> logger.info("{} = {}", key, value));
    }
    
    // Test Date Configuration
    public static int getDepartureDateOffset() {
        return getIntProperty("test.date.departure.offset", 30);
    }
    
    public static int getReturnDateOffset() {
        return getIntProperty("test.date.return.offset", 37);
    }
    
    public static String getDateFormat() {
        return getProperty("test.date.format", "dd.MM.yyyy");
    }
    
    // Flight Search Configuration
    public static String getFlightOrigin() {
        return getProperty("flight.search.origin", "Istanbul");
    }
    
    public static String getFlightDestination() {
        return getProperty("flight.search.destination", "Ankara");
    }
    
    public static String getFlightFilterTimeStart() {
        return getProperty("flight.filter.time.start", "10:00");
    }
    
    public static String getFlightFilterTimeEnd() {
        return getProperty("flight.filter.time.end", "17:00");
    }
}