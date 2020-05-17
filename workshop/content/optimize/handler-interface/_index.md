+++
title = "Handler Interface"
weight = 25
pre = ""
+++

## Analyze The Deployment Package

To determine the package size of your function, run the following command in the bash window in your AWS Cloud9 IDE:

```bash
cd ~/environment/lambda-on-java-workshop/labs
ls -lh lab-2-optimize/handler-interface/target/app.jar
```

It will report a package size of **13 MB**.

To be able to run your function locally to analyse it in more detail, export your `AWS_REGION` as following:

```bash
export AWS_REGION=$(aws --profile default configure get region)
```

To determine the number of classes which gets loaded to execute your function, run the following command in the bash window in your AWS Cloud9 IDE. Each application contains a helper Main class which invokes your AWS Lambda function locally:

```bash
# Java 8
java -cp lab-2-optimize/handler-interface/target/app.jar -verbose:class com.aws.samples.petclinic.Main | grep '\[Loaded' | wc -l
```

```bash
# Java 11
java -cp lab-2-optimize/handler-interface/target/app.jar -verbose:class com.aws.samples.petclinic.Main | grep '\[class,load\]' | wc -l
```

It will report **4833 classes got loaded** to execute your AWS Lambda function.

To get a breakdown by package name, run the following command:

```bash
# Java 8
java -cp lab-2-optimize/handler-interface/target/app.jar -verbose:class com.aws.samples.petclinic.Main | grep '\[Loaded' | grep '.jar\]' | sed 's/\[Loaded \([^A-Z]*\)[\$A-Za-z0-9]* from .*\]/\1/g' | sort | uniq -c | sort
```

You can run an instrumented version of your function which measure the execution time for all methods in the most interesting classes by running the following command:

```bash
java -cp lab-2-optimize/handler-interface/target/app.jar -javaagent:java-instrumentation-1.0-SNAPSHOT.jar=instrumentation.cfg com.aws.samples.petclinic.Main
```

You will a similar output like this (package names dropped), which gives you an idea where you spend most of the time:

```bash

```

## Deploy The Application

To deploy the application, run the following command. It also exports the service endpoint url and the function ARN as environment variables for easy access:

```bash
sam deploy --template-file lab-2-optimize/handler-interface/template.yaml \
    --stack-name handler-interface \
    --capabilities CAPABILITY_IAM \
    --guided
```

Follow the instructions and make sure your are chosing the right region.
Export the service endpoint url and the function ARN as environment variables for easy access:

```bash
export ENDPOINT=$(aws cloudformation describe-stacks \
    --stack-name handler-interface \
    --query 'Stacks[].Outputs[?OutputKey==`PetsApiServiceURL`].OutputValue' \
    --output text)
export FUNCTION_ARN=$(aws cloudformation describe-stacks \
    --stack-name handler-interface \
    --query 'Stacks[].Outputs[?OutputKey==`CreatePetLambdaHandlerFunction`].OutputValue' \
    --output text)
```

## Memory Configuration

We choose to go with 1024 MB for the load and performance tests.

## Run The Load Tests

```bash
export JAVA_OPTS="-DBASE_URL=$ENDPOINT"
for i in {1..10}; do aws lambda update-function-configuration --function-name $FUNCTION_ARN --environment "Variables={TABLE_NAME=$PETS_TABLE,BUCKET_NAME=$PETS_BUCKET,KeyName1=KeyValue$i}"; gatling.sh --simulations-folder lab-2-optimize/handler-interface/src/test/scala --simulation LoadTest --run-description "handler-interface-run-$i"; done
```

## Run the Cold-Start Tests

```bash
for i in {1..10}; do aws lambda update-function-configuration --function-name $FUNCTION_ARN --environment "Variables={TABLE_NAME=$PETS_TABLE,BUCKET_NAME=$PETS_BUCKET,KeyName1=KeyValue$i}"; curl -i -X POST -d '{"name": "Max", "type": "dog", "birthday": "2010-11-03", "medicalRecord": "bla bla bla"}' $ENDPOINT/pet; done
```

## Result Overview

### Gatling Load Test Result (best out of 10)

{{< figure src="handler-interface/gatling-1.png" height="400" >}}
{{< figure src="handler-interface/gatling-2.png" height="400" >}}

### Amazon X-Ray Cold-Start Trace (best out of 10)

{{< figure src="handler-interface/x-ray.png" >}}

## Source Code

Yo can find the source code **[here](https://github.com/muellerc/lambda-on-java-workshop/tree/master/labs/lab-2-optimize/handler-interface)**.
