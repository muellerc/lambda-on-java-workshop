package com.aws.samples.petclinic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.aws.samples.petclinic.pet.MedicalRecordStore;
import com.aws.samples.petclinic.pet.PetRecord;
import com.aws.samples.petclinic.pet.PetRepository;
import com.aws.samples.petclinic.pet.PetService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class CreatePetLambdaStreamHandler implements RequestStreamHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private PetService service;

    public CreatePetLambdaStreamHandler() {
        try {
            String table = System.getenv("TABLE_NAME");

            String bucket = System.getenv("BUCKET_NAME");

            Region region = Region.of(System.getenv("AWS_REGION"));

            AwsCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();

            SdkHttpClient dynamoDbDdkHttpClient = UrlConnectionHttpClient.builder().build();

            DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                    .httpClient(dynamoDbDdkHttpClient)
                    .credentialsProvider(credentialsProvider)
                    .region(region)
                    .endpointOverride(new URI(String.format("https://dynamodb.%s.amazonaws.com", region.id())))
                    .overrideConfiguration(
                            ClientOverrideConfiguration
                                    .builder()
//                                .addExecutionInterceptor(tracingInterceptor)
                                    .build())
                    .build();

            PetRepository repository = new PetRepository(dynamoDbClient, table);

            SdkHttpClient s3sdkHttpClient = UrlConnectionHttpClient.builder().build();

            S3Client s3Client = S3Client.builder()
                    .httpClient(s3sdkHttpClient)
                    .credentialsProvider(credentialsProvider)
                    .region(region)
                    .endpointOverride(new URI(String.format("https://s3.%s.amazonaws.com", region.id())))
                    .overrideConfiguration(
                            ClientOverrideConfiguration
                                    .builder()
//                                    .addExecutionInterceptor(tracingInterceptor)
                                    .build())
                    .build();

            MedicalRecordStore medicalRecordStore = new MedicalRecordStore(s3Client, bucket);

            service = new PetService(repository, medicalRecordStore);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        try {
            JsonNode inputNode = objectMapper.readTree(inputStream);
            JsonNode bodyNode = objectMapper.readTree(inputNode.get("body").asText());

            PetRecord petRecord = fromJsonNode(bodyNode);

            petRecord = service.addPet(petRecord);

            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("statusCode", 200);
            outputNode.put("body", objectMapper.writeValueAsString(asJsonNode(petRecord)));

            objectMapper.writeValue(outputStream, outputNode);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private ObjectNode asJsonNode(PetRecord pet) {
        ObjectNode petNode = objectMapper.createObjectNode();
        petNode.put("id", pet.getId());
        petNode.put("name", pet.getName());
        petNode.put("type", pet.getType());
        petNode.put("birthday", pet.getBirthday());
        petNode.put("medicalRecord", pet.getMedicalRecord());

        return petNode;
    }

    private PetRecord fromJsonNode(JsonNode bodyNode) {
        PetRecord pet = new PetRecord();
        pet.setName(bodyNode.get("name").asText());
        pet.setType(bodyNode.get("type").asText());
        pet.setBirthday(bodyNode.get("birthday").asText());
        pet.setMedicalRecord(bodyNode.get("medicalRecord").asText());

        return pet;
    }
}
