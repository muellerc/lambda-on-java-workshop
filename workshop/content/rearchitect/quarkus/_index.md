+++
title = "Quarkus"
weight = 31
pre = ""
+++

In this section, we use the popular "Supersonic Subatomic Java" framework [Quarkus](https://quarkus.io/) to build our Pet service. Quarkus supports the build of native Java images by leveraging the GraalVM native compiler. This is what we have implemented for that section.   

## Analyze The Deployment Package

To determine the package size of your function, run the following command in the bash window in your AWS Cloud9 IDE:

```bash
cd ~/environment/lambda-on-java-workshop/labs
ls -lh lab-3-rearchitect/quarkus/target/function.zip
```

It will report a package size of **17,0 MB**.

## Deploy The Application

To deploy the application, run the following command:

```bash
sam deploy --template-file lab-3-rearchitect/quarkus/template.yaml \
    --stack-name quarkus \
    --capabilities CAPABILITY_IAM \
    --guided
```

Follow the instructions and make sure your are chosing the right region.
Export the service endpoint url and the function ARN as environment variables for easy access:

```bash
export ENDPOINT=$(aws cloudformation describe-stacks \
    --stack-name quarkus \
    --query 'Stacks[].Outputs[?OutputKey==`PetsApiServiceURL`].OutputValue' \
    --output text)
export FUNCTION_ARN=$(aws cloudformation describe-stacks \
    --stack-name quarkus \
    --query 'Stacks[].Outputs[?OutputKey==`CreatePetLambdaHandlerFunction`].OutputValue' \
    --output text)
```

## Memory Configuration

We choose to go with 512 MB for the load and performance tests.

{{< figure src="quarkus/power-tuning.png" >}}

## Run The Load Tests

```bash
export JAVA_OPTS="-DBASE_URL=$ENDPOINT"
for i in {1..10}; do aws lambda update-function-configuration --function-name $FUNCTION_ARN --environment "Variables={DISABLE_SIGNAL_HANDLERS=true,TABLE_NAME=$TABLE_NAME,BUCKET_NAME=$BUCKET_NAME,KeyName1=KeyValue$i}"; gatling.sh --simulations-folder lab-3-rearchitect/quarkus/src/test/scala --simulation LoadTest --run-description "quarkus-run-$i"; done
```

## Run the Cold-Start Tests

```bash
for i in {1..10}; do aws lambda update-function-configuration --function-name $FUNCTION_ARN --environment "Variables={DISABLE_SIGNAL_HANDLERS=true,TABLE_NAME=$TABLE_NAME,BUCKET_NAME=$BUCKET_NAME,KeyName1=KeyValue$i}"; curl -i -X POST -d '{"name": "Max", "type": "dog", "birthday": "2010-11-03", "medicalRecord": "bla bla bla"}' $ENDPOINT/pet; done
```

## Result Overview

### Gatling Load Test Result (best out of 10)

{{< figure src="quarkus/gatling-1.png" height="400" >}}
{{< figure src="quarkus/gatling-2.png" height="400" >}}

### Amazon X-Ray Cold-Start Trace (best out of 10)

{{< figure src="quarkus/x-ray.png" >}}

## Source Code

Yo can find the source code **[here](https://github.com/muellerc/lambda-on-java-workshop/tree/master/labs/lab-3-rearchitect/quarkus)**.

