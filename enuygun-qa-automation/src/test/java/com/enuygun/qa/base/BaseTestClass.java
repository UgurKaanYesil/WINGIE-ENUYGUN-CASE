package com.enuygun.qa.base;

import org.testng.ITestResult;
import org.testng.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.enuygun.qa.utils.WebDriverFactory;
import com.enuygun.qa.utils.ScreenshotUtils;
import com.enuygun.qa.utils.ReportUtils;
import com.enuygun.qa.config.ConfigManager;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Method;
import java.time.Duration;

public class BaseTestClass {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTestClass.class);
    protected WebDriver driver;
    protected String testName;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        try {
            logger.info("=== Starting Test Suite Execution ===");
            logger.info("Environment: {}", ConfigManager.getEnvironment());
            logger.info("Browser: {}", ConfigManager.getBrowser());
            logger.info("Base URL: {}", ConfigManager.getBaseUrl());
            
            // Clean up old screenshots if configured
            ScreenshotUtils.cleanupOldScreenshots(7);
            
        } catch (Exception e) {
            logger.error("Error in beforeSuite setup", e);
            throw new RuntimeException("Failed to initialize test suite", e);
        }
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        try {
            logger.info("=== Starting Test Class: {} ===", this.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Error in beforeClass setup", e);
            throw new RuntimeException("Failed to initialize test class", e);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) {
        try {
            testName = method.getName();
            logger.info("=== Starting Test: {} ===", testName);
            
            // Initialize WebDriver
            initializeDriver();
            
            // Start ExtentReports test
            String testDescription = getTestDescription(method);
            ReportUtils.startTest(testName, testDescription);
            ReportUtils.assignCategory(getTestCategory(method));
            ReportUtils.assignAuthor(getTestAuthor());
            
            ReportUtils.logInfo("Test started: " + testName);
            ReportUtils.logInfo("Browser: " + ConfigManager.getBrowser());
            ReportUtils.logInfo("Environment: " + ConfigManager.getEnvironment());
            
        } catch (Exception e) {
            logger.error("Error in beforeMethod setup for test: {}", testName, e);
            ReportUtils.logFail("Failed to setup test: " + e.getMessage());
            throw new RuntimeException("Failed to setup test method", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        try {
            String methodName = result.getMethod().getMethodName();
            
            if (result.getStatus() == ITestResult.SUCCESS) {
                logger.info("=== Test PASSED: {} ===", methodName);
                ReportUtils.logPass("Test completed successfully");
                
            } else if (result.getStatus() == ITestResult.FAILURE) {
                logger.error("=== Test FAILED: {} ===", methodName);
                Throwable throwable = result.getThrowable();
                
                // Take screenshot on failure
                String screenshotPath = ScreenshotUtils.takeScreenshotOnFailure(driver, methodName, throwable);
                
                // Log failure in report
                ReportUtils.logFail("Test failed: " + throwable.getMessage(), throwable);
                if (screenshotPath != null) {
                    ReportUtils.addScreenshot(screenshotPath);
                }
                
            } else if (result.getStatus() == ITestResult.SKIP) {
                logger.warn("=== Test SKIPPED: {} ===", methodName);
                ReportUtils.logSkip("Test was skipped");
            }
            
        } catch (Exception e) {
            logger.error("Error in afterMethod cleanup for test: {}", testName, e);
        } finally {
            // Always quit driver and end test reporting
            quitDriver();
            ReportUtils.endTest();
        }
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        try {
            logger.info("=== Completed Test Class: {} ===", this.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Error in afterClass cleanup", e);
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        try {
            logger.info("=== Test Suite Execution Completed ===");
            
            // Flush ExtentReports
            ReportUtils.flushReport();
            
        } catch (Exception e) {
            logger.error("Error in afterSuite cleanup", e);
        }
    }

    protected void initializeDriver() {
        try {
            String browserName = ConfigManager.getBrowser();
            WebDriverFactory.BrowserType browserType = WebDriverFactory.getBrowserTypeFromString(browserName);
            
            driver = WebDriverFactory.createDriver(browserType);
            
            // Set timeouts
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigManager.getImplicitTimeout()));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigManager.getPageLoadTimeout()));
            
            logger.info("WebDriver initialized successfully for browser: {}", browserName);
            
        } catch (Exception e) {
            logger.error("Failed to initialize WebDriver", e);
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    protected void quitDriver() {
        try {
            if (driver != null) {
                WebDriverFactory.quitDriver();
                driver = null;
                logger.debug("WebDriver quit successfully");
            }
        } catch (Exception e) {
            logger.error("Error while quitting WebDriver", e);
        }
    }

    protected void navigateToUrl(String url) {
        try {
            driver.get(url);
            logger.info("Navigated to URL: {}", url);
            ReportUtils.logInfo("Navigated to URL: " + url);
        } catch (Exception e) {
            logger.error("Failed to navigate to URL: {}", url, e);
            ReportUtils.logFail("Failed to navigate to URL: " + url);
            throw new RuntimeException("Failed to navigate to URL", e);
        }
    }

    protected void navigateToBaseUrl() {
        navigateToUrl(ConfigManager.getBaseUrl());
    }

    protected String getTestDescription(Method method) {
        // Check for TestNG @Test annotation description
        Test testAnnotation = method.getAnnotation(Test.class);
        if (testAnnotation != null && !testAnnotation.description().isEmpty()) {
            return testAnnotation.description();
        }
        
        // Default description
        return "Automated test: " + method.getName();
    }

    protected String getTestCategory(Method method) {
        // Check for custom category annotation or groups
        Test testAnnotation = method.getAnnotation(Test.class);
        if (testAnnotation != null && testAnnotation.groups().length > 0) {
            return testAnnotation.groups()[0]; // Use first group as category
        }
        
        // Default category based on class name
        String className = this.getClass().getSimpleName();
        if (className.contains("API")) {
            return "API Tests";
        } else if (className.contains("UI")) {
            return "UI Tests";
        } else if (className.contains("Load")) {
            return "Load Tests";
        } else if (className.contains("Data")) {
            return "Data Tests";
        }
        
        return "General Tests";
    }

    protected String getTestAuthor() {
        return System.getProperty("user.name", "QA Engineer");
    }

    protected void logTestStep(String step) {
        logger.info("Test Step: {}", step);
        ReportUtils.logInfo("Step: " + step);
    }

    protected void logTestInfo(String info) {
        logger.info("Test Info: {}", info);
        ReportUtils.logInfo(info);
    }

    protected void assertAndLog(boolean condition, String passMessage, String failMessage) {
        if (condition) {
            logger.info("Assertion PASSED: {}", passMessage);
            ReportUtils.logPass(passMessage);
        } else {
            logger.error("Assertion FAILED: {}", failMessage);
            ReportUtils.logFail(failMessage);
            
            // Take screenshot on assertion failure
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, testName + "_assertion_failed");
            if (screenshotPath != null) {
                ReportUtils.addScreenshot(screenshotPath);
            }
            
            throw new AssertionError(failMessage);
        }
    }

    protected void takeScreenshot(String description) {
        try {
            String screenshotPath = ScreenshotUtils.takeScreenshotWithCustomName(driver, testName + "_" + description);
            if (screenshotPath != null) {
                ReportUtils.addScreenshot(screenshotPath);
                ReportUtils.logInfo("Screenshot taken: " + description);
            }
        } catch (Exception e) {
            logger.error("Failed to take screenshot: {}", description, e);
        }
    }

    // Retry mechanism for flaky tests
    protected void retryOnFailure(Runnable action, int maxRetries, String actionDescription) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                action.run();
                if (attempt > 1) {
                    ReportUtils.logInfo(actionDescription + " succeeded on attempt " + attempt);
                }
                return; // Success
            } catch (Exception e) {
                lastException = e;
                logger.warn("Attempt {} failed for action '{}': {}", attempt, actionDescription, e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(1000); // Wait before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        // All attempts failed
        String errorMessage = String.format("Action '%s' failed after %d attempts", actionDescription, maxRetries);
        logger.error(errorMessage, lastException);
        ReportUtils.logFail(errorMessage);
        throw new RuntimeException(errorMessage, lastException);
    }
}