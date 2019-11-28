./mvnw clean package

sam package --template-file template.yaml  --output-template-file packaged.yaml --s3-bucket tmp-cmr

sam deploy --template-file packaged.yaml --stack-name optimized-lambda-sdk-v2 --capabilities CAPABILITY_IAM
