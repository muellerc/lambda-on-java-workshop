package com.aws.samples.petclinic;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;
import com.aws.samples.petclinic.pet.MedicalRecordStore;
import com.aws.samples.petclinic.pet.PetRecord;
import com.aws.samples.petclinic.pet.PetRepository;
import com.aws.samples.petclinic.pet.PetService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreatePetLambdaHandler implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private PetService service;
    private ObjectMapper objectMapper;

    public CreatePetLambdaHandler() {
        objectMapper = new ObjectMapper();

        boolean enableXRay = System.getenv("AWS_XRAY_DAEMON_ADDRESS") != null;

        String table = System.getenv("TABLE_NAME");

        String bucket = System.getenv("BUCKET_NAME");

        Regions regions = Regions.fromName(System.getenv("AWS_REGION"));

        AWSCredentialsProvider credentialsProvider = DefaultAWSCredentialsProviderChain.getInstance();

        AmazonDynamoDBClientBuilder dynamoDBClientBuilder = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(regions);

        if (enableXRay) {
            dynamoDBClientBuilder.withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()));
        }

        AmazonDynamoDB amazonDynamoDB = dynamoDBClientBuilder.build();

        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        PetRepository repository = new PetRepository(dynamoDBMapper);

        AmazonS3ClientBuilder s3ClientBuilder = AmazonS3ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(regions);

        if (enableXRay) {
            s3ClientBuilder.withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()));
        }

        AmazonS3 amazonS3 = s3ClientBuilder.build();

        MedicalRecordStore medicalRecordStore = new MedicalRecordStore(amazonS3, bucket);

        service = new PetService(repository, medicalRecordStore);
    }

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent input, Context context) {
        APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();
        response.setStatusCode(200);

        try {
            PetRecord petRecord = objectMapper.readValue(input.getBody(), PetRecord.class);

            petRecord = service.addPet(petRecord);

            response.setBody(objectMapper.writeValueAsString(petRecord));
        } catch (Exception e) {
            response.setStatusCode(500);
            e.printStackTrace();
        }

        return response;
    }
}
