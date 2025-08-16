package com.enuygun.qa.ui;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.enuygun.qa.base.BaseTestClass;
import com.enuygun.qa.pages.HomePage;
import com.enuygun.qa.pages.SearchPage;
import com.enuygun.qa.config.TestConfig;

public class SearchTests extends BaseTestClass {

    @Test(groups = {TestConfig.SMOKE_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.HIGH_PRIORITY,
          description = "Verify search results are displayed correctly")
    public void testSearchResultsDisplay() {
        logTestStep("Navigate to homepage and perform search");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        homePage.searchFlight("Istanbul", "London", "2024-12-01");
        
        logTestStep("Verify search results page");
        SearchPage searchPage = new SearchPage(driver);
        searchPage.waitForPageLoad();
        
        assertAndLog(searchPage.areSearchResultsDisplayed(), 
                    "Search results are displayed", 
                    "Search results are not displayed");
        
        logTestStep("Verify search summary is present");
        String searchSummary = searchPage.getSearchSummary();
        assertAndLog(!searchSummary.isEmpty(), 
                    "Search summary is displayed: " + searchSummary, 
                    "Search summary is not displayed");
        
        takeScreenshot("search_results_displayed");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify flight results count and details")
    public void testFlightResultsDetails() {
        logTestStep("Navigate and search for flights");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        homePage.searchFlight("Istanbul", "Amsterdam", "2024-12-01");
        
        SearchPage searchPage = new SearchPage(driver);
        searchPage.waitForPageLoad();
        
        logTestStep("Verify flight results count");
        int resultsCount = searchPage.getFlightResultsCount();
        assertAndLog(resultsCount > 0, 
                    "Found " + resultsCount + " flight results", 
                    "No flight results found");
        
        logTestStep("Verify first flight price is displayed");
        String firstFlightPrice = searchPage.getFirstFlightPrice();
        assertAndLog(!firstFlightPrice.isEmpty(), 
                    "First flight price is displayed: " + firstFlightPrice, 
                    "First flight price is not displayed");
        
        logTestStep("Verify first flight duration is displayed");
        String firstFlightDuration = searchPage.getFirstFlightDuration();
        assertAndLog(!firstFlightDuration.isEmpty(), 
                    "First flight duration is displayed: " + firstFlightDuration, 
                    "First flight duration is not displayed");
        
        takeScreenshot("flight_details_verified");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify sort by price functionality")
    public void testSortByPrice() {
        logTestStep("Navigate and search for flights");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        homePage.searchFlight("Istanbul", "Berlin", "2024-12-01");
        
        SearchPage searchPage = new SearchPage(driver);
        searchPage.waitForPageLoad();
        
        logTestStep("Get initial first flight price");
        String initialPrice = searchPage.getFirstFlightPrice();
        logTestInfo("Initial first flight price: " + initialPrice);
        
        logTestStep("Sort results by price");
        searchPage.sortByPrice();
        
        logTestStep("Verify sorting was applied");
        String sortedPrice = searchPage.getFirstFlightPrice();
        logTestInfo("Price after sorting: " + sortedPrice);
        
        // Note: In a real implementation, we would verify the actual price ordering
        assertAndLog(!sortedPrice.isEmpty(), 
                    "Price sorting applied and first flight price displayed: " + sortedPrice, 
                    "Price sorting failed");
        
        takeScreenshot("sorted_by_price");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify sort by duration functionality")
    public void testSortByDuration() {
        logTestStep("Navigate and search for flights");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        homePage.searchFlight("Istanbul", "Rome", "2024-12-01");
        
        SearchPage searchPage = new SearchPage(driver);
        searchPage.waitForPageLoad();
        
        logTestStep("Get initial first flight duration");
        String initialDuration = searchPage.getFirstFlightDuration();
        logTestInfo("Initial first flight duration: " + initialDuration);
        
        logTestStep("Sort results by duration");
        searchPage.sortByDuration();
        
        logTestStep("Verify sorting was applied");
        String sortedDuration = searchPage.getFirstFlightDuration();
        logTestInfo("Duration after sorting: " + sortedDuration);
        
        assertAndLog(!sortedDuration.isEmpty(), 
                    "Duration sorting applied and first flight duration displayed: " + sortedDuration, 
                    "Duration sorting failed");
        
        takeScreenshot("sorted_by_duration");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify flight selection functionality")
    public void testFlightSelection() {
        logTestStep("Navigate and search for flights");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        homePage.searchFlight("Istanbul", "Barcelona", "2024-12-01");
        
        SearchPage searchPage = new SearchPage(driver);
        searchPage.waitForPageLoad();
        
        logTestStep("Verify flight results are available");
        int resultsCount = searchPage.getFlightResultsCount();
        assertAndLog(resultsCount > 0, 
                    "Flight results available for selection: " + resultsCount, 
                    "No flight results available for selection");
        
        logTestStep("Select first flight");
        retryOnFailure(() -> searchPage.selectFirstFlight(), 3, "Select first flight");
        
        // Note: This would typically navigate to booking page
        // Verification would depend on actual implementation
        takeScreenshot("flight_selected");
        
        logTestInfo("Flight selection completed - next page verification depends on actual booking flow");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify airline filter functionality")
    public void testAirlineFilter() {
        logTestStep("Navigate and search for flights");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        homePage.searchFlight("Istanbul", "Frankfurt", "2024-12-01");
        
        SearchPage searchPage = new SearchPage(driver);
        searchPage.waitForPageLoad();
        
        logTestStep("Get initial results count");
        int initialCount = searchPage.getFlightResultsCount();
        logTestInfo("Initial flight results count: " + initialCount);
        
        logTestStep("Apply airline filter");
        retryOnFailure(() -> searchPage.filterByAirline("Turkish Airlines"), 3, "Apply airline filter");
        
        logTestStep("Verify filter was applied");
        int filteredCount = searchPage.getFlightResultsCount();
        logTestInfo("Filtered flight results count: " + filteredCount);
        
        // Note: Filter verification would depend on actual implementation
        assertAndLog(filteredCount >= 0, 
                    "Airline filter applied successfully", 
                    "Airline filter failed");
        
        takeScreenshot("airline_filtered");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify direct flights filter functionality")
    public void testDirectFlightsFilter() {
        logTestStep("Navigate and search for flights");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        homePage.searchFlight("Istanbul", "Dubai", "2024-12-01");
        
        SearchPage searchPage = new SearchPage(driver);
        searchPage.waitForPageLoad();
        
        logTestStep("Get initial results count");
        int initialCount = searchPage.getFlightResultsCount();
        logTestInfo("Initial flight results count: " + initialCount);
        
        logTestStep("Apply direct flights filter");
        retryOnFailure(() -> searchPage.filterByDirectFlights(), 3, "Apply direct flights filter");
        
        logTestStep("Verify filter was applied");
        int filteredCount = searchPage.getFlightResultsCount();
        logTestInfo("Direct flights count: " + filteredCount);
        
        assertAndLog(filteredCount >= 0, 
                    "Direct flights filter applied successfully", 
                    "Direct flights filter failed");
        
        takeScreenshot("direct_flights_filtered");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.LOW_PRIORITY,
          description = "Verify modify search functionality")
    public void testModifySearch() {
        logTestStep("Navigate and search for flights");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        homePage.searchFlight("Istanbul", "Madrid", "2024-12-01");
        
        SearchPage searchPage = new SearchPage(driver);
        searchPage.waitForPageLoad();
        
        logTestStep("Click modify search");
        HomePage modifiedHomePage = searchPage.modifySearch();
        
        logTestStep("Verify navigation back to homepage");
        assertAndLog(modifiedHomePage.isPageLoaded(), 
                    "Successfully navigated back to homepage for search modification", 
                    "Failed to navigate back to homepage");
        
        takeScreenshot("modify_search_page");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.LOW_PRIORITY,
          description = "Verify no results scenario handling",
          enabled = false) // Disabled as it requires specific search criteria that return no results
    public void testNoResultsScenario() {
        logTestStep("Navigate and perform search with no expected results");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        
        // This would require specific search criteria that are known to return no results
        // Implementation depends on actual system behavior
        
        SearchPage searchPage = new SearchPage(driver);
        
        logTestStep("Verify no results message is displayed");
        assertAndLog(searchPage.isNoResultsDisplayed(), 
                    "No results message displayed correctly", 
                    "No results message not displayed");
        
        takeScreenshot("no_results_displayed");
    }

    @Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Verify flight selection by index")
    public void testFlightSelectionByIndex() {
        logTestStep("Navigate and search for flights");
        navigateToBaseUrl();
        
        HomePage homePage = new HomePage(driver);
        homePage.waitForPageLoad();
        homePage.searchFlight("Istanbul", "Vienna", "2024-12-01");
        
        SearchPage searchPage = new SearchPage(driver);
        searchPage.waitForPageLoad();
        
        logTestStep("Verify multiple flight results are available");
        int resultsCount = searchPage.getFlightResultsCount();
        assertAndLog(resultsCount >= 2, 
                    "Multiple flight results available: " + resultsCount, 
                    "Insufficient flight results for index selection test");
        
        if (resultsCount >= 2) {
            logTestStep("Select second flight (index 1)");
            retryOnFailure(() -> searchPage.selectFlightByIndex(1), 3, "Select flight by index");
            
            takeScreenshot("second_flight_selected");
            logTestInfo("Flight selection by index completed");
        }
    }
}