#!/bin/bash

# Flight Search Basic Test Runner Script
# Comprehensive test execution with different configurations

echo "=========================================="
echo "ENUYGUN QA AUTOMATION - FLIGHT SEARCH TESTS"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if command was successful
check_result() {
    if [ $? -eq 0 ]; then
        print_success "$1 completed successfully"
    else
        print_error "$1 failed"
        return 1
    fi
}

# Parse command line arguments
BROWSER="chrome"
ENVIRONMENT="dev"
TEST_TYPE="smoke"
HEADLESS="false"

while [[ $# -gt 0 ]]; do
    case $1 in
        -b|--browser)
            BROWSER="$2"
            shift 2
            ;;
        -e|--environment)
            ENVIRONMENT="$2"
            shift 2
            ;;
        -t|--test-type)
            TEST_TYPE="$2"
            shift 2
            ;;
        -h|--headless)
            HEADLESS="true"
            shift
            ;;
        --help)
            echo "Usage: $0 [OPTIONS]"
            echo "Options:"
            echo "  -b, --browser       Browser to use (chrome, firefox) [default: chrome]"
            echo "  -e, --environment   Environment (dev, prod) [default: dev]"
            echo "  -t, --test-type     Test type (smoke, regression, cross-browser, all) [default: smoke]"
            echo "  -h, --headless      Run in headless mode"
            echo "  --help              Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0 -b chrome -t smoke                    # Run smoke tests with Chrome"
            echo "  $0 -b firefox -t regression -h           # Run regression tests with Firefox headless"
            echo "  $0 -t cross-browser                      # Run cross-browser tests"
            echo "  $0 -t all                                # Run all test suites"
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            exit 1
            ;;
    esac
done

print_status "Starting Flight Search Tests with configuration:"
echo "  Browser: $BROWSER"
echo "  Environment: $ENVIRONMENT"
echo "  Test Type: $TEST_TYPE"
echo "  Headless: $HEADLESS"
echo ""

# Set system properties
SYSTEM_PROPS="-Dbrowser=$BROWSER -Denvironment=$ENVIRONMENT"
if [ "$HEADLESS" = "true" ]; then
    SYSTEM_PROPS="$SYSTEM_PROPS -Dbrowser.headless=true"
fi

# Clean previous test results
print_status "Cleaning previous test results..."
mvn clean > /dev/null 2>&1
check_result "Clean"

# Run tests based on type
case $TEST_TYPE in
    "smoke")
        print_status "Running Smoke Tests..."
        mvn test -Pflight-search-tests $SYSTEM_PROPS -Dgroups="Smoke"
        check_result "Smoke Tests"
        ;;
    "regression")
        print_status "Running Regression Tests..."
        mvn test -Pflight-search-tests $SYSTEM_PROPS -Dgroups="Regression"
        check_result "Regression Tests"
        ;;
    "cross-browser")
        print_status "Running Cross-Browser Tests..."
        print_warning "This will run tests in both Chrome and Firefox"
        mvn test -Pcross-browser $SYSTEM_PROPS
        check_result "Cross-Browser Tests"
        ;;
    "all")
        print_status "Running All Test Suites..."
        
        # Smoke Tests
        print_status "1/4 - Running Smoke Tests (Chrome)..."
        mvn test -Pflight-search-tests -Dbrowser=chrome -Denvironment=$ENVIRONMENT -Dgroups="Smoke"
        check_result "Smoke Tests"
        
        # Regression Tests
        print_status "2/4 - Running Regression Tests (Chrome)..."
        mvn test -Pflight-search-tests -Dbrowser=chrome -Denvironment=$ENVIRONMENT -Dgroups="Regression"
        check_result "Regression Tests"
        
        # Cross-Browser Smoke Tests
        print_status "3/4 - Running Cross-Browser Smoke Tests..."
        mvn test -Pcross-browser -Denvironment=$ENVIRONMENT -Dgroups="Smoke"
        check_result "Cross-Browser Tests"
        
        # API Tests (additional coverage)
        print_status "4/4 - Running API Tests for complete coverage..."
        mvn test -Papi-tests -Dgroups="API"
        check_result "API Tests"
        ;;
    *)
        print_error "Unknown test type: $TEST_TYPE"
        print_status "Available test types: smoke, regression, cross-browser, all"
        exit 1
        ;;
esac

# Check if reports were generated
print_status "Checking test reports..."
if [ -d "reports" ] && [ "$(ls -A reports)" ]; then
    print_success "Test reports generated in 'reports' directory"
    echo "  ExtentReports: reports/ExtentReport_*.html"
else
    print_warning "No reports found in reports directory"
fi

if [ -d "screenshots" ] && [ "$(ls -A screenshots)" ]; then
    print_warning "Screenshots found in 'screenshots' directory (test failures detected)"
    echo "  Check screenshots: screenshots/"
else
    print_success "No screenshots found (no test failures)"
fi

# Test results summary
if [ -f "target/surefire-reports/testng-results.xml" ]; then
    TOTAL_TESTS=$(grep -o 'total="[^"]*"' target/surefire-reports/testng-results.xml | cut -d'"' -f2)
    PASSED_TESTS=$(grep -o 'passed="[^"]*"' target/surefire-reports/testng-results.xml | cut -d'"' -f2)
    FAILED_TESTS=$(grep -o 'failed="[^"]*"' target/surefire-reports/testng-results.xml | cut -d'"' -f2)
    SKIPPED_TESTS=$(grep -o 'skipped="[^"]*"' target/surefire-reports/testng-results.xml | cut -d'"' -f2)
    
    echo ""
    echo "=========================================="
    echo "TEST EXECUTION SUMMARY"
    echo "=========================================="
    echo "Total Tests:   $TOTAL_TESTS"
    echo "Passed:        $PASSED_TESTS"
    echo "Failed:        $FAILED_TESTS"
    echo "Skipped:       $SKIPPED_TESTS"
    echo "=========================================="
    
    if [ "$FAILED_TESTS" -gt 0 ]; then
        print_error "Test execution completed with failures"
        exit 1
    else
        print_success "All tests passed successfully!"
    fi
else
    print_warning "TestNG results file not found"
fi

print_status "Flight Search Test execution completed!"