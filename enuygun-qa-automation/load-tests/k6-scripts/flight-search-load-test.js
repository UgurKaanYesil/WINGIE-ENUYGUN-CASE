import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';

// Custom metrics
const errorRate = new Rate('errors');
const responseTimeTrend = new Trend('response_time');
const requestCounter = new Counter('total_requests');
const flightSearchSuccessRate = new Rate('flight_search_success');

// Test configuration
export const options = {
  scenarios: {
    // Basic load test with 1 virtual user as requested
    basic_load_test: {
      executor: 'constant-vus',
      vus: 1, // 1 virtual user as specified
      duration: __ENV.DURATION || '5m', // Configurable duration
    },
  },
  
  thresholds: {
    http_req_duration: [
      'p(50)<2000',   // 50% of requests under 2s
      'p(90)<5000',   // 90% of requests under 5s
      'p(95)<8000',   // 95% of requests under 8s
    ],
    http_req_failed: ['rate<0.1'],           // Error rate under 10%
    errors: ['rate<0.1'],                    // Custom error rate under 10%
    flight_search_success: ['rate>0.8'],    // Flight search success rate over 80%
    total_requests: ['count>10'],            // At least 10 requests
  },
};

// Environment configuration
const config = {
  baseUrl: __ENV.BASE_URL || 'https://www.enuygun.com',
  thinkTime: {
    min: parseFloat(__ENV.THINK_TIME_MIN) || 2,
    max: parseFloat(__ENV.THINK_TIME_MAX) || 5,
  },
  timeout: parseInt(__ENV.TIMEOUT) || 30000, // 30 seconds default
};

// Test data for Istanbul-Ankara flight search
const flightSearchData = {
  origin: 'IST', // Istanbul
  destination: 'ESB', // Ankara
  departure_date: '2024-12-15',
  return_date: '2024-12-20',
  passenger_count: 1,
  cabin_class: 'economy',
};

// Alternative test data for variety
const alternativeFlightData = [
  {
    origin: 'SAW', // Istanbul Sabiha Gokcen
    destination: 'ESB', // Ankara
    departure_date: '2024-12-20',
    passenger_count: 2,
  },
  {
    origin: 'IST', // Istanbul
    destination: 'ADB', // Izmir
    departure_date: '2024-12-25',
    passenger_count: 1,
  },
];

// Utility functions
function getRandomElement(array) {
  return array[Math.floor(Math.random() * array.length)];
}

function getRandomThinkTime() {
  return Math.random() * (config.thinkTime.max - config.thinkTime.min) + config.thinkTime.min;
}

function generateFlightSearchPayload() {
  // 70% chance for main Istanbul-Ankara route, 30% for alternatives
  if (Math.random() < 0.7) {
    return flightSearchData;
  } else {
    return getRandomElement(alternativeFlightData);
  }
}

// Performance test scenarios
export function setup() {
  console.log('K6 Flight Search Load Test - Setup Phase');
  console.log(`Target URL: ${config.baseUrl}`);
  console.log(`Test Duration: ${__ENV.DURATION || '5m'}`);
  console.log(`Virtual Users: 1`);
  
  // Validate homepage accessibility
  const homepageCheck = http.get(config.baseUrl, { 
    timeout: `${config.timeout}ms`,
    headers: {
      'User-Agent': 'k6-load-test/1.0 (Enuygun Flight Search Performance Test)',
    }
  });
  
  console.log(`Homepage check: ${homepageCheck.status}`);
  
  return {
    homepageAvailable: homepageCheck.status === 200,
    startTime: new Date().toISOString(),
    testConfig: config,
  };
}

export default function (data) {
  requestCounter.add(1);
  
  // Select test scenario based on probability
  const scenario = selectScenario();
  
  try {
    switch (scenario) {
      case 'homepage_load':
        testHomepageLoad();
        break;
      case 'flight_search_istanbul_ankara':
        testFlightSearchIstanbulAnkara();
        break;
      case 'flight_listing_performance':
        testFlightListingPerformance();
        break;
    }
  } catch (error) {
    console.error(`Test scenario ${scenario} failed: ${error}`);
    errorRate.add(true);
  }
  
  // Simulate user think time
  sleep(getRandomThinkTime());
}

function selectScenario() {
  const scenarios = [
    { name: 'homepage_load', weight: 20 },
    { name: 'flight_search_istanbul_ankara', weight: 60 }, // Main focus
    { name: 'flight_listing_performance', weight: 20 },
  ];
  
  const random = Math.random() * 100;
  let cumulativeWeight = 0;
  
  for (const scenario of scenarios) {
    cumulativeWeight += scenario.weight;
    if (random <= cumulativeWeight) {
      return scenario.name;
    }
  }
  
  return scenarios[0].name;
}

function testHomepageLoad() {
  const startTime = Date.now();
  
  const response = http.get(config.baseUrl, {
    headers: {
      'User-Agent': 'k6-load-test/1.0 (Homepage Load Test)',
      'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
      'Accept-Language': 'tr-TR,tr;q=0.9,en;q=0.8',
      'Cache-Control': 'no-cache',
    },
    timeout: `${config.timeout}ms`,
  });
  
  const duration = Date.now() - startTime;
  responseTimeTrend.add(duration);
  
  const isSuccess = check(response, {
    'homepage loads successfully': (r) => r.status === 200,
    'homepage response time < 3s': (r) => r.timings.duration < 3000,
    'homepage contains search elements': (r) => 
      r.body.includes('search') || r.body.includes('arama') || r.body.includes('flight') || r.body.includes('uçuş'),
    'homepage size reasonable': (r) => r.body.length > 1000 && r.body.length < 5000000,
  });
  
  errorRate.add(!isSuccess);
  
  if (!isSuccess) {
    console.error(`Homepage load failed: Status ${response.status}, Duration: ${duration}ms`);
  }
}

function testFlightSearchIstanbulAnkara() {
  const searchData = generateFlightSearchPayload();
  
  // Step 1: Load homepage
  const homepageResponse = http.get(config.baseUrl, {
    headers: {
      'User-Agent': 'k6-load-test/1.0 (Flight Search Test)',
      'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
    },
    timeout: `${config.timeout}ms`,
  });
  
  if (homepageResponse.status !== 200) {
    errorRate.add(true);
    flightSearchSuccessRate.add(false);
    return;
  }
  
  sleep(1); // Simulate form filling time
  
  // Step 2: Perform flight search (simulate search request)
  // Note: This is a simplified version - actual implementation would need to match site's search mechanism
  const searchParams = [
    `origin=${searchData.origin}`,
    `destination=${searchData.destination}`,
    `departure_date=${searchData.departure_date}`,
    `passenger_count=${searchData.passenger_count || 1}`,
    `trip_type=one_way`
  ].join('&');
  
  const searchUrl = `${config.baseUrl}/ucak-bileti/arama?${searchParams}`;
  
  const searchResponse = http.get(searchUrl, {
    headers: {
      'User-Agent': 'k6-load-test/1.0 (Flight Search)',
      'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
      'Referer': config.baseUrl,
    },
    timeout: `${config.timeout * 2}ms`, // Double timeout for search
  });
  
  const isSuccess = check(searchResponse, {
    'flight search request successful': (r) => r.status === 200 || r.status === 302 || r.status === 301,
    'flight search response time < 10s': (r) => r.timings.duration < 10000,
    'search response contains results or loading': (r) => 
      r.body.includes('flight') || 
      r.body.includes('uçuş') || 
      r.body.includes('result') || 
      r.body.includes('sonuç') ||
      r.body.includes('loading') ||
      r.body.includes('yükleniyor'),
  });
  
  errorRate.add(!isSuccess);
  flightSearchSuccessRate.add(isSuccess);
  responseTimeTrend.add(searchResponse.timings.duration);
  
  if (!isSuccess) {
    console.error(`Flight search failed: ${searchData.origin}-${searchData.destination}, Status: ${searchResponse.status}, Duration: ${searchResponse.timings.duration}ms`);
  } else {
    console.log(`Flight search successful: ${searchData.origin}-${searchData.destination}, Duration: ${searchResponse.timings.duration}ms`);
  }
}

function testFlightListingPerformance() {
  // Simulate accessing a flight listing page with pre-searched results
  const listingUrl = `${config.baseUrl}/ucak-bileti/istanbul-ankara`;
  
  const response = http.get(listingUrl, {
    headers: {
      'User-Agent': 'k6-load-test/1.0 (Flight Listing Performance)',
      'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
      'Accept-Language': 'tr-TR,tr;q=0.9,en;q=0.8',
    },
    timeout: `${config.timeout}ms`,
  });
  
  const isSuccess = check(response, {
    'flight listing loads': (r) => r.status === 200 || r.status === 302,
    'listing response time < 5s': (r) => r.timings.duration < 5000,
    'listing contains flight data or search form': (r) => 
      r.body.includes('flight') || 
      r.body.includes('uçuş') || 
      r.body.includes('price') || 
      r.body.includes('fiyat') ||
      r.body.includes('search') ||
      r.body.includes('arama'),
  });
  
  errorRate.add(!isSuccess);
  responseTimeTrend.add(response.timings.duration);
  
  if (!isSuccess) {
    console.error(`Flight listing performance test failed: Status ${response.status}, Duration: ${response.timings.duration}ms`);
  }
}

export function teardown(data) {
  const endTime = new Date().toISOString();
  console.log('\n=== K6 Flight Search Load Test - Results Summary ===');
  console.log(`Test started: ${data.startTime}`);
  console.log(`Test ended: ${endTime}`);
  console.log(`Homepage available: ${data.homepageAvailable}`);
  console.log(`Target URL: ${data.testConfig.baseUrl}`);
  console.log(`Focus Route: Istanbul (IST) → Ankara (ESB)`);
  console.log('==========================================\n');
}

// HTML Report Generation
export function handleSummary(data) {
  return {
    'flight-search-load-test-report.html': htmlReport(data),
    'flight-search-summary.json': JSON.stringify(data, null, 2),
  };
}