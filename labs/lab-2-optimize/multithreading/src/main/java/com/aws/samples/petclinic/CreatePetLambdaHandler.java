package com.aws.samples.petclinic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.aws.samples.petclinic.pet.MedicalRecordStore;
import com.aws.samples.petclinic.pet.PetRecord;
import com.aws.samples.petclinic.pet.PetRepository;
import com.aws.samples.petclinic.pet.PetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreatePetLambdaHandler implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    private PetService service;
    private ObjectMapper objectMapper;
    ExecutorService executorService;

    public CreatePetLambdaHandler() {
        objectMapper = new ObjectMapper();

        String table = System.getenv("TABLE_NAME");

        String bucket = System.getenv("BUCKET_NAME");

        Region regions = Region.of(System.getenv("AWS_REGION"));

        AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(regions)
                .overrideConfiguration(ClientOverrideConfiguration.builder()
//                        .addExecutionInterceptor(new TracingInterceptor())
                        .build()
                )
                .build();

        PetRepository repository = new PetRepository(dynamoDbClient, table);

        S3Client s3Client = S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(regions)
                .overrideConfiguration(ClientOverrideConfiguration.builder()
//                        .addExecutionInterceptor(new TracingInterceptor())
                        .build()
                )
                .build();

        MedicalRecordStore medicalRecordStore = new MedicalRecordStore(s3Client, bucket);

        executorService = Executors.newFixedThreadPool(2);

        service = new PetService(executorService, repository, medicalRecordStore);
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
