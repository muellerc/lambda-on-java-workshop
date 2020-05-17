+++
title = "Dagger2 Lambda"
weight = 14
pre = ""
+++

## Analyze The Deployment Package

To determine the package size of your function, run the following command in the bash window in your AWS Cloud9 IDE:

```bash
cd ~/environment/lambda-on-java-workshop/labs
ls -lh lab-1-replatform/dagger2-lambda/target/app.jar
```

It will report a package size of **8.9 MB**.

To be able to run your function locally to analyse it in more detail, export your `AWS_REGION` as following:

```bash
export AWS_REGION=$(aws --profile default configure get region)
```

To determine the number of classes which gets loaded to execute your function, run the following command in the bash window in your AWS Cloud9 IDE. Each application contains a helper Main class which invokes your AWS Lambda function locally:

```bash
java -cp lab-1-replatform/dagger2-lambda/target/app.jar -verbose:class Main | grep '\[Loaded' | wc -l
```

It will report **4516 classes got loaded** to execute your AWS Lambda function.

To get a breakdown by package name, run the following command:

```bash
java -cp lab-1-replatform/dagger2-lambda/target/app.jar -verbose:class Main | grep '\[Loaded' | grep '.jar\]' | sed 's/\[Loaded \([^A-Z]*\)[\$A-Za-z0-9]* from .*\]/\1/g' | sort | uniq -c | sort
```

You can run an instrumented version of your function which measure the execution time for all methods in the most interesting classes by running the following command:

```bash
java -cp lab-1-replatform/dagger2-lambda/target/app.jar -javaagent:java-instrumentation-1.0-SNAPSHOT.jar=instrumentation.cfg Main
```

You will a similar output like this (package names dropped), which gives you an idea where you spend most of the time:

```bash
ObjectMapper._findRootDeserializer(DeserializationContext,JavaType) : 190
ObjectMapper._readMapAndClose(JsonParser,JavaType) : 194
ObjectMapper.readValue(URL,Class) : 222
ObjectMapper._readMapAndClose(JsonParser,JavaType) : 61
ObjectMapper.readValue(URL,Class) : 62
ObjectMapper._findRootDeserializer(DeserializationContext,JavaType) : 53
ObjectMapper._readMapAndClose(JsonParser,JavaType) : 79
ObjectMapper.readValue(URL,Class) : 80
ObjectMapper._findRootDeserializer(DeserializationContext,JavaType) : 56
ObjectMapper._readMapAndClose(JsonParser,JavaType) : 175
ObjectMapper.readValue(InputStream,Class) : 175
CreatePetLambdaHandler.handleRequest(APIGatewayV2ProxyRequestEvent,Context) : 1555
```

## Deploy The Application

To deploy the application, run the following command. 

```bash
sam deploy --template-file lab-1-replatform/dagger2-lambda/template.yaml \
    --stack-name dagger2-lambda \
    --capabilities CAPABILITY_IAM \
    --guided
```

Follow the instructions and make sure your are chosing the right region.
Export the service endpoint url and the function ARN as environment variables for easy access:

```bash
export ENDPOINT=$(aws cloudformation describe-stacks \
    --stack-name dagger2-lambda \
    --query 'Stacks[].Outputs[?OutputKey==`PetsApiServiceURL`].OutputValue' \
    --output text)
export FUNCTION_ARN=$(aws cloudformation describe-stacks \
    --stack-name dagger2-lambda \
    --query 'Stacks[].Outputs[?OutputKey==`CreatePetLambdaHandlerFunction`].OutputValue' \
    --output text)
```

## Memory Configuration

We choose to go with 1024 MB for the load and performance tests.

{{< figure src="dagger2-lambda/power-tuning.png" >}}

## Run The Load Tests

```bash
export JAVA_OPTS="-DBASE_URL=$ENDPOINT"
for i in {1..10}; do aws lambda update-function-configuration --function-name $FUNCTION_ARN --environment "Variables={TABLE_NAME=$PETS_TABLE,BUCKET_NAME=$PETS_BUCKET,KeyName1=KeyValue$i}"; gatling.sh --simulations-folder lab-1-replatform/dagger2-lambda/src/test/scala --simulation LoadTest --run-description "dagger2-lambda-run-$i"; done
```

## Run the Cold-Start Tests

```bash
for i in {1..10}; do aws lambda update-function-configuration --function-name $FUNCTION_ARN --environment "Variables={TABLE_NAME=$PETS_TABLE,BUCKET_NAME=$PETS_BUCKET,KeyName1=KeyValue$i}"; curl -i -X POST -d '{"name": "Max", "type": "dog", "birthday": "2010-11-03", "medicalRecord": "bla bla bla"}' $ENDPOINT/pet; done
```

## Result Overview

### Gatling Load Test Result (best out of 10)

{{< figure src="dagger2-lambda/gatling-1.png" height="400" >}}
{{< figure src="dagger2-lambda/gatling-2.png" height="400" >}}

### Amazon X-Ray Cold-Start Trace (best out of 10)

{{< figure src="dagger2-lambda/x-ray.png" >}}

## Source Code

Yo can find the source code **[here](https://github.com/muellerc/lambda-on-java-workshop/tree/master/labs/lab-1-replatform/dagger2-lambda)**.
