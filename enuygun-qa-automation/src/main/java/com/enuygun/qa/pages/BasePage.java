package com.enuygun.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.enuygun.qa.utils.WaitUtils;
import com.enuygun.qa.utils.ScreenshotUtils;
import com.enuygun.qa.utils.ReportUtils;
import com.enuygun.qa.config.ConfigManager;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected WebDriver driver;
    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(ConfigManager.getDefaultTimeout());

    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        logger.debug("Initialized page: {}", this.getClass().getSimpleName());
    }

    protected WebElement findElement(By locator) {
        try {
            return WaitUtils.waitForElementPresence(driver, locator);
        } catch (Exception e) {
            logger.error("Element not found: {}", locator, e);
            ReportUtils.logFail("Element not found: " + locator);
            throw e;
        }
    }

    protected List<WebElement> findElements(By locator) {
        try {
            return WaitUtils.waitForElementsPresence(driver, locator);
        } catch (Exception e) {
            logger.error("Elements not found: {}", locator, e);
            ReportUtils.logFail("Elements not found: " + locator);
            throw e;
        }
    }

    protected void click(By locator) {
        try {
            WebElement element = WaitUtils.waitForElementToBeClickable(driver, locator);
            element.click();
            logger.debug("Clicked on element: {}", locator);
            ReportUtils.logInfo("Clicked on element: " + locator);
        } catch (Exception e) {
            logger.error("Failed to click on element: {}", locator, e);
            ReportUtils.logFail("Failed to click on element: " + locator);
            ScreenshotUtils.takeScreenshot(driver, "click_failed");
            throw e;
        }
    }

    protected void click(WebElement element) {
        try {
            element.click();
            logger.debug("Clicked on element: {}", element);
            ReportUtils.logInfo("Clicked on element");
        } catch (Exception e) {
            logger.error("Failed to click on element: {}", element, e);
            ReportUtils.logFail("Failed to click on element");
            ScreenshotUtils.takeScreenshot(driver, "click_failed");
            throw e;
        }
    }

    protected void sendKeys(By locator, String text) {
        try {
            WebElement element = WaitUtils.waitForElementToBeVisible(driver, locator);
            element.clear();
            element.sendKeys(text);
            logger.debug("Entered text '{}' in element: {}", text, locator);
            ReportUtils.logInfo("Entered text in element: " + locator);
        } catch (Exception e) {
            logger.error("Failed to enter text '{}' in element: {}", text, locator, e);
            ReportUtils.logFail("Failed to enter text in element: " + locator);
            ScreenshotUtils.takeScreenshot(driver, "sendkeys_failed");
            throw e;
        }
    }

    protected void sendKeys(WebElement element, String text) {
        try {
            element.clear();
            element.sendKeys(text);
            logger.debug("Entered text '{}' in element", text);
            ReportUtils.logInfo("Entered text in element");
        } catch (Exception e) {
            logger.error("Failed to enter text '{}' in element", text, e);
            ReportUtils.logFail("Failed to enter text in element");
            ScreenshotUtils.takeScreenshot(driver, "sendkeys_failed");
            throw e;
        }
    }

    protected String getText(By locator) {
        try {
            WebElement element = WaitUtils.waitForElementToBeVisible(driver, locator);
            String text = element.getText();
            logger.debug("Retrieved text '{}' from element: {}", text, locator);
            return text;
        } catch (Exception e) {
            logger.error("Failed to get text from element: {}", locator, e);
            ReportUtils.logFail("Failed to get text from element: " + locator);
            throw e;
        }
    }

    protected String getText(WebElement element) {
        try {
            String text = element.getText();
            logger.debug("Retrieved text '{}' from element", text);
            return text;
        } catch (Exception e) {
            logger.error("Failed to get text from element", e);
            ReportUtils.logFail("Failed to get text from element");
            throw e;
        }
    }

    protected String getAttribute(By locator, String attribute) {
        try {
            WebElement element = WaitUtils.waitForElementPresence(driver, locator);
            String value = element.getAttribute(attribute);
            logger.debug("Retrieved attribute '{}' value '{}' from element: {}", attribute, value, locator);
            return value;
        } catch (Exception e) {
            logger.error("Failed to get attribute '{}' from element: {}", attribute, locator, e);
            ReportUtils.logFail("Failed to get attribute from element: " + locator);
            throw e;
        }
    }

    protected void selectByText(By locator, String text) {
        try {
            WebElement element = WaitUtils.waitForElementPresence(driver, locator);
            Select select = new Select(element);
            select.selectByVisibleText(text);
            logger.debug("Selected option '{}' from dropdown: {}", text, locator);
            ReportUtils.logInfo("Selected option from dropdown: " + locator);
        } catch (Exception e) {
            logger.error("Failed to select option '{}' from dropdown: {}", text, locator, e);
            ReportUtils.logFail("Failed to select option from dropdown: " + locator);
            throw e;
        }
    }

    protected void selectByValue(By locator, String value) {
        try {
            WebElement element = WaitUtils.waitForElementPresence(driver, locator);
            Select select = new Select(element);
            select.selectByValue(value);
            logger.debug("Selected value '{}' from dropdown: {}", value, locator);
            ReportUtils.logInfo("Selected value from dropdown: " + locator);
        } catch (Exception e) {
            logger.error("Failed to select value '{}' from dropdown: {}", value, locator, e);
            ReportUtils.logFail("Failed to select value from dropdown: " + locator);
            throw e;
        }
    }

    protected boolean isElementPresent(By locator) {
        return WaitUtils.isElementPresent(driver, locator);
    }

    protected boolean isElementVisible(By locator) {
        return WaitUtils.isElementVisible(driver, locator);
    }

    protected void waitForElementToDisappear(By locator) {
        try {
            WaitUtils.waitForElementToDisappear(driver, locator);
            logger.debug("Element disappeared: {}", locator);
        } catch (Exception e) {
            logger.error("Element did not disappear: {}", locator, e);
            throw e;
        }
    }

    protected void waitForTextToBePresentInElement(By locator, String text) {
        try {
            WaitUtils.waitForTextToBePresentInElement(driver, locator, text);
            logger.debug("Text '{}' found in element: {}", text, locator);
        } catch (Exception e) {
            logger.error("Text '{}' not found in element: {}", text, locator, e);
            throw e;
        }
    }

    protected void scrollToElement(By locator) {
        try {
            WebElement element = findElement(locator);
            scrollToElement(element);
        } catch (Exception e) {
            logger.error("Failed to scroll to element: {}", locator, e);
            throw e;
        }
    }

    protected void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            logger.debug("Scrolled to element");
        } catch (Exception e) {
            logger.error("Failed to scroll to element", e);
            throw e;
        }
    }

    protected void refreshPage() {
        try {
            driver.navigate().refresh();
            logger.debug("Page refreshed");
            ReportUtils.logInfo("Page refreshed");
        } catch (Exception e) {
            logger.error("Failed to refresh page", e);
            ReportUtils.logFail("Failed to refresh page");
            throw e;
        }
    }

    protected void navigateBack() {
        try {
            driver.navigate().back();
            logger.debug("Navigated back");
            ReportUtils.logInfo("Navigated back");
        } catch (Exception e) {
            logger.error("Failed to navigate back", e);
            ReportUtils.logFail("Failed to navigate back");
            throw e;
        }
    }

    protected void navigateForward() {
        try {
            driver.navigate().forward();
            logger.debug("Navigated forward");
            ReportUtils.logInfo("Navigated forward");
        } catch (Exception e) {
            logger.error("Failed to navigate forward", e);
            ReportUtils.logFail("Failed to navigate forward");
            throw e;
        }
    }

    protected String getCurrentUrl() {
        try {
            String url = driver.getCurrentUrl();
            logger.debug("Current URL: {}", url);
            return url;
        } catch (Exception e) {
            logger.error("Failed to get current URL", e);
            throw e;
        }
    }

    protected String getPageTitle() {
        try {
            String title = driver.getTitle();
            logger.debug("Page title: {}", title);
            return title;
        } catch (Exception e) {
            logger.error("Failed to get page title", e);
            throw e;
        }
    }

    protected void waitForPageToLoad() {
        try {
            WaitUtils.waitForUrlToContain(driver, "");
            logger.debug("Page loaded successfully");
        } catch (Exception e) {
            logger.error("Page did not load properly", e);
            throw e;
        }
    }

    public abstract boolean isPageLoaded();
    
    public abstract void waitForPageLoad();
}