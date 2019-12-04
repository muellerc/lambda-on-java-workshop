+++
title = "Tooling"
weight = 3
pre = ""
+++

## How do we measure performance & throughput

To determine the behavior under load, we are running a load test for 1 minute, starting with 1 user (which determines the concurrency) and increase it linearly, until we reached 20 users after one minute (meaning we are increasing the concurrency by 1 after each 3 seconds for 1 minute). We are running this test 10 times per service implementation against a new deployed instance and report the best result.

To determine the cold-start time, we are measure 10 cold-starts and report the best one for each service implementation.

The load test is running in an AWS Cloud9 instance (m5.large) in the same region as we have deployed our services.

### AWS Lambda Power Tuning

Our goal is to run our AWS Lambda function with a performance optimized configuration, taking cost and scaling into account as well. We are using **[AWS Lambda Power Tuning](https://github.com/alexcasalboni/aws-lambda-power-tuning)** to find the optimal configuration.

The easiest way is to install it via the **[AWS Serverless Application Repository](https://aws.amazon.com/serverless/serverlessrepo/)** by clicking at this **[link](https://serverlessrepo.aws.amazon.com/applications/arn:aws:serverlessrepo:us-east-1:451282441545:applications~aws-lambda-power-tuning)**.

A sample visualization with recommendations looks like this:

{{< figure src="tooling/aws-lambda-power-tuning.png" >}}

### Gatling

We are using the Scala based load generating tool **[Gatling](https://gatling.io/)** to load test our different service implementations. Each service comes with a Scala based load test in the folder `src/test/scala/xxxLoadTest.scala`.  

In a future version, we may integrate the load test scenario directly into our Maven project, leveraging the **[Gatling Maven Plugin](https://gatling.io/docs/2.3/extensions/maven_plugin)**.

At the end of a load test, Gatling creates a nice report about the performance and throughput from a client perspective:  
![Sample Gatling Report 1](tooling/sample_gatling_report_1.png)
![Sample Gatling Report 2](tooling/sample_gatling_report_2.png)

To get started with Gatling, please follow the **[Quickstart](https://gatling.io/docs/current/quickstart/)** and the **[Advanced Tutorial](https://gatling.io/docs/current/advanced_tutorial/)**.

### Amazon X-Ray

In addition, we are using **[AWS X-Ray](https://aws.amazon.com/xray/)** to sample some requests to get better insights where we spend our time by processing these requests.

![X-Ray](tooling/x-ray.png)

### Javaagent Instrumentation

Because we have situations where we don't want or cannot use Amazon X-Ray, we use **[Javaagent Instrumentation](https://github.com/mvd199/javaagent-instrumentation)** in addition to get better insights.

You will get similar output like this (package names dropped), which gives you an idea where you spend most of the time executing the request. You have to specify the classes, which should be instrumented:

```bash
ApacheHttpClient.createClient(ApacheHttpClient$DefaultBuilder,AttributeMap) : 323
DefaultDynamoDbClient.init(BaseAwsJsonProtocolFactory$Builder) : 79
ObjectMapper._findRootDeserializer(DeserializationContext,JavaType) : 140
ObjectMapper._readMapAndClose(JsonParser,JavaType) : 142
ObjectMapper.readValue(String,JavaType) : 161
ObjectMapper.readValue(java.lang.String,java.lang.Class) : 165
ApacheHttpClient.execute(HttpRequestBase) : 429
ApacheHttpClient.access$500(ApacheHttpClient,HttpRequestBase) : 429
DefaultDynamoDbClient.putItem(PutItemRequest) : 707
ApacheHttpClient.execute(HttpRequestBase) : 251
ApacheHttpClient.access$500(ApacheHttpClient,HttpRequestBase) : 251
DefaultS3Client.putObject(PutObjectRequest,RequestBody) : 329
CreatePetLambdaHandler.handleRequest(APIGatewayV2ProxyRequestEvent,Context) : 1428
```