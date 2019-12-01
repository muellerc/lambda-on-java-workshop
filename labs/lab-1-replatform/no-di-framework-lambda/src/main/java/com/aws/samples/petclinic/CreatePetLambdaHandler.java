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

        Regions regions = Regions.fromName(System.getenv("AWS_REGION"));

        AWSCredentialsProvider credentialsProvider = DefaultAWSCredentialsProviderChain.getInstance();

        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
                .withCredentials(credentialsProvider)
                .withRegion(regions)
                .build();

        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        PetRepository repository = new PetRepository(dynamoDBMapper);

        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
                .withCredentials(credentialsProvider)
                .withRegion(regions)
                .build();

        MedicalRecordStore medicalRecordStore = new MedicalRecordStore(amazonS3);

        service = new PetService(repository, medicalRecordStore);
    }

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent input, Context context) {
        APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();
        response.setStatusCode(200);

        try {
            AWSXRay.beginSubsegment("deserializePetRecord");
            PetRecord petRecord = objectMapper.readValue(input.getBody(), PetRecord.class);
            AWSXRay.endSubsegment();

            petRecord = service.addPet(petRecord);

            AWSXRay.beginSubsegment("serializePetRecord");
            response.setBody(objectMapper.writeValueAsString(petRecord));
            AWSXRay.endSubsegment();
        } catch (Exception e) {
            response.setStatusCode(500);
            e.printStackTrace();
        }

        return response;
    }
}