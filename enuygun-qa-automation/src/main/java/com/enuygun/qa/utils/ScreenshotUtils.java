package com.enuygun.qa.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.enuygun.qa.config.ConfigManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = ConfigManager.getScreenshotDirectory();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    static {
        createScreenshotDirectory();
    }

    public static String takeScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            logger.error("WebDriver is null, cannot take screenshot");
            return null;
        }

        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            String fileName = String.format("%s_%s.png", testName, timestamp);
            String filePath = SCREENSHOT_DIR + File.separator + fileName;

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File destFile = new File(filePath);

            FileUtils.copyFile(sourceFile, destFile);
            
            logger.info("Screenshot saved: {}", filePath);
            return filePath;
            
        } catch (IOException e) {
            logger.error("Failed to take screenshot for test: {}", testName, e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error while taking screenshot for test: {}", testName, e);
            return null;
        }
    }

    public static String takeScreenshotOnFailure(WebDriver driver, String testName, Throwable failure) {
        String screenshotPath = takeScreenshot(driver, testName + "_FAILED");
        if (screenshotPath != null) {
            logger.error("Test failed: {}. Screenshot saved: {}", testName, screenshotPath);
            logger.error("Failure reason: ", failure);
        }
        return screenshotPath;
    }

    public static String takeScreenshotWithCustomName(WebDriver driver, String customName) {
        if (driver == null) {
            logger.error("WebDriver is null, cannot take screenshot");
            return null;
        }

        try {
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            String fileName = String.format("%s_%s.png", customName, timestamp);
            String filePath = SCREENSHOT_DIR + File.separator + fileName;

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File destFile = new File(filePath);

            FileUtils.copyFile(sourceFile, destFile);
            
            logger.info("Custom screenshot saved: {}", filePath);
            return filePath;
            
        } catch (IOException e) {
            logger.error("Failed to take custom screenshot: {}", customName, e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error while taking custom screenshot: {}", customName, e);
            return null;
        }
    }

    public static byte[] takeScreenshotAsBytes(WebDriver driver) {
        if (driver == null) {
            logger.error("WebDriver is null, cannot take screenshot");
            return null;
        }

        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshot = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            logger.debug("Screenshot taken as byte array");
            return screenshot;
        } catch (Exception e) {
            logger.error("Failed to take screenshot as bytes", e);
            return null;
        }
    }

    public static String getScreenshotDirectory() {
        return SCREENSHOT_DIR;
    }

    private static void createScreenshotDirectory() {
        try {
            File directory = new File(SCREENSHOT_DIR);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    logger.info("Screenshot directory created: {}", SCREENSHOT_DIR);
                } else {
                    logger.error("Failed to create screenshot directory: {}", SCREENSHOT_DIR);
                }
            }
        } catch (Exception e) {
            logger.error("Error creating screenshot directory: {}", SCREENSHOT_DIR, e);
        }
    }

    public static void cleanupOldScreenshots(int daysToKeep) {
        try {
            File directory = new File(SCREENSHOT_DIR);
            if (!directory.exists()) {
                return;
            }

            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
            if (files == null) {
                return;
            }

            long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L);
            int deletedCount = 0;

            for (File file : files) {
                if (file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        deletedCount++;
                    } else {
                        logger.warn("Failed to delete old screenshot: {}", file.getName());
                    }
                }
            }

            if (deletedCount > 0) {
                logger.info("Cleaned up {} old screenshots (older than {} days)", deletedCount, daysToKeep);
            }
        } catch (Exception e) {
            logger.error("Error during screenshot cleanup", e);
        }
    }
}