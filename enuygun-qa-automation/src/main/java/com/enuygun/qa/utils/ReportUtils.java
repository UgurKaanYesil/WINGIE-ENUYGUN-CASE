package com.enuygun.qa.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.enuygun.qa.config.ConfigManager;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportUtils {
    private static final Logger logger = LoggerFactory.getLogger(ReportUtils.class);
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static final String REPORTS_DIR = ConfigManager.getReportsDirectory();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    static {
        initializeReport();
    }

    private static void initializeReport() {
        try {
            createReportsDirectory();
            
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            String reportPath = REPORTS_DIR + File.separator + "ExtentReport_" + timestamp + ".html";
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            configureSparkReporter(sparkReporter);
            
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            setSystemInfo();
            
            logger.info("ExtentReports initialized successfully. Report path: {}", reportPath);
        } catch (Exception e) {
            logger.error("Failed to initialize ExtentReports", e);
            throw new RuntimeException("Failed to initialize ExtentReports", e);
        }
    }

    private static void configureSparkReporter(ExtentSparkReporter sparkReporter) {
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("Enuygun QA Automation Test Report");
        sparkReporter.config().setReportName("Test Execution Report");
        sparkReporter.config().setTimeStampFormat("dd/MM/yyyy HH:mm:ss");
    }

    private static void setSystemInfo() {
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("User", System.getProperty("user.name"));
        extentReports.setSystemInfo("Environment", ConfigManager.getEnvironment());
        extentReports.setSystemInfo("Browser", ConfigManager.getBrowser());
    }

    private static void createReportsDirectory() {
        try {
            File directory = new File(REPORTS_DIR);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    logger.info("Reports directory created: {}", REPORTS_DIR);
                } else {
                    logger.error("Failed to create reports directory: {}", REPORTS_DIR);
                }
            }
        } catch (Exception e) {
            logger.error("Error creating reports directory: {}", REPORTS_DIR, e);
        }
    }

    public static void startTest(String testName, String description) {
        try {
            ExtentTest test = extentReports.createTest(testName, description);
            extentTest.set(test);
            logger.debug("Started test: {}", testName);
        } catch (Exception e) {
            logger.error("Failed to start test: {}", testName, e);
        }
    }

    public static void startTest(String testName) {
        startTest(testName, "");
    }

    public static void logInfo(String message) {
        try {
            ExtentTest test = extentTest.get();
            if (test != null) {
                test.log(Status.INFO, message);
                logger.info(message);
            }
        } catch (Exception e) {
            logger.error("Failed to log info message: {}", message, e);
        }
    }

    public static void logPass(String message) {
        try {
            ExtentTest test = extentTest.get();
            if (test != null) {
                test.log(Status.PASS, message);
                logger.info("PASS: {}", message);
            }
        } catch (Exception e) {
            logger.error("Failed to log pass message: {}", message, e);
        }
    }

    public static void logFail(String message) {
        try {
            ExtentTest test = extentTest.get();
            if (test != null) {
                test.log(Status.FAIL, message);
                logger.error("FAIL: {}", message);
            }
        } catch (Exception e) {
            logger.error("Failed to log fail message: {}", message, e);
        }
    }

    public static void logFail(String message, Throwable throwable) {
        try {
            ExtentTest test = extentTest.get();
            if (test != null) {
                test.log(Status.FAIL, message);
                test.log(Status.FAIL, throwable);
                logger.error("FAIL: {}", message, throwable);
            }
        } catch (Exception e) {
            logger.error("Failed to log fail message with throwable: {}", message, e);
        }
    }

    public static void logSkip(String message) {
        try {
            ExtentTest test = extentTest.get();
            if (test != null) {
                test.log(Status.SKIP, message);
                logger.warn("SKIP: {}", message);
            }
        } catch (Exception e) {
            logger.error("Failed to log skip message: {}", message, e);
        }
    }

    public static void addScreenshot(String screenshotPath) {
        try {
            ExtentTest test = extentTest.get();
            if (test != null && screenshotPath != null) {
                test.addScreenCaptureFromPath(screenshotPath);
                logger.debug("Screenshot added to report: {}", screenshotPath);
            }
        } catch (Exception e) {
            logger.error("Failed to add screenshot to report: {}", screenshotPath, e);
        }
    }

    public static void addScreenshotOnFailure(String screenshotPath, String failureMessage) {
        try {
            logFail(failureMessage);
            addScreenshot(screenshotPath);
        } catch (Exception e) {
            logger.error("Failed to add screenshot on failure", e);
        }
    }

    public static void assignCategory(String category) {
        try {
            ExtentTest test = extentTest.get();
            if (test != null) {
                test.assignCategory(category);
                logger.debug("Category assigned: {}", category);
            }
        } catch (Exception e) {
            logger.error("Failed to assign category: {}", category, e);
        }
    }

    public static void assignAuthor(String author) {
        try {
            ExtentTest test = extentTest.get();
            if (test != null) {
                test.assignAuthor(author);
                logger.debug("Author assigned: {}", author);
            }
        } catch (Exception e) {
            logger.error("Failed to assign author: {}", author, e);
        }
    }

    public static void flushReport() {
        try {
            if (extentReports != null) {
                extentReports.flush();
                logger.info("ExtentReports flushed successfully");
            }
        } catch (Exception e) {
            logger.error("Failed to flush ExtentReports", e);
        }
    }

    public static ExtentTest getCurrentTest() {
        return extentTest.get();
    }

    public static void endTest() {
        try {
            extentTest.remove();
            logger.debug("Test ended and removed from ThreadLocal");
        } catch (Exception e) {
            logger.error("Failed to end test", e);
        }
    }

    public static String getReportsDirectory() {
        return REPORTS_DIR;
    }
}