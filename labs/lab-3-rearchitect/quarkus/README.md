./mvnw clean package

sam package --template-file template.yaml  --output-template-file packaged.yaml --s3-bucket tmp-cmr

sam deploy --template-file packaged.yaml --stack-name quarkus-lambda --capabilities CAPABILITY_IAM

export ENDPOINT=$(aws cloudformation describe-stacks \
    --stack-name quarkus-lambda \
    --query 'Stacks[].Outputs[?OutputKey==`QuarkusLambdaPetStoreApi`].OutputValue' \
    --output text)

curl -i -X POST -d '{"name": "Max", "type": "dog", "birthday": "2010-11-03"}' $ENDPOINT
