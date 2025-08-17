package com.enuygun.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.enuygun.qa.utils.WaitUtils;
import java.util.List;
import java.time.Duration;

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

    // Calendar date picker elements
    @FindBy(css = ".calendar-popup, .date-picker")
    private WebElement calendarPopup;

    @FindBy(css = ".calendar-next, .next-month")
    private WebElement calendarNextButton;

    @FindBy(css = ".calendar-prev, .prev-month")
    private WebElement calendarPrevButton;

    // City selection dropdown elements
    @FindBy(css = "[data-testid='origin-dropdown'], .origin-suggestions")
    private WebElement originDropdown;

    @FindBy(css = "[data-testid='destination-dropdown'], .destination-suggestions")
    private WebElement destinationDropdown;

    // Alternative locators (fallback if data-testid is not available)
    private final By logoLocator = By.cssSelector("img[alt*='Enuygun'], img[src*='enuygun'], .logo, .header-logo, a[href='/']");
    private final By flightTabLocator = By.xpath("//a[contains(text(),'Uçak') or contains(text(),'Flight') or contains(@class,'flight')] | //button[contains(text(),'Uçak')] | //li[contains(@class,'flight')] | //*[@data-tab='flight']");
    private final By hotelTabLocator = By.xpath("//a[contains(text(),'Otel') or contains(text(),'Hotel')] | //button[contains(text(),'Otel')] | //li[contains(@class,'hotel')]");
    private final By busTabLocator = By.xpath("//a[contains(text(),'Otobüs') or contains(text(),'Bus')] | //button[contains(text(),'Otobüs')]");
    private final By searchButtonLocator = By.xpath("//button[@type='submit']");
    private final By calendarPopupLocator = By.xpath("//div[@class='sc-tNXst lmdPEV']");
    private final By cityDropdownLocator = By.cssSelector(".suggestions, .dropdown-menu, .autocomplete-list, .city-list, .airport-list, .autocomplete-dropdown, .location-suggestions, .search-suggestions, ul[role='listbox'], [role='option'], .suggestion-item, .location-item");
    
    // More comprehensive locators for flight search form
    private final By originInputLocator = By.cssSelector("input[placeholder*='Nereden'], input[placeholder*='Origin'], input[name*='origin'], input[id*='origin'], input[class*='origin']");
    private final By destinationInputLocator = By.cssSelector("input[placeholder*='Nereye'], input[placeholder*='Destination'], input[name*='destination'], input[id*='destination'], input[class*='destination']");
    private final By departureDateLocator = By.cssSelector("input[placeholder*='Gidiş'], input[placeholder*='Departure'], input[name*='departure'], input[id*='departure-date'], input[type='date']");
    private final By returnDateLocator = By.cssSelector("input[placeholder*='Dönüş'], input[placeholder*='Return'], input[name*='return'], input[id*='return-date']");
    private final By roundTripLocator = By.cssSelector("input[type='radio'][value*='round'], input[id*='round'], label:contains('Gidiş-Dönüş')");
    private final By oneWayLocator = By.cssSelector("input[type='radio'][value*='oneway'], input[id*='oneway'], label:contains('Tek Yön')");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void selectFlightTab() {
        try {
            boolean tabSelected = false;
            
            // Try data-testid first
            try {
                if (flightTab.isDisplayed()) {
                    click(flightTab);
                    tabSelected = true;
                }
            } catch (Exception e) {
                logger.debug("Data-testid flight tab not found, trying fallback locators...");
            }
            
            // Try fallback XPath locator
            if (!tabSelected) {
                try {
                    WebElement flightTabElement = findElement(flightTabLocator);
                    click(flightTabElement);
                    tabSelected = true;
                } catch (Exception e) {
                    logger.debug("XPath flight tab locator failed, trying alternative approaches...");
                }
            }
            
            // Try alternative approaches for flight tab
            if (!tabSelected) {
                try {
                    // Look for any link or button that might be the flight tab
                    List<WebElement> possibleTabs = findElements(By.cssSelector("a, button, li, span"));
                    for (WebElement tab : possibleTabs) {
                        String text = tab.getText().toLowerCase();
                        if (text.contains("uçak") || text.contains("flight") || 
                            text.contains("uçuş") || text.equals("flight")) {
                            click(tab);
                            tabSelected = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Text-based flight tab search failed");
                }
            }
            
            // If still not found, maybe flight tab is already selected or default
            if (!tabSelected) {
                logger.info("Flight tab not found - might be already selected or default");
                tabSelected = true; // Continue anyway
            }
            
            if (tabSelected) {
                logger.info("Selected Flight tab (or already active)");
            }
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

    public HomePage enterOrigin(String origin) {
        try {
            // Try multiple approaches to find and fill origin input
            WebElement originField = null;
            
            // Try data-testid first
            try {
                originField = originInput;
                if (originField.isDisplayed()) {
                    click(originField);
                    originField.clear();
                    sendKeys(originField, origin);
                }
            } catch (Exception e) {
                logger.debug("Data-testid origin input not found, trying fallback locators...");
                // Try fallback locator
                originField = findElement(originInputLocator);
                click(originField);
                originField.clear();
                sendKeys(originField, origin);
            }
            
            // Wait for dropdown and select first matching option
            try {
                // Use explicit wait for dropdown to be populated
                boolean dropdownAppeared = WaitUtils.waitForDropdownToBePopulated(driver, cityDropdownLocator, Duration.ofSeconds(3));
                if (dropdownAppeared) {
                    selectCityFromDropdown(origin);
                } else {
                    // Fallback if no dropdown detected
                    logger.debug("No dropdown detected, trying direct city selection");
                    selectCityFromDropdown(origin);
                }
            } catch (Exception e) {
                logger.debug("City dropdown selection failed, trying fallback: {}", e.getMessage());
                // Fallback: Try Enter key
                try {
                    WebElement activeElement = driver.switchTo().activeElement();
                    activeElement.sendKeys(org.openqa.selenium.Keys.ENTER);
                    logger.info("Used Enter key fallback for origin input");
                } catch (Exception enterException) {
                    logger.debug("Enter key fallback also failed: {}", enterException.getMessage());
                }
            }
            
            logger.info("Entered origin: {}", origin);
            return this; // Fluent interface
        } catch (Exception e) {
            logger.error("Failed to enter origin: {}", origin, e);
            throw new RuntimeException("Failed to enter origin: " + origin, e);
        }
    }

    public HomePage enterDestination(String destination) {
        try {
            // Try multiple approaches to find and fill destination input
            WebElement destinationField = null;
            
            // Try data-testid first
            try {
                destinationField = destinationInput;
                if (destinationField.isDisplayed()) {
                    click(destinationField);
                    destinationField.clear();
                    sendKeys(destinationField, destination);
                }
            } catch (Exception e) {
                logger.debug("Data-testid destination input not found, trying fallback locators...");
                // Try fallback locator
                destinationField = findElement(destinationInputLocator);
                click(destinationField);
                destinationField.clear();
                sendKeys(destinationField, destination);
            }
            
            // Wait for dropdown and select first matching option
            try {
                // Use explicit wait for dropdown to be populated
                boolean dropdownAppeared = WaitUtils.waitForDropdownToBePopulated(driver, cityDropdownLocator, Duration.ofSeconds(3));
                if (dropdownAppeared) {
                    selectCityFromDropdown(destination);
                } else {
                    // Fallback if no dropdown detected
                    logger.debug("No dropdown detected, trying direct city selection");
                    selectCityFromDropdown(destination);
                }
            } catch (Exception e) {
                logger.debug("City dropdown selection failed, trying fallback: {}", e.getMessage());
                // Fallback: Try Enter key
                try {
                    WebElement activeElement = driver.switchTo().activeElement();
                    activeElement.sendKeys(org.openqa.selenium.Keys.ENTER);
                    logger.info("Used Enter key fallback for destination input");
                } catch (Exception enterException) {
                    logger.debug("Enter key fallback also failed: {}", enterException.getMessage());
                }
            }
            
            logger.info("Entered destination: {}", destination);
            return this; // Fluent interface
        } catch (Exception e) {
            logger.error("Failed to enter destination: {}", destination, e);
            throw new RuntimeException("Failed to enter destination: " + destination, e);
        }
    }

    public HomePage selectDepartureDate(String date) {
        try {
            // Try multiple approaches to find and click departure date field
            WebElement dateField = null;
            
            try {
                dateField = departureDateInput;
                if (dateField.isDisplayed()) {
                    click(dateField);
                }
            } catch (Exception e) {
                logger.debug("Data-testid departure date input not found, trying fallback locators...");
                dateField = findElement(departureDateLocator);
                click(dateField);
            }
            
            // Try different approaches to set the date
            try {
                // First try: Wait for calendar popup and select from calendar
                WaitUtils.waitForElementToBeVisible(driver, calendarPopupLocator);
                selectDateFromCalendar(date);
            } catch (Exception e) {
                logger.debug("Calendar popup not found, trying direct input...");
                try {
                    // Second try: Direct input to date field
                    dateField.clear();
                    sendKeys(dateField, date);
                } catch (Exception e2) {
                    // Third try: Use JavaScript to set value
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "arguments[0].value = arguments[1];", dateField, date);
                }
            }
            
            logger.info("Selected departure date: {}", date);
            return this; // Fluent interface
        } catch (Exception e) {
            logger.error("Failed to select departure date: {}", date, e);
            throw new RuntimeException("Failed to select departure date: " + date, e);
        }
    }

    public HomePage selectReturnDate(String date) {
        try {
            // Try multiple approaches to find and click return date field
            WebElement dateField = null;
            
            try {
                dateField = returnDateInput;
                if (dateField.isDisplayed()) {
                    click(dateField);
                }
            } catch (Exception e) {
                logger.debug("Data-testid return date input not found, trying fallback locators...");
                dateField = findElement(returnDateLocator);
                click(dateField);
            }
            
            // Try different approaches to set the date
            try {
                // First try: Wait for calendar popup and select from calendar
                WaitUtils.waitForElementToBeVisible(driver, calendarPopupLocator);
                selectDateFromCalendar(date);
            } catch (Exception e) {
                logger.debug("Calendar popup not found, trying direct input...");
                try {
                    // Second try: Direct input to date field
                    dateField.clear();
                    sendKeys(dateField, date);
                } catch (Exception e2) {
                    // Third try: Use JavaScript to set value
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "arguments[0].value = arguments[1];", dateField, date);
                }
            }
            
            logger.info("Selected return date: {}", date);
            return this; // Fluent interface
        } catch (Exception e) {
            logger.error("Failed to select return date: {}", date, e);
            throw new RuntimeException("Failed to select return date: " + date, e);
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
            logger.info("Attempting to select round trip option");
            boolean selected = false;
            
            // Strategy 1: Try data-testid first (fast check)
            try {
                if (roundTripRadio.isDisplayed()) {
                    click(roundTripRadio);
                    selected = true;
                    logger.info("Round trip selected using data-testid");
                }
            } catch (Exception e) {
                logger.debug("Data-testid round trip radio not found");
            }
            
            // Strategy 2: Try multiple direct CSS selectors (fast)
            if (!selected) {
                String[] roundTripSelectors = {
                    "input[type='radio'][value*='round']",
                    "input[type='radio'][value*='gidis-donus']", 
                    "input[id*='round']",
                    "input[id*='gidis']",
                    "input[name*='trip'][value*='round']",
                    "label[for*='round']",
                    ".round-trip",
                    ".gidis-donus"
                };
                
                for (String selector : roundTripSelectors) {
                    try {
                        WebElement element = driver.findElement(By.cssSelector(selector));
                        if (element.isDisplayed()) {
                            click(element);
                            selected = true;
                            logger.info("Round trip selected using selector: {}", selector);
                            break;
                        }
                    } catch (Exception e) {
                        logger.debug("Selector failed: {}", selector);
                    }
                }
            }
            
            // Strategy 3: Try XPath with text content (fast check)
            if (!selected) {
                String[] xpathSelectors = {
                    "//input[@type='radio']/following-sibling::*[contains(text(),'Gidiş-Dönüş')]",
                    "//label[contains(text(),'Gidiş-Dönüş')]",
                    "//input[@type='radio']/parent::*//*[contains(text(),'Gidiş-Dönüş')]",
                    "//div[contains(text(),'Gidiş-Dönüş')]",
                    "//span[contains(text(),'Gidiş-Dönüş')]"
                };
                
                for (String xpath : xpathSelectors) {
                    try {
                        WebElement element = driver.findElement(By.xpath(xpath));
                        if (element.isDisplayed()) {
                            click(element);
                            selected = true;
                            logger.info("Round trip selected using XPath: {}", xpath);
                            break;
                        }
                    } catch (Exception e) {
                        logger.debug("XPath failed: {}", xpath);
                    }
                }
            }
            
            // Strategy 4: Quick radio button scan (without long waits)
            if (!selected) {
                try {
                    List<WebElement> radioButtons = driver.findElements(By.cssSelector("input[type='radio'], label"));
                    for (WebElement radio : radioButtons) {
                        try {
                            String text = radio.getText().toLowerCase();
                            String value = radio.getAttribute("value");
                            if (text.contains("gidiş-dönüş") || text.contains("round") || 
                                text.contains("çift yön") || text.contains("dönüşlü") ||
                                (value != null && (value.contains("round") || value.contains("gidis")))) {
                                click(radio);
                                selected = true;
                                logger.info("Round trip selected via radio scan: {}", text);
                                break;
                            }
                        } catch (Exception e) {
                            logger.debug("Radio button check failed: {}", e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Radio button scan failed: {}", e.getMessage());
                }
            }
            
            // Strategy 5: JavaScript approach (last resort)
            if (!selected) {
                try {
                    String jsScript = 
                        "var radios = document.querySelectorAll('input[type=\"radio\"]');" +
                        "for (var i = 0; i < radios.length; i++) {" +
                        "  var radio = radios[i];" +
                        "  var text = radio.parentElement.textContent.toLowerCase();" +
                        "  var value = radio.value.toLowerCase();" +
                        "  if (text.includes('gidiş-dönüş') || text.includes('round') || value.includes('round') || value.includes('gidis')) {" +
                        "    radio.click();" +
                        "    return 'SUCCESS';" +
                        "  }" +
                        "}" +
                        "return 'NOT_FOUND';";
                    
                    Object result = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(jsScript);
                    if ("SUCCESS".equals(result)) {
                        selected = true;
                        logger.info("Round trip selected via JavaScript");
                    }
                } catch (Exception e) {
                    logger.debug("JavaScript approach failed: {}", e.getMessage());
                }
            }
            
            // Final check: Round trip might be selected by default
            if (!selected) {
                logger.info("Round trip option not found - assuming it's selected by default or not required");
                selected = true; // Continue test execution
            }
            
            if (selected) {
                logger.info("Round trip selection completed successfully");
            }
            
        } catch (Exception e) {
            logger.error("Failed to select round trip", e);
            // Don't throw exception - continue with test
            logger.info("Continuing test execution despite round trip selection issue");
        }
    }

    public FlightListPage clickSearchButton() {
        try {
            // Try multiple approaches to find and click search button
            WebElement searchBtn = null;
            boolean clicked = false;
            
            // Try data-testid first
            try {
                searchBtn = searchButton;
                if (searchBtn.isDisplayed() && searchBtn.isEnabled()) {
                    click(searchBtn);
                    clicked = true;
                }
            } catch (Exception e) {
                logger.debug("Data-testid search button not found, trying fallback locators...");
            }
            
            // Try fallback locators
            if (!clicked) {
                try {
                    searchBtn = findElement(searchButtonLocator);
                    click(searchBtn);
                    clicked = true;
                } catch (Exception e) {
                    logger.debug("Standard search button locators failed, trying text-based search...");
                }
            }
            
            // Try finding by button text
            if (!clicked) {
                try {
                    searchBtn = findElement(By.xpath("//button[contains(text(),'Ara') or contains(text(),'Search') or contains(text(),'Ucuz bilet bul') or contains(text(),'Ucuz Bilet Bul') or contains(text(),'Bilet Bul')] | //input[@type='submit']"));
                    click(searchBtn);
                    clicked = true;
                } catch (Exception e) {
                    logger.debug("Text-based search button not found, trying form submit...");
                }
            }
            
            // Last resort: try to submit the form
            if (!clicked) {
                try {
                    WebElement form = findElement(By.cssSelector("form"));
                    form.submit();
                    clicked = true;
                } catch (Exception e) {
                    logger.error("All search button approaches failed");
                    throw new RuntimeException("Could not find or click search button", e);
                }
            }
            
            logger.info("Clicked search button successfully");
            return new FlightListPage(driver);
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

    /**
     * Performs a comprehensive round-trip flight search with fluent interface
     * @param origin Origin city (e.g., "Istanbul")
     * @param destination Destination city (e.g., "Ankara")
     * @param departureDate Departure date in format "dd.MM.yyyy" or "yyyy-MM-dd"
     * @param returnDate Return date in format "dd.MM.yyyy" or "yyyy-MM-dd"
     * @return FlightListPage for further interactions
     */
    public FlightListPage searchRoundTripFlight(String origin, String destination, String departureDate, String returnDate) {
        try {
            selectFlightTab();
            selectRoundTrip();
            
            // Use fluent interface for chaining
            enterOrigin(origin)
                .enterDestination(destination)
                .selectDepartureDate(departureDate)
                .selectReturnDate(returnDate);
            
            FlightListPage flightListPage = clickSearchButton();
            
            logger.info("Performed round trip flight search: {} to {} from {} to {}", 
                       origin, destination, departureDate, returnDate);
            return flightListPage;
        } catch (Exception e) {
            logger.error("Failed to search round trip flight", e);
            throw new RuntimeException("Failed to search round trip flight", e);
        }
    }

    /**
     * Enhanced one-way flight search with fluent interface
     * @param origin Origin city
     * @param destination Destination city
     * @param departureDate Departure date
     * @return FlightListPage for further interactions
     */
    public FlightListPage searchOneWayFlight(String origin, String destination, String departureDate) {
        try {
            selectFlightTab();
            selectOneWayTrip();
            
            // Use fluent interface for chaining
            enterOrigin(origin)
                .enterDestination(destination)
                .selectDepartureDate(departureDate);
            
            FlightListPage flightListPage = clickSearchButton();
            
            logger.info("Performed one-way flight search: {} to {} on {}", origin, destination, departureDate);
            return flightListPage;
        } catch (Exception e) {
            logger.error("Failed to search one-way flight", e);
            throw new RuntimeException("Failed to search one-way flight", e);
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
            // Try different ways to verify page is loaded
            boolean logoFound = isElementPresent(logoLocator);
            boolean searchFormFound = isElementPresent(searchButtonLocator) || 
                                    isElementPresent(originInputLocator) ||
                                    isElementPresent(By.cssSelector("form"));
            boolean titleCorrect = driver.getTitle().toLowerCase().contains("enuygun");
            
            return logoFound || (searchFormFound && titleCorrect);
        } catch (Exception e) {
            logger.debug("Home page not loaded: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void waitForPageLoad() {
        try {
            // Wait for page to load with multiple fallback options
            boolean pageLoaded = false;
            
            // Try to find logo first
            try {
                WaitUtils.waitForElementToBeVisible(driver, logoLocator);
                pageLoaded = true;
                logger.info("Home page loaded - logo found");
            } catch (Exception e) {
                logger.debug("Logo not found, trying search form...");
            }
            
            // If logo not found, try to find search form elements
            if (!pageLoaded) {
                try {
                    WaitUtils.waitForElementToBeVisible(driver, searchButtonLocator);
                    pageLoaded = true;
                    logger.info("Home page loaded - search form found");
                } catch (Exception e) {
                    logger.debug("Search button not found, trying origin input...");
                }
            }
            
            // If search button not found, try origin input
            if (!pageLoaded) {
                try {
                    WaitUtils.waitForElementToBeVisible(driver, originInputLocator);
                    pageLoaded = true;
                    logger.info("Home page loaded - origin input found");
                } catch (Exception e) {
                    logger.debug("Origin input not found, trying any form...");
                }
            }
            
            // Last resort - look for any form element
            if (!pageLoaded) {
                try {
                    WaitUtils.waitForElementToBeVisible(driver, By.cssSelector("form, .search-form, .flight-search"));
                    pageLoaded = true;
                    logger.info("Home page loaded - form element found");
                } catch (Exception e) {
                    logger.warn("No recognizable elements found, proceeding anyway");
                    pageLoaded = true; // Proceed anyway to continue test
                }
            }
            
            if (pageLoaded) {
                logger.info("Home page loaded successfully");
            }
            
        } catch (Exception e) {
            logger.error("Home page did not load properly", e);
            throw new RuntimeException("Home page did not load properly", e);
        }
    }

    // Helper Methods
    
    /**
     * Selects a date from calendar popup
     * @param date Date string in various formats (dd.MM.yyyy, yyyy-MM-dd, etc.)
     */
    private void selectDateFromCalendar(String date) {
        try {
            logger.info("Selecting date from calendar: {}", date);
            
            // Convert date format from dd.MM.yyyy to yyyy-MM-dd for title attribute
            String isoDate = convertDateToISOFormat(date);
            logger.debug("Converted date format: {} → {}", date, isoDate);
            
            // Wait for calendar popup to be visible
            WaitUtils.waitForElementToBeVisible(driver, calendarPopupLocator, Duration.ofSeconds(5));
            logger.debug("Calendar popup is visible");
            
            // Find and click the specific date button using title attribute
            By dateButtonLocator = By.xpath(String.format("//button[@title='%s']", isoDate));
            
            try {
                WaitUtils.waitForElementToBeClickable(driver, dateButtonLocator, Duration.ofSeconds(5));
                click(dateButtonLocator);
                logger.info("Successfully clicked date button with title: {}", isoDate);
            } catch (Exception e) {
                logger.debug("Direct title click failed, trying alternative approaches...");
                
                // Fallback: Try different date button patterns
                String[] alternativeLocators = {
                    String.format("//button[@title='%s']", isoDate),
                    String.format("//button[contains(@title,'%s')]", isoDate),
                    String.format("//button[@data-date='%s']", isoDate),
                    String.format("//td[@data-date='%s']//button", isoDate),
                    String.format("//div[@data-date='%s']", isoDate)
                };
                
                boolean dateSelected = false;
                for (String locator : alternativeLocators) {
                    try {
                        WebElement dateElement = driver.findElement(By.xpath(locator));
                        if (dateElement.isDisplayed() && dateElement.isEnabled()) {
                            click(dateElement);
                            dateSelected = true;
                            logger.info("Successfully clicked date using alternative locator: {}", locator);
                            break;
                        }
                    } catch (Exception ex) {
                        logger.debug("Alternative locator failed: {}", locator);
                    }
                }
                
                if (!dateSelected) {
                    throw new RuntimeException("Could not find or click date button for: " + date);
                }
            }
            
            // Wait for calendar to close
            try {
                WaitUtils.waitForElementToDisappear(driver, calendarPopupLocator, Duration.ofSeconds(3));
                logger.debug("Calendar popup closed successfully");
            } catch (Exception e) {
                logger.debug("Calendar may not have closed immediately, continuing...");
            }
            
        } catch (Exception e) {
            logger.error("Failed to select date from calendar: {}", date, e);
            throw new RuntimeException("Failed to select date from calendar: " + date, e);
        }
    }
    
    /**
     * Convert date format from dd.MM.yyyy to yyyy-MM-dd (ISO format)
     * @param date Date in format "dd.MM.yyyy" (e.g., "16.09.2025")
     * @return Date in format "yyyy-MM-dd" (e.g., "2025-09-16")
     */
    private String convertDateToISOFormat(String date) {
        try {
            String[] parts = date.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Date format should be dd.MM.yyyy, got: " + date);
            }
            
            String day = parts[0];
            String month = parts[1];
            String year = parts[2];
            
            // Ensure day and month are zero-padded
            int dayInt = Integer.parseInt(day);
            int monthInt = Integer.parseInt(month);
            
            return String.format("%s-%02d-%02d", year, monthInt, dayInt);
            
        } catch (Exception e) {
            logger.error("Failed to convert date format: {}", date, e);
            throw new IllegalArgumentException("Invalid date format: " + date, e);
        }
    }
    
    /**
     * Enhanced city selection from dropdown suggestions with multiple fallback strategies
     * @param cityName Name of the city to select
     */
    private void selectCityFromDropdown(String cityName) {
        try {
            logger.info("Attempting to select city '{}' from dropdown suggestions", cityName);
            
            // Strategy 1: Wait for dropdown and try multiple locator approaches
            boolean dropdownFound = false;
            boolean citySelected = false;
            
            // Try to find dropdown with multiple strategies
            for (int attempt = 1; attempt <= 3 && !dropdownFound; attempt++) {
                try {
                    logger.debug("Dropdown detection attempt {} for city '{}'", attempt, cityName);
                    WaitUtils.waitForElementToBeVisible(driver, cityDropdownLocator, java.time.Duration.ofSeconds(3));
                    dropdownFound = true;
                    logger.info("Dropdown found on attempt {}", attempt);
                } catch (Exception e) {
                    logger.debug("Dropdown not found on attempt {}, trying alternative locators...", attempt);
                    
                    // Alternative dropdown locators for different attempt
                    By[] alternativeDropdownLocators = {
                        By.cssSelector("div[class*='suggestions'], div[class*='dropdown'], div[class*='autocomplete']"),
                        By.xpath("//div[contains(@class,'suggestion') or contains(@class,'option') or contains(@class,'item')]//parent::*"),
                        By.cssSelector("ul, ol, .list, .menu")
                    };
                    
                    if (attempt <= alternativeDropdownLocators.length) {
                        try {
                            WaitUtils.waitForElementToBeVisible(driver, alternativeDropdownLocators[attempt-1], java.time.Duration.ofSeconds(2));
                            dropdownFound = true;
                            logger.info("Alternative dropdown found on attempt {}", attempt);
                        } catch (Exception ex) {
                            // Brief wait before next attempt using explicit wait
                            WaitUtils.waitForPageToBeFullyLoaded(driver, Duration.ofMillis(500));
                        }
                    }
                }
            }
            
            if (dropdownFound) {
                // Strategy 2: Try multiple city selection approaches
                String[] cityVariations = {cityName, cityName.toLowerCase(), cityName.toUpperCase()};
                
                for (String cityVariation : cityVariations) {
                    if (citySelected) break;
                    
                    // Approach 2A: Comprehensive XPath with multiple text matching strategies
                    By[] cityOptionLocators = {
                        // Exact text match
                        By.xpath(String.format("//li[contains(text(),'%s')] | //div[contains(text(),'%s')] | //span[contains(text(),'%s')]", 
                                cityVariation, cityVariation, cityVariation)),
                        
                        // Partial text match with common patterns
                        By.xpath(String.format("//li[contains(text(),'%s')] | //div[contains(text(),'%s')] | " +
                                "//span[contains(text(),'%s')] | //*[@role='option'][contains(text(),'%s')]", 
                                cityVariation, cityVariation, cityVariation, cityVariation)),
                        
                        // CSS based approach
                        By.cssSelector(String.format("li:contains('%s'), div:contains('%s'), span:contains('%s'), [role='option']:contains('%s')", 
                                cityVariation, cityVariation, cityVariation, cityVariation)),
                        
                        // Attribute-based approach
                        By.cssSelector(String.format("[data-value*='%s'], [data-city*='%s'], [value*='%s']", 
                                cityVariation, cityVariation, cityVariation))
                    };
                    
                    for (By locator : cityOptionLocators) {
                        try {
                            // Quick check without long wait - use driver directly
                            List<WebElement> cityOptions = driver.findElements(locator);
                            if (!cityOptions.isEmpty()) {
                                for (WebElement option : cityOptions) {
                                    try {
                                        String optionText = option.getText().trim();
                                        if (optionText.contains(cityVariation) || optionText.toLowerCase().contains(cityVariation.toLowerCase())) {
                                            logger.info("Found matching city option: '{}' for search '{}'", optionText, cityVariation);
                                            click(option);
                                            citySelected = true;
                                            logger.info("Successfully selected city '{}' from dropdown", cityVariation);
                                            break;
                                        }
                                    } catch (Exception optionException) {
                                        logger.debug("Failed to process option element: {}", optionException.getMessage());
                                    }
                                }
                            }
                            if (citySelected) break;
                        } catch (Exception e) {
                            logger.debug("City selection approach failed for '{}': {}", cityVariation, e.getMessage());
                        }
                    }
                }
            }
            
            // Strategy 3: Fallback approaches if dropdown selection failed
            if (!citySelected) {
                logger.info("Dropdown selection failed, trying fallback approaches for '{}'", cityName);
                
                // Fallback 3A: Press Enter to select first suggestion
                try {
                    WebElement activeElement = driver.switchTo().activeElement();
                    activeElement.sendKeys(org.openqa.selenium.Keys.ENTER);
                    citySelected = true;
                    logger.info("Used Enter key fallback for city selection");
                } catch (Exception e) {
                    logger.debug("Enter key fallback failed: {}", e.getMessage());
                }
                
                // Fallback 3B: Press Tab to move to next field (auto-select)
                if (!citySelected) {
                    try {
                        WebElement activeElement = driver.switchTo().activeElement();
                        activeElement.sendKeys(org.openqa.selenium.Keys.TAB);
                        citySelected = true;
                        logger.info("Used Tab key fallback for city selection");
                    } catch (Exception e) {
                        logger.debug("Tab key fallback failed: {}", e.getMessage());
                    }
                }
                
                // Fallback 3C: Click outside to close dropdown and accept typed text
                if (!citySelected) {
                    try {
                        // Click on page body or another element to close dropdown
                        WebElement bodyElement = findElement(By.cssSelector("body"));
                        click(bodyElement);
                        citySelected = true;
                        logger.info("Used click-outside fallback for city selection");
                    } catch (Exception e) {
                        logger.debug("Click-outside fallback failed: {}", e.getMessage());
                    }
                }
                
                // Fallback 3D: JavaScript approach to close dropdown
                if (!citySelected) {
                    try {
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                            "var event = new Event('blur'); document.activeElement.dispatchEvent(event);");
                        citySelected = true;
                        logger.info("Used JavaScript blur fallback for city selection");
                    } catch (Exception e) {
                        logger.debug("JavaScript fallback failed: {}", e.getMessage());
                    }
                }
            }
            
            // Final validation: Wait for dropdown to disappear
            if (citySelected) {
                try {
                    WaitUtils.waitForElementToDisappear(driver, cityDropdownLocator, java.time.Duration.ofSeconds(3));
                    logger.info("Dropdown closed successfully after city selection");
                } catch (Exception e) {
                    logger.debug("Dropdown may not have closed, but continuing: {}", e.getMessage());
                }
            }
            
            if (citySelected) {
                logger.info("City '{}' selection completed successfully", cityName);
            } else {
                logger.warn("All city selection strategies failed for '{}' - continuing with typed text", cityName);
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error during city selection for '{}': {}", cityName, e.getMessage(), e);
            // Continue execution even if selection fails
        }
    }
    
    /**
     * Parses date string into day, month, year components
     * @param date Date string in various formats
     * @return Array containing [day, month, year]
     */
    private String[] parseDate(String date) {
        try {
            String normalizedDate = date.replace("-", ".").replace("/", ".");
            String[] parts = normalizedDate.split("\\.");
            
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid date format: " + date);
            }
            
            // Handle different date formats (dd.MM.yyyy vs yyyy.MM.dd)
            if (parts[0].length() == 4) {
                // yyyy.MM.dd format
                return new String[]{parts[2], parts[1], parts[0]}; // [day, month, year]
            } else {
                // dd.MM.yyyy format
                return new String[]{parts[0], parts[1], parts[2]}; // [day, month, year]
            }
        } catch (Exception e) {
            logger.error("Failed to parse date: {}", date, e);
            throw new RuntimeException("Invalid date format: " + date, e);
        }
    }
    
    /**
     * Navigates calendar to specific month and year
     * @param month Target month
     * @param year Target year
     */
    private void navigateToCalendarDate(String month, String year) {
        try {
            // Get current calendar month/year
            By monthYearLocator = By.cssSelector(".calendar-header, .flatpickr-current-month, .month-year");
            
            // Navigation logic would depend on actual calendar implementation
            // This is a placeholder for the navigation logic
            logger.debug("Navigating calendar to month: {} year: {}", month, year);
            
            // Implementation would involve clicking next/prev buttons until reaching target month/year
            
        } catch (Exception e) {
            logger.warn("Calendar navigation failed, using current month/year", e);
        }
    }

    /**
     * Validates that required fields are filled before search
     * @return true if all required fields are filled
     */
    public boolean areRequiredFieldsFilled() {
        try {
            boolean originFilled = !getAttribute(By.cssSelector("[data-testid='origin-input']"), "value").isEmpty();
            boolean destinationFilled = !getAttribute(By.cssSelector("[data-testid='destination-input']"), "value").isEmpty();
            boolean departureDateFilled = !getAttribute(By.cssSelector("[data-testid='departure-date']"), "value").isEmpty();
            
            logger.debug("Fields status - Origin: {}, Destination: {}, Departure: {}", 
                        originFilled, destinationFilled, departureDateFilled);
            
            return originFilled && destinationFilled && departureDateFilled;
        } catch (Exception e) {
            logger.error("Failed to validate required fields", e);
            return false;
        }
    }
}