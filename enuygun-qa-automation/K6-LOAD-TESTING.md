# K6 Load Testing Framework

Bu dokümantasyon, Enuygun.com flight search functionality için oluşturulan K6 load testing framework'ünün kapsamlı açıklamasını içerir.

## 📖 Genel Bakış

K6 Load Testing Framework, Enuygun.com'un uçuş arama işlevselliğinin performansını test etmek için geliştirilmiş modern bir yük testi çözümüdür. 1 virtual user ile başlayarak configurable duration ile çalışır ve comprehensive metrics collection sağlar.

## 🏗️ Proje Yapısı

```
load-tests/
├── k6-scripts/
│   ├── flight-search-load-test.js     # Ana K6 test script'i
│   ├── load-test-flights.js           # Mevcut flight load test
│   └── performance-test.js            # Mevcut performance test
├── config/
│   └── load-test-config.json          # Test konfigürasyon dosyası
├── scripts/
│   └── run-flight-search-load-test.sh # Test execution script
└── ../load-test-reports/              # Test raporları
    ├── flight-search-load-test-report.html
    └── flight-search-summary.json
```

## 🎯 Test Kapsamı

### Ana Test Senaryoları

#### 1. Homepage Load Test (%20)
- **Hedef**: Ana sayfa yükleme performansı
- **Metrikler**: Response time, availability, page size
- **Threshold**: < 3s response time

#### 2. Flight Search Istanbul-Ankara (%60)
- **Hedef**: Ana uçuş arama senaryosu (İstanbul → Ankara)
- **Route**: IST → ESB (Primary focus)
- **Alternative Routes**: SAW → ESB, IST → ADB
- **Metrikler**: Search response time, success rate
- **Threshold**: < 10s search time

#### 3. Flight Listing Performance (%20)
- **Hedef**: Uçuş listesi sayfası performansı
- **URL Pattern**: `/ucak-bileti/istanbul-ankara`
- **Metrikler**: Listing load time, content validation
- **Threshold**: < 5s response time

## 🛠️ Teknik Özellikler

### Framework Detayları
- **K6 Version**: Latest
- **Virtual Users**: 1 (configurable)
- **Default Duration**: 5 minutes (configurable)
- **Think Time**: 2-5 seconds (configurable)
- **Target URL**: https://www.enuygun.com

### Custom Metrics
```javascript
const errorRate = new Rate('errors');
const responseTimeTrend = new Trend('response_time');
const requestCounter = new Counter('total_requests');
const flightSearchSuccessRate = new Rate('flight_search_success');
```

### Performance Thresholds
```javascript
thresholds: {
  http_req_duration: [
    'p(50)<2000',   // 50% of requests under 2s
    'p(90)<5000',   // 90% of requests under 5s  
    'p(95)<8000',   // 95% of requests under 8s
  ],
  http_req_failed: ['rate<0.1'],           // Error rate under 10%
  flight_search_success: ['rate>0.8'],    // Success rate over 80%
  total_requests: ['count>10'],            // At least 10 requests
}
```

## 🚀 Kullanım

### 1. Hızlı Başlangıç

#### Temel Test Çalıştırma
```bash
cd load-tests/scripts
./run-flight-search-load-test.sh
```

#### K6 ile Direkt Çalıştırma
```bash
cd load-tests/k6-scripts
k6 run flight-search-load-test.js
```

### 2. Konfigürasyonlu Çalıştırma

#### Custom Duration
```bash
./run-flight-search-load-test.sh -d 10m
```

#### Staging Environment
```bash
./run-flight-search-load-test.sh -e staging -d 3m
```

#### Custom URL
```bash
./run-flight-search-load-test.sh --url https://test.enuygun.com
```

#### Verbose Output
```bash
./run-flight-search-load-test.sh -v -d 2m
```

### 3. Environment Variables

```bash
# Test duration
export DURATION="10m"

# Target URL
export BASE_URL="https://staging.enuygun.com"

# Think time configuration
export THINK_TIME_MIN="1"
export THINK_TIME_MAX="3"

# Request timeout
export TIMEOUT="45000"

k6 run flight-search-load-test.js
```

## 📊 Test Verileri

### Flight Routes
```javascript
const flightSearchData = {
  origin: 'IST',        // Istanbul
  destination: 'ESB',   // Ankara
  departure_date: '2024-12-15',
  return_date: '2024-12-20',
  passenger_count: 1,
  cabin_class: 'economy',
};
```

### Alternative Routes
- **SAW → ESB**: Istanbul Sabiha Gökçen → Ankara
- **IST → ADB**: Istanbul → İzmir
- **ESB → IST**: Ankara → Istanbul (return)

### Passenger Configurations
- **Count**: 1, 2, 3, 4 passengers
- **Class**: Economy, Business, First
- **Date Range**: December 2024 - January 2025

## 📈 Raporlama

### HTML Report
- **Dosya**: `load-test-reports/flight-search-load-test-report.html`
- **İçerik**: Interactive graphs, detailed metrics, scenario breakdown
- **Güncellik**: Her test çalıştırmasında otomatik oluşur

### JSON Summary
- **Dosya**: `load-test-reports/flight-search-summary.json`
- **İçerik**: Raw metrics data, programmatic analysis için
- **Format**: Structured JSON with all K6 metrics

### Console Output
```
=== K6 Flight Search Load Test - Results Summary ===
Test started: 2024-08-17T10:30:00Z
Test ended: 2024-08-17T10:35:00Z
Homepage available: true
Target URL: https://www.enuygun.com
Focus Route: Istanbul (IST) → Ankara (ESB)
==========================================
```

## 🎛️ Konfigürasyon

### Environment Ayarları

#### Production (Default)
```json
{
  "baseUrl": "https://www.enuygun.com",
  "timeout": 30000,
  "thinkTime": { "min": 2, "max": 5 }
}
```

#### Staging
```json
{
  "baseUrl": "https://staging.enuygun.com", 
  "timeout": 45000,
  "thinkTime": { "min": 1, "max": 3 }
}
```

#### Development
```json
{
  "baseUrl": "https://dev.enuygun.com",
  "timeout": 60000,
  "thinkTime": { "min": 1, "max": 2 }
}
```

### Test Scenarios

#### Basic Load Test
```json
{
  "virtualUsers": 1,
  "duration": "5m",
  "description": "Basic flight search load test"
}
```

#### Stress Test
```json
{
  "virtualUsers": 10,
  "duration": "10m", 
  "rampUp": "2m",
  "rampDown": "1m"
}
```

#### Spike Test
```json
{
  "virtualUsers": 50,
  "duration": "2m",
  "spikeDuration": "30s"
}
```

## 🔍 Monitoring ve Alerting

### Real-time Metrics
- **Response Time Trends**: P50, P90, P95 percentiles
- **Error Rate Monitoring**: HTTP errors, custom errors
- **Success Rate Tracking**: Flight search success rate
- **Throughput Monitoring**: Requests per second

### Alert Thresholds
```json
{
  "errorRateThreshold": 0.15,
  "responseTimeThreshold": 10000,
  "availabilityThreshold": 0.95
}
```

### Key Performance Indicators (KPIs)
- ✅ **Homepage Load Time P50**: < 2s
- ✅ **Flight Search Time P90**: < 8s 
- ✅ **Error Rate**: < 10%
- ✅ **Search Success Rate**: > 80%
- ✅ **Availability**: > 99%

## 🧪 Test Execution Flow

### 1. Setup Phase
```javascript
export function setup() {
  // Validate homepage accessibility
  // Check API endpoints
  // Return test configuration
}
```

### 2. Main Test Loop
```javascript
export default function(data) {
  // Select scenario (weighted probability)
  // Execute selected test scenario
  // Record metrics
  // Simulate think time
}
```

### 3. Teardown Phase
```javascript
export function teardown(data) {
  // Print test summary
  // Log final metrics
  // Generate reports
}
```

## 🔧 Troubleshooting

### Yaygın Problemler

#### 1. K6 Not Installed
```bash
# macOS
brew install k6

# Ubuntu/Debian
sudo apt update && sudo apt install k6

# Windows
choco install k6
```

#### 2. Permission Denied (Shell Script)
```bash
chmod +x load-tests/scripts/run-flight-search-load-test.sh
```

#### 3. Network Timeouts
```bash
# Increase timeout
export TIMEOUT="60000"
./run-flight-search-load-test.sh
```

#### 4. High Error Rate
- Check target URL accessibility
- Verify network connectivity
- Review threshold configurations
- Check for rate limiting

### Debug Mode
```bash
# Enable verbose logging
./run-flight-search-load-test.sh -v

# Direct K6 debug
k6 run --http-debug="full" flight-search-load-test.js
```

## 📋 Best Practices

### Test Design
- **Realistic User Behavior**: Think time simulation
- **Gradual Load**: Avoid sudden spikes in production
- **Scenario Diversity**: Mix of different user journeys
- **Data Variety**: Multiple routes and configurations

### Performance Optimization
- **Connection Reuse**: HTTP keep-alive enabled
- **Efficient Assertions**: Minimal response validation
- **Metric Selection**: Focus on business-critical KPIs
- **Resource Management**: Clean teardown processes

### Monitoring Strategy
- **Baseline Establishment**: Regular performance benchmarks
- **Trend Analysis**: Track performance over time
- **Alert Integration**: Connect to monitoring systems
- **Regression Testing**: Automated performance validation

## 🔗 Integration

### CI/CD Pipeline
```yaml
# GitHub Actions Example
- name: Run K6 Load Test
  run: |
    cd enuygun-qa-automation/load-tests/scripts
    ./run-flight-search-load-test.sh -d 2m
```

### Jenkins Pipeline
```groovy
stage('Load Testing') {
    steps {
        script {
            sh 'cd load-tests/scripts && ./run-flight-search-load-test.sh -d 3m'
        }
    }
    post {
        always {
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'load-test-reports',
                reportFiles: 'flight-search-load-test-report.html',
                reportName: 'K6 Load Test Report'
            ])
        }
    }
}
```

## 📊 Expected Results

### Performance Benchmarks
- **Homepage**: < 2s (P50), < 5s (P90)
- **Flight Search**: < 5s (P50), < 10s (P90) 
- **Listing Page**: < 3s (P50), < 8s (P90)

### Success Criteria
- ✅ **No HTTP 5xx errors**
- ✅ **< 10% total error rate**
- ✅ **> 80% search success rate**
- ✅ **All thresholds passed**

### Typical Metrics
```
✓ homepage loads successfully..................: 100.00%
✓ flight search request successful..............: 85.71%
✓ flight search response time < 10s............: 100.00%
✓ listing response time < 5s...................: 95.00%

http_req_duration................: avg=2.1s  p(50)=1.8s p(90)=4.2s p(95)=6.1s
http_req_failed..................: 5.26%
flight_search_success............: 85.71%
total_requests...................: 45
```

## 🔄 Continuous Improvement

### Metric Collection
- Historical performance data
- Regression trend analysis
- Capacity planning insights
- User experience correlation

### Test Evolution
- Add new user scenarios
- Increase test complexity
- Enhanced validation logic
- Better error simulation

### Reporting Enhancement
- Custom dashboard integration
- Real-time alerting
- Performance trend visualization
- Business impact correlation

## 📝 Changelog

### v1.0.0 (2024-08-17)
- ✅ Initial K6 load testing framework
- ✅ Flight search focused scenarios
- ✅ Istanbul-Ankara route optimization
- ✅ HTML/JSON report generation
- ✅ Configurable test parameters
- ✅ Shell script automation
- ✅ Comprehensive documentation

### Future Enhancements
- [ ] Multiple concurrent routes testing
- [ ] API endpoint load testing integration
- [ ] Mobile app performance testing
- [ ] Database performance correlation
- [ ] Real user monitoring integration

---

**Framework Version**: 1.0.0  
**Target Application**: Enuygun.com Flight Search  
**Primary Route**: Istanbul (IST) → Ankara (ESB)  
**Last Updated**: August 17, 2024  
**Maintainer**: QA Team