#!/bin/bash

# K6 Flight Search Load Test Runner Script
# This script runs the flight search load test with configurable parameters

set -e

# Default configuration
DEFAULT_DURATION="5m"
DEFAULT_BASE_URL="https://www.enuygun.com"
DEFAULT_ENV="production"
DEFAULT_THINK_TIME_MIN="2"
DEFAULT_THINK_TIME_MAX="5"
DEFAULT_TIMEOUT="30000"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
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

# Function to show help
show_help() {
    echo "K6 Flight Search Load Test Runner"
    echo ""
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -d, --duration DURATION      Test duration (default: $DEFAULT_DURATION)"
    echo "  -u, --url URL                Base URL to test (default: $DEFAULT_BASE_URL)"
    echo "  -e, --env ENVIRONMENT        Environment (production|staging|dev, default: $DEFAULT_ENV)"
    echo "  -t, --timeout TIMEOUT        Request timeout in ms (default: $DEFAULT_TIMEOUT)"
    echo "  --think-time-min MIN         Minimum think time in seconds (default: $DEFAULT_THINK_TIME_MIN)"
    echo "  --think-time-max MAX         Maximum think time in seconds (default: $DEFAULT_THINK_TIME_MAX)"
    echo "  --html-report                Generate HTML report (default: enabled)"
    echo "  --json-report                Generate JSON report (default: enabled)"
    echo "  -v, --verbose                Verbose output"
    echo "  -h, --help                   Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                                    # Run with default settings"
    echo "  $0 -d 10m -v                        # Run for 10 minutes with verbose output"
    echo "  $0 -e staging -d 3m                 # Run on staging for 3 minutes"
    echo "  $0 --url https://test.enuygun.com   # Run against custom URL"
    echo ""
}

# Parse command line arguments
DURATION="$DEFAULT_DURATION"
BASE_URL="$DEFAULT_BASE_URL"
ENVIRONMENT="$DEFAULT_ENV"
TIMEOUT="$DEFAULT_TIMEOUT"
THINK_TIME_MIN="$DEFAULT_THINK_TIME_MIN"
THINK_TIME_MAX="$DEFAULT_THINK_TIME_MAX"
VERBOSE=false
HTML_REPORT=true
JSON_REPORT=true

while [[ $# -gt 0 ]]; do
    case $1 in
        -d|--duration)
            DURATION="$2"
            shift 2
            ;;
        -u|--url)
            BASE_URL="$2"
            shift 2
            ;;
        -e|--env)
            ENVIRONMENT="$2"
            shift 2
            ;;
        -t|--timeout)
            TIMEOUT="$2"
            shift 2
            ;;
        --think-time-min)
            THINK_TIME_MIN="$2"
            shift 2
            ;;
        --think-time-max)
            THINK_TIME_MAX="$2"
            shift 2
            ;;
        --html-report)
            HTML_REPORT=true
            shift
            ;;
        --json-report)
            JSON_REPORT=true
            shift
            ;;
        -v|--verbose)
            VERBOSE=true
            shift
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

# Validate k6 installation
if ! command -v k6 &> /dev/null; then
    print_error "k6 is not installed. Please install k6 first."
    echo "Installation instructions: https://k6.io/docs/getting-started/installation/"
    exit 1
fi

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$SCRIPT_DIR")")"
K6_SCRIPT="$SCRIPT_DIR/../k6-scripts/flight-search-load-test.js"
REPORTS_DIR="$PROJECT_ROOT/load-test-reports"

# Validate k6 script exists
if [[ ! -f "$K6_SCRIPT" ]]; then
    print_error "K6 script not found: $K6_SCRIPT"
    exit 1
fi

# Create reports directory if it doesn't exist
mkdir -p "$REPORTS_DIR"

# Generate timestamp for report files
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
REPORT_PREFIX="flight_search_load_test_${TIMESTAMP}"

print_info "Starting K6 Flight Search Load Test..."
print_info "Configuration:"
echo "  Duration: $DURATION"
echo "  Base URL: $BASE_URL"
echo "  Environment: $ENVIRONMENT"
echo "  Timeout: ${TIMEOUT}ms"
echo "  Think Time: ${THINK_TIME_MIN}s - ${THINK_TIME_MAX}s"
echo "  Reports Directory: $REPORTS_DIR"
echo ""

# Prepare k6 command
K6_CMD="k6 run"

# Add environment variables
K6_ENV_VARS=(
    "DURATION=$DURATION"
    "BASE_URL=$BASE_URL"
    "ENVIRONMENT=$ENVIRONMENT"
    "TIMEOUT=$TIMEOUT"
    "THINK_TIME_MIN=$THINK_TIME_MIN"
    "THINK_TIME_MAX=$THINK_TIME_MAX"
)

# Add verbose flag if requested
if [[ "$VERBOSE" == true ]]; then
    K6_CMD="$K6_CMD --verbose"
fi

# Build the full command
FULL_CMD=""
for env_var in "${K6_ENV_VARS[@]}"; do
    FULL_CMD="$FULL_CMD $env_var"
done
FULL_CMD="$FULL_CMD $K6_CMD $K6_SCRIPT"

print_info "Executing: $FULL_CMD"
echo ""

# Run the test
if eval "$FULL_CMD"; then
    print_success "Load test completed successfully!"
    
    # Check if reports were generated
    if [[ -f "$REPORTS_DIR/flight-search-load-test-report.html" ]]; then
        print_success "HTML report generated: $REPORTS_DIR/flight-search-load-test-report.html"
    fi
    
    if [[ -f "$REPORTS_DIR/flight-search-summary.json" ]]; then
        print_success "JSON summary generated: $REPORTS_DIR/flight-search-summary.json"
    fi
    
    echo ""
    print_info "Test Results Summary:"
    echo "- Check the HTML report for detailed metrics and graphs"
    echo "- JSON summary contains raw data for further analysis"
    echo "- Focus was on Istanbul-Ankara flight search performance"
    echo ""
    
else
    print_error "Load test failed!"
    exit 1
fi