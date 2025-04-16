import http from "k6/http";
import { sleep, check } from "k6";

const BASE_URL = __ENV.BASE_URL || "http://localhost:3333";

export const options = {
  stages: [
    // ramp up from 0 to 20 VUs over the next 30 seconds
    { duration: '30s', target: 120 },
    // run 20 VUs over the next 30 seconds
    { duration: '30s', target: 120 },
    // ramp down from 20 to 0 VUs over the next 30 seconds
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    // SLO #1: 95% of requests must complete within 1.1 seconds
    'http_req_duration': ['p(95)<1100'], // 1100ms = 1.1s
    
    // SLO #2: Failed requests must be less than 1%
    'http_req_failed': ['rate<0.01'],
    
    // SLO #3: Ensure our checks (including body size) have at least 98% pass rate
    'checks': ['rate>0.98'],
  },
};

export default function () {
  let restrictions = {
    maxCaloriesPerSlice: 500,
    mustBeVegetarian: false,
    excludedIngredients: ["pepperoni"],
    excludedTools: ["knife"],
    maxNumberOfToppings: 6,
    minNumberOfToppings: 2,
  };
  
  let res = http.post(`${BASE_URL}/api/pizza`, JSON.stringify(restrictions), {
    headers: {
      "Content-Type": "application/json",
      "X-User-ID": 23423,
      "Authorization": "token abcdef0123456789"
    },
  });
  
  // First check if status is 200 and body exists
  let statusCheck = check(res, {
    "is status 200": (r) => r.status === 200,
    "body size is less than 1K": (r) => r.body.length < 1024,
    "checks": ["rate>0.98"],
  });
  
  // Only try to parse JSON if we have a valid response
  if (statusCheck && res.body.length > 0) {
    try {
      // Try to parse the JSON and then validate the structure
      let responseData = res.json();
      
      check(responseData, {
        "has valid pizza response": (data) => data.pizza !== undefined,
        "contains pizza name": (data) => data.pizza && data.pizza.name !== undefined,
        "contains pizza ingredients": (data) => data.pizza && Array.isArray(data.pizza.ingredients),
      });
    } catch (e) {
      console.error(`JSON parsing error: ${e.message}. Response body: ${res.body.substring(0, 100)}...`);
      
      // Add a failed check to track these errors
      check(res, {
        "valid JSON response": () => false
      });
    }
  }
}