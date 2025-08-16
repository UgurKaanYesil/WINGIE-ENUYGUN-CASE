package com.enuygun.qa.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.enuygun.qa.config.ConfigManager;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class WaitUtils {
    private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(ConfigManager.getDefaultTimeout());
    private static final Duration DEFAULT_POLLING_INTERVAL = Duration.ofMillis(500);

    public static WebElement waitForElementToBeVisible(WebDriver driver, By locator) {
        return waitForElementToBeVisible(driver, locator, DEFAULT_TIMEOUT);
    }

    public static WebElement waitForElementToBeVisible(WebDriver driver, By locator, Duration timeout) {
        try {
            logger.debug("Waiting for element to be visible: {}", locator);
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.debug("Element found and visible: {}", locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Element not visible within timeout: {} seconds for locator: {}", timeout.getSeconds(), locator);
            throw new TimeoutException("Element not visible: " + locator, e);
        }
    }

    public static WebElement waitForElementToBeClickable(WebDriver driver, By locator) {
        return waitForElementToBeClickable(driver, locator, DEFAULT_TIMEOUT);
    }

    public static WebElement waitForElementToBeClickable(WebDriver driver, By locator, Duration timeout) {
        try {
            logger.debug("Waiting for element to be clickable: {}", locator);
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            logger.debug("Element is clickable: {}", locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Element not clickable within timeout: {} seconds for locator: {}", timeout.getSeconds(), locator);
            throw new TimeoutException("Element not clickable: " + locator, e);
        }
    }

    public static WebElement waitForElementPresence(WebDriver driver, By locator) {
        return waitForElementPresence(driver, locator, DEFAULT_TIMEOUT);
    }

    public static WebElement waitForElementPresence(WebDriver driver, By locator, Duration timeout) {
        try {
            logger.debug("Waiting for element presence: {}", locator);
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            logger.debug("Element present: {}", locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Element not present within timeout: {} seconds for locator: {}", timeout.getSeconds(), locator);
            throw new TimeoutException("Element not present: " + locator, e);
        }
    }

    public static List<WebElement> waitForElementsPresence(WebDriver driver, By locator) {
        return waitForElementsPresence(driver, locator, DEFAULT_TIMEOUT);
    }

    public static List<WebElement> waitForElementsPresence(WebDriver driver, By locator, Duration timeout) {
        try {
            logger.debug("Waiting for elements presence: {}", locator);
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
            logger.debug("Elements found: {} count for locator: {}", elements.size(), locator);
            return elements;
        } catch (TimeoutException e) {
            logger.error("Elements not present within timeout: {} seconds for locator: {}", timeout.getSeconds(), locator);
            throw new TimeoutException("Elements not present: " + locator, e);
        }
    }

    public static boolean waitForElementToDisappear(WebDriver driver, By locator) {
        return waitForElementToDisappear(driver, locator, DEFAULT_TIMEOUT);
    }

    public static boolean waitForElementToDisappear(WebDriver driver, By locator, Duration timeout) {
        try {
            logger.debug("Waiting for element to disappear: {}", locator);
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            boolean disappeared = wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            logger.debug("Element disappeared: {}", locator);
            return disappeared;
        } catch (TimeoutException e) {
            logger.error("Element still visible after timeout: {} seconds for locator: {}", timeout.getSeconds(), locator);
            return false;
        }
    }

    public static boolean waitForTextToBePresentInElement(WebDriver driver, By locator, String text) {
        return waitForTextToBePresentInElement(driver, locator, text, DEFAULT_TIMEOUT);
    }

    public static boolean waitForTextToBePresentInElement(WebDriver driver, By locator, String text, Duration timeout) {
        try {
            logger.debug("Waiting for text '{}' to be present in element: {}", text, locator);
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            boolean textPresent = wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            logger.debug("Text '{}' found in element: {}", text, locator);
            return textPresent;
        } catch (TimeoutException e) {
            logger.error("Text '{}' not found in element within timeout: {} seconds for locator: {}", text, timeout.getSeconds(), locator);
            return false;
        }
    }

    public static boolean waitForUrlToContain(WebDriver driver, String urlFragment) {
        return waitForUrlToContain(driver, urlFragment, DEFAULT_TIMEOUT);
    }

    public static boolean waitForUrlToContain(WebDriver driver, String urlFragment, Duration timeout) {
        try {
            logger.debug("Waiting for URL to contain: {}", urlFragment);
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            boolean urlContains = wait.until(ExpectedConditions.urlContains(urlFragment));
            logger.debug("URL contains fragment: {}", urlFragment);
            return urlContains;
        } catch (TimeoutException e) {
            logger.error("URL does not contain '{}' within timeout: {} seconds", urlFragment, timeout.getSeconds());
            return false;
        }
    }

    public static <T> T fluentWait(WebDriver driver, Function<WebDriver, T> condition) {
        return fluentWait(driver, condition, DEFAULT_TIMEOUT, DEFAULT_POLLING_INTERVAL);
    }

    public static <T> T fluentWait(WebDriver driver, Function<WebDriver, T> condition, Duration timeout, Duration pollingInterval) {
        try {
            logger.debug("Starting fluent wait with timeout: {} seconds", timeout.getSeconds());
            FluentWait<WebDriver> wait = new FluentWait<>(driver)
                    .withTimeout(timeout)
                    .pollingEvery(pollingInterval)
                    .ignoring(NoSuchElementException.class);
            
            T result = wait.until(condition);
            logger.debug("Fluent wait condition met");
            return result;
        } catch (TimeoutException e) {
            logger.error("Fluent wait condition not met within timeout: {} seconds", timeout.getSeconds());
            throw new TimeoutException("Fluent wait condition not met", e);
        }
    }

    public static boolean isElementPresent(WebDriver driver, By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static boolean isElementVisible(WebDriver driver, By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}