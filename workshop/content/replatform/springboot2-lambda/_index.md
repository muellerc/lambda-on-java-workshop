+++
title = "Springboot2 Lambda"
weight = 13
pre = ""
+++

## Analyze The Deployment Package

To determine the package size of your function, run the following command in the bash window in your AWS Cloud9 IDE:

```bash
cd ~/environment/lambda-on-java-workshop/labs
ls -lh lab-1-replatform/springboot2-lambda/target/springboot2-lambda.jar
```

It will report a package size of **39 MB**.

## Deploy The Application

To deploy the application, run the following command. It also exports the service endpoint url and the function ARN as environment variables for easy access:

```bash
sam deploy --template-file lab-1-replatform/springboot2-lambda/template.yaml \
    --stack-name springboot2-lambda \
    --capabilities CAPABILITY_IAM \
    --guided
```

Follow the instructions and make sure your are chosing the right region.
Export the service endpoint url and the function ARN as environment variables for easy access:

```bash
export ENDPOINT=$(aws cloudformation describe-stacks \
    --stack-name springboot2-lambda \
    --query 'Stacks[].Outputs[?OutputKey==`PetsApiServiceURL`].OutputValue' \
    --output text)
export FUNCTION_ARN=$(aws cloudformation describe-stacks \
    --stack-name springboot2-lambda \
    --query 'Stacks[].Outputs[?OutputKey==`CreatePetLambdaHandlerFunction`].OutputValue' \
    --output text)
```

## Memory Configuration

We choose to go with 1024 MB for the load and performance tests.

{{< figure src="springboot2-lambda/power-tuning.png" >}}

## Run The Load Tests

```bash
export JAVA_OPTS="-DBASE_URL=$ENDPOINT"
for i in {1..10}; do aws lambda update-function-configuration --function-name $FUNCTION_ARN --environment "Variables={TABLE_NAME=$PETS_TABLE,BUCKET_NAME=$PETS_BUCKET,KeyName1=KeyValue$i}"; gatling.sh --simulations-folder lab-1-replatform/springboot2-lambda/src/test/scala --simulation LoadTest --run-description "springboot2-lambda-run-$i"; done
```

## Run the Cold-Start Tests

```bash
for i in {1..10}; do aws lambda update-function-configuration --function-name $FUNCTION_ARN --environment "Variables={TABLE_NAME=$PETS_TABLE,BUCKET_NAME=$PETS_BUCKET,KeyName1=KeyValue$i}"; curl -i -X POST -H 'content-type: application/json' -d '{"name": "Max", "type": "dog", "birthday": "2010-11-03", "medicalRecord": "bla bla bla"}' $ENDPOINT/pet; done
```

## Result Overview

### Gatling Load Test Result (best out of 10)

{{< figure src="springboot2-lambda/gatling-1.png" height="400" >}}
{{< figure src="springboot2-lambda/gatling-2.png" height="400" >}}

### Amazon X-Ray Cold-Start Trace (best out of 10)

{{< figure src="springboot2-lambda/x-ray.png" >}}

## Source Code

Yo can find the source code **[here](https://github.com/muellerc/lambda-on-java-workshop/tree/master/labs/lab-1-replatform/springboot2-lambda)**.
