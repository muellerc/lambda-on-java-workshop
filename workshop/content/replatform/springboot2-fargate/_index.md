+++
title = "Springboot2 in Fargate"
weight = 11
pre = ""
+++

## Analyze The Deployment Package

To determine the package size of your function, run the following command in the bash window in your AWS Cloud9 IDE:

```bash
cd ~/environment/lambda-on-java-workshop/labs
ls -lh lab-1-replatform/springboot2-fargate/target/app.jar
```

It will report a package size of **42 MB**.

## Deploy The Application

First, we have to build our Docker image and upload it to Amazon ECR, our private image registry:

```bash
$(aws ecr get-login --no-include-email --region REPLACE_ME_WITH_YOUR_REGION)
aws ecr create-repository --repository-name spring-fargate

cd ~/environment/lambda-on-java-workshop/labs/labslab-1-replatform/springboot2-fargate
docker build -t spring-fargate .
docker tag spring-fargate:latest REPLACE_ME_WITH_YOUR_AWS_ACCOUNT_ID.dkr.ecr.REPLACE_ME_WITH_YOUR_REGION.amazonaws.com/spring-fargate:latest
docker push REPLACE_ME_WITH_YOUR_AWS_ACCOUNT_ID.dkr.ecr.REPLACE_ME_WITH_YOUR_REGION.amazonaws.com/spring-fargate:latest
```

Last, we have to update the file `~/environment/lambda-on-java-workshop/labs/lab-1-replatform/springboot2-fargate/template.yaml` with your AWS account id and region:

```
Image: !Sub 'REPLACE_ME_WITH_YOUR_AWS_ACCOUNT_ID.dkr.ecr.REPLACE_ME_WITH_YOUR_REGION.amazonaws.com/spring-fargate:latest'
```

To deploy the application, run the following command. It also exports the service endpoint url and the function ARN as environment variables for easy access:

```bash
cd ~/environment/lambda-on-java-workshop/labs
sam package --template-file lab-1-replatform/springboot2-fargate/template.yaml \
    --output-template-file lab-1-replatform/springboot2-fargate/packaged.yaml \
    --s3-bucket $SAM_ARTIFACT_BUCKET
sam deploy --template-file lab-1-replatform/springboot2-fargate/packaged.yaml \
    --stack-name springboot2-fargate \
    --capabilities CAPABILITY_IAM
export ENDPOINT=$(aws cloudformation describe-stacks \
    --stack-name springboot2-fargate \
    --query 'Stacks[].Outputs[?OutputKey==`PetsApiServiceURL`].OutputValue' \
    --output text)
export FUNCTION_ARN=$(aws cloudformation describe-stacks \
    --stack-name springboot2-fargate \
    --query 'Stacks[].Outputs[?OutputKey==`CreatePetLambdaHandlerFunction`].OutputValue' \
    --output text)
```

## Memory Configuration

We choose to go with 1024 MB for the load and performance tests.

## Run The Load Tests

```bash
export JAVA_OPTS="-DBASE_URL=$ENDPOINT"
for i in {1..10}; do aws lambda update-function-configuration --function-name $FUNCTION_ARN --environment "Variables={TABLE_NAME=$PETS_TABLE,BUCKET_NAME=$PETS_BUCKET,KeyName1=KeyValue$i}"; gatling.sh --simulations-folder lab-1-replatform/springboot2-fargate/src/test/scala --simulation LoadTest --run-description "springboot2-fargate-run-$i"; done
```

## Run the Cold-Start Tests

```bash
for i in {1..10}; do aws lambda update-function-configuration --function-name $FUNCTION_ARN --environment "Variables={TABLE_NAME=$PETS_TABLE,BUCKET_NAME=$PETS_BUCKET,KeyName1=KeyValue$i}"; curl -i -X POST -H 'content-type: application/json' -d '{"name": "Max", "type": "dog", "birthday": "2010-11-03", "medicalRecord": "bla bla bla"}' $ENDPOINT/pet; done
```

## Result Overview

### Gatling Load Test Result (best out of 10)

{{< figure src="springboot2-fargate/gatling-1.png" height="400" >}}
{{< figure src="springboot2-fargate/gatling-2.png" height="400" >}}


## Source Code

Yo can find the source code **[here](https://github.com/muellerc/lambda-on-java-workshop/tree/master/labs/lab-1-replatform/springboot2-fargate)**.
