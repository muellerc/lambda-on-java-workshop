+++
title = "Measurement"
weight = 3
pre = ""
+++

## How do we measure performance & throughput

To determine the behavior under load, we are running a load test for 1 minute, starting with 1 user (which determines the concurrency) and increase it linearly, until we reached 20 users after one minute (meaning we are increasing the concurrency by 1 after each 3 seconds for 1 minute). We are running this test 10 times per service implementation against a new deployed instance and report the best result.

To determine the cold-start time, we are measure 10 cold-starts and report the best one for each service implementation.

The load test is running in an AWS Cloud9 instance (m5.large) in the same region as we have deployed our services.

### Gatling

We are using the Scala based load generating tool **[Gatling](https://gatling.io/)** to load test our different service implementations. Each service comes with a Scala based load test in the folder `src/test/scala/xxxLoadTest.scala`.  

In a future version, we may integrate the load test scenario directly into our Maven project, leveraging the **[Gatling Maven Plugin](https://gatling.io/docs/2.3/extensions/maven_plugin)**.

At the end of a load test, Gatling creates a nice report about the performance and throughput from a client perspective:  
![Sample Gatling Report 1](measurement/sample_gatling_report_1.png)
![Sample Gatling Report 2](measurement/sample_gatling_report_2.png)

To get started with Gatling, please follow the **[Quickstart](https://gatling.io/docs/current/quickstart/)** and the **[Advanced Tutorial](https://gatling.io/docs/current/advanced_tutorial/)**.

### Amazon X-Ray

In addition, we are using **[AWS X-Ray](https://aws.amazon.com/xray/)** to sample some requests to get better insights where we spend our time by processing these requests.

![X-Ray](measurement/x-ray.png)

### Javaagent Instrumentation

Because we have situations where we don't want or cannot use Amazon X-Ray, we use **[Javaagent Instrumentation](https://github.com/mvd199/javaagent-instrumentation)** in addition to get better insights.