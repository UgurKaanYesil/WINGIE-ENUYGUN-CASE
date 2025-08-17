# Flight Search Basic Test Suite

## 🎯 Test Scenario Overview

**Test Case**: Basic Flight Search and Time Filter  
**Priority**: High  
**Test Type**: UI Automation with Cross-Browser Support

### Test Description
This test suite validates the core flight search functionality on Enuygun.com with departure time filtering capabilities.

#### Test Steps:
1. Navigate to www.enuygun.com
2. Search for Istanbul-Ankara round-trip flights
3. Validate flight list is properly displayed
4. Apply departure time filter (10:00-18:00)
5. Validate all flights have departure times within specified range
6. Validate search results match selected route

## 🛠️ Technical Implementation

### Architecture & Design Patterns
- **Strict Page Object Model (POM)** implementation
- **Fluent Interface** for method chaining
- **Page Factory** pattern with @FindBy annotations
- **Inheritance hierarchy** with BasePage class
- **Encapsulation** of page-specific logic
- **Abstraction** through interface contracts

### Key Features
- ✅ **NO Thread.sleep** - Only explicit waits using WaitUtils
- ✅ **Automatic screenshot capture** on test failures
- ✅ **ExtentReports integration** with detailed logging
- ✅ **Cross-browser testing** (Chrome & Firefox)
- ✅ **Parametric testing** with TestNG DataProvider
- ✅ **Robust error handling** with meaningful messages
- ✅ **Comprehensive logging** at all levels

### Class Structure

#### Pages Package (`src/main/java/com/enuygun/qa/pages/`)
- **BasePage.java** - Abstract base class with common functionality
- **HomePage.java** - Enhanced with fluent interface and robust date/city selection
- **FlightListPage.java** - Complete time filter and validation functionality

#### Tests Package (`src/test/java/com/enuygun/qa/ui/`)
- **FlightSearchBasicTest.java** - Comprehensive test suite with multiple scenarios

#### Test Data (`test-data/`)
- **flight-search-test-data.csv** - Parametric test data with multiple city combinations
- **test-config.properties** - Environment and browser configurations

## 🚀 Running the Tests

### Quick Start
```bash
# Run smoke tests (default Chrome)
./run-flight-search-tests.sh

# Run with specific browser
./run-flight-search-tests.sh -b firefox -t smoke

# Run regression tests in headless mode
./run-flight-search-tests.sh -b chrome -t regression -h

# Run cross-browser tests
./run-flight-search-tests.sh -t cross-browser

# Run all test suites
./run-flight-search-tests.sh -t all
```

### Maven Commands
```bash
# Smoke tests only
mvn test -Pflight-search-tests -Dgroups="Smoke"

# Regression tests with Firefox
mvn test -Pflight-search-tests -Dbrowser=firefox -Dgroups="Regression"

# Cross-browser parallel execution
mvn test -Pcross-browser

# Headless execution
mvn test -Pflight-search-tests -Dbrowser.headless=true
```

### Test Execution Options

#### Available Test Types:
- **smoke** - Critical path tests (basic functionality)
- **regression** - Full test coverage with edge cases
- **cross-browser** - Tests executed on both Chrome and Firefox
- **all** - Complete test suite execution

#### Browser Support:
- **Chrome** (default) - Latest stable version
- **Firefox** - Latest stable version
- **Headless mode** - For CI/CD environments

#### Test Categories:
- **@Test(groups = "Smoke")** - High priority, quick execution
- **@Test(groups = "Regression")** - Comprehensive coverage
- **@Test(groups = "UI")** - User interface specific tests

## 📊 Test Coverage

### Test Scenarios Implemented:

1. **testBasicFlightSearchWithTimeFilter** (Smoke)
   - Core functionality validation
   - Time filter application and validation
   - Route matching verification

2. **testFlightSearchWithVariousTimeRanges** (Regression)
   - Multiple time range combinations
   - Filter effectiveness validation
   - Flight count validation

3. **testClearFiltersFunction** (Regression)
   - Filter reset functionality
   - Result restoration validation

4. **testNoFlightsInTimeRange** (Regression)
   - Edge case: narrow time ranges
   - No results scenario handling

5. **testInputValidation** (Regression)
   - Required fields validation
   - Input data validation

6. **testFlightSearchPerformance** (Load - Optional)
   - Search response time validation
   - Performance threshold checking

### Validation Points:
- ✅ Flight list proper display
- ✅ Flight times within specified range (10:00-18:00)
- ✅ Search results match selected route (Istanbul-Ankara)
- ✅ Filter application effectiveness
- ✅ UI element interactions
- ✅ Error handling and recovery

## 🧪 Test Data Management

### Parametric Testing
```java
@DataProvider(name = "flightSearchData")
public Object[][] getFlightSearchData() {
    return new Object[][] {
        {"Istanbul", "Ankara", "30.12.2024", "06.01.2025", "10:00", "18:00"},
        {"İstanbul", "Ankara", "15.01.2025", "22.01.2025", "09:00", "17:00"},
        // Additional combinations...
    };
}
```

### CSV Data Source
External test data file supports:
- Multiple city combinations (Turkish/English names)
- Various date ranges
- Different time filter ranges
- Test categorization

## 📈 Reporting & Screenshots

### ExtentReports Integration
- **Detailed HTML reports** with step-by-step execution
- **Pass/Fail status** with timestamps
- **Test categorization** and grouping
- **Browser and environment information**
- **Performance metrics** and execution time

### Screenshot Management
- **Automatic capture** on test failures
- **Manual capture** for verification points
- **Organized storage** with timestamp and test name
- **Report integration** for visual debugging

### Log Management
- **Structured logging** with SLF4J and Logback
- **Debug level** information for troubleshooting
- **Test execution** flow tracking
- **Error details** with stack traces

## 🔧 Troubleshooting

### Common Issues & Solutions

#### Element Not Found
```java
// Robust locator strategy with fallbacks
@FindBy(css = "[data-testid='element']")
private WebElement primaryElement;

private final By fallbackLocator = By.cssSelector(".fallback-class, .alternative");
```

#### Timing Issues
```java
// Only explicit waits - NO Thread.sleep
WaitUtils.waitForElementToBeClickable(driver, locator);
WaitUtils.waitForElementToDisappear(driver, loadingLocator);
```

#### Browser Compatibility
```java
// Cross-browser support with WebDriverManager
WebDriverFactory.createDriver(BrowserType.CHROME);
WebDriverFactory.createDriver(BrowserType.FIREFOX);
```

### Debug Mode
```bash
# Enable debug logging
mvn test -Pflight-search-tests -Dlog.level=DEBUG

# Single test execution
mvn test -Dtest=FlightSearchBasicTest#testBasicFlightSearchWithTimeFilter
```

## 📋 Requirements Compliance

### ✅ MANDATORY Requirements Met:

#### Page Object Model (POM)
- **Strict implementation** with BasePage inheritance
- **Page Factory pattern** with @FindBy annotations
- **Encapsulation** of page-specific logic
- **Fluent interface** for method chaining

#### Cross-Browser Testing
- **Chrome and Firefox** support verified
- **WebDriverManager** for automatic driver management
- **Parallel execution** capability
- **Browser-specific configurations**

#### Screenshot Capture
- **Automatic** on test failures
- **Manual capture** for verification points
- **ExtentReports integration**
- **Organized file management**

#### Test Reporting System
- **ExtentReports** with comprehensive HTML output
- **Step-by-step execution** logging
- **Pass/Fail status** with details
- **Browser and environment** information

#### Proper Wait Strategies
- **ABSOLUTELY NO Thread.sleep** usage
- **Only explicit waits** with WaitUtils
- **Fluent wait** for complex conditions
- **Element state validation** before interaction

#### Technical Implementation
- **TestNG annotations** with proper configuration
- **Data providers** for parametric testing
- **External test data** file support
- **Robust locator strategy** (CSS preferred, XPath fallback)
- **Page Factory pattern** throughout
- **Fluent interface design** for better readability
- **Comprehensive error handling** with meaningful messages
- **OOP principles** strictly followed
- **Maintainable and reusable** code structure

## 🎯 Success Criteria

### Test Execution Success Indicators:
1. **All tests pass** in smoke test suite
2. **Cross-browser compatibility** verified
3. **Time filter validation** works correctly
4. **No Thread.sleep** usage detected
5. **Screenshots captured** only on failures
6. **ExtentReports generated** with full details
7. **Performance within** acceptable limits
8. **Error handling** graceful and informative

### Quality Metrics:
- **Test execution time** < 5 minutes for smoke suite
- **Cross-browser success rate** > 95%
- **Screenshot capture rate** on failures = 100%
- **Report generation** success = 100%
- **Code coverage** of critical user journeys = 100%

---

**Implementation Status**: ✅ Complete  
**Last Updated**: December 2024  
**Framework Version**: 1.0.0  
**Compliance**: All mandatory requirements satisfied