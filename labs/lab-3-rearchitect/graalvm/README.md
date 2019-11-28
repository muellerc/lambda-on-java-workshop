./mvnw clean package

sam package --template-file template.yaml  --output-template-file packaged.yaml --s3-bucket tmp-cmr

sam deploy --template-file packaged.yaml --stack-name graalvm-native-image-lambda --capabilities CAPABILITY_IAM

export ENDPOINT=$(aws cloudformation describe-stacks \
    --stack-name graalvm-native-image-lambda \
    --query 'Stacks[].Outputs[?OutputKey==`GraalVMNativeImageLambdaClinicServiceURL`].OutputValue' \
    --output text)

curl -i -X POST -d '{"name": "Max", "type": "dog", "birthday": "2010-11-03", "medicalRecord": "bla bla bla"}' $ENDPOINT/pet
