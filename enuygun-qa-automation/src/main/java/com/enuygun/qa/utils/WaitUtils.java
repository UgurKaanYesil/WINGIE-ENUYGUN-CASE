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
    
    /**
     * Custom ExpectedConditions for complex UI interactions
     */
    public static class CustomExpectedConditions {
        
        /**
         * Wait for dropdown to appear and be populated with options
         */
        public static Function<WebDriver, Boolean> dropdownToBePopulated(By dropdownLocator) {
            return driver -> {
                try {
                    List<WebElement> dropdownElements = driver.findElements(dropdownLocator);
                    return !dropdownElements.isEmpty() && 
                           dropdownElements.stream().anyMatch(WebElement::isDisplayed);
                } catch (Exception e) {
                    return false;
                }
            };
        }
        
        /**
         * Wait for any element from a list of locators to be visible
         */
        public static Function<WebDriver, WebElement> anyElementToBeVisible(By... locators) {
            return driver -> {
                for (By locator : locators) {
                    try {
                        List<WebElement> elements = driver.findElements(locator);
                        for (WebElement element : elements) {
                            if (element.isDisplayed()) {
                                return element;
                            }
                        }
                    } catch (Exception e) {
                        // Continue to next locator
                    }
                }
                return null;
            };
        }
        
        /**
         * Wait for filter panel to expand/be ready
         */
        public static Function<WebDriver, Boolean> filterPanelToBeReady() {
            return driver -> {
                try {
                    // Check for common filter panel indicators
                    By[] filterIndicators = {
                        By.cssSelector("input[type='range'], .slider, .range-slider"),
                        By.cssSelector("input[type='time'], input[placeholder*='saat']"),
                        By.cssSelector(".filter-content, .expanded, [class*='open']")
                    };
                    
                    for (By locator : filterIndicators) {
                        List<WebElement> elements = driver.findElements(locator);
                        if (!elements.isEmpty() && elements.stream().anyMatch(WebElement::isDisplayed)) {
                            return true;
                        }
                    }
                    return false;
                } catch (Exception e) {
                    return false;
                }
            };
        }
        
        /**
         * Wait for page to be fully loaded (no loading indicators)
         */
        public static Function<WebDriver, Boolean> pageToBeFullyLoaded() {
            return driver -> {
                try {
                    // Check if any loading indicators are still present
                    By[] loadingIndicators = {
                        By.cssSelector(".loading, .spinner, .loader, [class*='loading']"),
                        By.xpath("//*[contains(@class,'loading') or contains(@class,'spinner')]")
                    };
                    
                    for (By locator : loadingIndicators) {
                        List<WebElement> loadingElements = driver.findElements(locator);
                        if (loadingElements.stream().anyMatch(WebElement::isDisplayed)) {
                            return false; // Still loading
                        }
                    }
                    
                    // Additional check: JavaScript document ready state
                    String readyState = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("return document.readyState");
                    
                    return "complete".equals(readyState);
                } catch (Exception e) {
                    return false;
                }
            };
        }
    }
    
    /**
     * Convenience methods using custom ExpectedConditions
     */
    public static boolean waitForDropdownToBePopulated(WebDriver driver, By dropdownLocator, Duration timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(CustomExpectedConditions.dropdownToBePopulated(dropdownLocator));
        } catch (TimeoutException e) {
            logger.debug("Timeout waiting for dropdown to be populated: {}", dropdownLocator);
            return false;
        }
    }
    
    public static WebElement waitForAnyElementToBeVisible(WebDriver driver, Duration timeout, By... locators) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(CustomExpectedConditions.anyElementToBeVisible(locators));
        } catch (TimeoutException e) {
            logger.debug("Timeout waiting for any element to be visible from: {}", (Object) locators);
            return null;
        }
    }
    
    public static boolean waitForFilterPanelToBeReady(WebDriver driver, Duration timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(CustomExpectedConditions.filterPanelToBeReady());
        } catch (TimeoutException e) {
            logger.debug("Timeout waiting for filter panel to be ready");
            return false;
        }
    }
    
    public static boolean waitForPageToBeFullyLoaded(WebDriver driver, Duration timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(CustomExpectedConditions.pageToBeFullyLoaded());
        } catch (TimeoutException e) {
            logger.debug("Timeout waiting for page to be fully loaded");
            return false;
        }
    }
}