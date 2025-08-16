import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const responseTimeTrend = new Trend('response_time');

// Test configuration
export const options = {
  stages: [
    { duration: '2m', target: 5 },   // Ramp up to 5 users over 2 minutes
    { duration: '5m', target: 10 },  // Stay at 10 users for 5 minutes
    { duration: '2m', target: 15 },  // Ramp up to 15 users over 2 minutes
    { duration: '5m', target: 15 },  // Stay at 15 users for 5 minutes
    { duration: '2m', target: 0 },   // Ramp down to 0 users over 2 minutes
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95% of requests must be below 2s
    http_req_failed: ['rate<0.05'],    // Error rate must be below 5%
    errors: ['rate<0.05'],             // Custom error rate must be below 5%
  },
};

// Base URL - should match your environment
const BASE_URL = __ENV.BASE_URL || 'https://www.enuygun.com';
const API_BASE_URL = __ENV.API_BASE_URL || 'https://api.enuygun.com';

// Test data
const testData = {
  origins: ['IST', 'ESB', 'ADB', 'AYT', 'TZX'],
  destinations: ['LHR', 'CDG', 'AMS', 'FCO', 'MAD', 'BCN', 'MUC', 'ZUR'],
  dates: ['2024-12-01', '2024-12-15', '2024-12-30', '2025-01-15'],
};

// Helper function to get random element from array
function getRandomElement(arr) {
  return arr[Math.floor(Math.random() * arr.length)];
}

// Helper function to generate flight search payload
function generateFlightSearchPayload() {
  return {
    origin: getRandomElement(testData.origins),
    destination: getRandomElement(testData.destinations),
    departure_date: getRandomElement(testData.dates),
    passenger_count: Math.floor(Math.random() * 4) + 1,
    cabin_class: 'economy',
  };
}

export function setup() {
  console.log('Load test setup - checking API availability');
  
  // Check if API is available
  const healthCheck = http.get(`${API_BASE_URL}/health`, {
    timeout: '10s',
  });
  
  if (healthCheck.status !== 200) {
    console.warn(`API health check failed with status: ${healthCheck.status}`);
  }
  
  return { apiAvailable: healthCheck.status === 200 };
}

export default function (data) {
  // Test scenario weights
  const scenarios = [
    { name: 'homepage_load', weight: 30 },
    { name: 'flight_search_ui', weight: 40 },
    { name: 'flight_search_api', weight: 30 },
  ];
  
  const randomScenario = Math.random() * 100;
  let cumulativeWeight = 0;
  let selectedScenario = scenarios[0];
  
  for (const scenario of scenarios) {
    cumulativeWeight += scenario.weight;
    if (randomScenario <= cumulativeWeight) {
      selectedScenario = scenario;
      break;
    }
  }
  
  switch (selectedScenario.name) {
    case 'homepage_load':
      testHomepageLoad();
      break;
    case 'flight_search_ui':
      testFlightSearchUI();
      break;
    case 'flight_search_api':
      if (data.apiAvailable) {
        testFlightSearchAPI();
      } else {
        testHomepageLoad(); // Fallback
      }
      break;
  }
  
  // Random sleep between 1-3 seconds to simulate user think time
  sleep(Math.random() * 2 + 1);
}

function testHomepageLoad() {
  const response = http.get(BASE_URL, {
    headers: {
      'User-Agent': 'k6-load-test/1.0',
    },
    timeout: '30s',
  });
  
  const isSuccess = check(response, {
    'homepage status is 200': (r) => r.status === 200,
    'homepage response time < 3s': (r) => r.timings.duration < 3000,
    'homepage contains title': (r) => r.body.includes('enuygun') || r.body.includes('Enuygun'),
  });
  
  errorRate.add(!isSuccess);
  responseTimeTrend.add(response.timings.duration);
  
  if (!isSuccess) {
    console.error(`Homepage load failed: Status ${response.status}, Duration: ${response.timings.duration}ms`);
  }
}

function testFlightSearchUI() {
  // First load homepage
  const homepageResponse = http.get(BASE_URL, {
    headers: {
      'User-Agent': 'k6-load-test/1.0',
    },
    timeout: '30s',
  });
  
  if (homepageResponse.status !== 200) {
    errorRate.add(true);
    return;
  }
  
  sleep(1); // Simulate user interaction time
  
  // Simulate form submission (this would need to match actual form implementation)
  const searchPayload = generateFlightSearchPayload();
  
  const searchResponse = http.post(`${BASE_URL}/search`, searchPayload, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'User-Agent': 'k6-load-test/1.0',
    },
    timeout: '30s',
  });
  
  const isSuccess = check(searchResponse, {
    'search status is 200 or 302': (r) => r.status === 200 || r.status === 302,
    'search response time < 5s': (r) => r.timings.duration < 5000,
  });
  
  errorRate.add(!isSuccess);
  responseTimeTrend.add(searchResponse.timings.duration);
  
  if (!isSuccess) {
    console.error(`Flight search UI failed: Status ${searchResponse.status}, Duration: ${searchResponse.timings.duration}ms`);
  }
}

function testFlightSearchAPI() {
  const searchPayload = generateFlightSearchPayload();
  
  const response = http.post(`${API_BASE_URL}/flights/search`, JSON.stringify(searchPayload), {
    headers: {
      'Content-Type': 'application/json',
      'User-Agent': 'k6-load-test/1.0',
    },
    timeout: '30s',
  });
  
  const isSuccess = check(response, {
    'API status is 200': (r) => r.status === 200,
    'API response time < 3s': (r) => r.timings.duration < 3000,
    'API response has data': (r) => {
      try {
        const json = r.json();
        return json.success === true && json.data !== null;
      } catch (e) {
        return false;
      }
    },
  });
  
  errorRate.add(!isSuccess);
  responseTimeTrend.add(response.timings.duration);
  
  if (!isSuccess) {
    console.error(`Flight search API failed: Status ${response.status}, Duration: ${response.timings.duration}ms`);
  }
}

export function teardown(data) {
  console.log('Load test teardown completed');
  console.log(`API was available: ${data.apiAvailable}`);
}