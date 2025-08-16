package com.enuygun.qa.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.enuygun.qa.config.ConfigManager;

public class WebDriverFactory {
    private static final Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public enum BrowserType {
        CHROME, FIREFOX
    }

    public static WebDriver createDriver(BrowserType browserType) {
        try {
            WebDriver driver;
            
            switch (browserType) {
                case CHROME:
                    driver = createChromeDriver();
                    break;
                case FIREFOX:
                    driver = createFirefoxDriver();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported browser type: " + browserType);
            }
            
            configureDriver(driver);
            driverThreadLocal.set(driver);
            logger.info("WebDriver created successfully for browser: {}", browserType);
            return driver;
            
        } catch (Exception e) {
            logger.error("Failed to create WebDriver for browser: {}", browserType, e);
            throw new RuntimeException("Failed to create WebDriver", e);
        }
    }

    private static WebDriver createChromeDriver() {
        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            
            if (ConfigManager.isHeadless()) {
                options.addArguments("--headless");
            }
            
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-web-security");
            options.addArguments("--allow-running-insecure-content");
            
            return new ChromeDriver(options);
        } catch (Exception e) {
            logger.error("Failed to create Chrome driver", e);
            throw new RuntimeException("Failed to create Chrome driver", e);
        }
    }

    private static WebDriver createFirefoxDriver() {
        try {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            
            if (ConfigManager.isHeadless()) {
                options.addArguments("--headless");
            }
            
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
            
            return new FirefoxDriver(options);
        } catch (Exception e) {
            logger.error("Failed to create Firefox driver", e);
            throw new RuntimeException("Failed to create Firefox driver", e);
        }
    }

    private static void configureDriver(WebDriver driver) {
        try {
            driver.manage().window().maximize();
            driver.manage().deleteAllCookies();
            logger.debug("Driver configured successfully");
        } catch (Exception e) {
            logger.error("Failed to configure driver", e);
            throw new RuntimeException("Failed to configure driver", e);
        }
    }

    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not initialized. Call createDriver() first.");
        }
        return driver;
    }

    public static void quitDriver() {
        try {
            WebDriver driver = driverThreadLocal.get();
            if (driver != null) {
                driver.quit();
                driverThreadLocal.remove();
                logger.info("WebDriver quit successfully");
            }
        } catch (Exception e) {
            logger.error("Error while quitting WebDriver", e);
        }
    }

    public static BrowserType getBrowserTypeFromString(String browserName) {
        if (browserName == null || browserName.trim().isEmpty()) {
            return BrowserType.CHROME; // Default browser
        }
        
        try {
            return BrowserType.valueOf(browserName.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Unknown browser type: {}. Using Chrome as default.", browserName);
            return BrowserType.CHROME;
        }
    }
}