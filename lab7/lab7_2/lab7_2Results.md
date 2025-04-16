# Load Test Results Analysis

### How long did the API calls take?

- Average (avg): 164.93ms
- Minimum (min): 4.53ms
- Maximum (max): 707.83ms
- Median (med): 142.35ms
- 90th percentile (p90): 315.28ms
- 95th percentile (p95): 377.28ms


### How many requests were made?

1847 requests were made during the test. This can be seen in the http_reqs metric which shows "1847" with a rate of approximately 91.7 requests per second.


### How many requests failed?

0 requests failed. The http_req_failed metric shows "0.00% 0 out of 1847", indicating that all requests were successful. This is also confirmed by the checks information showing 100% of checks succeeded (1847 out of 1847).

The test ran for about 20.1 seconds with a maximum of 20 virtual users, completing 1847 iterations without any failures or interruptions.





### █ TOTAL RESULTS 

    checks_total.......................: 1847    91.703089/s
    checks_succeeded...................: 100.00% 1847 out of 1847
    checks_failed......................: 0.00%   0 out of 1847

    ✓ is status 200

    HTTP
    http_req_duration.......................................................: avg=164.93ms min=4.53ms med=142.35ms max=707.83ms p(90)=315.28ms p(95)=377.28ms
      { expected_response:true }............................................: avg=164.93ms min=4.53ms med=142.35ms max=707.83ms p(90)=315.28ms p(95)=377.28ms
    http_req_failed.........................................................: 0.00%  0 out of 1847
    http_reqs...............................................................: 1847   91.703089/s

    EXECUTION
    iteration_duration......................................................: avg=165.12ms min=4.63ms med=142.55ms max=707.96ms p(90)=315.78ms p(95)=377.48ms
    iterations..............................................................: 1847   91.703089/s
    vus.....................................................................: 1      min=1         max=20
    vus_max.................................................................: 20     min=20        max=20

    NETWORK
    data_received...........................................................: 1.3 MB 66 kB/s
    data_sent...............................................................: 674 kB 34 kB/s


    running (20.1s), 00/20 VUs, 1847 complete and 0 interrupted iterations
    default ✓ [======================================] 00/20 VUs  20s