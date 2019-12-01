# Maven build steps
`./mvnw clean package`

# Docker build steps
`docker build -t 0-spring-fargate .`  

#RUN THE APPLICATION IN DOCKER LOCALLY
`AWS_ACCESS_KEY_ID=$(aws --profile default configure get aws_access_key_id)`  

`AWS_SECRET_ACCESS_KEY=$(aws --profile default configure get aws_secret_access_key)`  

`AWS_REGION=$(aws --profile default configure get region)`  

`docker run -it --rm -e AWS_REGION=$AWS_REGION -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY -e PORT=80 -p 80:80 0-spring-fargate:latest`  

## Store a Pet
`curl -i -X POST -d '{"name": "Max", "type": "dog", "birthday": "2010-11-03", "medicalRecord": "bla bla bla"}' -H "Content-Type: application/json" http://localhost/pet` 

# Upload to Amazon ECR
`aws ecr create-repository --repository-name 0-spring-fargate`  

`$(aws ecr get-login --no-include-email --region eu-central-1)`  

`docker tag 0-spring-fargate:latest 689573718314.dkr.ecr.eu-central-1.amazonaws.com/0-spring-fargate:latest`  

`docker push 689573718314.dkr.ecr.eu-central-1.amazonaws.com/0-spring-fargate:latest`  

```
aws cloudformation create-stack \
    --stack-name spring-fargate \
    --template-body file://template.yaml \
    --capabilities CAPABILITY_IAM
```


```
aws cloudformation wait stack-create-complete \
    --stack-name spring-fargate
```