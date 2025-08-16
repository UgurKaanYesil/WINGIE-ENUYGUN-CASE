package com.enuygun.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.enuygun.qa.utils.WaitUtils;

import java.util.List;

public class SearchPage extends BasePage {

    // Page Elements using Page Factory
    @FindBy(css = "[data-testid='search-results']")
    private WebElement searchResults;

    @FindBy(css = "[data-testid='flight-list']")
    private WebElement flightList;

    @FindBy(css = "[data-testid='filter-panel']")
    private WebElement filterPanel;

    @FindBy(css = "[data-testid='sort-options']")
    private WebElement sortOptions;

    @FindBy(css = "[data-testid='loading-indicator']")
    private WebElement loadingIndicator;

    @FindBy(css = "[data-testid='search-summary']")
    private WebElement searchSummary;

    @FindBy(css = "[data-testid='modify-search']")
    private WebElement modifySearchButton;

    // Filter elements
    @FindBy(css = "[data-testid='airline-filter']")
    private WebElement airlineFilter;

    @FindBy(css = "[data-testid='price-filter']")
    private WebElement priceFilter;

    @FindBy(css = "[data-testid='departure-time-filter']")
    private WebElement departureTimeFilter;

    @FindBy(css = "[data-testid='duration-filter']")
    private WebElement durationFilter;

    @FindBy(css = "[data-testid='stops-filter']")
    private WebElement stopsFilter;

    // Sort elements
    @FindBy(css = "[data-testid='sort-price']")
    private WebElement sortByPrice;

    @FindBy(css = "[data-testid='sort-duration']")
    private WebElement sortByDuration;

    @FindBy(css = "[data-testid='sort-departure']")
    private WebElement sortByDeparture;

    // Alternative locators (fallback)
    private final By searchResultsLocator = By.cssSelector(".search-results, .flight-results, .results-container");
    private final By flightListLocator = By.cssSelector(".flight-list, .flights, .results-list");
    private final By loadingLocator = By.cssSelector(".loading, .spinner, .loading-indicator");
    private final By noResultsLocator = By.cssSelector(".no-results, .empty-results");

    public SearchPage(WebDriver driver) {
        super(driver);
    }

    public boolean areSearchResultsDisplayed() {
        try {
            return searchResults.isDisplayed() || isElementVisible(searchResultsLocator);
        } catch (Exception e) {
            logger.debug("Search results not visible");
            return false;
        }
    }

    public void waitForSearchResults() {
        try {
            // Wait for loading to disappear
            if (isElementPresent(loadingLocator)) {
                waitForElementToDisappear(loadingLocator);
            }
            
            // Wait for results to appear
            WaitUtils.waitForElementToBeVisible(driver, searchResultsLocator);
            logger.info("Search results loaded successfully");
        } catch (Exception e) {
            logger.error("Search results did not load properly", e);
            throw new RuntimeException("Search results did not load properly", e);
        }
    }

    public int getFlightResultsCount() {
        try {
            List<WebElement> flights = findElements(By.cssSelector("[data-testid='flight-item'], .flight-item, .flight-card"));
            logger.info("Found {} flight results", flights.size());
            return flights.size();
        } catch (Exception e) {
            logger.error("Failed to get flight results count", e);
            return 0;
        }
    }

    public List<WebElement> getFlightResults() {
        try {
            return findElements(By.cssSelector("[data-testid='flight-item'], .flight-item, .flight-card"));
        } catch (Exception e) {
            logger.error("Failed to get flight results", e);
            throw new RuntimeException("Failed to get flight results", e);
        }
    }

    public void selectFirstFlight() {
        try {
            List<WebElement> flights = getFlightResults();
            if (!flights.isEmpty()) {
                WebElement firstFlight = flights.get(0);
                WebElement selectButton = firstFlight.findElement(By.cssSelector("[data-testid='select-flight'], .select-btn, .book-btn"));
                click(selectButton);
                logger.info("Selected first flight from results");
            } else {
                throw new RuntimeException("No flights available to select");
            }
        } catch (Exception e) {
            logger.error("Failed to select first flight", e);
            throw new RuntimeException("Failed to select first flight", e);
        }
    }

    public void selectFlightByIndex(int index) {
        try {
            List<WebElement> flights = getFlightResults();
            if (index >= 0 && index < flights.size()) {
                WebElement flight = flights.get(index);
                WebElement selectButton = flight.findElement(By.cssSelector("[data-testid='select-flight'], .select-btn, .book-btn"));
                click(selectButton);
                logger.info("Selected flight at index {}", index);
            } else {
                throw new RuntimeException("Invalid flight index: " + index);
            }
        } catch (Exception e) {
            logger.error("Failed to select flight at index {}", index, e);
            throw new RuntimeException("Failed to select flight at index " + index, e);
        }
    }

    public void filterByAirline(String airline) {
        try {
            click(airlineFilter);
            WebElement airlineOption = findElement(By.xpath("//label[contains(text(),'" + airline + "')]"));
            click(airlineOption);
            logger.info("Applied airline filter: {}", airline);
        } catch (Exception e) {
            logger.error("Failed to filter by airline: {}", airline, e);
            throw new RuntimeException("Failed to filter by airline", e);
        }
    }

    public void filterByPriceRange(String minPrice, String maxPrice) {
        try {
            click(priceFilter);
            WebElement minPriceInput = findElement(By.cssSelector("[data-testid='min-price'], .min-price"));
            WebElement maxPriceInput = findElement(By.cssSelector("[data-testid='max-price'], .max-price"));
            
            sendKeys(minPriceInput, minPrice);
            sendKeys(maxPriceInput, maxPrice);
            
            WebElement applyButton = findElement(By.cssSelector("[data-testid='apply-price-filter'], .apply-filter"));
            click(applyButton);
            
            logger.info("Applied price filter: {} - {}", minPrice, maxPrice);
        } catch (Exception e) {
            logger.error("Failed to filter by price range: {} - {}", minPrice, maxPrice, e);
            throw new RuntimeException("Failed to filter by price range", e);
        }
    }

    public void filterByDirectFlights() {
        try {
            click(stopsFilter);
            WebElement directFlightOption = findElement(By.xpath("//label[contains(text(),'Aktarmasız') or contains(text(),'Direct')]"));
            click(directFlightOption);
            logger.info("Applied direct flights filter");
        } catch (Exception e) {
            logger.error("Failed to filter by direct flights", e);
            throw new RuntimeException("Failed to filter by direct flights", e);
        }
    }

    public void sortByPrice() {
        try {
            click(sortByPrice);
            waitForSearchResults(); // Wait for results to refresh
            logger.info("Sorted results by price");
        } catch (Exception e) {
            logger.error("Failed to sort by price", e);
            throw new RuntimeException("Failed to sort by price", e);
        }
    }

    public void sortByDuration() {
        try {
            click(sortByDuration);
            waitForSearchResults(); // Wait for results to refresh
            logger.info("Sorted results by duration");
        } catch (Exception e) {
            logger.error("Failed to sort by duration", e);
            throw new RuntimeException("Failed to sort by duration", e);
        }
    }

    public void sortByDepartureTime() {
        try {
            click(sortByDeparture);
            waitForSearchResults(); // Wait for results to refresh
            logger.info("Sorted results by departure time");
        } catch (Exception e) {
            logger.error("Failed to sort by departure time", e);
            throw new RuntimeException("Failed to sort by departure time", e);
        }
    }

    public String getSearchSummary() {
        try {
            return getText(searchSummary);
        } catch (Exception e) {
            logger.error("Failed to get search summary", e);
            return "";
        }
    }

    public boolean isNoResultsDisplayed() {
        try {
            return isElementVisible(noResultsLocator);
        } catch (Exception e) {
            return false;
        }
    }

    public HomePage modifySearch() {
        try {
            click(modifySearchButton);
            logger.info("Clicked modify search button");
            return new HomePage(driver);
        } catch (Exception e) {
            logger.error("Failed to modify search", e);
            throw new RuntimeException("Failed to modify search", e);
        }
    }

    public String getFirstFlightPrice() {
        try {
            List<WebElement> flights = getFlightResults();
            if (!flights.isEmpty()) {
                WebElement firstFlight = flights.get(0);
                WebElement priceElement = firstFlight.findElement(By.cssSelector("[data-testid='flight-price'], .price, .amount"));
                String price = getText(priceElement);
                logger.info("First flight price: {}", price);
                return price;
            } else {
                throw new RuntimeException("No flights found to get price");
            }
        } catch (Exception e) {
            logger.error("Failed to get first flight price", e);
            throw new RuntimeException("Failed to get first flight price", e);
        }
    }

    public String getFirstFlightDuration() {
        try {
            List<WebElement> flights = getFlightResults();
            if (!flights.isEmpty()) {
                WebElement firstFlight = flights.get(0);
                WebElement durationElement = firstFlight.findElement(By.cssSelector("[data-testid='flight-duration'], .duration, .flight-time"));
                String duration = getText(durationElement);
                logger.info("First flight duration: {}", duration);
                return duration;
            } else {
                throw new RuntimeException("No flights found to get duration");
            }
        } catch (Exception e) {
            logger.error("Failed to get first flight duration", e);
            throw new RuntimeException("Failed to get first flight duration", e);
        }
    }

    @Override
    public boolean isPageLoaded() {
        try {
            return areSearchResultsDisplayed() && !isElementPresent(loadingLocator);
        } catch (Exception e) {
            logger.debug("Search page not loaded");
            return false;
        }
    }

    @Override
    public void waitForPageLoad() {
        try {
            waitForSearchResults();
            logger.info("Search page loaded successfully");
        } catch (Exception e) {
            logger.error("Search page did not load properly", e);
            throw new RuntimeException("Search page did not load properly", e);
        }
    }
}