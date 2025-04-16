  █ TOTAL RESULTS 

    HTTP
    http_req_duration.......................................................: avg=99.33ms min=99.33ms med=99.33ms max=99.33ms p(90)=99.33ms p(95)=99.33ms
      { expected_response:true }............................................: avg=99.33ms min=99.33ms med=99.33ms max=99.33ms p(90)=99.33ms p(95)=99.33ms
    http_req_failed.........................................................: 0.00% 0 out of 1
    http_reqs...............................................................: 1     10.005154/s

    EXECUTION
    iteration_duration......................................................: avg=99.85ms min=99.85ms med=99.85ms max=99.85ms p(90)=99.85ms p(95)=99.85ms
    iterations..............................................................: 1     10.005154/s

    NETWORK
    data_received...........................................................: 705 B 7.1 kB/s
    data_sent...............................................................: 365 B 3.7 kB/s


    running (00m00.1s), 0/1 VUs, 1 complete and 0 interrupted iterations
    default ✓ [======================================] 1 VUs  00m00.1s/10m0s  1/1 iters, 1 per VU



### How long did the API call take?

The API call took 99.33ms on average (avg=99.33ms). This can be seen in the http_req_duration metric which shows consistent values for min, med, max, p(90), and p(95) all at 99.33ms.

### How many requests were made?

According to the output, 1 request was made. This is shown in the http_reqs metric which shows "1" with a rate of approximately 10 requests per second (which makes sense for a single request executed in about 0.1 seconds).

### How many requests failed?

0 requests failed. The http_req_failed metric shows "0.00% 0 out of 1", indicating that all requests were successful (returned an HTTP status code in the 200-399 range).

The test completed successfully with 1 virtual user making a single request to the pizza recommendation API, and the API responded with a pizza named "The Little Quattro Formaggi" which has 5 ingredients.
