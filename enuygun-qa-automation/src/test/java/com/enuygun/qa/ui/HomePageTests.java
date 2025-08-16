package com.enuygun.qa.ui;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.enuygun.qa.base.BaseTestClass;
import com.enuygun.qa.pages.HomePage;
import com.enuygun.qa.pages.SearchPage;
import com.enuygun.qa.config.TestConfig;

public class HomePageTests extends BaseTestClass {

    @Test(groups = {TestConfig.SMOKE_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.HIGH_PRIORITY,
          description = "Verify that the Enuygun homepage loads successfully and displays all main elements")
    public void testHomePageLoad() {
        logTestStep("Navigate to Enuygun homepage");
        navigateToBaseUrl();
        
        logTestStep("Initialize HomePage object");
        HomePage homePage = new HomePage(driver);
        
        logTestStep("Wait for page to load completely");
        homePage.waitForPageLoad();
        
        logTestStep("Verify page is loaded");
        assertAndLog(homePage.isPageLoaded(), 
                    "Homepage loaded successfully", 
                    "Homepage failed to load properly");
        
        logTestStep("Verify logo is displayed");
        assertAndLog(homePage.isLogoDisplayed(), 
                    "Enuygun logo is displayed", 
                    "Enuygun logo is not displayed");
        
        logTestStep("Verify page title contains expected text");
        String pageTitle = driver.getTitle();
        assertAndLog(pageTitle.toLowerCase().contains("enuygun"), 
                    "Page title contains 'enuygun': " + pageTitle, 
                    "Page title does not contain 'enuygun': " + pageTitle);
        
        takeScreenshot("homepage_loaded");
    }

    @Test(groups = {TestConfig.SMOKE_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.HIGH_PRIORITY,
          description = "Verify flight tab selection functionality")
    public void testFlightTabSelection() {
        logTestStep("Navigate to homepage");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        
        logTestStep("Select flight tab");
        homePage.selectFlightTab();
        
        logTestStep("Verify flight tab is active");
        assertAndLog(homePage.isFlightTabActive(), 
                    "Flight tab is active", 
                    "Flight tab is not active");
        
        takeScreenshot("flight_tab_selected");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify one-way flight search functionality")
    public void testOneWayFlightSearch() {
        logTestStep("Navigate to homepage");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        
        logTestStep("Perform one-way flight search");
        String origin = "Istanbul";
        String destination = "London";
        String departureDate = "2024-12-01";
        
        homePage.searchFlight(origin, destination, departureDate);
        
        logTestStep("Verify search results page is displayed");
        SearchPage searchPage = new SearchPage(driver);
        searchPage.waitForPageLoad();
        
        assertAndLog(searchPage.isPageLoaded(), 
                    "Search results page loaded successfully", 
                    "Search results page failed to load");
        
        logTestStep("Verify search results are displayed");
        assertAndLog(searchPage.areSearchResultsDisplayed(), 
                    "Search results are displayed", 
                    "Search results are not displayed");
        
        logTestStep("Verify flight results count");
        int resultsCount = searchPage.getFlightResultsCount();
        assertAndLog(resultsCount > 0, 
                    "Found " + resultsCount + " flight results", 
                    "No flight results found");
        
        takeScreenshot("flight_search_results");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify round-trip flight search functionality")
    public void testRoundTripFlightSearch() {
        logTestStep("Navigate to homepage");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        
        logTestStep("Perform round-trip flight search");
        String origin = "Istanbul";
        String destination = "Paris";
        String departureDate = "2024-12-01";
        String returnDate = "2024-12-08";
        
        homePage.searchRoundTripFlight(origin, destination, departureDate, returnDate);
        
        logTestStep("Verify search results page is displayed");
        SearchPage searchPage = new SearchPage(driver);
        searchPage.waitForPageLoad();
        
        assertAndLog(searchPage.isPageLoaded(), 
                    "Search results page loaded successfully", 
                    "Search results page failed to load");
        
        logTestStep("Verify search results are displayed");
        assertAndLog(searchPage.areSearchResultsDisplayed(), 
                    "Search results are displayed", 
                    "Search results are not displayed");
        
        takeScreenshot("roundtrip_search_results");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify hotel tab functionality")
    public void testHotelTabSelection() {
        logTestStep("Navigate to homepage");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        
        logTestStep("Select hotel tab");
        homePage.selectHotelTab();
        
        // Note: This test would need to be expanded based on actual hotel search implementation
        takeScreenshot("hotel_tab_selected");
        
        logTestInfo("Hotel tab functionality test completed - would need actual hotel search implementation for full verification");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify bus tab functionality")
    public void testBusTabSelection() {
        logTestStep("Navigate to homepage");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        
        logTestStep("Select bus tab");
        homePage.selectBusTab();
        
        // Note: This test would need to be expanded based on actual bus search implementation
        takeScreenshot("bus_tab_selected");
        
        logTestInfo("Bus tab functionality test completed - would need actual bus search implementation for full verification");
    }

    @Test(groups = {TestConfig.SMOKE_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.LOW_PRIORITY,
          description = "Verify page navigation functionality")
    public void testPageNavigation() {
        logTestStep("Navigate to homepage");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        
        logTestStep("Get current URL");
        String currentUrl = homePage.getCurrentUrl();
        logTestInfo("Current URL: " + currentUrl);
        
        logTestStep("Get page title");
        String pageTitle = homePage.getPageTitle();
        logTestInfo("Page title: " + pageTitle);
        
        logTestStep("Refresh page");
        homePage.refreshPage();
        homePage.waitForPageLoad();
        
        assertAndLog(homePage.isPageLoaded(), 
                    "Page refreshed and loaded successfully", 
                    "Page failed to load after refresh");
        
        takeScreenshot("page_after_refresh");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.LOW_PRIORITY,
          description = "Verify error handling for invalid search input",
          enabled = false) // Disabled as it depends on actual validation implementation
    public void testInvalidSearchInput() {
        logTestStep("Navigate to homepage");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        
        logTestStep("Enter invalid search data");
        // This would test entering invalid data and verifying error messages
        // Implementation depends on actual validation behavior
        
        logTestInfo("Invalid search input test - implementation depends on actual validation behavior");
    }
}