readme.# Enuygun QA Automation Framework

A comprehensive test automation framework for Enuygun.com featuring UI automation, API testing, load testing, and data analysis capabilities.

## 🚀 Features

- **UI Automation**: Selenium WebDriver with Page Object Model
- **API Testing**: REST Assured with JSON schema validation
- **Load Testing**: k6 performance testing scripts
- **Data Analysis**: Java-based data processing and visualization
- **Cross-Browser Testing**: Chrome and Firefox support
- **Comprehensive Reporting**: ExtentReports with screenshots
- **CI/CD Ready**: Maven profiles and TestNG configurations

## 📁 Project Structure

```
enuygun-qa-automation/
├── pom.xml                          # Maven configuration with all dependencies
├── README.md                        # This file
├── src/
│   ├── main/java/com/enuygun/qa/
│   │   ├── config/                  # Configuration management
│   │   │   ├── ConfigManager.java   # Properties-based configuration
│   │   │   └── TestConfig.java      # Test constants and configurations
│   │   ├── pages/                   # Page Object Model classes
│   │   │   ├── BasePage.java        # Base page with common functionality
│   │   │   ├── HomePage.java        # Homepage page object
│   │   │   └── SearchPage.java      # Search results page object
│   │   ├── utils/                   # Utility classes
│   │   │   ├── WebDriverFactory.java    # Cross-browser driver management
│   │   │   ├── WaitUtils.java            # Explicit wait strategies
│   │   │   ├── ScreenshotUtils.java      # Screenshot capture utilities
│   │   │   └── ReportUtils.java          # ExtentReports integration
│   │   └── models/                  # Data models
│   │       ├── ApiResponse.java     # API response model
│   │       └── TestData.java        # Test data model
│   └── test/java/com/enuygun/qa/
│       ├── base/
│       │   └── BaseTestClass.java   # Base test with setup/teardown
│       ├── ui/                      # UI test classes
│       │   ├── HomePageTests.java   # Homepage functionality tests
│       │   └── SearchTests.java     # Search functionality tests
│       ├── api/                     # API test classes
│       │   ├── FlightApiTests.java  # Flight API endpoint tests
│       │   └── HotelApiTests.java   # Hotel API endpoint tests
│       └── data/                    # Data analysis tests
│           └── DataAnalysisTests.java    # Data processing and analysis
├── load-tests/k6-scripts/
│   ├── load-test-flights.js         # Flight search load testing
│   └── performance-test.js          # Performance testing scenarios
├── test-data/
│   ├── test-config.properties       # Test configuration file
│   ├── api-schemas/                 # JSON schema files for API validation
│   └── test-datasets/               # Test data files
├── reports/                         # ExtentReports output directory
└── screenshots/                     # Failed test screenshots
```

## 🛠️ Prerequisites

### System Requirements
- **Java**: JDK 11 or higher
- **Maven**: 3.6.0 or higher
- **Chrome Browser**: Latest stable version
- **Firefox Browser**: Latest stable version (optional)
- **k6**: For load testing (optional)

### Installation Steps

1. **Install Java JDK 11+**
   ```bash
   # Check Java version
   java -version
   javac -version
   ```

2. **Install Maven**
   ```bash
   # Check Maven version
   mvn -version
   ```

3. **Install k6 (for load testing)**
   ```bash
   # macOS (using Homebrew)
   brew install k6
   
   # Windows (using Chocolatey)
   choco install k6
   
   # Linux
   sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
   echo "deb https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
   sudo apt-get update
   sudo apt-get install k6
   ```

## ⚙️ Configuration

### Test Configuration File
Edit `test-data/test-config.properties` to customize test settings:

```properties
# Browser Configuration
browser.default=chrome
browser.headless=false

# URLs
url.base=https://www.enuygun.com
url.api.base=https://api.enuygun.com

# Timeouts (in seconds)
timeout.default=10
timeout.page.load=30

# Test Data
flight.departure.city=Istanbul
flight.arrival.city=London
```

### Environment Variables
You can override configuration using system properties:
```bash
# Set browser type
-Dbrowser=firefox

# Set environment
-Denvironment=prod

# Set headless mode
-Dbrowser.headless=true
```

## 🏃‍♂️ Running Tests

### Quick Start
```bash
# Clone the repository
git clone <repository-url>
cd enuygun-qa-automation

# Install dependencies
mvn clean install

# Run smoke tests
mvn test -Dgroups="Smoke"
```

### Running Different Test Suites

#### 1. UI Tests
```bash
# Run all UI tests with Chrome (default)
mvn test -Pui-tests

# Run UI tests with Firefox
mvn test -Pui-tests -Dbrowser=firefox

# Run specific UI test class
mvn test -Dtest=HomePageTests

# Run in headless mode
mvn test -Pui-tests -Dbrowser.headless=true
```

#### 2. API Tests
```bash
# Run all API tests
mvn test -Papi-tests

# Run specific API test class
mvn test -Dtest=FlightApiTests
```

#### 3. Data Analysis Tests
```bash
# Run data analysis tests
mvn test -Dtest=DataAnalysisTests
```

#### 4. Test Groups
```bash
# Run smoke tests only
mvn test -Dgroups="Smoke"

# Run regression tests
mvn test -Dgroups="Regression"

# Run specific test categories
mvn test -Dgroups="UI,API"
```

## 🎯 Flight Search Time Filter Test Scenario

### Overview
A comprehensive end-to-end test scenario that validates flight search functionality with time filtering capabilities. This test demonstrates advanced automation techniques including dynamic element handling, time filter validation, and comprehensive result verification.

### Test Scenario Details
- **Route**: Istanbul to Ankara (round-trip)
- **Time Filter**: 10:00-17:00 (using "Öğle" button)
- **Validation**: All displayed flights must be within the specified time range
- **Technology**: Selenium WebDriver with Page Object Model

### Key Features
- ✅ **Robust City Selection**: Multiple fallback strategies for dropdown interactions
- ✅ **Smart Date Handling**: Calendar navigation with dynamic date selection
- ✅ **Time Filter Validation**: Comprehensive flight time range verification
- ✅ **Enhanced Error Handling**: Detailed logging and screenshot capture
- ✅ **Öğle Button Support**: Simplified time filtering (10:00-17:00)

### Running the Flight Search Test

#### Quick Execution
```bash
# Run the specific flight search test
mvn test -Dtest=FlightSearchBasicTest#testBasicFlightSearchWithTimeFilter

# Run using the dedicated test runner script
./run-flight-search-tests.sh

# Run with custom browser
./run-flight-search-tests.sh chrome

# Run with specific test configuration
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-flight-search.xml
```

#### Test Configuration Options
```bash
# Run with different time ranges (via ConfigManager)
mvn test -Dtest=FlightSearchBasicTest -Dflight.filter.time.start=10:00 -Dflight.filter.time.end=17:00

# Run with different routes
mvn test -Dtest=FlightSearchBasicTest -Dflight.origin=Istanbul -Dflight.destination=Ankara

# Run in headless mode for CI/CD
mvn test -Dtest=FlightSearchBasicTest -Dbrowser.headless=true
```

### Test Files Structure
```
src/
├── main/java/com/enuygun/qa/
│   └── pages/
│       ├── HomePage.java              # Enhanced with flight search functionality
│       └── FlightListPage.java        # New: Flight results and time filter handling
├── test/java/com/enuygun/qa/ui/
│   └── FlightSearchBasicTest.java     # Main test implementation
└── test/resources/
    └── testng-flight-search.xml       # TestNG configuration for flight tests

run-flight-search-tests.sh             # Dedicated test runner script
test-data/flight-search-test-data.csv  # Test data for parametric testing
FLIGHT-SEARCH-TESTS.md                 # Detailed test documentation
```

### Validation Features

#### Comprehensive Time Validation
```java
// The test validates that ALL flights are within the specified time range
validateAllFlightsInTimeRange("10:00", "17:00")

// Detailed logging for each flight validation
✅ Flight #1: 11:45 is within range 10:00-17:00
✅ Flight #2: 14:30 is within range 10:00-17:00
❌ Flight #3: 18:15 is OUTSIDE range 10:00-17:00
```

#### Enhanced Error Handling
- **Smart Element Detection**: Multiple locator strategies with fallbacks
- **Automatic Screenshots**: Captured on any failure for debugging
- **Detailed Logging**: Step-by-step execution tracking
- **Thread.sleep Elimination**: Uses only explicit waits for stability

#### Test Execution Flow
1. **🏠 Navigate to Enuygun homepage**
2. **✈️ Select flight tab and round-trip option**
3. **🏙️ Enter origin city (Istanbul) with dropdown selection**
4. **🎯 Enter destination city (Ankara) with dropdown selection**
5. **📅 Select departure and return dates**
6. **🔍 Click search and wait for results**
7. **⏰ Apply time filter using "Öğle" button (10:00-17:00)**
8. **✅ Validate ALL flights are within the specified time range**
9. **📊 Generate comprehensive test report**

### Expected Results
- **Flight List Display**: Successfully loads flight results page
- **Time Filter Application**: "Öğle" button successfully applies 10:00-17:00 filter
- **Validation Success**: All displayed flights have departure times between 10:00-17:00
- **Test Completion**: Detailed logs show comprehensive validation execution

### Troubleshooting
```bash
# If city selection fails
# Check logs for dropdown detection issues, multiple fallback strategies will be attempted

# If time filter doesn't apply
# Verify "Öğle" button visibility, test includes scroll functionality for better element access

# If validation fails
# Check flight element detection, multiple selectors are used for robust element finding
```

### Cross-Browser Testing
```bash
# Chrome (default)
mvn test -Dbrowser=chrome

# Firefox
mvn test -Dbrowser=firefox

# Headless Chrome
mvn test -Dbrowser=chrome -Dbrowser.headless=true
```

## 🚛 Load Testing with k6

### Running Load Tests
```bash
# Navigate to load test directory
cd load-tests/k6-scripts

# Run flight search load test
k6 run load-test-flights.js

# Run with custom parameters
k6 run -e BASE_URL=https://www.enuygun.com -e API_BASE_URL=https://api.enuygun.com load-test-flights.js

# Run performance test
k6 run performance-test.js

# Run with custom virtual users and duration
k6 run --vus 20 --duration 5m load-test-flights.js
```

### Load Test Configuration
Modify the test scripts to adjust:
- Virtual users (VUs)
- Test duration
- Ramp-up patterns
- Performance thresholds

## 📊 Reports and Results

### ExtentReports
- **Location**: `reports/ExtentReport_[timestamp].html`
- **Features**: 
  - Test execution status
  - Screenshots on failures
  - Test duration and timestamps
  - Browser and environment information

### Test Screenshots
- **Location**: `screenshots/`
- **Automatic capture**: On test failures
- **Manual capture**: Using `takeScreenshot()` method

### Logs
- **Application logs**: `logs/automation.log`
- **Test results**: `logs/test-results.log`

### Data Analysis Results
- **CSV data**: `test-data/flight_bookings_sample.csv`
- **Charts**: `reports/[chart_name].png`
- **Summary**: `reports/data_analysis_summary.txt`

## 🔧 Development Guidelines

### Adding New Tests

#### 1. UI Tests
```java
@Test(groups = {TestConfig.REGRESSION_TEST, TestConfig.UI_TEST}, 
      priority = TestConfig.MEDIUM_PRIORITY,
      description = "Test description")
public void testNewFeature() {
    logTestStep("Step description");
    // Test implementation
    assertAndLog(condition, "Success message", "Failure message");
}
```

#### 2. API Tests
```java
@Test(groups = {TestConfig.API_TEST})
public void testNewApiEndpoint() {
    Response response = requestSpec
        .body(requestBody)
        .when()
        .post("/new-endpoint")
        .then()
        .extract().response();
        
    assertAndLog(response.getStatusCode() == 200, 
                "API call successful", 
                "API call failed");
}
```

### Page Object Model Guidelines
- Extend `BasePage` for all page classes
- Use `@FindBy` annotations for element location
- Implement `isPageLoaded()` and `waitForPageLoad()` methods
- Use descriptive method names

### Best Practices
- **Wait Strategies**: Always use explicit waits, never `Thread.sleep()`
- **Error Handling**: Implement proper try-catch blocks with logging
- **Screenshots**: Capture screenshots on failures automatically
- **Assertions**: Use `assertAndLog()` for better reporting
- **Configuration**: Use ConfigManager for all configurable values

## 🐛 Troubleshooting

### Common Issues

#### 1. WebDriver Issues
```bash
# WebDriver not found
# Solution: WebDriverManager handles this automatically, ensure internet connection

# Browser version mismatch
# Solution: Update browser to latest version or specify compatible driver version
```

#### 2. Test Failures
```bash
# Element not found
# Check if element locators are correct and page is fully loaded

# Timeout exceptions
# Increase timeout values in test-config.properties
```

#### 3. API Test Issues
```bash
# Connection refused
# Verify API base URL and ensure API is accessible

# Authentication errors
# Check API key configuration in test-config.properties
```

### Debug Mode
```bash
# Run with debug logging
mvn test -Dtest=TestClassName -Dlog.level=DEBUG

# Run single test method
mvn test -Dtest=TestClassName#testMethodName
```

## 📈 CI/CD Integration

### Maven Profiles
- `ui-tests`: UI test execution
- `api-tests`: API test execution
- `chrome`: Chrome browser tests
- `firefox`: Firefox browser tests

### GitHub Actions Example
```yaml
name: QA Automation Tests
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
      - name: Run Smoke Tests
        run: mvn test -Dgroups="Smoke" -Dbrowser.headless=true
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Follow coding standards and best practices
4. Add tests for new functionality
5. Update documentation
6. Submit a pull request

## 📚 Additional Resources

- [Selenium Documentation](https://selenium.dev/documentation/)
- [REST Assured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/doc/)
- [k6 Documentation](https://k6.io/docs/)
- [ExtentReports Documentation](https://www.extentreports.com/docs/versions/5/java/index.html)

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Note**: This framework is designed for educational and testing purposes. Ensure you have proper permissions before running tests against any production systems.