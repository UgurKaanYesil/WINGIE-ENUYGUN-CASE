package com.enuygun.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.enuygun.qa.utils.WaitUtils;

public class HomePage extends BasePage {

    // Page Elements using Page Factory
    @FindBy(css = "[data-testid='enuygun-logo']")
    private WebElement logo;

    @FindBy(css = "[data-testid='flight-tab']")
    private WebElement flightTab;

    @FindBy(css = "[data-testid='hotel-tab']")
    private WebElement hotelTab;

    @FindBy(css = "[data-testid='bus-tab']")
    private WebElement busTab;

    @FindBy(css = "[data-testid='car-tab']")
    private WebElement carTab;

    @FindBy(css = "[data-testid='origin-input']")
    private WebElement originInput;

    @FindBy(css = "[data-testid='destination-input']")
    private WebElement destinationInput;

    @FindBy(css = "[data-testid='departure-date']")
    private WebElement departureDateInput;

    @FindBy(css = "[data-testid='return-date']")
    private WebElement returnDateInput;

    @FindBy(css = "[data-testid='passenger-selection']")
    private WebElement passengerSelection;

    @FindBy(css = "[data-testid='search-button']")
    private WebElement searchButton;

    @FindBy(css = "[data-testid='one-way-radio']")
    private WebElement oneWayRadio;

    @FindBy(css = "[data-testid='round-trip-radio']")
    private WebElement roundTripRadio;

    // Alternative locators (fallback if data-testid is not available)
    private final By logoLocator = By.cssSelector(".header-logo, .logo, .enuygun-logo");
    private final By flightTabLocator = By.xpath("//a[contains(text(),'Uçak') or contains(text(),'Flight')]");
    private final By hotelTabLocator = By.xpath("//a[contains(text(),'Otel') or contains(text(),'Hotel')]");
    private final By busTabLocator = By.xpath("//a[contains(text(),'Otobüs') or contains(text(),'Bus')]");
    private final By searchButtonLocator = By.cssSelector("button[type='submit'], .search-btn, .btn-search");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void selectFlightTab() {
        try {
            if (flightTab.isDisplayed()) {
                click(flightTab);
            } else {
                click(flightTabLocator);
            }
            logger.info("Selected Flight tab");
        } catch (Exception e) {
            logger.error("Failed to select Flight tab", e);
            throw new RuntimeException("Failed to select Flight tab", e);
        }
    }

    public void selectHotelTab() {
        try {
            if (hotelTab.isDisplayed()) {
                click(hotelTab);
            } else {
                click(hotelTabLocator);
            }
            logger.info("Selected Hotel tab");
        } catch (Exception e) {
            logger.error("Failed to select Hotel tab", e);
            throw new RuntimeException("Failed to select Hotel tab", e);
        }
    }

    public void selectBusTab() {
        try {
            if (busTab.isDisplayed()) {
                click(busTab);
            } else {
                click(busTabLocator);
            }
            logger.info("Selected Bus tab");
        } catch (Exception e) {
            logger.error("Failed to select Bus tab", e);
            throw new RuntimeException("Failed to select Bus tab", e);
        }
    }

    public void enterOrigin(String origin) {
        try {
            sendKeys(originInput, origin);
            logger.info("Entered origin: {}", origin);
        } catch (Exception e) {
            logger.error("Failed to enter origin: {}", origin, e);
            throw new RuntimeException("Failed to enter origin", e);
        }
    }

    public void enterDestination(String destination) {
        try {
            sendKeys(destinationInput, destination);
            logger.info("Entered destination: {}", destination);
        } catch (Exception e) {
            logger.error("Failed to enter destination: {}", destination, e);
            throw new RuntimeException("Failed to enter destination", e);
        }
    }

    public void selectDepartureDate(String date) {
        try {
            click(departureDateInput);
            // Note: Date selection logic would depend on the actual calendar implementation
            // This is a placeholder - actual implementation would need to handle calendar popup
            logger.info("Selected departure date: {}", date);
        } catch (Exception e) {
            logger.error("Failed to select departure date: {}", date, e);
            throw new RuntimeException("Failed to select departure date", e);
        }
    }

    public void selectReturnDate(String date) {
        try {
            click(returnDateInput);
            // Note: Date selection logic would depend on the actual calendar implementation
            logger.info("Selected return date: {}", date);
        } catch (Exception e) {
            logger.error("Failed to select return date: {}", date, e);
            throw new RuntimeException("Failed to select return date", e);
        }
    }

    public void selectPassengerCount(int passengerCount) {
        try {
            click(passengerSelection);
            // Note: Passenger selection logic would depend on the actual dropdown implementation
            logger.info("Selected passenger count: {}", passengerCount);
        } catch (Exception e) {
            logger.error("Failed to select passenger count: {}", passengerCount, e);
            throw new RuntimeException("Failed to select passenger count", e);
        }
    }

    public void selectOneWayTrip() {
        try {
            click(oneWayRadio);
            logger.info("Selected one-way trip");
        } catch (Exception e) {
            logger.error("Failed to select one-way trip", e);
            throw new RuntimeException("Failed to select one-way trip", e);
        }
    }

    public void selectRoundTrip() {
        try {
            click(roundTripRadio);
            logger.info("Selected round trip");
        } catch (Exception e) {
            logger.error("Failed to select round trip", e);
            throw new RuntimeException("Failed to select round trip", e);
        }
    }

    public SearchPage clickSearchButton() {
        try {
            if (searchButton.isDisplayed()) {
                click(searchButton);
            } else {
                click(searchButtonLocator);
            }
            logger.info("Clicked search button");
            return new SearchPage(driver);
        } catch (Exception e) {
            logger.error("Failed to click search button", e);
            throw new RuntimeException("Failed to click search button", e);
        }
    }

    public void searchFlight(String origin, String destination, String departureDate) {
        try {
            selectFlightTab();
            selectOneWayTrip();
            enterOrigin(origin);
            enterDestination(destination);
            selectDepartureDate(departureDate);
            clickSearchButton();
            logger.info("Performed flight search: {} to {} on {}", origin, destination, departureDate);
        } catch (Exception e) {
            logger.error("Failed to search flight", e);
            throw new RuntimeException("Failed to search flight", e);
        }
    }

    public void searchRoundTripFlight(String origin, String destination, String departureDate, String returnDate) {
        try {
            selectFlightTab();
            selectRoundTrip();
            enterOrigin(origin);
            enterDestination(destination);
            selectDepartureDate(departureDate);
            selectReturnDate(returnDate);
            clickSearchButton();
            logger.info("Performed round trip flight search: {} to {} from {} to {}", 
                       origin, destination, departureDate, returnDate);
        } catch (Exception e) {
            logger.error("Failed to search round trip flight", e);
            throw new RuntimeException("Failed to search round trip flight", e);
        }
    }

    public boolean isLogoDisplayed() {
        try {
            return logo.isDisplayed() || isElementVisible(logoLocator);
        } catch (Exception e) {
            logger.debug("Logo not visible");
            return false;
        }
    }

    public boolean isFlightTabActive() {
        try {
            String classAttribute = flightTab.getAttribute("class");
            return classAttribute != null && classAttribute.contains("active");
        } catch (Exception e) {
            logger.debug("Flight tab status unknown");
            return false;
        }
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return WaitUtils.waitForElementToBeVisible(driver, logoLocator) != null &&
                   isElementVisible(By.cssSelector("[data-testid='search-button'], .search-btn"));
        } catch (Exception e) {
            logger.debug("Home page not loaded");
            return false;
        }
    }

    @Override
    public void waitForPageLoad() {
        try {
            WaitUtils.waitForElementToBeVisible(driver, logoLocator);
            WaitUtils.waitForElementToBeVisible(driver, By.cssSelector("[data-testid='search-button'], .search-btn"));
            logger.info("Home page loaded successfully");
        } catch (Exception e) {
            logger.error("Home page did not load properly", e);
            throw new RuntimeException("Home page did not load properly", e);
        }
    }
}