

`export AWS_ACCESS_KEY_ID=$(aws --profile default configure get aws_access_key_id)`  
`export AWS_SECRET_ACCESS_KEY=$(aws --profile default configure get aws_secret_access_key)`  
`export AWS_REGION=$(aws --profile default configure get region)`  

java -cp lab-2-optimize/aws-java-sdk-v2/target/app.jar -verbose:class com.aws.samples.petclinic.Main | grep '\[Loaded' | wc -l

java -cp lab-2-optimize/aws-java-sdk-v2/target/app.jar -verbose:class com.aws.samples.petclinic.Main | grep '\[Loaded' | grep '.jar\]' | sed 's/\[Loaded \([^A-Z]*\)[\$A-Za-z0-9]* from .*\]/\1/g' | sort | uniq -c | sort

java -cp lab-2-optimize/aws-java-sdk-v2/target/app.jar -javaagent:java-instrumentation-1.0-SNAPSHOT.jar=instrumentation.cfg com.aws.samples.petclinic.Main

sam package --template-file lab-2-optimize/aws-java-sdk-v2/template.yaml  --output-template-file lab-2-optimize/aws-java-sdk-v2/packaged.yaml --s3-bucket $SAM_ARTIFACT_BUCKET
sam deploy --template-file lab-2-optimize/aws-java-sdk-v2/packaged.yaml --stack-name aws-java-sdk-v2 --capabilities CAPABILITY_IAM
export ENDPOINT=$(aws cloudformation describe-stacks \
    --stack-name aws-java-sdk-v2 \
    --query 'Stacks[].Outputs[?OutputKey==`PetsApiServiceURL`].OutputValue' \
    --output text)
export FUNCTION_ARN=$(aws cloudformation describe-stacks \
    --stack-name aws-java-sdk-v2 \
    --query 'Stacks[].Outputs[?OutputKey==`CreatePetLambdaHandlerFunction`].OutputValue' \
    --output text)


export JAVA_OPTS="-DBASE_URL=$ENDPOINT"
for i in {1..10}; do aws lambda update-function-configuration --function-name $FUNCTION_ARN --environment "Variables={KeyName1=KeyValue$i}"; gatling.sh --simulations-folder lab-2-optimize/aws-java-sdk-v2/src/test/scala --simulation LoadTest --run-description "aws-java-sdk-v2-run-$i"; done

    
    
    
    
    
curl -i -X POST -d '{"name": "Max", "type": "dog", "birthday": "2010-11-03", "medicalRecord": "bla bla bla"}' $ENDPOINT/pet

aws lambda update-function-configuration --function-name $FUNCTION_ARN --environment Variables={KeyName1=KeyValue1}
curl -i -X POST -d '{"name": "Max", "type": "dog", "birthday": "2010-11-03", "medicalRecord": "bla bla bla"}' $ENDPOINT/pet
