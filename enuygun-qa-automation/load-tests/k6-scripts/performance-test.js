import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const responseTimeTrend = new Trend('response_time');
const requestCounter = new Counter('total_requests');

// Test configuration for performance testing
export const options = {
  scenarios: {
    // Spike test - sudden increase in load
    spike_test: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        { duration: '1m', target: 5 },   // Normal load
        { duration: '30s', target: 50 }, // Spike to 50 users
        { duration: '1m', target: 5 },   // Back to normal
      ],
      gracefulRampDown: '30s',
    },
    
    // Stress test - gradually increasing load
    stress_test: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        { duration: '2m', target: 10 },  // Ramp up to 10 users
        { duration: '3m', target: 20 },  // Ramp up to 20 users
        { duration: '3m', target: 30 },  // Ramp up to 30 users
        { duration: '2m', target: 0 },   // Ramp down
      ],
      gracefulRampDown: '30s',
    },
    
    // Soak test - sustained load over time
    soak_test: {
      executor: 'constant-vus',
      vus: 15,
      duration: '10m',
    },
  },
  
  thresholds: {
    http_req_duration: [
      'p(50)<1000',  // 50% of requests under 1s
      'p(90)<2000',  // 90% of requests under 2s
      'p(95)<3000',  // 95% of requests under 3s
      'p(99)<5000',  // 99% of requests under 5s
    ],
    http_req_failed: ['rate<0.1'],     // Error rate under 10%
    errors: ['rate<0.1'],              // Custom error rate under 10%
    total_requests: ['count>100'],     // At least 100 requests
  },
};

// Environment configuration
const config = {
  baseUrl: __ENV.BASE_URL || 'https://www.enuygun.com',
  apiBaseUrl: __ENV.API_BASE_URL || 'https://api.enuygun.com',
  thinkTime: {
    min: parseFloat(__ENV.THINK_TIME_MIN) || 1,
    max: parseFloat(__ENV.THINK_TIME_MAX) || 3,
  },
};

// Test data pools
const testDataPools = {
  cities: {
    origins: ['Istanbul', 'Ankara', 'Izmir', 'Antalya', 'Bursa'],
    destinations: ['London', 'Paris', 'Amsterdam', 'Rome', 'Barcelona'],
  },
  airports: {
    origins: ['IST', 'ESB', 'ADB', 'AYT', 'BTZ'],
    destinations: ['LHR', 'CDG', 'AMS', 'FCO', 'BCN'],
  },
  dates: [
    '2024-12-01', '2024-12-15', '2024-12-30',
    '2025-01-15', '2025-01-30', '2025-02-14',
  ],
  passengers: [1, 2, 3, 4],
  cabinClasses: ['economy', 'business', 'first'],
};

// Utility functions
function getRandomElement(array) {
  return array[Math.floor(Math.random() * array.length)];
}

function getRandomThinkTime() {
  return Math.random() * (config.thinkTime.max - config.thinkTime.min) + config.thinkTime.min;
}

function generateSearchData() {
  return {
    origin: getRandomElement(testDataPools.airports.origins),
    destination: getRandomElement(testDataPools.airports.destinations),
    departure_date: getRandomElement(testDataPools.dates),
    passenger_count: getRandomElement(testDataPools.passengers),
    cabin_class: getRandomElement(testDataPools.cabinClasses),
  };
}

// Performance test scenarios
export function setup() {
  console.log('Performance test setup - validating endpoints');
  
  // Validate homepage
  const homepageCheck = http.get(config.baseUrl, { timeout: '10s' });
  console.log(`Homepage check: ${homepageCheck.status}`);
  
  // Validate API
  const apiCheck = http.get(`${config.apiBaseUrl}/health`, { timeout: '10s' });
  console.log(`API check: ${apiCheck.status}`);
  
  return {
    homepageAvailable: homepageCheck.status === 200,
    apiAvailable: apiCheck.status === 200,
    startTime: new Date().toISOString(),
  };
}

export default function (data) {
  requestCounter.add(1);
  
  // Select test scenario based on probability
  const scenario = selectScenario();
  
  try {
    switch (scenario) {
      case 'homepage_performance':
        testHomepagePerformance();
        break;
      case 'search_flow_performance':
        testSearchFlowPerformance();
        break;
      case 'api_performance':
        if (data.apiAvailable) {
          testApiPerformance();
        } else {
          testHomepagePerformance(); // Fallback
        }
        break;
      case 'mixed_load':
        testMixedLoad();
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
    { name: 'homepage_performance', weight: 25 },
    { name: 'search_flow_performance', weight: 35 },
    { name: 'api_performance', weight: 25 },
    { name: 'mixed_load', weight: 15 },
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

function testHomepagePerformance() {
  const startTime = Date.now();
  
  const response = http.get(config.baseUrl, {
    headers: {
      'User-Agent': 'k6-performance-test/1.0',
      'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
      'Accept-Language': 'en-US,en;q=0.5',
      'Cache-Control': 'no-cache',
    },
    timeout: '30s',
  });
  
  const duration = Date.now() - startTime;
  responseTimeTrend.add(duration);
  
  const isSuccess = check(response, {
    'homepage loads successfully': (r) => r.status === 200,
    'homepage response time acceptable': (r) => r.timings.duration < 3000,
    'homepage contains expected elements': (r) => 
      r.body.includes('search') && r.body.includes('flight'),
    'homepage size reasonable': (r) => r.body.length > 1000 && r.body.length < 2000000,
  });
  
  errorRate.add(!isSuccess);
  
  if (!isSuccess) {
    console.error(`Homepage performance test failed: ${response.status}, ${duration}ms`);
  }
}

function testSearchFlowPerformance() {
  // Step 1: Load homepage
  const homepageResponse = http.get(config.baseUrl, {
    headers: { 'User-Agent': 'k6-performance-test/1.0' },
    timeout: '30s',
  });
  
  if (homepageResponse.status !== 200) {
    errorRate.add(true);
    return;
  }
  
  sleep(0.5); // Simulate form filling time
  
  // Step 2: Perform search
  const searchData = generateSearchData();
  const searchParams = new URLSearchParams({
    origin: searchData.origin,
    destination: searchData.destination,
    departure_date: searchData.departure_date,
    passenger_count: searchData.passenger_count,
  });
  
  const searchResponse = http.get(`${config.baseUrl}/search?${searchParams}`, {
    headers: { 'User-Agent': 'k6-performance-test/1.0' },
    timeout: '45s',
  });
  
  const isSuccess = check(searchResponse, {
    'search request successful': (r) => r.status === 200 || r.status === 302,
    'search response time acceptable': (r) => r.timings.duration < 8000,
  });
  
  errorRate.add(!isSuccess);
  responseTimeTrend.add(searchResponse.timings.duration);
}

function testApiPerformance() {
  const searchData = generateSearchData();
  
  const response = http.post(
    `${config.apiBaseUrl}/flights/search`,
    JSON.stringify(searchData),
    {
      headers: {
        'Content-Type': 'application/json',
        'User-Agent': 'k6-performance-test/1.0',
      },
      timeout: '30s',
    }
  );
  
  const isSuccess = check(response, {
    'API responds successfully': (r) => r.status === 200,
    'API response time acceptable': (r) => r.timings.duration < 5000,
    'API returns valid JSON': (r) => {
      try {
        const json = r.json();
        return typeof json === 'object' && json !== null;
      } catch (e) {
        return false;
      }
    },
    'API response has expected structure': (r) => {
      try {
        const json = r.json();
        return json.hasOwnProperty('success') || json.hasOwnProperty('data');
      } catch (e) {
        return false;
      }
    },
  });
  
  errorRate.add(!isSuccess);
  responseTimeTrend.add(response.timings.duration);
}

function testMixedLoad() {
  // Simulate a user journey with multiple requests
  const requests = [
    { url: config.baseUrl, name: 'Homepage' },
    { url: `${config.baseUrl}/flights`, name: 'Flights Page' },
    { url: `${config.baseUrl}/hotels`, name: 'Hotels Page' },
  ];
  
  for (const request of requests) {
    const response = http.get(request.url, {
      headers: { 'User-Agent': 'k6-performance-test/1.0' },
      timeout: '30s',
    });
    
    const isSuccess = check(response, {
      [`${request.name} loads successfully`]: (r) => r.status === 200,
      [`${request.name} response time acceptable`]: (r) => r.timings.duration < 5000,
    });
    
    errorRate.add(!isSuccess);
    responseTimeTrend.add(response.timings.duration);
    
    sleep(0.2); // Short pause between requests
  }
}

export function teardown(data) {
  const endTime = new Date().toISOString();
  console.log('Performance test completed');
  console.log(`Test started: ${data.startTime}`);
  console.log(`Test ended: ${endTime}`);
  console.log(`Homepage available: ${data.homepageAvailable}`);
  console.log(`API available: ${data.apiAvailable}`);
}